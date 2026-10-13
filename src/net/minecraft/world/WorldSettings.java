package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings {
   private final long seed;
   private final WorldSettings.GameType theGameType;
   private final boolean mapFeaturesEnabled;
   private final boolean hardcoreEnabled;
   private final WorldType terrainType;
   private boolean commandsAllowed;
   private boolean bonusChestEnabled;
   private String worldName = "";

   public WorldSettings(long var1, WorldSettings.GameType var3, boolean var4, boolean var5, WorldType var6) {
      this.seed = var1;
      this.theGameType = var3;
      this.mapFeaturesEnabled = var4;
      this.hardcoreEnabled = var5;
      this.terrainType = var6;
   }

   public WorldSettings(WorldInfo var1) {
      this(var1.getSeed(), var1.getGameType(), var1.isMapFeaturesEnabled(), var1.isHardcoreModeEnabled(), var1.getTerrainType());
   }

   public WorldSettings enableBonusChest() {
      this.bonusChestEnabled = true;
      return this;
   }

   public WorldSettings enableCommands() {
      this.commandsAllowed = true;
      return this;
   }

   public WorldSettings setWorldName(String var1) {
      this.worldName = var1;
      return this;
   }

   public boolean isBonusChestEnabled() {
      return this.bonusChestEnabled;
   }

   public long getSeed() {
      return this.seed;
   }

   public WorldSettings.GameType getGameType() {
      return this.theGameType;
   }

   public boolean getHardcoreEnabled() {
      return this.hardcoreEnabled;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public boolean areCommandsAllowed() {
      return this.commandsAllowed;
   }

   public static WorldSettings.GameType getGameTypeById(int var0) {
      return WorldSettings.GameType.getByID(var0);
   }

   public String getWorldName() {
      return this.worldName;
   }

   public enum GameType {
      NOT_SET(-1, ""),
      SURVIVAL(0, "survival"),
      CREATIVE(1, "creative"),
      ADVENTURE(2, "adventure"),
      SPECTATOR(3, "spectator");

      public int id;
      public String name;
      private static final long a = 91109890500568L;

      GameType(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      public int getID() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public void configurePlayerCapabilities(PlayerCapabilities var1) {
         if (this == CREATIVE) {
            var1.allowFlying = true;
            var1.isCreativeMode = true;
            var1.disableDamage = true;
         } else if (this == SPECTATOR) {
            var1.allowFlying = true;
            var1.isCreativeMode = false;
            var1.disableDamage = true;
            var1.isFlying = true;
         } else {
            var1.allowFlying = false;
            var1.isCreativeMode = false;
            var1.disableDamage = false;
            var1.isFlying = false;
         }

         var1.allowEdit = !this.isAdventure();
      }

      public boolean isAdventure() {
         return this == ADVENTURE || this == SPECTATOR;
      }

      public boolean isCreative() {
         return this == CREATIVE;
      }

      public boolean isSurvivalOrAdventure() {
         return this == SURVIVAL || this == ADVENTURE;
      }

      public static WorldSettings.GameType getByID(int var0) {
         for (WorldSettings.GameType var7 : values()) {
            if (var7.id == var0) {
               return var7;
            }
         }

         return SURVIVAL;
      }

      public static WorldSettings.GameType getByName(String var0) {
         for (WorldSettings.GameType var7 : values()) {
            if (var7.name.equals(var0)) {
               return var7;
            }
         }

         return SURVIVAL;
      }
   }
}
