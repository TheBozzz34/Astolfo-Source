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
   private static final long a = 40739642359755L;
   private static final long f = 4675006755632059272L;

   public HttpPipelineSender(HttpPipelineConnection var1) {
      super("HttpPipelineSender");
      this.httpPipelineConnection = var1;
   }

   @Override
   public void run() {
      HttpPipelineRequest var4 = null;

      try {
         this.connect();

         while (!Thread.interrupted()) {
            var4 = this.httpPipelineConnection.getNextRequestSend();
            HttpRequest var5 = var4.getHttpRequest();
            OutputStream var6 = this.httpPipelineConnection.getOutputStream();
            this.writeRequest(var5, var6);
            this.httpPipelineConnection.onRequestSent(var4);
         }
      } catch (InterruptedException var7) {
         return;
      } catch (Exception var8) {
         this.httpPipelineConnection.onExceptionSend(var4, var8);
      }
   }

   private void connect() throws IOException {
      String var3 = this.httpPipelineConnection.getHost();
      int var4 = this.httpPipelineConnection.getPort();
      Proxy var5 = this.httpPipelineConnection.getProxy();
      Socket var6 = new Socket(var5);
      var6.connect(new InetSocketAddress(var3, var4), (int)f);
      this.httpPipelineConnection.setSocket(var6);
   }

   private void writeRequest(HttpRequest var1, OutputStream var2) throws IOException {
      this.write(var2, var1.getMethod() + " " + var1.getFile() + " " + var1.getHttp() + "\r\n");
      Map var6 = var1.getHeaders();

      for (Object var8$raw : var6.keySet()) {
         String var8 = (String)var8$raw;
         String var9 = var1.getHeaders().get(var8);
         this.write(var2, var8 + ": " + var9 + "\r\n");
      }

      this.write(var2, "\r\n");
   }

   private void write(OutputStream var1, String var2) throws IOException {
      byte[] var3 = var2.getBytes(ASCII);
      var1.write(var3);
   }
}
