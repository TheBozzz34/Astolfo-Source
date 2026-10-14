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

   public HttpPipelineReceiver(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineReceiver");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   @Override
   public void run() {
      while (!Thread.interrupted()) {
         HttpPipelineRequest httpPipelineRequest = null;

         try {
            httpPipelineRequest = this.httpPipelineConnection.getNextRequestReceive();
            InputStream inputStream = this.httpPipelineConnection.getInputStream();
            HttpResponse httpResponse = this.readResponse(inputStream);
            this.httpPipelineConnection.onResponseReceived(httpPipelineRequest, httpResponse);
         } catch (InterruptedException interruptedException) {
            return;
         } catch (Exception exception) {
            this.httpPipelineConnection.onExceptionReceive(httpPipelineRequest, exception);
         }
      }
   }

   private HttpResponse readResponse(InputStream in) throws IOException {
      String string = this.readLine(in);
      String[] strings = Config.tokenize(string, " ");
      if (strings.length < 3) {
         throw new IOException("Invalid status line: " + string);
      }

      String string2 = strings[0];
      int i = Config.parseInt(strings[1], 0);
      String string3 = strings[2];
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      while (true) {
         String string4 = this.readLine(in);
         if (string4.length() <= 0) {
            byte[] bytes = null;
            String string5 = (String)linkedHashMap.get("Content-Length");
            if (string5 != null) {
               int j = Config.parseInt(string5, -1);
               if (j > 0) {
                  bytes = new byte[j];
                  this.readFull(bytes, in);
               }
            } else {
               String string6 = (String)linkedHashMap.get("Transfer-Encoding");
               if (Config.equals(string6, "chunked")) {
                  bytes = this.readContentChunked(in);
               }
            }

            return new HttpResponse(i, string, linkedHashMap, bytes);
         }

         int k = string4.indexOf(":");
         if (k > 0) {
            String string7 = string4.substring(0, k).trim();
            String string8 = string4.substring(k + 1).trim();
            linkedHashMap.put(string7, string8);
         }
      }
   }

   private byte[] readContentChunked(InputStream in) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      int i;
      do {
         String string = this.readLine(in);
         String[] strings = Config.tokenize(string, "; ");
         i = Integer.parseInt(strings[0], 16);
         byte[] bytes = new byte[i];
         this.readFull(bytes, in);
         byteArrayOutputStream.write(bytes);
         this.readLine(in);
      } while (i != 0);

      return byteArrayOutputStream.toByteArray();
   }

   private void readFull(byte[] buf, InputStream in) throws IOException {
      int i = 0;

      while (i < buf.length) {
         int j = in.read(buf, i, buf.length - i);
         if (j < 0) {
            throw new EOFException();
         }

         i += j;
      }
   }

   private String readLine(InputStream in) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int i = -1;
      boolean bl = false;

      while (true) {
         int j = in.read();
         if (j < 0) {
            break;
         }

         byteArrayOutputStream.write(j);
         if (i == 13 && j == 10) {
            bl = true;
            break;
         }

         i = j;
      }

      byte[] bytes = byteArrayOutputStream.toByteArray();
      String string = new String(bytes, ASCII);
      if (bl) {
         string = string.substring(0, string.length() - 2);
      }

      return string;
   }

   static {
      long l = a ^ 7637533993369L;
      long m = l ^ 5120024432034644375L;
      m = l ^ 1460243355857750299L;
   }
}
