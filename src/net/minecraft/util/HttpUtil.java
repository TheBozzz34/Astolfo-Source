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

   public static String buildPostString(Map<String, Object> data) {
      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : data.entrySet()) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append('&');
         }

         try {
            stringBuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
         }

         if (entry.getValue() != null) {
            stringBuilder.append('=');

            try {
               stringBuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException unsupportedEncodingException2) {
               unsupportedEncodingException2.printStackTrace();
            }
         }
      }

      return stringBuilder.toString();
   }

   public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors) {
      return post(url, buildPostString(data), skipLoggingErrors);
   }

   private static String post(URL url, String content, boolean skipLoggingErrors) {
      try {
         Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
         if (proxy == null) {
            proxy = Proxy.NO_PROXY;
         }

         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpURLConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
         httpURLConnection.setRequestProperty("Content-Language", "en-US");
         httpURLConnection.setUseCaches(false);
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
         dataOutputStream.writeBytes(content);
         dataOutputStream.flush();
         dataOutputStream.close();
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
         StringBuffer stringBuffer = new StringBuffer();

         String string;
         while ((string = bufferedReader.readLine()) != null) {
            stringBuffer.append(string);
            stringBuffer.append('\r');
         }

         bufferedReader.close();
         return stringBuffer.toString();
      } catch (Exception exception) {
         if (!skipLoggingErrors) {
            logger.error("Could not post to " + url, exception);
         }

         return "";
      }
   }

   public static ListenableFuture<Object> downloadResourcePack(
      final File saveFile, final String packUrl, final Map<String, String> map, final int maxSize, final IProgressUpdate iProgressUpdate, final Proxy proxy
   ) {
      return (ListenableFuture<Object>)field_180193_a.submit(new Runnable() {
         @Override
         public void run() {
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            DataOutputStream dataOutputStream = null;
            if (iProgressUpdate != null) {
               iProgressUpdate.resetProgressAndMessage("Downloading Resource Pack");
               iProgressUpdate.displayLoadingString("Making Request...");
            }

            try {
               byte[] bytes = new byte[4096];
               URL url = new URL(packUrl);
               httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
               float g = 0.0F;
               float h = map.entrySet().size();

               for (Entry entry : map.entrySet()) {
                  httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                  if (iProgressUpdate != null) {
                     iProgressUpdate.setLoadingProgress((int)(++g / h * 100.0F));
                  }
               }

               inputStream = httpURLConnection.getInputStream();
               h = httpURLConnection.getContentLength();
               int i = httpURLConnection.getContentLength();
               if (iProgressUpdate != null) {
                  iProgressUpdate.displayLoadingString(String.format("Downloading file (%.2f MB)...", h / 1000.0F / 1000.0F));
               }

               if (saveFile.exists()) {
                  long l = saveFile.length();
                  if (l == i) {
                     if (iProgressUpdate != null) {
                        iProgressUpdate.setDoneWorking();
                     }

                     return;
                  }

                  HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + i + " vs our " + l + ").");
                  FileUtils.deleteQuietly(saveFile);
               } else if (saveFile.getParentFile() != null) {
                  saveFile.getParentFile().mkdirs();
               }

               dataOutputStream = new DataOutputStream(new FileOutputStream(saveFile));
               if (maxSize > 0 && h > maxSize) {
                  if (iProgressUpdate != null) {
                     iProgressUpdate.setDoneWorking();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + g + ", limit is " + maxSize + ")");
               }

               int j = 0;

               while (true) {
                  if ((j = inputStream.read(bytes)) < 0) {
                     if (iProgressUpdate == null) {
                        return;
                     }

                     iProgressUpdate.setDoneWorking();
                     return;
                  }

                  g += j;
                  if (iProgressUpdate != null) {
                     iProgressUpdate.setLoadingProgress((int)(g / h * 100.0F));
                  }

                  if (maxSize > 0 && g > maxSize) {
                     if (iProgressUpdate != null) {
                        iProgressUpdate.setDoneWorking();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + g + ", limit was " + maxSize + ")");
                  }

                  if (Thread.interrupted()) {
                     HttpUtil.logger.error("INTERRUPTED");
                     if (iProgressUpdate != null) {
                        iProgressUpdate.setDoneWorking();
                     }
                     break;
                  }

                  dataOutputStream.write(bytes, 0, j);
               }
            } catch (Throwable throwable) {
               throwable.printStackTrace();
               if (httpURLConnection != null) {
                  InputStream inputStream2 = httpURLConnection.getErrorStream();

                  try {
                     HttpUtil.logger.error(IOUtils.toString(inputStream2));
                  } catch (IOException ioException) {
                     ioException.printStackTrace();
                  }
               }

               if (iProgressUpdate != null) {
                  iProgressUpdate.setDoneWorking();
                  return;
               }

               return;
            } finally {
               IOUtils.closeQuietly(inputStream);
               IOUtils.closeQuietly(dataOutputStream);
            }
         }
      });
   }

   public static int getSuitableLanPort() throws IOException {
      ServerSocket serverSocket = null;
      int i = -1;

      try {
         serverSocket = new ServerSocket(0);
         i = serverSocket.getLocalPort();
      } finally {
         try {
            if (serverSocket != null) {
               serverSocket.close();
            }
         } catch (IOException ioException) {
         }
      }

      return i;
   }

   public static String get(URL url) throws IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
      httpURLConnection.setRequestMethod("GET");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
      StringBuilder stringBuilder = new StringBuilder();

      String string;
      while ((string = bufferedReader.readLine()) != null) {
         stringBuilder.append(string);
         stringBuilder.append('\r');
      }

      bufferedReader.close();
      return stringBuilder.toString();
   }
}
