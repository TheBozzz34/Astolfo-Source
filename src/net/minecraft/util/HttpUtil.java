package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build())
   );
   private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private static final long a = 69641840283211L;

   public static String buildPostString(Map<String, Object> var0) {
      StringBuilder var4 = new StringBuilder();

      for (Entry var6 : var0.entrySet()) {
         if (var4.length() > 0) {
            var4.append('&');
         }

         try {
            var4.append(URLEncoder.encode((String)var6.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException var9) {
            var9.printStackTrace();
         }

         if (var6.getValue() != null) {
            var4.append('=');

            try {
               var4.append(URLEncoder.encode(var6.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException var8) {
               var8.printStackTrace();
            }
         }
      }

      return var4.toString();
   }

   public static String postMap(URL var0, Map<String, Object> var1, boolean var2) {
      return post(var0, buildPostString(var1), var2);
   }

   private static String post(URL var0, String var1, boolean var2) {
      try {
         Proxy var6 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
         if (var6 == null) {
            var6 = Proxy.NO_PROXY;
         }

         HttpURLConnection var7 = (HttpURLConnection)var0.openConnection(var6);
         var7.setRequestMethod("POST");
         var7.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var7.setRequestProperty("Content-Length", "" + var1.getBytes().length);
         var7.setRequestProperty("Content-Language", "en-US");
         var7.setUseCaches(false);
         var7.setDoInput(true);
         var7.setDoOutput(true);
         DataOutputStream var8 = new DataOutputStream(var7.getOutputStream());
         var8.writeBytes(var1);
         var8.flush();
         var8.close();
         BufferedReader var9 = new BufferedReader(new InputStreamReader(var7.getInputStream()));
         StringBuffer var10 = new StringBuffer();

         String var11;
         while ((var11 = var9.readLine()) != null) {
            var10.append(var11);
            var10.append('\r');
         }

         var9.close();
         return var10.toString();
      } catch (Exception var12) {
         if (!var2) {
            logger.error("Could not post to " + var0, var12);
         }

         return "";
      }
   }

   public static ListenableFuture<Object> downloadResourcePack(
      final File var0, final String var1, final Map<String, String> var2, final int var3, final IProgressUpdate var4, final Proxy var5
   ) {
      return (ListenableFuture<Object>)field_180193_a.submit(new Runnable() {
         private static final long a = 121781366071882L;
         private static final long e = 2436631665389342720L;

         @Override
         public void run() {
            HttpURLConnection var4x = null;
            InputStream var5x = null;
            DataOutputStream var6 = null;
            if (var4 != null) {
               var4.resetProgressAndMessage("Downloading Resource Pack");
               var4.displayLoadingString("Making Request...");
            }

            try {
               byte[] var7 = new byte[(int)e];
               URL var21 = new URL(var1);
               var4x = (HttpURLConnection)var21.openConnection(var5);
               float var9 = 0.0F;
               float var10 = var2.entrySet().size();

               for (Entry var12 : var2.entrySet()) {
                  var4x.setRequestProperty((String)var12.getKey(), (String)var12.getValue());
                  if (var4 != null) {
                     var4.setLoadingProgress((int)(++var9 / var10 * 100.0F));
                  }
               }

               var5x = var4x.getInputStream();
               var10 = var4x.getContentLength();
               int var23 = var4x.getContentLength();
               if (var4 != null) {
                  var4.displayLoadingString(String.format("Downloading file (%.2f MB)...", var10 / 1000.0F / 1000.0F));
               }

               if (var0.exists()) {
                  long var24 = var0.length();
                  if (var24 == var23) {
                     if (var4 != null) {
                        var4.setDoneWorking();
                     }

                     return;
                  }

                  HttpUtil.logger.warn("Deleting " + var0 + " as it does not match what we currently have (" + var23 + " vs our " + var24 + ").");
                  FileUtils.deleteQuietly(var0);
               } else if (var0.getParentFile() != null) {
                  var0.getParentFile().mkdirs();
               }

               var6 = new DataOutputStream(new FileOutputStream(var0));
               if (var3 > 0 && var10 > var3) {
                  if (var4 != null) {
                     var4.setDoneWorking();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + var9 + ", limit is " + var3 + ")");
               }

               int var25 = 0;

               while (true) {
                  if ((var25 = var5x.read(var7)) < 0) {
                     if (var4 == null) {
                        return;
                     }

                     var4.setDoneWorking();
                     return;
                  }

                  var9 += var25;
                  if (var4 != null) {
                     var4.setLoadingProgress((int)(var9 / var10 * 100.0F));
                  }

                  if (var3 > 0 && var9 > var3) {
                     if (var4 != null) {
                        var4.setDoneWorking();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + var9 + ", limit was " + var3 + ")");
                  }

                  if (Thread.interrupted()) {
                     HttpUtil.logger.error("INTERRUPTED");
                     if (var4 != null) {
                        var4.setDoneWorking();
                     }
                     break;
                  }

                  var6.write(var7, 0, var25);
               }
            } catch (Throwable var19) {
               var19.printStackTrace();
               if (var4x != null) {
                  InputStream var8 = var4x.getErrorStream();

                  try {
                     HttpUtil.logger.error(IOUtils.toString(var8));
                  } catch (IOException var18) {
                     var18.printStackTrace();
                  }
               }

               if (var4 != null) {
                  var4.setDoneWorking();
                  return;
               }

               return;
            } finally {
               IOUtils.closeQuietly(var5x);
               IOUtils.closeQuietly(var6);
            }
         }
      });
   }

   public static int getSuitableLanPort() throws IOException {
      ServerSocket var3 = null;
      int var4 = -1;

      try {
         var3 = new ServerSocket(0);
         var4 = var3.getLocalPort();
      } finally {
         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var11) {
         }
      }

      return var4;
   }

   public static String get(URL var0) throws IOException {
      HttpURLConnection var4 = (HttpURLConnection)var0.openConnection();
      var4.setRequestMethod("GET");
      BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.getInputStream()));
      StringBuilder var6 = new StringBuilder();

      String var7;
      while ((var7 = var5.readLine()) != null) {
         var6.append(var7);
         var6.append('\r');
      }

      var5.close();
      return var6.toString();
   }
}
