package net.minecraft.src;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpPipelineConnection {
   private String host = null;
   private int port = 0;
   private Proxy proxy = Proxy.NO_PROXY;
   private List<HttpPipelineRequest> listRequests = new LinkedList<>();
   private List<HttpPipelineRequest> listRequestsSend = new LinkedList<>();
   private Socket socket = null;
   private InputStream inputStream = null;
   private OutputStream outputStream = null;
   private HttpPipelineSender httpPipelineSender = null;
   private HttpPipelineReceiver httpPipelineReceiver = null;
   private int countRequests = 0;
   private boolean responseReceived = false;
   private long keepaliveTimeoutMs = 5000L;
   private int keepaliveMaxCount = 1000;
   private long timeLastActivityMs = System.currentTimeMillis();
   private boolean terminated = false;
   public static final int TIMEOUT_CONNECT_MS = 5000;
   public static final int TIMEOUT_READ_MS = 5000;
   private static final Pattern patternFullUrl = Pattern.compile("^[a-zA-Z]+://.*");

   public HttpPipelineConnection(String host, int port) {
      this(host, port, Proxy.NO_PROXY);
   }

   public HttpPipelineConnection(String host, int port, Proxy proxy) {
      this.host = host;
      this.port = port;
      this.proxy = proxy;
      this.httpPipelineSender = new HttpPipelineSender(this);
      this.httpPipelineSender.start();
      this.httpPipelineReceiver = new HttpPipelineReceiver(this);
      this.httpPipelineReceiver.start();
   }

   public synchronized boolean addRequest(HttpPipelineRequest pr) {
      if (this.isClosed()) {
         return false;
      }

      this.addRequest(pr, this.listRequests);
      this.addRequest(pr, this.listRequestsSend);
      this.countRequests++;
      return true;
   }

   private void addRequest(HttpPipelineRequest pr, List<HttpPipelineRequest> list) {
      list.add(pr);
      this.notifyAll();
   }

   public synchronized void setSocket(Socket s) throws IOException {
      if (!this.terminated) {
         if (this.socket != null) {
            throw new IllegalArgumentException("Already connected");
         }

         this.socket = s;
         this.socket.setTcpNoDelay(true);
         this.inputStream = this.socket.getInputStream();
         this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
         this.onActivity();
         this.notifyAll();
      }
   }

   public synchronized OutputStream getOutputStream() throws IOException, InterruptedException {
      while (this.outputStream == null) {
         this.checkTimeout();
         this.wait(1000L);
      }

      return this.outputStream;
   }

   public synchronized InputStream getInputStream() throws IOException, InterruptedException {
      while (this.inputStream == null) {
         this.checkTimeout();
         this.wait(1000L);
      }

      return this.inputStream;
   }

   public synchronized HttpPipelineRequest getNextRequestSend() throws InterruptedException, IOException {
      if (this.listRequestsSend.size() <= 0 && this.outputStream != null) {
         this.outputStream.flush();
      }

      return this.getNextRequest(this.listRequestsSend, true);
   }

   public synchronized HttpPipelineRequest getNextRequestReceive() throws InterruptedException {
      return this.getNextRequest(this.listRequests, false);
   }

   private HttpPipelineRequest getNextRequest(List<HttpPipelineRequest> list, boolean removeIn) throws InterruptedException {
      while (list.size() <= 0) {
         this.checkTimeout();
         this.wait(1000L);
      }

      this.onActivity();
      return removeIn ? list.remove(0) : list.get(0);
   }

   private void checkTimeout() {
      if (this.socket != null) {
         long l = this.keepaliveTimeoutMs;
         if (this.listRequests.size() > 0) {
            l = 5000L;
         }

         long m = System.currentTimeMillis();
         if (m > this.timeLastActivityMs + l) {
            this.terminate(new InterruptedException("Timeout " + l));
         }
      }
   }

   private void onActivity() {
      this.timeLastActivityMs = System.currentTimeMillis();
   }

   public synchronized void onRequestSent(HttpPipelineRequest pr) {
      if (!this.terminated) {
         this.onActivity();
      }
   }

   public synchronized void onResponseReceived(HttpPipelineRequest pr, HttpResponse resp) {
      if (!this.terminated) {
         this.responseReceived = true;
         this.onActivity();
         if (this.listRequests.size() <= 0 || this.listRequests.get(0) != pr) {
            throw new IllegalArgumentException("Response out of order: " + pr);
         }

         this.listRequests.remove(0);
         pr.setClosed(true);
         String string = resp.getHeader("Location");
         if (resp.getStatus() / 100 == 3 && string != null && pr.getHttpRequest().getRedirects() < 5) {
            try {
               string = this.normalizeUrl(string, pr.getHttpRequest());
               HttpRequest httpRequest = HttpPipeline.makeRequest(string, pr.getHttpRequest().getProxy());
               httpRequest.setRedirects(pr.getHttpRequest().getRedirects() + 1);
               HttpPipelineRequest httpPipelineRequest = new HttpPipelineRequest(httpRequest, pr.getHttpListener());
               HttpPipeline.addRequest(httpPipelineRequest);
            } catch (IOException ioException) {
               pr.getHttpListener().failed(pr.getHttpRequest(), ioException);
            }
         } else {
            HttpListener httpListener = pr.getHttpListener();
            httpListener.finished(pr.getHttpRequest(), resp);
         }

         this.checkResponseHeader(resp);
      }
   }

   private String normalizeUrl(String url, HttpRequest hr) {
      if (patternFullUrl.matcher(url).matches()) {
         return url;
      }

      if (url.startsWith("//")) {
         return "http:" + url;
      }

      String string = hr.getHost();
      if (hr.getPort() != 80) {
         string = string + ":" + hr.getPort();
      }

      if (url.startsWith("/")) {
         return "http://" + string + url;
      }

      String string2 = hr.getFile();
      int i = string2.lastIndexOf("/");
      return i >= 0 ? "http://" + string + string2.substring(0, i + 1) + url : "http://" + string + "/" + url;
   }

   private void checkResponseHeader(HttpResponse resp) {
      String string = resp.getHeader("Connection");
      if (string != null && !string.toLowerCase().equals("keep-alive")) {
         this.terminate(new EOFException("Connection not keep-alive"));
      }

      String string2 = resp.getHeader("Keep-Alive");
      if (string2 != null) {
         String[] strings = Config.tokenize(string2, ",;");

         for (int i = 0; i < strings.length; i++) {
            String string3 = strings[i];
            String[] strings2 = this.split(string3, '=');
            if (strings2.length >= 2) {
               if (strings2[0].equals("timeout")) {
                  int j = Config.parseInt(strings2[1], -1);
                  if (j > 0) {
                     this.keepaliveTimeoutMs = j * 1000;
                  }
               }

               if (strings2[0].equals("max")) {
                  int k = Config.parseInt(strings2[1], -1);
                  if (k > 0) {
                     this.keepaliveMaxCount = k;
                  }
               }
            }
         }
      }
   }

   private String[] split(String str, char separator) {
      int i = str.indexOf(separator);
      if (i < 0) {
         return new String[]{str};
      }

      String string = str.substring(0, i);
      String string2 = str.substring(i + 1);
      return new String[]{string, string2};
   }

   public synchronized void onExceptionSend(HttpPipelineRequest pr, Exception eIn) {
      this.terminate(eIn);
   }

   public synchronized void onExceptionReceive(HttpPipelineRequest pr, Exception eIn) {
      this.terminate(eIn);
   }

   private synchronized void terminate(Exception eIn) {
      if (!this.terminated) {
         this.terminated = true;
         this.terminateRequests(eIn);
         if (this.httpPipelineSender != null) {
            this.httpPipelineSender.interrupt();
         }

         if (this.httpPipelineReceiver != null) {
            this.httpPipelineReceiver.interrupt();
         }

         try {
            if (this.socket != null) {
               this.socket.close();
            }
         } catch (IOException ioException) {
         }

         this.socket = null;
         this.inputStream = null;
         this.outputStream = null;
      }
   }

   private void terminateRequests(Exception eIn) {
      if (this.listRequests.size() > 0) {
         if (!this.responseReceived) {
            HttpPipelineRequest httpPipelineRequest = this.listRequests.remove(0);
            httpPipelineRequest.getHttpListener().failed(httpPipelineRequest.getHttpRequest(), eIn);
            httpPipelineRequest.setClosed(true);
         }

         while (this.listRequests.size() > 0) {
            HttpPipelineRequest httpPipelineRequest2 = this.listRequests.remove(0);
            HttpPipeline.addRequest(httpPipelineRequest2);
         }
      }
   }

   public synchronized boolean isClosed() {
      return this.terminated ? true : this.countRequests >= this.keepaliveMaxCount;
   }

   public int K() {
      return this.countRequests;
   }

   public synchronized boolean hasActiveRequests() {
      return this.listRequests.size() > 0;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public Proxy getProxy() {
      return this.proxy;
   }
}
