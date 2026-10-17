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

   public static void addRequest(String urlStr, HttpListener listener) throws IOException {
      addRequest(urlStr, listener, Proxy.NO_PROXY);
   }

   public static void addRequest(String urlStr, HttpListener listener, Proxy proxy) throws IOException {
      HttpRequest hr = makeRequest(urlStr, proxy);
      HttpPipelineRequest hpr = new HttpPipelineRequest(hr, listener);
      addRequest(hpr);
   }

   public static HttpRequest makeRequest(String urlStr, Proxy proxy) throws IOException {
      URL url = new URL(urlStr);
      if (!url.getProtocol().equals("http")) {
         throw new IOException("Only protocol http is supported: " + url);
      }

      String string = url.getFile();
      String string2 = url.getHost();
      int i = url.getPort();
      if (i <= 0) {
         i = 80;
      }

      String string3 = "GET";
      String string4 = "HTTP/1.1";
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("User-Agent", "Java/" + System.getProperty("java.version"));
      linkedHashMap.put("Host", string2);
      linkedHashMap.put("Accept", "text/html, image/gif, image/png");
      linkedHashMap.put("Connection", "keep-alive");
      byte[] bytes = new byte[0];
      return new HttpRequest(string2, i, proxy, string3, string, string4, linkedHashMap, bytes);
   }

   public static void addRequest(HttpPipelineRequest pr) {
      HttpRequest httpRequest = pr.getHttpRequest();

      for (HttpPipelineConnection httpPipelineConnection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy());
         !httpPipelineConnection.addRequest(pr);
         httpPipelineConnection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy())
      ) {
         removeConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy(), httpPipelineConnection);
      }
   }

   private static synchronized HttpPipelineConnection getConnection(String host, int port, Proxy proxy) {
      String string = makeConnectionKey(host, port, proxy);
      HttpPipelineConnection httpPipelineConnection = (HttpPipelineConnection)mapConnections.get(string);
      if (httpPipelineConnection == null) {
         httpPipelineConnection = new HttpPipelineConnection(host, port, proxy);
         mapConnections.put(string, httpPipelineConnection);
      }

      return httpPipelineConnection;
   }

   private static synchronized void removeConnection(String host, int port, Proxy proxy, HttpPipelineConnection hpc) {
      String string = makeConnectionKey(host, port, proxy);
      HttpPipelineConnection httpPipelineConnection = (HttpPipelineConnection)mapConnections.get(string);
      if (httpPipelineConnection == hpc) {
         mapConnections.remove(string);
      }
   }

   private static String makeConnectionKey(String host, int port, Proxy proxy) {
      return host + ":" + port + "-" + proxy;
   }

   public static byte[] get(String urlStr) throws IOException {
      return get(urlStr, Proxy.NO_PROXY);
   }

   public static byte[] get(String urlStr, Proxy proxy) throws IOException {
      HttpRequest httpRequest = makeRequest(urlStr, proxy);
      HttpResponse httpResponse = executeRequest(httpRequest);
      if (httpResponse.getStatus() / 100 != 2) {
         throw new IOException("HTTP response: " + httpResponse.getStatus());
      } else {
         return httpResponse.getBody();
      }
   }

   public static HttpResponse executeRequest(HttpRequest req) throws IOException {
      final HashMap hashMap = new HashMap();
      HttpListener httpListener = new HttpListener() {
         @Override
         public void finished(HttpRequest req, HttpResponse resp) {
            synchronized (hashMap) {
               hashMap.put("Response", resp);
               hashMap.notifyAll();
            }
         }

         @Override
         public void failed(HttpRequest req, Exception e) {
            synchronized (hashMap) {
               hashMap.put("Exception", e);
               hashMap.notifyAll();
            }
         }
      };
      synchronized (hashMap) {
         HttpPipelineRequest httpPipelineRequest = new HttpPipelineRequest(req, httpListener);
         addRequest(httpPipelineRequest);

         try {
            hashMap.wait();
         } catch (InterruptedException interruptedException) {
            throw new InterruptedIOException("Interrupted");
         }

         Exception exception = (Exception)hashMap.get("Exception");
         if (exception != null) {
            if (exception instanceof IOException) {
               throw (IOException)exception;
            } else if (exception instanceof RuntimeException) {
               throw (RuntimeException)exception;
            } else {
               throw new RuntimeException(exception.getMessage(), exception);
            }
         } else {
            HttpResponse httpResponse = (HttpResponse)hashMap.get("Response");
            if (httpResponse == null) {
               throw new IOException("Response is null");
            } else {
               return httpResponse;
            }
         }
      }
   }

   public static boolean hasActiveRequests() {
      for (Object object : mapConnections.values()) {
         if (((HttpPipelineConnection)object).hasActiveRequests()) {
            return true;
         }
      }

      return false;
   }
}
