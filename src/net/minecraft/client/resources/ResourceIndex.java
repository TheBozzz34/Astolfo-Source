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
   private static final long a = 109626545375961L;

   public ResourceIndex(File var1, String var2) {
      if (var2 != null) {
         File var6 = new File(var1, "objects");
         File var7 = new File(var1, "indexes/" + var2 + ".json");
         BufferedReader var8 = null;

         try {
            var8 = Files.newReader(var7, Charsets.UTF_8);
            JsonObject var9 = new JsonParser().parse(var8).getAsJsonObject();
            JsonObject var10 = JsonUtils.getJsonObject(var9, "objects", (JsonObject)null);
            if (var10 != null) {
               for (Entry var12 : var10.entrySet()) {
                  JsonObject var13 = (JsonObject)var12.getValue();
                  String var14 = (String)var12.getKey();
                  String[] var15 = var14.split("/", 2);
                  String var16 = var15.length == 1 ? var15[0] : var15[0] + ":" + var15[1];
                  String var17 = JsonUtils.getString(var13, "hash");
                  File var18 = new File(var6, var17.substring(0, 2) + "/" + var17);
                  this.resourceMap.put(var16, var18);
               }
            }
         } catch (JsonParseException var23) {
            logger.error("Unable to parse resource index file: " + var7);
         } catch (FileNotFoundException var24) {
            logger.error("Can't find the resource index file: " + var7);
         } finally {
            IOUtils.closeQuietly(var8);
         }
      }
   }

   public Map<String, File> getResourceMap() {
      return this.resourceMap;
   }
}
