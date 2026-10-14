package bypass.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FileUtil {
   public static final ExecutorService w = Executors.newSingleThreadExecutor();
   private static final long a = 112447251599516L;

   public static JsonElement readJson(File file) {
      JsonElement jsonElement = null;
      FileReader fileReader = null;

      try {
         fileReader = new FileReader(file);
         jsonElement = new JsonParser().parse(fileReader);
      } catch (FileNotFoundException | JsonIOException | JsonSyntaxException value) {
         value.printStackTrace();
      } finally {
         if (fileReader != null) {
            try {
               fileReader.close();
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
         }
      }

      return jsonElement;
   }

   public static void writeJson(File file, JsonElement jsonElement) {
      FileWriter fileWriter = null;

      try {
         fileWriter = new FileWriter(file);
         fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement));
      } catch (IOException ioException) {
         ioException.printStackTrace();
      } finally {
         if (fileWriter != null) {
            try {
               fileWriter.close();
            } catch (IOException ioException2) {
               ioException2.printStackTrace();
            }
         }
      }
   }

   public static void writeLines(File file, Collection<String> collection) {
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

         String string;
         while ((string = bufferedReader.readLine()) != null) {
            collection.add(string);
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
      } finally {
         try {
            if (bufferedReader != null) {
               bufferedReader.close();
            }
         } catch (IOException ioException2) {
            ioException2.printStackTrace();
         }
      }
   }

   public static File chooseFile(File file) {
      File file2 = null;
      TopFileChooser topFileChooser = new TopFileChooser(file);
      if (topFileChooser.showOpenDialog(topFileChooser) == 0) {
         file2 = topFileChooser.getSelectedFile();
      }

      return file2;
   }
}
