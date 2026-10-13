package net.minecraft.src;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpPipeline {
   private static Map mapConnections = new HashMap();
   public static final String HEADER_USER_AGENT = "User-Agent";
   public static final String HEADER_HOST = "Host";
   public static final String HEADER_ACCEPT = "Accept";
   public static final String HEADER_LOCATION = "Location";
   public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
   public static final String HEADER_CONNECTION = "Connection";
   public static final String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
   public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
   public static final String HEADER_VALUE_CHUNKED = "chunked";
   private static final long c = 97344573252971L;

   public static void addRequest(String var0, HttpListener var1) throws IOException {
      addRequest(var0, var1, Proxy.NO_PROXY);
   }

   public static void addRequest(String var0, HttpListener var1, Proxy var2) throws IOException {
      HttpRequest var3 = makeRequest(var0, var2);
      HttpPipelineRequest var4 = new HttpPipelineRequest(var3, var1);
      addRequest(var4);
   }

   public static HttpRequest makeRequest(String var0, Proxy var1) throws IOException {
      URL var5 = new URL(var0);
      if (!var5.getProtocol().equals("http")) {
         throw new IOException("Only protocol http is supported: " + var5);
      }

      String var6 = var5.getFile();
      String var7 = var5.getHost();
      int var8 = var5.getPort();
      if (var8 <= 0) {
         var8 = 80;
      }

      String var9 = "GET";
      String var10 = "HTTP/1.1";
      LinkedHashMap var11 = new LinkedHashMap();
      var11.put("User-Agent", "Java/" + System.getProperty("java.version"));
      var11.put("Host", var7);
      var11.put("Accept", "text/html, image/gif, image/png");
      var11.put("Connection", "keep-alive");
      byte[] var12 = new byte[0];
      return new HttpRequest(var7, var8, var1, var9, var6, var10, var11, var12);
   }

   public static void addRequest(HttpPipelineRequest var0) {
      HttpRequest var4 = var0.getHttpRequest();

      for (HttpPipelineConnection var5 = getConnection(var4.getHost(), var4.getPort(), var4.getProxy());
         !var5.addRequest(var0);
         var5 = getConnection(var4.getHost(), var4.getPort(), var4.getProxy())
      ) {
         removeConnection(var4.getHost(), var4.getPort(), var4.getProxy(), var5);
      }
   }

   private static synchronized HttpPipelineConnection getConnection(String var0, int var1, Proxy var2) {
      String var6 = makeConnectionKey(var0, var1, var2);
      HttpPipelineConnection var7 = (HttpPipelineConnection)mapConnections.get(var6);
      if (var7 == null) {
         var7 = new HttpPipelineConnection(var0, var1, var2);
         mapConnections.put(var6, var7);
      }

      return var7;
   }

   private static synchronized void removeConnection(String var0, int var1, Proxy var2, HttpPipelineConnection var3) {
      String var7 = makeConnectionKey(var0, var1, var2);
      HttpPipelineConnection var8 = (HttpPipelineConnection)mapConnections.get(var7);
      if (var8 == var3) {
         mapConnections.remove(var7);
      }
   }

   private static String makeConnectionKey(String var0, int var1, Proxy var2) {
      return var0 + ":" + var1 + "-" + var2;
   }

   public static byte[] get(String var0) throws IOException {
      return get(var0, Proxy.NO_PROXY);
   }

   public static byte[] get(String var0, Proxy var1) throws IOException {
      HttpRequest var5 = makeRequest(var0, var1);
      HttpResponse var6 = executeRequest(var5);
      if (var6.getStatus() / 100 != 2) {
         throw new IOException("HTTP response: " + var6.getStatus());
      } else {
         return var6.getBody();
      }
   }

   public static HttpResponse executeRequest(HttpRequest var0) throws IOException {
      final HashMap var4 = new HashMap();
      HttpListener var7 = new HttpListener() {
         private static final long a = 60137406596133L;

         @Override
         public void finished(HttpRequest var1, HttpResponse var2) {
            synchronized (var4) {
               var4.put("Response", var2);
               var4.notifyAll();
            }
         }

         @Override
         public void failed(HttpRequest var1, Exception var2) {
            synchronized (var4) {
               var4.put("Exception", var2);
               var4.notifyAll();
            }
         }
      };
      synchronized (var4) {
         HttpPipelineRequest var9 = new HttpPipelineRequest(var0, var7);
         addRequest(var9);

         try {
            var4.wait();
         } catch (InterruptedException var13) {
            throw new InterruptedIOException("Interrupted");
         }

         Exception var10 = (Exception)var4.get("Exception");
         if (var10 != null) {
            if (var10 instanceof IOException) {
               throw (IOException)var10;
            } else if (var10 instanceof RuntimeException) {
               throw (RuntimeException)var10;
            } else {
               throw new RuntimeException(var10.getMessage(), var10);
            }
         } else {
            HttpResponse var11 = (HttpResponse)var4.get("Response");
            if (var11 == null) {
               throw new IOException("Response is null");
            } else {
               return var11;
            }
         }
      }
   }

   public static boolean hasActiveRequests() {
      for (Object var4 : mapConnections.values()) {
         if (((HttpPipelineConnection)var4).hasActiveRequests()) {
            return true;
         }
      }

      return false;
   }
}
