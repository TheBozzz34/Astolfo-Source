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
   private static final long a = 119396430619104L;

   public HttpPipelineConnection(String var1, int var2) {
      this(var1, var2, Proxy.NO_PROXY);
   }

   public HttpPipelineConnection(String var1, int var2, Proxy var3) {
      this.host = var1;
      this.port = var2;
      this.proxy = var3;
      this.httpPipelineSender = new HttpPipelineSender(this);
      this.httpPipelineSender.start();
      this.httpPipelineReceiver = new HttpPipelineReceiver(this);
      this.httpPipelineReceiver.start();
   }

   public synchronized boolean addRequest(HttpPipelineRequest var1) {
      if (this.isClosed()) {
         return false;
      }

      this.addRequest(var1, this.listRequests);
      this.addRequest(var1, this.listRequestsSend);
      this.countRequests++;
      return true;
   }

   private void addRequest(HttpPipelineRequest var1, List<HttpPipelineRequest> var2) {
      var2.add(var1);
      this.notifyAll();
   }

   public synchronized void setSocket(Socket var1) throws IOException {
      if (!this.terminated) {
         if (this.socket != null) {
            throw new IllegalArgumentException("Already connected");
         }

         this.socket = var1;
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

   private HttpPipelineRequest getNextRequest(List<HttpPipelineRequest> var1, boolean var2) throws InterruptedException {
      while (var1.size() <= 0) {
         this.checkTimeout();
         this.wait(1000L);
      }

      this.onActivity();
      return var2 ? (HttpPipelineRequest)var1.remove(0) : (HttpPipelineRequest)var1.get(0);
   }

   private void checkTimeout() {
      if (this.socket != null) {
         long var4 = this.keepaliveTimeoutMs;
         if (this.listRequests.size() > 0) {
            var4 = 5000L;
         }

         long var6 = System.currentTimeMillis();
         if (var6 > this.timeLastActivityMs + var4) {
            this.terminate(new InterruptedException("Timeout " + var4));
         }
      }
   }

   private void onActivity() {
      this.timeLastActivityMs = System.currentTimeMillis();
   }

   public synchronized void onRequestSent(HttpPipelineRequest var1) {
      if (!this.terminated) {
         this.onActivity();
      }
   }

   public synchronized void onResponseReceived(HttpPipelineRequest var1, HttpResponse var2) {
      if (!this.terminated) {
         this.responseReceived = true;
         this.onActivity();
         if (this.listRequests.size() <= 0 || this.listRequests.get(0) != var1) {
            throw new IllegalArgumentException("Response out of order: " + var1);
         }

         this.listRequests.remove(0);
         var1.setClosed(true);
         String var6 = var2.getHeader("Location");
         if (var2.getStatus() / 100 == 3 && var6 != null && var1.getHttpRequest().getRedirects() < 5) {
            try {
               var6 = this.normalizeUrl(var6, var1.getHttpRequest());
               HttpRequest var11 = HttpPipeline.makeRequest(var6, var1.getHttpRequest().getProxy());
               var11.setRedirects(var1.getHttpRequest().getRedirects() + 1);
               HttpPipelineRequest var8 = new HttpPipelineRequest(var11, var1.getHttpListener());
               HttpPipeline.addRequest(var8);
            } catch (IOException var9) {
               var1.getHttpListener().failed(var1.getHttpRequest(), var9);
            }
         } else {
            HttpListener var7 = var1.getHttpListener();
            var7.finished(var1.getHttpRequest(), var2);
         }

         this.checkResponseHeader(var2);
      }
   }

   private String normalizeUrl(String var1, HttpRequest var2) {
      if (patternFullUrl.matcher(var1).matches()) {
         return var1;
      }

      if (var1.startsWith("//")) {
         return "http:" + var1;
      }

      String var6 = var2.getHost();
      if (var2.getPort() != 80) {
         var6 = var6 + ":" + var2.getPort();
      }

      if (var1.startsWith("/")) {
         return "http://" + var6 + var1;
      }

      String var7 = var2.getFile();
      int var8 = var7.lastIndexOf("/");
      return var8 >= 0 ? "http://" + var6 + var7.substring(0, var8 + 1) + var1 : "http://" + var6 + "/" + var1;
   }

   private void checkResponseHeader(HttpResponse var1) {
      String var5 = var1.getHeader("Connection");
      if (var5 != null && !var5.toLowerCase().equals("keep-alive")) {
         this.terminate(new EOFException("Connection not keep-alive"));
      }

      String var6 = var1.getHeader("Keep-Alive");
      if (var6 != null) {
         String[] var7 = Config.tokenize(var6, ",;");

         for (int var8 = 0; var8 < var7.length; var8++) {
            String var9 = var7[var8];
            String[] var10 = this.split(var9, '=');
            if (var10.length >= 2) {
               if (var10[0].equals("timeout")) {
                  int var11 = Config.parseInt(var10[1], -1);
                  if (var11 > 0) {
                     this.keepaliveTimeoutMs = var11 * 1000;
                  }
               }

               if (var10[0].equals("max")) {
                  int var12 = Config.parseInt(var10[1], -1);
                  if (var12 > 0) {
                     this.keepaliveMaxCount = var12;
                  }
               }
            }
         }
      }
   }

   private String[] split(String var1, char var2) {
      int var6 = var1.indexOf(var2);
      if (var6 < 0) {
         return new String[]{var1};
      }

      String var7 = var1.substring(0, var6);
      String var8 = var1.substring(var6 + 1);
      return new String[]{var7, var8};
   }

   public synchronized void onExceptionSend(HttpPipelineRequest var1, Exception var2) {
      this.terminate(var2);
   }

   public synchronized void onExceptionReceive(HttpPipelineRequest var1, Exception var2) {
      this.terminate(var2);
   }

   private synchronized void terminate(Exception var1) {
      if (!this.terminated) {
         this.terminated = true;
         this.terminateRequests(var1);
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
         } catch (IOException var6) {
         }

         this.socket = null;
         this.inputStream = null;
         this.outputStream = null;
      }
   }

   private void terminateRequests(Exception var1) {
      if (this.listRequests.size() > 0) {
         if (!this.responseReceived) {
            HttpPipelineRequest var5 = this.listRequests.remove(0);
            var5.getHttpListener().failed(var5.getHttpRequest(), var1);
            var5.setClosed(true);
         }

         while (this.listRequests.size() > 0) {
            HttpPipelineRequest var6 = this.listRequests.remove(0);
            HttpPipeline.addRequest(var6);
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

   static {
      long var20 = a ^ 18979438127173L;
      long var10000 = var20 ^ 934104904770245707L;
      var10000 = var20 ^ 934104904770245707L;
   }
}
