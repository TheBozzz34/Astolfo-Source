package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
   private static final Logger logger = LogManager.getLogger();
   private final Map<String, File> resourceMap = Maps.newHashMap();

   public ResourceIndex(File file, String string) {
      if (string != null) {
         File file2 = new File(file, "objects");
         File file3 = new File(file, "indexes/" + string + ".json");
         BufferedReader bufferedReader = null;

         try {
            bufferedReader = Files.newReader(file3, Charsets.UTF_8);
            JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
            JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "objects", (JsonObject)null);
            if (jsonObject2 != null) {
               for (Entry entry : jsonObject2.entrySet()) {
                  JsonObject jsonObject3 = (JsonObject)entry.getValue();
                  String string2 = (String)entry.getKey();
                  String[] strings = string2.split("/", 2);
                  String string3 = strings.length == 1 ? strings[0] : strings[0] + ":" + strings[1];
                  String string4 = JsonUtils.getString(jsonObject3, "hash");
                  File file4 = new File(file2, string4.substring(0, 2) + "/" + string4);
                  this.resourceMap.put(string3, file4);
               }
            }
         } catch (JsonParseException jsonParseException) {
            logger.error("Unable to parse resource index file: " + file3);
         } catch (FileNotFoundException fileNotFoundException) {
            logger.error("Can't find the resource index file: " + file3);
         } finally {
            IOUtils.closeQuietly(bufferedReader);
         }
      }
   }

   public Map<String, File> getResourceMap() {
      return this.resourceMap;
   }
}
