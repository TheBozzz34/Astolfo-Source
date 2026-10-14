package net.minecraft.src;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpPipelineSender extends Thread {
   private HttpPipelineConnection httpPipelineConnection = null;
   private static Charset ASCII = Charset.forName("ASCII");

   public HttpPipelineSender(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineSender");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   @Override
   public void run() {
      HttpPipelineRequest httpPipelineRequest = null;

      try {
         this.connect();

         while (!Thread.interrupted()) {
            httpPipelineRequest = this.httpPipelineConnection.getNextRequestSend();
            HttpRequest httpRequest = httpPipelineRequest.getHttpRequest();
            OutputStream outputStream = this.httpPipelineConnection.getOutputStream();
            this.writeRequest(httpRequest, outputStream);
            this.httpPipelineConnection.onRequestSent(httpPipelineRequest);
         }
      } catch (InterruptedException interruptedException) {
         return;
      } catch (Exception exception) {
         this.httpPipelineConnection.onExceptionSend(httpPipelineRequest, exception);
      }
   }

   private void connect() throws IOException {
      String string = this.httpPipelineConnection.getHost();
      int i = this.httpPipelineConnection.getPort();
      Proxy proxy = this.httpPipelineConnection.getProxy();
      Socket socket = new Socket(proxy);
      socket.connect(new InetSocketAddress(string, i), 5000);
      this.httpPipelineConnection.setSocket(socket);
   }

   private void writeRequest(HttpRequest req, OutputStream out) throws IOException {
      this.write(out, req.getMethod() + " " + req.getFile() + " " + req.getHttp() + "\r\n");
      Map map = req.getHeaders();

      for (Object object : map.keySet()) {
         String string = (String)object;
         String string2 = req.getHeaders().get(string);
         this.write(out, string + ": " + string2 + "\r\n");
      }

      this.write(out, "\r\n");
   }

   private void write(OutputStream out, String str) throws IOException {
      byte[] bytes = str.getBytes(ASCII);
      out.write(bytes);
   }
}
