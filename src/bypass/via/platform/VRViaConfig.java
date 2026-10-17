package bypass.via.platform;

import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.minecraft.WorldIdentifiers;
import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class VRViaConfig implements ViaVersionConfig {
   @Override
   public boolean isCheckForUpdates() {
      return false;
   }

   @Override
   public void setCheckForUpdates(boolean bl) {
   }

   @Override
   public boolean isPreventCollision() {
      return true;
   }

   @Override
   public boolean isNewEffectIndicator() {
      return true;
   }

   @Override
   public boolean isShowNewDeathMessages() {
      return true;
   }

   @Override
   public boolean isSuppressMetadataErrors() {
      return true;
   }

   @Override
   public boolean isShieldBlocking() {
      return true;
   }

   @Override
   public boolean isNoDelayShieldBlocking() {
      return true;
   }

   @Override
   public boolean isShowShieldWhenSwordInHand() {
      return true;
   }

   @Override
   public boolean isHologramPatch() {
      return false;
   }

   @Override
   public boolean isPistonAnimationPatch() {
      return false;
   }

   @Override
   public boolean isBossbarPatch() {
      return true;
   }

   @Override
   public boolean isBossbarAntiflicker() {
      return false;
   }

   @Override
   public double getHologramYOffset() {
      return -0.96;
   }

   @Override
   public boolean isAutoTeam() {
      return true;
   }

   @Override
   public int getMaxPPS() {
      return 800;
   }

   @Override
   public String getMaxPPSKickMessage() {
      return "You are sending too many packets, :(";
   }

   @Override
   public int getTrackingPeriod() {
      return 6;
   }

   @Override
   public int getWarningPPS() {
      return 120;
   }

   @Override
   public int getMaxWarnings() {
      return 4;
   }

   @Override
   public String getMaxWarningsKickMessage() {
      return "You are sending too many packets, :(";
   }

   @Override
   public boolean isAntiXRay() {
      return true;
   }

   @Override
   public boolean isSendSupportedVersions() {
      return false;
   }

   @Override
   public boolean isSimulatePlayerTick() {
      return true;
   }

   @Override
   public boolean isItemCache() {
      return false;
   }

   @Override
   public boolean isNMSPlayerTicking() {
      return true;
   }

   @Override
   public boolean isReplacePistons() {
      return false;
   }

   @Override
   public int getPistonReplacementId() {
      return 0;
   }

   @Override
   public boolean isChunkBorderFix() {
      return false;
   }

   @Override
   public boolean isForceJsonTransform() {
      return false;
   }

   @Override
   public boolean is1_12NBTArrayFix() {
      return true;
   }

   @Override
   public boolean is1_13TeamColourFix() {
      return true;
   }

   @Override
   public boolean is1_12QuickMoveActionFix() {
      return true;
   }

   @Override
   public BlockedProtocolVersions blockedProtocolVersions() {
      return new VRViaConfig.NoBlockedVersions();
   }

   @Override
   public String getBlockedDisconnectMsg() {
      return "You are using an unsupported Minecraft version!";
   }

   @Override
   public String getReloadDisconnectMsg() {
      return "Server reload, please rejoin!";
   }

   @Override
   public boolean isSuppressConversionWarnings() {
      return false;
   }

   @Override
   public boolean isDisable1_13AutoComplete() {
      return true;
   }

   @Override
   public boolean isMinimizeCooldown() {
      return true;
   }

   @Override
   public boolean isServersideBlockConnections() {
      return true;
   }

   @Override
   public String getBlockConnectionMethod() {
      return "packet";
   }

   @Override
   public boolean isReduceBlockStorageMemory() {
      return false;
   }

   @Override
   public boolean isStemWhenBlockAbove() {
      return false;
   }

   @Override
   public boolean isVineClimbFix() {
      return false;
   }

   @Override
   public boolean isSnowCollisionFix() {
      return false;
   }

   @Override
   public boolean isInfestedBlocksFix() {
      return true;
   }

   @Override
   public int get1_13TabCompleteDelay() {
      return 100;
   }

   @Override
   public boolean isTruncate1_14Books() {
      return false;
   }

   @Override
   public boolean isLeftHandedHandling() {
      return true;
   }

   @Override
   public boolean is1_9HitboxFix() {
      return false;
   }

   @Override
   public boolean is1_14HitboxFix() {
      return false;
   }

   @Override
   public boolean isNonFullBlockLightFix() {
      return true;
   }

   @Override
   public boolean is1_14HealthNaNFix() {
      return true;
   }

   @Override
   public boolean is1_15InstantRespawn() {
      return false;
   }

   @Override
   public boolean isIgnoreLong1_16ChannelNames() {
      return true;
   }

   @Override
   public boolean isForcedUse1_17ResourcePack() {
      return false;
   }

   @Override
   public JsonElement get1_17ResourcePackPrompt() {
      return new JsonObject();
   }

   @Override
   public WorldIdentifiers get1_16WorldNamesMap() {
      return new WorldIdentifiers("Overworld");
   }

   @Override
   public boolean cache1_17Light() {
      return true;
   }

   public class NoBlockedVersions implements BlockedProtocolVersions {
      @Override
      public boolean contains(int i) {
         return false;
      }

      @Override
      public int blocksBelow() {
         return 0;
      }

      @Override
      public int blocksAbove() {
         return 0;
      }

      @Override
      public IntSet singleBlockedVersions() {
         return IntSet.of();
      }
   }
}
