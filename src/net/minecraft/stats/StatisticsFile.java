package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsFile extends StatFileWriter {
   private static final Logger logger = LogManager.getLogger();
   private final MinecraftServer mcServer;
   private final File statsFile;
   private final Set<StatBase> field_150888_e = Sets.newHashSet();
   private int field_150885_f = -300;
   private boolean field_150886_g = false;
   private static final long c = 23749326931841L;

   public StatisticsFile(MinecraftServer serverIn, File statsFileIn) {
      this.mcServer = serverIn;
      this.statsFile = statsFileIn;
   }

   public void readStatFile() {
      if (this.statsFile.isFile()) {
         try {
            this.statsData.clear();
            this.statsData.putAll(this.parseJson(FileUtils.readFileToString(this.statsFile)));
         } catch (IOException ioException) {
            logger.error("Couldn't read statistics file " + this.statsFile, ioException);
         } catch (JsonParseException jsonParseException) {
            logger.error("Couldn't parse statistics file " + this.statsFile, jsonParseException);
         }
      }
   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, dumpJson(this.statsData));
      } catch (IOException ioException) {
         logger.error("Couldn't save stats", ioException);
      }
   }

   @Override
   public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int i) {
      int j = statIn.isAchievement() ? this.readStat(statIn) : 0;
      super.unlockAchievement(playerIn, statIn, i);
      this.field_150888_e.add(statIn);
      if (statIn.isAchievement() && j == 0 && i > 0) {
         this.field_150886_g = true;
         if (this.mcServer.isAnnouncingPlayerAchievements()) {
            this.mcServer
               .getConfigurationManager()
               .sendChatMsg(new ChatComponentTranslation("chat.type.achievement", playerIn.getDisplayName(), statIn.createChatComponent()));
         }
      }

      if (statIn.isAchievement() && j > 0 && i == 0) {
         this.field_150886_g = true;
         if (this.mcServer.isAnnouncingPlayerAchievements()) {
            this.mcServer
               .getConfigurationManager()
               .sendChatMsg(new ChatComponentTranslation("chat.type.achievement.taken", playerIn.getDisplayName(), statIn.createChatComponent()));
         }
      }
   }

   public Set<StatBase> func_150878_c() {
      HashSet hashSet = Sets.newHashSet(this.field_150888_e);
      this.field_150888_e.clear();
      this.field_150886_g = false;
      return hashSet;
   }

   public Map<StatBase, TupleIntJsonSerializable> parseJson(String string) {
      JsonElement jsonElement = new JsonParser().parse(string);
      if (!jsonElement.isJsonObject()) {
         return Maps.newHashMap();
      }

      JsonObject jsonObject = jsonElement.getAsJsonObject();
      HashMap hashMap = Maps.newHashMap();

      for (Entry entry : jsonObject.entrySet()) {
         StatBase statBase = StatList.getOneShotStat((String)entry.getKey());
         if (statBase != null) {
            TupleIntJsonSerializable tupleIntJsonSerializable = new TupleIntJsonSerializable();
            if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber()) {
               tupleIntJsonSerializable.setIntegerValue(((JsonElement)entry.getValue()).getAsInt());
            } else if (((JsonElement)entry.getValue()).isJsonObject()) {
               JsonObject jsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
               if (jsonObject2.has("value") && jsonObject2.get("value").isJsonPrimitive() && jsonObject2.get("value").getAsJsonPrimitive().isNumber()) {
                  tupleIntJsonSerializable.setIntegerValue(jsonObject2.getAsJsonPrimitive("value").getAsInt());
               }

               if (jsonObject2.has("progress") && statBase.func_150954_l() != null) {
                  try {
                     Constructor constructor = statBase.func_150954_l().getConstructor();
                     IJsonSerializable iJsonSerializable = (IJsonSerializable)constructor.newInstance();
                     iJsonSerializable.fromJson(jsonObject2.get("progress"));
                     tupleIntJsonSerializable.setJsonSerializableValue(iJsonSerializable);
                  } catch (Throwable throwable) {
                     logger.warn("Invalid statistic progress in " + this.statsFile, throwable);
                  }
               }
            }

            hashMap.put(statBase, tupleIntJsonSerializable);
         } else {
            logger.warn("Invalid statistic in " + this.statsFile + ": Don't know what " + (String)entry.getKey() + " is");
         }
      }

      return hashMap;
   }

   public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> map) {
      JsonObject jsonObject = new JsonObject();

      for (Entry entry : map.entrySet()) {
         if (((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue() != null) {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("value", ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());

            try {
               jsonObject2.add("progress", ((TupleIntJsonSerializable)entry.getValue()).<IJsonSerializable>getJsonSerializableValue().getSerializableElement());
            } catch (Throwable throwable) {
               logger.warn("Couldn't save statistic " + ((StatBase)entry.getKey()).getStatName() + ": error serializing progress", throwable);
            }

            jsonObject.add(((StatBase)entry.getKey()).statId, jsonObject2);
         } else {
            jsonObject.addProperty(((StatBase)entry.getKey()).statId, ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());
         }
      }

      return jsonObject.toString();
   }

   public void func_150877_d() {
      for (StatBase statBase : this.statsData.keySet()) {
         this.field_150888_e.add(statBase);
      }
   }

   public void func_150876_a(EntityPlayerMP entityPlayerMP) {
      int i = this.mcServer.getTickCounter();
      HashMap hashMap = Maps.newHashMap();
      if (this.field_150886_g || i - this.field_150885_f > 300) {
         this.field_150885_f = i;

         for (StatBase statBase : this.func_150878_c()) {
            hashMap.put(statBase, this.readStat(statBase));
         }
      }

      entityPlayerMP.playerNetServerHandler.sendPacket(new S37PacketStatistics(hashMap));
   }

   public void sendAchievements(EntityPlayerMP player) {
      HashMap hashMap = Maps.newHashMap();

      for (Achievement achievement : AchievementList.achievementList) {
         if (this.hasAchievementUnlocked(achievement)) {
            hashMap.put(achievement, this.readStat(achievement));
            this.field_150888_e.remove(achievement);
         }
      }

      player.playerNetServerHandler.sendPacket(new S37PacketStatistics(hashMap));
   }

   public boolean func_150879_e() {
      return this.field_150886_g;
   }
}
