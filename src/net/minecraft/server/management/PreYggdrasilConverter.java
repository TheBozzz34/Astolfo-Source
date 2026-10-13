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
   private static final long b = 97545681299179L;
   private static final long c = 5769579566154645520L;

   private static void lookupNames(MinecraftServer var0, Collection<String> var1, ProfileLookupCallback var2) {
      String[] var6 = Iterators.toArray(Iterators.filter(var1.iterator(), new Predicate<String>() {
         private static final long a = 120303536291525L;

         public boolean apply(String var1) {
            return !StringUtils.isNullOrEmpty(var1);
         }
      }), String.class);
      if (var0.isServerInOnlineMode()) {
         var0.getGameProfileRepository().findProfilesByNames(var6, Agent.MINECRAFT, var2);
      } else {
         for (String var10 : var6) {
            UUID var11 = EntityPlayer.getUUID(new GameProfile((UUID)null, var10));
            GameProfile var12 = new GameProfile(var11, var10);
            var2.onProfileLookupSucceeded(var12);
         }
      }
   }

   public static String getStringUUIDFromName(String var0) {
      if (!StringUtils.isNullOrEmpty(var0) && var0.length() <= (int)c) {
         final MinecraftServer var4 = MinecraftServer.getServer();
         GameProfile var5 = var4.getPlayerProfileCache().getGameProfileForUsername(var0);
         if (var5 != null && var5.getId() != null) {
            return var5.getId().toString();
         } else if (!var4.isSinglePlayer() && var4.isServerInOnlineMode()) {
            final ArrayList var6 = Lists.newArrayList();
            ProfileLookupCallback var7 = new ProfileLookupCallback() {
               private static final long a = 130015043464921L;

               @Override
               public void onProfileLookupSucceeded(GameProfile var1) {
                  var4.getPlayerProfileCache().addEntry(var1);
                  var6.add(var1);
               }

               @Override
               public void onProfileLookupFailed(GameProfile var1, Exception var2) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + var1.getName(), var2);
               }
            };
            lookupNames(var4, Lists.newArrayList(var0), var7);
            return var6.size() > 0 && ((GameProfile)var6.get(0)).getId() != null ? ((GameProfile)var6.get(0)).getId().toString() : "";
         } else {
            return EntityPlayer.getUUID(new GameProfile((UUID)null, var0)).toString();
         }
      } else {
         return var0;
      }
   }
}
