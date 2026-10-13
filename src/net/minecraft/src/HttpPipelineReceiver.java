package net.minecraft.src;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

public class HttpPipelineReceiver extends Thread {
   private HttpPipelineConnection httpPipelineConnection = null;
   private static final Charset ASCII = Charset.forName("ASCII");
   private static final char CR = '\r';
   private static final char LF = '\n';
   private static final long a = 29908504001806L;

   public HttpPipelineReceiver(HttpPipelineConnection var1) {
      super("HttpPipelineReceiver");
      this.httpPipelineConnection = var1;
   }

   @Override
   public void run() {
      while (!Thread.interrupted()) {
         HttpPipelineRequest var4 = null;

         try {
            var4 = this.httpPipelineConnection.getNextRequestReceive();
            InputStream var5 = this.httpPipelineConnection.getInputStream();
            HttpResponse var6 = this.readResponse(var5);
            this.httpPipelineConnection.onResponseReceived(var4, var6);
         } catch (InterruptedException var7) {
            return;
         } catch (Exception var8) {
            this.httpPipelineConnection.onExceptionReceive(var4, var8);
         }
      }
   }

   private HttpResponse readResponse(InputStream var1) throws IOException {
      String var5 = this.readLine(var1);
      String[] var6 = Config.tokenize(var5, " ");
      if (var6.length < 3) {
         throw new IOException("Invalid status line: " + var5);
      }

      String var7 = var6[0];
      int var8 = Config.parseInt(var6[1], 0);
      String var9 = var6[2];
      LinkedHashMap var10 = new LinkedHashMap();

      while (true) {
         String var11 = this.readLine(var1);
         if (var11.length() <= 0) {
            byte[] var15 = null;
            String var16 = (String)var10.get("Content-Length");
            if (var16 != null) {
               int var17 = Config.parseInt(var16, -1);
               if (var17 > 0) {
                  var15 = new byte[var17];
                  this.readFull(var15, var1);
               }
            } else {
               String var18 = (String)var10.get("Transfer-Encoding");
               if (Config.equals(var18, "chunked")) {
                  var15 = this.readContentChunked(var1);
               }
            }

            return new HttpResponse(var8, var5, var10, var15);
         }

         int var12 = var11.indexOf(":");
         if (var12 > 0) {
            String var13 = var11.substring(0, var12).trim();
            String var14 = var11.substring(var12 + 1).trim();
            var10.put(var13, var14);
         }
      }
   }

   private byte[] readContentChunked(InputStream var1) throws IOException {
      ByteArrayOutputStream var5 = new ByteArrayOutputStream();

      int var8;
      do {
         String var6 = this.readLine(var1);
         String[] var7 = Config.tokenize(var6, "; ");
         var8 = Integer.parseInt(var7[0], 16);
         byte[] var9 = new byte[var8];
         this.readFull(var9, var1);
         var5.write(var9);
         this.readLine(var1);
      } while (var8 != 0);

      return var5.toByteArray();
   }

   private void readFull(byte[] var1, InputStream var2) throws IOException {
      int var7 = 0;

      while (var7 < var1.length) {
         int var6 = var2.read(var1, var7, var1.length - var7);
         if (var6 < 0) {
            throw new EOFException();
         }

         var7 += var6;
      }
   }

   private String readLine(InputStream var1) throws IOException {
      ByteArrayOutputStream var5 = new ByteArrayOutputStream();
      int var6 = -1;
      boolean var7 = false;

      while (true) {
         int var8 = var1.read();
         if (var8 < 0) {
            break;
         }

         var5.write(var8);
         if (var6 == 13 && var8 == 10) {
            var7 = true;
            break;
         }

         var6 = var8;
      }

      byte[] var10 = var5.toByteArray();
      String var9 = new String(var10, ASCII);
      if (var7) {
         var9 = var9.substring(0, var9.length() - 2);
      }

      return var9;
   }

   static {
      long var20 = a ^ 7637533993369L;
      long var10000 = var20 ^ 5120024432034644375L;
      var10000 = var20 ^ 1460243355857750299L;
   }
}
