package net.minecraft.world;

public class WorldType {
   public static final WorldType[] worldTypes = new WorldType[16];
   public static final WorldType DEFAULT = new WorldType(0, "default", 1).c();
   public static final WorldType FLAT = new WorldType(1, "flat");
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType AMPLIFIED = new WorldType(3, "amplified").w();
   public static final WorldType CUSTOMIZED = new WorldType(4, "customized");
   public static final WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");
   public static final WorldType DEFAULT_1_1 = new WorldType(8, "default_1_1", 0).setCanBeCreated(false);
   private final int worldTypeId;
   private final String worldType;
   private final int generatorVersion;
   private boolean canBeCreated;
   private boolean isWorldTypeVersioned;
   private boolean hasNotificationData;
   private static final long b = 106586809277169L;

   private WorldType(int var1, String var2) {
      this(var1, var2, 0);
   }

   private WorldType(int var1, String var2, int var3) {
      this.worldType = var2;
      this.generatorVersion = var3;
      this.canBeCreated = true;
      this.worldTypeId = var1;
      worldTypes[var1] = this;
   }

   public String getWorldTypeName() {
      return this.worldType;
   }

   public String getTranslateName() {
      return "generator." + this.worldType;
   }

   public String getTranslatedInfo() {
      return this.getTranslateName() + ".info";
   }

   public int getGeneratorVersion() {
      return this.generatorVersion;
   }

   public WorldType getWorldTypeForGeneratorVersion(int var1) {
      return this == DEFAULT && var1 == 0 ? DEFAULT_1_1 : this;
   }

   private WorldType setCanBeCreated(boolean var1) {
      this.canBeCreated = var1;
      return this;
   }

   public boolean getCanBeCreated() {
      return this.canBeCreated;
   }

   private WorldType c() {
      this.isWorldTypeVersioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.isWorldTypeVersioned;
   }

   public static WorldType parseWorldType(String var0) {
      for (int var4 = 0; var4 < worldTypes.length; var4++) {
         if (worldTypes[var4] != null && worldTypes[var4].worldType.equalsIgnoreCase(var0)) {
            return worldTypes[var4];
         }
      }

      return null;
   }

   public int getWorldTypeID() {
      return this.worldTypeId;
   }

   public boolean showWorldInfoNotice() {
      return this.hasNotificationData;
   }

   private WorldType w() {
      this.hasNotificationData = true;
      return this;
   }
}
