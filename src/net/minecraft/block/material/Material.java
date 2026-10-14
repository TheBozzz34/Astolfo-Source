package net.minecraft.block.material;

public class Material {
   public static final Material air = new MaterialTransparent(MapColor.airColor);
   public static final Material grass = new Material(MapColor.grassColor);
   public static final Material ground = new Material(MapColor.dirtColor);
   public static final Material wood = new Material(MapColor.woodColor).c();
   public static final Material rock = new Material(MapColor.stoneColor).d();
   public static final Material iron = new Material(MapColor.ironColor).d();
   public static final Material anvil = new Material(MapColor.ironColor).d().a();
   public static final Material water = new MaterialLiquid(MapColor.waterColor).k();
   public static final Material lava = new MaterialLiquid(MapColor.tntColor).k();
   public static final Material leaves = new Material(MapColor.foliageColor).c().K().k();
   public static final Material plants = new MaterialLogic(MapColor.foliageColor).k();
   public static final Material vine = new MaterialLogic(MapColor.foliageColor).c().k().X();
   public static final Material sponge = new Material(MapColor.yellowColor);
   public static final Material cloth = new Material(MapColor.clothColor).c();
   public static final Material fire = new MaterialTransparent(MapColor.airColor).k();
   public static final Material sand = new Material(MapColor.sandColor);
   public static final Material circuits = new MaterialLogic(MapColor.airColor).k();
   public static final Material carpet = new MaterialLogic(MapColor.clothColor).c();
   public static final Material glass = new Material(MapColor.airColor).K().h();
   public static final Material redstoneLight = new Material(MapColor.airColor).h();
   public static final Material tnt = new Material(MapColor.tntColor).c().K();
   public static final Material coral = new Material(MapColor.foliageColor).k();
   public static final Material ice = new Material(MapColor.iceColor).K().h();
   public static final Material packedIce = new Material(MapColor.iceColor).h();
   public static final Material snow = new MaterialLogic(MapColor.snowColor).X().K().d().k();
   public static final Material craftedSnow = new Material(MapColor.snowColor).d();
   public static final Material cactus = new Material(MapColor.foliageColor).K().k();
   public static final Material clay = new Material(MapColor.clayColor);
   public static final Material gourd = new Material(MapColor.foliageColor).k();
   public static final Material dragonEgg = new Material(MapColor.foliageColor).k();
   public static final Material portal = new MaterialPortal(MapColor.airColor).a();
   public static final Material cake = new Material(MapColor.airColor).k();
   public static final Material web = (new Material(MapColor.clothColor) {
      @Override
      public boolean blocksMovement() {
         return false;
      }
   }).d().k();
   public static final Material piston = new Material(MapColor.stoneColor).a();
   public static final Material barrier = new Material(MapColor.airColor).d().a();
   private boolean canBurn;
   private boolean replaceable;
   private boolean isTranslucent;
   private final MapColor materialMapColor;
   private boolean requiresNoTool = true;
   private int mobilityFlag;
   private boolean isAdventureModeExempt;
   private static final long a = 26504449481523L;
   private static final long c = -4229544477246619646L;

   public Material(MapColor color) {
      this.materialMapColor = color;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean blocksLight() {
      return true;
   }

   public boolean blocksMovement() {
      return true;
   }

   private Material K() {
      this.isTranslucent = true;
      return this;
   }

   public Material d() {
      this.requiresNoTool = false;
      return this;
   }

   public Material c() {
      this.canBurn = true;
      return this;
   }

   public boolean getCanBurn() {
      return this.canBurn;
   }

   public Material X() {
      this.replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isOpaque() {
      return this.isTranslucent ? false : this.blocksMovement();
   }

   public boolean isToolNotRequired() {
      return this.requiresNoTool;
   }

   public int getMaterialMobility() {
      return this.mobilityFlag;
   }

   public Material k() {
      this.mobilityFlag = 1;
      return this;
   }

   public Material a() {
      this.mobilityFlag = (int)c;
      return this;
   }

   public Material h() {
      this.isAdventureModeExempt = true;
      return this;
   }

   public MapColor getMaterialMapColor() {
      return this.materialMapColor;
   }
}
