package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
   public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
   public static final File OLD_OPS_FILE = new File("ops.txt");
   public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

   private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback) {
      String[] strings = Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>() {
         public boolean apply(String string) {
            return !StringUtils.isNullOrEmpty(string);
         }
      }), String.class);
      if (server.isServerInOnlineMode()) {
         server.getGameProfileRepository().findProfilesByNames(strings, Agent.MINECRAFT, callback);
      } else {
         for (String string2 : strings) {
            UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, string2));
            GameProfile gameProfile = new GameProfile(uuid, string2);
            callback.onProfileLookupSucceeded(gameProfile);
         }
      }
   }

   public static String getStringUUIDFromName(String string) {
      if (!StringUtils.isNullOrEmpty(string) && string.length() <= 16) {
         final MinecraftServer minecraftServer = MinecraftServer.getServer();
         GameProfile gameProfile2 = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(string);
         if (gameProfile2 != null && gameProfile2.getId() != null) {
            return gameProfile2.getId().toString();
         } else if (!minecraftServer.isSinglePlayer() && minecraftServer.isServerInOnlineMode()) {
            final ArrayList arrayList = Lists.newArrayList();
            ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
               @Override
               public void onProfileLookupSucceeded(GameProfile gameProfile) {
                  minecraftServer.getPlayerProfileCache().addEntry(gameProfile);
                  arrayList.add(gameProfile);
               }

               @Override
               public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + gameProfile.getName(), exception);
               }
            };
            lookupNames(minecraftServer, Lists.newArrayList(string), profileLookupCallback);
            return arrayList.size() > 0 && ((GameProfile)arrayList.get(0)).getId() != null ? ((GameProfile)arrayList.get(0)).getId().toString() : "";
         } else {
            return EntityPlayer.getUUID(new GameProfile((UUID)null, string)).toString();
         }
      } else {
         return string;
      }
   }
}
