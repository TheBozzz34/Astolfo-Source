package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderNameTagEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.combat.ArmorSlot;
import bypass.module.combat.AutoArmor;
import bypass.module.combat.KillAura;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.BotDetector;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.other.AutoHypixel;
import bypass.module.player.OldInventoryManager;
import bypass.relation.Relation;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.setting.StringSetting;
import bypass.user.AstolfoUser;
import bypass.user.AstolfoUserStatus;
import bypass.util.GameUtil;
import bypass.util.InventoryUtil;
import bypass.util.MathUtil;
import bypass.util.collection.ShapedMap;
import java.awt.Color;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public final class ESP extends Module {
   private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
   private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projected = BufferUtils.createFloatBuffer(116);
   private final int outlineColor = new Color(1, 1, 1, 150).getRGB();
   private final int unusedColor = new Color(1, 1, 1, 125).getRGB();
   private final int unusedRed = new Color(216, 60, 60, 255).getRGB();
   private final BooleanSetting highlight = new BooleanSetting("Highlight", "Highlights player in killaura", true);
   private final BooleanSetting itemName = new BooleanSetting("Item-Name", "Shows players item name", false);
   private final BooleanSetting pitItemPerks = new BooleanSetting("Pit-Item-Perks", "Shows item perks name", false);
   private final BooleanSetting teams = new BooleanSetting("Teams", "teams ", false);
   private final EnumSetting<ESPHealthMode> health = new EnumSetting<>("Health", "Set your health display mode", ESPHealthMode.HEARTS);
   private final EnumSetting<ESPHPosMode> hPos = new EnumSetting<>("HPos", "Set your health position mode", ESPHPosMode.STANDARD);
   private final MapSetting nameTags = new MapSettingBuilder("Name")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable name", true))
      .add("Mode", new EnumSetting<>("Mode", "Mode of displaying name", ESPNameMode.NAME))
      .add("Background", new BooleanSetting("Background", "Background behind name", true))
      .add("Outline", new BooleanSetting("Outline", "Outline the name", false))
      .add("Bar", new EnumSetting<>("Bar", "Bar around the name", ESPNameBarMode.NONE))
      .add("Armor", new BooleanSetting("Armor", "Armor", true))
      .add("CFont", new BooleanSetting("CFont", "CFont", false))
      .add("Potions", new BooleanSetting("Potions", "Potions", true))
      .add("Color", new ColorSetting("Color", "Color", Color.WHITE))
      .add("Scale", new NumberSetting<>("Scale", "Scale of da names", 1.0F, 1.0F, 4.0F))
      .build();
   private final MapSetting items = new MapSettingBuilder("Items")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable name", true))
      .add("Highlighted-Only", new BooleanSetting("Highlighted-Only", "Only highlighted items", true))
      .add("Item-Scale", new NumberSetting<>("Item-Scale", "Scale of da items", 1.0F, 1.0F, 4.0F))
      .build();
   private final MapSetting offScreen = new MapSettingBuilder("Off-Screen")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable name", true))
      .add("Pulsing", new BooleanSetting("Pulsing", "color pulse", true))
      .add("Scale", new NumberSetting<>("Scale", "Scale", 10, 1, 20))
      .add("Radius", new NumberSetting<>("Radius", "radius", 0.5F, 0.1F, 1.0F))
      .add("Color", new ColorSetting("Color", "Color", Color.RED))
      .build();
   private final MapSetting box = new MapSettingBuilder("Box")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable boxes", true))
      .add("Mode", new EnumSetting<>("Box-Mode", "Set your box mode", ESPBoxMode.FULL))
      .add("Width", new NumberSetting<>("Width", "Amount of width", 1, 1, 10))
      .add("Height", new NumberSetting<>("Height", "Amount of height", 1, 1, 10))
      .add("Outline", new BooleanSetting("Outline", "Outlines box", true))
      .add("Color", new ColorSetting("Color", "Color", Color.RED))
      .build();
   private final MapSetting healthBar = new MapSettingBuilder("Health-Bar")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable healthbar", true))
      .add("Color-Mode", new EnumSetting<>("Color-Mode", "Set your color mode", ESPHealthBarColorMode.NORMAL))
      .add("Color", new ColorSetting("Color", "Color", Color.GREEN))
      .add("Animated", new BooleanSetting("Animated", "Animated healthbar", true))
      .add("Speed", new NumberSetting<>("Speed", "Speed", 100, 1, 100))
      .add("Bottom-Color", new ColorSetting("Bottom-Color", "Color", Color.BLACK))
      .build();
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("esp blocky")
      .add("Players", new BooleanSetting("Players", "Renders players", true))
      .add("Hostiles", new BooleanSetting("Hostiles", "Renders monsters", false))
      .add("Passives", new BooleanSetting("Passives", "Renders passives", false))
      .add("Neutrals", new BooleanSetting("Neutrals", "Renders neutrals", false))
      .add("Bosses", new BooleanSetting("Bosses", "Renders bosses", false))
      .add("Vehicles", new BooleanSetting("Vehicles", "Renders vehicles", false))
      .add("Others", new BooleanSetting("Others", "Renders others", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Renders invisibles", false))
      .add("Local", new BooleanSetting("Local", "Renders local player", false))
      .add("Bots", new BooleanSetting("Bots", "Renders bots", false))
      .add("Items", new BooleanSetting("Items", "Renders items", false))
      .build();
   private final MapSetting clickESP = new MapSettingBuilder("Click-ESP")
      .ordered()
      .add("Enable", new BooleanSetting("Enable", "Enable healthbar", false))
      .add("Text", new StringSetting("Text", "Enable healthbar", "hello {user}"))
      .add("Right-Click", new BooleanSetting("Right-Click", "Uses right click instead of left click", false))
      .build();
   private final MapSetting blocks = new MapSettingBuilder("Blocks")
      .ordered()
      .add("Chest", new BooleanSetting("Chest", "Add chests to blocks", true))
      .add("Hopper", new BooleanSetting("Hopper", "Add hoppers to blocks", true))
      .add("Ender Chest", new BooleanSetting("Ender-Chest", "Add ender chests to blocks", true))
      .build();
   private final Color defaultFriendColor = new Color(15, 176, 245, 255);
   private final int botColor = new Color(60, 60, 60).getRGB();
   private final Color defaultAstolfoColor = new Color(233, 113, 254);
   private final MapSetting groupColor = new MapSettingBuilder("Group-Color")
      .ordered()
      .add("Friends", new ColorSetting("Friends", "Color", this.defaultFriendColor))
      .add("Enemies", new ColorSetting("Enemies", "Color", Color.RED))
      .add("Astolfo", new ColorSetting("Astolfo", "Color", this.defaultAstolfoColor))
      .build();
   private EntityLivingBase menuTarget;
   private float menuX;
   private float menuY;
   private boolean menuOpen;
   private final List<EntityItem> groupedItems = new ArrayList<>();
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = var1x -> {
      long var2 = cb ^ 104633692323475L;
      long var10001 = var2 ^ 15956055177460L;
      int var4 = (int)((var2 ^ 15956055177460L) >>> 56);
      int var5 = (int)((var2 ^ 15956055177460L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      switch (var1x.getType()) {
         case WORLD:
            this.saveMatrices(var1x.getPartialTicks());
            break;
         case SCREEN:
            int var8 = var1x.getResolution().getScaledHeight();
            int var9 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
            int var10 = var8 - Mouse.getY() * var8 / mc.displayHeight - 1;
            if (this.entities.getSetting("Items").asBoolean()) {
               for (Entity var12 : mc.theWorld.loadedEntityList) {
                  if (var12 instanceof EntityItem
                     && (
                        ((EntityItem)var12).getEntityItem().isItemEnchanted()
                           || this.isHighlightedItem((EntityItem)var12)
                           || !this.items.getSetting("Highlighted-Only").asBoolean()
                     )) {
                     EntityItem var13 = (EntityItem)var12;
                     if (!this.groupedItems.contains(var13)) {
                        ArrayList var14 = new ArrayList();

                        for (Object var16 : mc.theWorld.loadedEntityList) {
                           if (var16 instanceof EntityItem && var16 != var13) {
                              EntityItem var17 = (EntityItem)var16;
                              if ((
                                    var17.getEntityItem().isItemEnchanted()
                                       || this.isHighlightedItem(var17)
                                       || !this.items.getSetting("Highlighted-Only").asBoolean()
                                 )
                                 && !(var17.getDistanceToEntity(var13) >= 3.0F)) {
                                 var14.add(var17);
                                 if (!this.groupedItems.contains(var17)) {
                                    this.groupedItems.add(var17);
                                 }
                              }
                           }
                        }

                        var14.add(var13);
                        double var31 = MathUtil.interpolate(var12.lastTickPosX, var12.posX, var1x.getPartialTicks(), mc.getRenderManager().viewerPosX);
                        double var36 = MathUtil.interpolate(var12.lastTickPosY, var12.posY, var1x.getPartialTicks(), mc.getRenderManager().viewerPosY);
                        double var19 = MathUtil.interpolate(var12.lastTickPosZ, var12.posZ, var1x.getPartialTicks(), mc.getRenderManager().viewerPosZ);
                        this.drawItemGroup(var1x, var13, var14, (float)var31, (float)var36, (float)var19);
                     }
                  }
               }
            }

            List var22 = mc.theWorld.loadedTileEntityList;
            int var23 = 0;

            for (int var25 = var22.size(); var23 < var25; var23++) {
               TileEntity var28 = (TileEntity)var22.get(var23);
               if (this.shouldRenderTileEntity(var28)) {
                  double var32 = var28.getPos().getX() - mc.getRenderManager().renderPosX;
                  double var37 = var28.getPos().getY() - mc.getRenderManager().renderPosY;
                  double var38 = var28.getPos().getZ() - mc.getRenderManager().renderPosZ;
                  this.drawTileEntity(var1x, var28, var32, var37, var38);
               }
            }

            List var24 = mc.theWorld.loadedEntityList;
            int var26 = 0;

            for (int var29 = var24.size(); var26 < var29; var26++) {
               Entity var33 = (Entity)var24.get(var26);
               if (this.shouldRender(var33)) {
                  double var35 = MathUtil.interpolate(var33.lastTickPosX, var33.posX, var1x.getPartialTicks(), mc.getRenderManager().viewerPosX);
                  double var18 = MathUtil.interpolate(var33.lastTickPosY, var33.posY, var1x.getPartialTicks(), mc.getRenderManager().viewerPosY);
                  double var20 = MathUtil.interpolate(var33.lastTickPosZ, var33.posZ, var1x.getPartialTicks(), mc.getRenderManager().viewerPosZ);
                  this.drawEntity(var1x, var33, (float)var35, (float)var18, (float)var20, var9, var10);
               }
            }

            if (this.menuTarget != null) {
               float var27 = var1x.getResolution().getScaledWidth() / 2.0F;
               float var30 = var1x.getResolution().getScaledHeight() / 2.0F;
               this.menuX = this.menuX - mc.mouseHelper.a();
               this.menuY = this.menuY + mc.mouseHelper.z();
               int var34 = mc.fontRendererObj.getStringWidth(this.menuTarget.getName());
               RenderUtil.drawRect(
                  var27 - var34 + this.menuX,
                  var30 - 100.0F + this.menuY,
                  var27 + var34 + this.menuX,
                  var30 - 90.0F + this.menuY + 9.0F,
                  new Color(1, 1, 1, 120).getRGB()
               );
               mc.fontRendererObj.drawStringWithShadow(this.menuTarget.getName(), var27 - var34 / 2.0F + this.menuX, var30 - 90.0F + this.menuY - 5.0F, -1);
               this.drawMenuButton("Custom", var27 + 20.0F, var30 - 75.0F, var27 + 75.0F, var30 - 20.0F);
               this.drawMenuButton("Enemy", var27 - 75.0F, var30 + 20.0F, var27 - 20.0F, var30 + 75.0F);
               this.drawMenuButton("Friend", var27 + 20.0F, var30 + 20.0F, var27 + 75.0F, var30 + 75.0F);
               this.drawMenuButton("Teleport", var27 - 75.0F, var30 - 75.0F, var27 - 20.0F, var30 - 20.0F);
               RenderUtil.drawLine(1.0F, var27 + this.menuX, var30 + this.menuY, var27, var30, -1);
               if (this.clickESP.getSetting("Right-Click").asBoolean()) {
                  mc.gameSettings.Z7.pressed = false;
               } else {
                  mc.gameSettings.lu.pressed = false;
               }

               this.menuOpen = true;
            }

            if (this.menuOpen && !Mouse.isButtonDown(this.clickESP.getSetting("Right-Click").asBoolean() ? 1 : 0) && this.menuTarget != null) {
               if (this.menuX >= 20.0F && this.menuY >= 20.0F && this.menuX <= 75.0F && this.menuY <= 75.0F) {
                  mc.thePlayer.sendClientChatMessage(".teleport " + this.menuTarget.getName());
               }

               if (this.menuX >= 20.0F && this.menuY <= -20.0F && this.menuY >= -75.0F && this.menuX <= 75.0F) {
                  if (client.getRelationManager().D(this.menuTarget)) {
                     client.getRelationManager().n().remove(this.menuTarget.getName());
                     client.getRelationManager().Z().remove(this.menuTarget.getName());
                  } else {
                     client.getRelationManager().n().remove(this.menuTarget.getName());
                     client.getRelationManager().Z().remove(this.menuTarget.getName());
                     client.getRelationManager().add(new Relation(this.menuTarget.getName(), this.menuTarget.getName(), false));
                  }
               }

               if (this.menuX <= -20.0F && this.menuY <= -20.0F && this.menuY >= -75.0F && this.menuX >= -75.0F) {
                  if (client.getRelationManager().z(this.menuTarget)) {
                     client.getRelationManager().n().remove(this.menuTarget.getName());
                     client.getRelationManager().Z().remove(this.menuTarget.getName());
                  } else {
                     client.getRelationManager().n().remove(this.menuTarget.getName());
                     client.getRelationManager().Z().remove(this.menuTarget.getName());
                     client.getRelationManager().add(new Relation(this.menuTarget.getName(), this.menuTarget.getName(), true));
                  }
               }

               if (this.menuX <= -20.0F && this.menuY >= 20.0F && this.menuX >= -75.0F && this.menuY <= 75.0F) {
                  mc.thePlayer.sendClientChatMessage(this.clickESP.getSetting("Text").asString().replace("{user}", this.menuTarget.getName()));
               }

               this.menuX = this.menuY = 0.0F;
               this.menuOpen = false;
               this.menuTarget = null;
            }
      }
   };
   private final int healthBarShadeColor = new Color(1, 1, 1, 120).getRGB();
   private final ShapedMap<Integer, Float> displayedHealth = new ShapedMap<>();
   private final int nameBackgroundColor = new Color(1, 1, 1, 120).getRGB();
   @EventHandler(RenderNameTagEvent.class)
   private final Listener<RenderNameTagEvent> onRenderNameTag = var1x -> {
      if (this.nameTags.getSetting("Enable").asBoolean()) {
         var1x.cancel();
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      this.displayedHealth.clear();
      this.groupedItems.clear();
   };
   private final int trappedChestColor = new Color(204, 0, 0).getRGB();
   private final int chestColor = new Color(255, 153, 0).getRGB();
   private final int enderChestColor = new Color(204, 51, 153).getRGB();
   private final int hopperColor = new Color(128, 128, 128).getRGB();
   private final int unusedGray = new Color(128, 128, 128).getRGB();
   private static final long cb = 36170214269754L;

   public ESP(Category var1) {
      super(var1);
      this.addSettings(
         this.entities,
         this.blocks,
         this.box,
         this.nameTags,
         this.items,
         this.groupColor,
         this.offScreen,
         this.healthBar,
         this.clickESP,
         this.itemName,
         this.pitItemPerks,
         this.health,
         this.hPos,
         this.highlight,
         this.teams
      );
      this.setHidden(false);
   }

   private void drawMenuButton(String var1, float var2, float var3, float var4, float var5) {
      float var9 = var2 + this.menuX;
      float var10 = var3 + this.menuY;
      float var11 = var4 + this.menuX;
      float var12 = var5 + this.menuY;
      int var13 = mc.fontRendererObj.getStringWidth(var1) / 2;
      RenderUtil.drawRect(var9, var10, var11, var12, new Color(1, 1, 1, 120).getRGB());
      mc.fontRendererObj.drawStringWithShadow(var1, (var11 + var9) / 2.0F - var13, var12 - 13.0F, -1);
      switch (var1) {
         case "Teleport":
            GuiMainMenu.G(new ResourceLocation("client/icon/teleport.png"), 16.0F, (var11 + var9) / 2.0F - 16.0F, var10 + 5.0F);
            break;
         case "Friend":
            GuiMainMenu.G(new ResourceLocation("client/icon/friend.png"), 16.0F, (var11 + var9) / 2.0F - 16.0F, var10 + 5.0F);
            break;
         case "Enemy":
            GuiMainMenu.G(new ResourceLocation("client/icon/enemy.png"), 16.0F, (var11 + var9) / 2.0F - 16.0F, var10 + 5.0F);
            break;
         case "Custom":
            GuiMainMenu.G(new ResourceLocation("client/icon/options.png"), 16.0F, (var11 + var9) / 2.0F - 16.0F, var10 + 5.0F);
      }
   }

   private void drawItemGroup(RenderEvent var1, EntityItem var2, List<EntityItem> var3, float var4, float var5, float var6) {
      float[] var10 = this.project(var4, var5, var6);
      if (var10 != null) {
         if (!(var10[2] > 1.0F) && this.isOnScreen(var10[0], var10[1])) {
            AxisAlignedBB var11 = AxisAlignedBB.fromBounds(
               var4 - var2.width + 0.2,
               var5,
               var6 - var2.width + 0.2,
               var4 + var2.width - 0.2,
               var5 + var2.height + (var2.isSneaking() ? 0.02 : 0.2),
               var6 + var2.width - 0.2
            );
            int var13 = Integer.MAX_VALUE;
            int var14 = Integer.MIN_VALUE;
            int var15 = Integer.MAX_VALUE;
            int var16 = Integer.MIN_VALUE;
            float[][] var17 = new float[][]{
               {(float)var11.minX, (float)var11.minY, (float)var11.minZ},
               {(float)var11.minX, (float)var11.maxY, (float)var11.minZ},
               {(float)var11.maxX, (float)var11.maxY, (float)var11.minZ},
               {(float)var11.maxX, (float)var11.minY, (float)var11.minZ},
               {(float)var11.maxX, (float)var11.maxY, (float)var11.maxZ},
               {(float)var11.minX, (float)var11.maxY, (float)var11.maxZ},
               {(float)var11.minX, (float)var11.minY, (float)var11.maxZ},
               {(float)var11.maxX, (float)var11.minY, (float)var11.maxZ}
            };

            for (int var18 = 0; var18 < 8; var18++) {
               float[] var12 = this.project(var17[var18][0], var17[var18][1], var17[var18][2]);
               if (var12 != null) {
                  var13 = Math.min(var13, (int)var12[0]);
                  var14 = Math.max(var14, (int)var12[0]);
                  var15 = Math.min(var15, (int)var12[1]);
                  var16 = Math.max(var16, (int)var12[1]);
               }
            }

            if (var13 != Integer.MAX_VALUE && var14 != Integer.MIN_VALUE && var15 != Integer.MAX_VALUE && var16 != Integer.MIN_VALUE) {
               GlStateManager.pushMatrix();
               int var36 = var1.getResolution().getScaleFactor();
               double var19 = var36 / Math.pow(var36, 2.0);
               GlStateManager.scale(var19, var19, var19);
               float var21 = this.items.getSetting("Item-Scale").asFloat();
               GlStateManager.popMatrix();
               GlStateManager.pushMatrix();
               GlStateManager.scale(var19 * var21, var19 * var21, var19 * var21);
               float var22 = var13 / var21;
               float var23 = var15 / var21;
               RenderUtil.drawTriangle(var22, var23 + 1.0F, var22 - 8.0F, var23 - 6.0F, var22 + 8.0F, var23 - 6.0F, -872415232);
               ArrayList var24 = new ArrayList();

               for (EntityItem var26 : var3) {
                  ItemStack var27 = var26.getEntityItem();
                  boolean var28 = false;
                  if (var26.getEntityItem().getItem() instanceof ItemArmor) {
                     ItemArmor var29 = (ItemArmor)var27.getItem();
                     ArmorSlot var30 = AutoArmor.N(var29.armorType);
                     if (var30 != null) {
                        int var31 = InventoryUtil.getArmorScore(var27);
                        Slot var32 = mc.thePlayer.inventoryContainer.getSlot(var30.slot);
                        int var33 = InventoryUtil.getArmorScore(var32.getStack());
                        if (var33 < var31) {
                           var28 = true;
                        }
                     }
                  }

                  NBTTagList var41 = var26.getEntityItem().getEnchantmentTagList();
                  if (var41 == null) {
                     var24.add(
                        (var28 ? EnumChatFormatting.AQUA : "")
                           + var26.getEntityItem().getDisplayName()
                           + (var26.getEntityItem().stackSize > 1 ? EnumChatFormatting.GRAY + " x" + var26.getEntityItem().stackSize : "")
                     );
                  } else {
                     StringBuilder var43 = new StringBuilder(
                        (var28 ? EnumChatFormatting.AQUA : EnumChatFormatting.RED) + var26.getEntityItem().getDisplayName() + "§f:"
                     );

                     for (int var46 = 0; var46 < var41.tagCount(); var46++) {
                        short var48 = var41.getCompoundTagAt(var46).getShort("id");
                        short var49 = var41.getCompoundTagAt(var46).getShort("lvl");
                        Enchantment var34 = Enchantment.getEnchantmentById(var48);
                        if (var34 != null) {
                           String var35 = I18n.format(var34.getName()).substring(0, 3).toUpperCase() + " " + var49;
                           var43.append(" ").append(var35);
                        }
                     }

                     var24.add(var43.toString());
                  }
               }

               ArrayList var37 = new ArrayList();
               int var38 = 0;

               for (int var39 = 0; var39 < var24.size(); var39++) {
                  String var40 = (String)var24.get(var39);
                  int var42 = 0;
                  if (!var37.contains(var40)) {
                     for (Object var47$raw : var24) {
                        String var47 = (String)var47$raw;
                        if (var47.equals(var40)) {
                           var42++;
                           var37.add(var47);
                        }
                     }

                     if (var42 > 1) {
                        var40 = var40 + "(x" + var42 + ")";
                     }

                     float var45 = client.getFontRenderer().getStringWidth(var40) / 2 + 4;
                     RenderUtil.drawRect(var22 - var45, var23 - var38 * 11 - 17.0F, var22 + var45, var23 - 11 * var38 - 6.0F, -872415232);
                     client.getFontRenderer().drawStringWithShadow(var40, var22 - var45 + 4.0F, var23 - var38 * 11 - 15.0F, -1);
                     var38++;
                  }
               }

               GL11.glPopMatrix();
            }
         }
      }
   }

   private void drawEntity(RenderEvent var1, Entity var2, float var3, float var4, float var5, int var6, int var7) {
      long var8 = cb ^ 89864445271109L;
      long var10001 = var8 ^ 76560292386L;
      int var10 = (int)((var8 ^ 76560292386L) >>> 56);
      int var11 = (int)((var8 ^ 76560292386L) << 8 >>> 32);
      int var12 = (int)(var10001 << 40 >>> 40);
      long var13 = var8 ^ 25305447317381L;
      float[] var16 = this.project(var3, var4, var5);
      if (var16 != null) {
         if (!(var16[2] > 1.0F) && this.isOnScreen(var16[0], var16[1])) {
            AxisAlignedBB var17 = AxisAlignedBB.fromBounds(
               var3 - var2.width + 0.2,
               var4,
               var5 - var2.width + 0.2,
               var3 + var2.width - 0.2,
               var4 + var2.height + (var2.isSneaking() ? 0.02 : 0.2),
               var5 + var2.width - 0.2
            );
            if (var2 instanceof EntityHorse) {
               var17 = AxisAlignedBB.fromBounds(
                  var3 - var2.width + 0.6, var4 + 0.1, var5 - var2.width + 0.2, var3 + var2.width - 0.6, var4 + var2.height + 0.6, var5 + var2.width - 0.2
               );
            } else if (var2 instanceof EntityCreeper) {
               var17 = AxisAlignedBB.fromBounds(
                  var3 - var2.width + 0.2, var4, var5 - var2.width + 0.2, var3 + var2.width - 0.2, var4 + var2.height - 0.1, var5 + var2.width - 0.2
               );
            } else if (var2 instanceof EntityItem) {
               var17 = AxisAlignedBB.fromBounds(
                  var3 - var2.width + 0.2, var4 + 0.2, var5 - var2.width + 0.05, var3 + var2.width - 0.1, var4 + var2.height + 0.3, var5 + var2.width - 0.1
               );
            }

            int var19 = Integer.MAX_VALUE;
            int var20 = Integer.MIN_VALUE;
            int var21 = Integer.MAX_VALUE;
            int var22 = Integer.MIN_VALUE;
            float[][] var23 = new float[][]{
               {(float)var17.minX, (float)var17.minY, (float)var17.minZ},
               {(float)var17.minX, (float)var17.maxY, (float)var17.minZ},
               {(float)var17.maxX, (float)var17.maxY, (float)var17.minZ},
               {(float)var17.maxX, (float)var17.minY, (float)var17.minZ},
               {(float)var17.maxX, (float)var17.maxY, (float)var17.maxZ},
               {(float)var17.minX, (float)var17.maxY, (float)var17.maxZ},
               {(float)var17.minX, (float)var17.minY, (float)var17.maxZ},
               {(float)var17.maxX, (float)var17.minY, (float)var17.maxZ}
            };

            for (int var24 = 0; var24 < 8; var24++) {
               float[] var18 = this.project(var23[var24][0], var23[var24][1], var23[var24][2]);
               if (var18 != null) {
                  var19 = Math.min(var19, (int)var18[0]);
                  var20 = Math.max(var20, (int)var18[0]);
                  var21 = Math.min(var21, (int)var18[1]);
                  var22 = Math.max(var22, (int)var18[1]);
               }
            }

            if (var19 != Integer.MAX_VALUE && var20 != Integer.MIN_VALUE && var21 != Integer.MAX_VALUE && var22 != Integer.MIN_VALUE) {
               GlStateManager.pushMatrix();
               int var48 = var1.getResolution().getScaleFactor();
               double var25 = var48 / Math.pow(var48, 2.0);
               GlStateManager.scale(var25, var25, var25);
               int var27 = (var19 + var20) / 2;
               if (this.box.getSetting("Enable").asBoolean()) {
                  this.drawBox(var19, var20, var21, var22, this.getBoxColor(var2));
               }

               if (var2 instanceof EntityLivingBase) {
                  EntityLivingBase var28 = (EntityLivingBase)var2;
                  if (!Double.isNaN(var28.getHealth()) && this.healthBar.getSetting("Enable").asBoolean()) {
                     Float var29 = this.displayedHealth.get(var28.getEntityId());
                     if (var29 != null && this.healthBar.getSetting("Animated").asBoolean()) {
                        if (var29 > var28.getHealth()) {
                           var29 = var29 - this.healthBar.getSetting("Speed").asFloat() / Minecraft.B();
                        } else {
                           var29 = var28.getHealth();
                        }
                     } else {
                        var29 = var28.getHealth();
                     }

                     this.displayedHealth.put(var28.getEntityId(), var29);
                     int var30 = var22 + 1;
                     if (var29.floatValue() != 0.0) {
                        var30 = (int)(var22 + (var21 - var22) * (var29 / var28.getMaxHealth() * 100.0F * 0.01F));
                     }

                     switch ((ESPHealthBarColorMode)this.healthBar.getSetting("Color-Mode").getValue()) {
                        case NORMAL:
                           RenderUtil.drawRect(var19 - 6, var21 - 1, var19 - 2, var22 + 2, this.outlineColor);
                           RenderUtil.drawRect(var19 - 5, var30, var19 - 3, var22 + 1, GameUtil.getHealthColorRGB(var28));
                           break;
                        case GRADIENT:
                           Color var31 = (Color)this.healthBar.getSetting("Bottom-Color").getValue();
                           int var32 = var31.darker().darker().getRGB();
                           int var33 = var31.darker().darker().darker().getRGB();
                           Color var34 = (Color)this.healthBar.getSetting("Color").getValue();
                           int var35 = var34.darker().darker().getRGB();
                           int var36 = var34.darker().darker().darker().darker().getRGB();
                           RenderUtil.drawVerticalGradient(var19 - 6, var21 - 1, var19 - 2, var22 + 2, var33, var36);
                           RenderUtil.drawVerticalGradient(var19 - 5, var21, var19 - 3, var22 + 1, var32, var35);
                           RenderUtil.drawVerticalGradient(var19 - 5, var21, var19 - 3, var22 + 1, var31.getRGB(), var34.getRGB());
                           RenderUtil.drawRect(var19 - 5, var21, var19 - 3, var30, this.healthBarShadeColor);
                           break;
                        case CUSTOM:
                           RenderUtil.drawRect(var19 - 6, var21 - 1, var19 - 2, var22 + 2, this.outlineColor);
                           RenderUtil.drawRect(var19 - 5, var30, var19 - 3, var22 + 1, ((ColorSetting)this.healthBar.getSetting("Color")).getValue().getRGB());
                     }

                     if (var28.getAbsorptionAmount() > 0.0F) {
                        int var54 = (int)(var22 + (var21 - var22) * (var28.getAbsorptionAmount() / var28.getMaxHealth() * 100.0F * 0.01F));
                        if (var54 > var22) {
                           var54 = var21;
                        }

                        Gui.drawRect(var19 - 5, var22 + 1, var19 - 3, var54, Color.YELLOW.getRGB());
                     }

                     if (this.hPos.getValue().equals(ESPHPosMode.HEALTH_BAR) && var28.getHealth() != var28.getMaxHealth()) {
                        String var55 = this.health.getValue() == ESPHealthMode.HEARTS || this.health.getValue() == ESPHealthMode.HEALTH
                           ? MathUtil.ONE_DECIMAL.format(this.getHealthValue(var28)) + "§c❤"
                           : (
                              this.health.getValue().equals(ESPHealthMode.PERCENTAGE)
                                 ? MathUtil.ONE_DECIMAL.format(this.getHealthValue(var28)) + "%"
                                 : String.valueOf(var28.getHealth())
                           );
                        client.getFontRenderer()
                           .O(
                              var55,
                              (var19 - 4 + var19 - 2) / 2 - client.getFontRenderer().getStringWidth(var55) - 4,
                              var30 - client.getFontRenderer().FONT_HEIGHT + 9,
                              Color.WHITE.getRGB()
                           );
                     }
                  }

                  if (this.clickESP.getSetting("Enable").asBoolean()
                     && var28 instanceof EntityPlayer
                     && Mouse.isButtonDown(this.clickESP.getSetting("Right-Click").asBoolean() ? 1 : 0)
                     && this.menuTarget == null) {
                     float var50 = Display.getWidth() / 2.0F;
                     float var52 = Display.getHeight() / 2.0F;
                     float var56 = var50 - var19;
                     float var58 = var50 - var20;
                     float var60 = var52 - var21;
                     float var62 = var52 - var22;
                     if (Keyboard.isKeyDown(29)
                        && (
                           this.isInside(var19, var21, var20, var22, (int)(var6 / var25), (int)(var7 / var25))
                              || var56 > 0.0F && var58 < 0.0F && var60 > 0.0F && var62 < 0.0F
                        )) {
                        this.menuTarget = var28;
                     }
                  }

                  boolean var51 = this.nameTags.getSetting("CFont").asBoolean() && !this.nameTags.getSetting("Outline").asBoolean();
                  float var53 = this.nameTags.getSetting("Scale").asFloat() + (var51 ? 0.5F : 0.0F);
                  int var57 = Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F);
                  GlStateManager.popMatrix();
                  GlStateManager.pushMatrix();
                  GlStateManager.scale(var25 * var53, var25 * var53, var25 * var53);
                  float var59 = var27 / var53;
                  float var61 = var21 / var53;
                  if (var28 instanceof EntityPlayer) {
                     boolean var63 = false;
                     if (this.nameTags.getSetting("Armor").asBoolean()) {
                        EntityPlayer var66 = (EntityPlayer)var28;
                        ArrayList var70 = new ArrayList();

                        for (int var37 = 4; var37 >= 0; var37--) {
                           ItemStack var38 = var37 == 4 ? var28.getHeldItem() : var66.inventory.armorInventory[var37];
                           if (var38 != null) {
                              var70.add(var38);
                           }
                        }

                        float var74 = var59;
                        String var78 = Denicker.nicks.get(var2.getName());
                        float var39 = var61 - 34.0F - (var78 == null ? 0 : 12);

                        for (Object var41$raw : var70) {
                           ItemStack var41 = (ItemStack)var41$raw;
                           if (var41 != null) {
                              int var42 = 9 * var70.size();
                              GlStateManager.clear(256);
                              GlStateManager.disableDepth();
                              GlStateManager.disableTexture2D();
                              GlStateManager.enableTexture2D();
                              GlStateManager.enableDepth();
                              float var43 = 1.0F;
                              GlStateManager.scale(var43, var43, 0.0F);
                              float var44 = -var42 + var74;
                              mc.getRenderItem().K(var41, var44 / var43, var39 / var43);
                              mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var41, (int)(var44 / var43), (int)(var39 / var43));
                              GlStateManager.scale(1.0F / var43, 1.0F / var43, 0.0F);
                              GlStateManager.disableLighting();
                              this.drawEnchantments(var41, -var42 + var74 + 7.0F, var61 - 34.0F);
                              var63 = true;
                              var74 += 16.0F;
                           }
                        }
                     }

                     if (this.nameTags.getSetting("Potions").asBoolean()) {
                        EntityPlayer var67 = (EntityPlayer)var28;
                        List var71 = HypixelGameTracker.getEffects(var67);
                        if (!var71.isEmpty()) {
                           float var75 = 0.0F;

                           for (Object var82$raw : var71) {
                              PotionEffect var82 = (PotionEffect)var82$raw;
                              Potion var85 = Potion.potionTypes[var82.getPotionID()];
                              byte var87 = 18;
                              boolean var89 = false;
                              short var90 = 198;
                              byte var91 = 8;
                              GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                              mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
                              int var45 = var85.getStatusIconIndex();
                              float var46 = 1.0F;
                              String var47 = Denicker.nicks.get(var2.getName());
                              GlStateManager.scale(var46, var46, 0.0F);
                              mc.ingameGUI
                                 .drawTexturedModalRect(
                                    (float)(var59 + var75 - var71.size() * 18 / 2.0F),
                                    (float)(var61 - (var63 ? 52 + (var47 == null ? 0 : 12) : 34)),
                                    0 + var45 % 8 * 18,
                                    198 + var45 / 8 * 18,
                                    18,
                                    18
                                 );
                              GlStateManager.disableLighting();
                              GlStateManager.scale(1.0F / var46, 1.0F / var46, 0.0F);
                              var75 += 18.0F;
                           }
                        }
                     }
                  }

                  if (this.nameTags.getSetting("Enable").asBoolean()) {
                     AstolfoUser var64 = client.getUserManager().g(var2.getName());
                     int var68 = var64 == null
                        ? ((ColorSetting)this.nameTags.getSetting("Color")).getValue().getRGB()
                        : (var64.K == AstolfoUserStatus.C ? var57 : ((ColorSetting)this.groupColor.getSetting("Astolfo")).getValue().getRGB());
                     Relation var72 = client.getRelationManager().Z().get(var2.getName());
                     String var76 = Denicker.nicks.get(var2.getName());
                     String var80 = var76 != null
                        ? var76
                        : (
                           var72 != null
                              ? (var72.z() ? "§3" : "§4") + var72.m()
                              : (
                                 var64 == null
                                    ? (
                                       this.nameTags.getSetting("Mode").getValue() == ESPNameMode.NAME
                                          ? var2.getName()
                                          : var2.getDisplayName().getUnformattedTextForChat()
                                    )
                                    : var64.s
                              )
                        );
                     if (this.hPos.getValue().equals(ESPHPosMode.STANDARD)) {
                        String var83 = this.health.getValue().equals(ESPHealthMode.HEARTS)
                           ? String.format(
                              "§7[§f%s§c❤§7]", Double.isNaN(this.getHealthValue(var28)) ? "NaN" : MathUtil.ONE_DECIMAL.format(this.getHealthValue(var28))
                           )
                           : String.format(
                              "§7[§f%s%%§7]", Double.isNaN(this.getHealthValue(var28)) ? "NaN" : MathUtil.ONE_DECIMAL.format(this.getHealthValue(var28))
                           );
                        this.drawNameTag(var76 == null ? "" : var2.getName(), var80 + " " + var83, var59, var61, var68);
                     } else {
                        this.drawNameTag(var76 == null ? "" : var2.getName(), var80, var59, var61, var68);
                     }
                  }

                  GlStateManager.popMatrix();
                  GlStateManager.pushMatrix();
                  float var65 = 1.0F + (var51 ? 0.35F : 0.0F);
                  GlStateManager.scale(var25 * var65, var25 * var65, var25 * var65);
                  float var69 = var27 / var65;
                  float var73 = var22 / var65;
                  if (var28 instanceof EntityPlayer && var28.getHeldItem() != null && this.itemName.getValue()) {
                     if (this.pitItemPerks.getValue() && GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && var28.getHeldItem().V() != null) {
                        byte var77 = 32;
                        if (var28.getHeldItem().V().hasKey("display", 10)) {
                           NBTTagCompound var81 = var28.getHeldItem().V().getCompoundTag("display");
                           NBTTagList var84 = var81.getTagList("Lore", 8);
                           if (var84.tagCount() > 0) {
                              for (int var86 = 0; var86 < var84.tagCount(); var86++) {
                                 String var88 = var84.getStringTagAt(var86);
                                 if (!var88.contains("Unbreakable")
                                    && !var88.contains("Attack Damage")
                                    && (var88.startsWith("§dRARE!") || var88.startsWith("§9"))) {
                                    this.drawNameTag("", var84.getStringTagAt(var86), var69, var73 + var77, -1);
                                    var77 += 12;
                                 }
                              }
                           }
                        }
                     }

                     this.drawNameTag(
                        "",
                        var28.getHeldItem().getDisplayName() + (var28.getHeldItem().stackSize > 1 ? " §7x" + var28.getHeldItem().stackSize : ""),
                        var69,
                        var73 + 20.0F,
                        -1
                     );
                  }
               }

               GlStateManager.popMatrix();
            }
         } else {
            if (var2 instanceof EntityLivingBase && this.offScreen.getSetting("Enable").asBoolean()) {
               this.drawOffScreenArrow(var1, (EntityLivingBase)var2, var16[0], var16[1], var16[2]);
            }
         }
      }
   }

   private int drawEnchantments(ItemStack var1, float var2, float var3) {
      GlStateManager.disableDepth();
      int var7 = 0;
      if (var1.isItemEnchanted()) {
         NBTTagList var8 = var1.getEnchantmentTagList();
         byte var9 = -4;

         for (int var10 = 0; var10 < Objects.requireNonNull(var8).tagCount(); var10++) {
            short var11 = var8.getCompoundTagAt(var10).getShort("id");
            short var12 = var8.getCompoundTagAt(var10).getShort("lvl");
            Enchantment var13 = Enchantment.getEnchantmentById(var11);
            if ((
                  var13 == Enchantment.protection
                     || var13 == Enchantment.sharpness
                     || var13 == Enchantment.unbreaking
                     || var13 == Enchantment.fireAspect
                     || var13 == Enchantment.efficiency
                     || var13 == Enchantment.flame
                     || var13 == Enchantment.knockback
               )
               && (!GameUtil.isOnHypixel() || var13 != Enchantment.unbreaking)) {
               String var14 = var13.getTranslatedName(var12).substring(0, 2).trim() + var12;
               var14 = var14.substring(0, 1).toUpperCase() + var14.substring(1).toLowerCase();
               this.drawScaledString(var14, var2, var3 + var9, 0.5F);
               var9 -= 8;
               var7++;
            }
         }
      }

      GlStateManager.disableLighting();
      return var7;
   }

   private void drawScaledString(String var1, float var2, float var3, float var4) {
      var2 *= 1.0F / var4;
      var3 *= 1.0F / var4;
      GlStateManager.scale(var4, var4, 0.0F);
      var2 -= 6.0F;
      var3 -= 3.0F;
      mc.fontRendererObj.drawString(var1, var2 + 0.7F, var3 + 0.7F, 0, false);
      mc.fontRendererObj.drawString(var1, var2, var3, -1, false);
      GlStateManager.scale(1.0F / var4, 1.0F / var4, 0.0F);
   }

   private void drawOffScreenArrow(RenderEvent var1, EntityLivingBase var2, float var3, float var4, float var5) {
      long var6 = cb ^ 4831986420509L;
      long var10001 = var6 ^ 94351469989242L;
      int var8 = (int)((var6 ^ 94351469989242L) >>> 56);
      int var9 = (int)((var6 ^ 94351469989242L) << 8 >>> 32);
      int var10 = (int)(var10001 << 40 >>> 40);
      float var12 = var1.getResolution().getScaledWidth() / 2.0F;
      float var13 = var1.getResolution().getScaledHeight() / 2.0F;
      double var14 = var3 / var1.getResolution().getScaleFactor();
      double var16 = var4 / var1.getResolution().getScaleFactor();
      double var18 = var13 * this.offScreen.getSetting("Radius").asFloat();
      float var20 = this.angleTo(var12, var14, var13, var16) + (var5 > 1.0 ? 180 : 0);
      double var21 = var18 * Math.cos(Math.toRadians(var20));
      double var23 = var18 * Math.sin(Math.toRadians(var20));
      GL11.glPushMatrix();
      GL11.glTranslated(var21 + var12, var23 + var13, 0.0);
      GL11.glRotatef(var20 - 90.0F, 0.0F, 0.0F, 1.0F);
      float var25 = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 3000.0F / 3000.0F * (float) Math.PI * 2.0F) * 0.6F);
      Color var26 = client.getRelationManager().z(var2) ? Color.BLUE.brighter() : ((ColorSetting)this.offScreen.getSetting("Color")).getValue();
      RenderUtil.drawArrow(
         0.0,
         0.0,
         this.offScreen.getSetting("Scale").asInt(),
         false,
         this.offScreen.getSetting("Pulsing").asBoolean()
            ? new Color(var26.getRed(), var26.getGreen(), var26.getBlue(), MathHelper.clamp_int((int)(var26.getAlpha() * var25), 0, 255)).getRGB()
            : var26.getRGB()
      );
      GL11.glPopMatrix();
   }

   private float angleTo(double var1, double var3, double var5, double var7) {
      return (float)Math.toDegrees(Math.atan2(var7 - var5, var3 - var1));
   }

   private boolean isInside(int var1, int var2, int var3, int var4, int var5, int var6) {
      return var5 >= var1 && var5 <= var3 && var6 >= var2 && var6 <= var4;
   }

   private void drawNameTag(String var1, String var2, float var3, float var4, int var5) {
      if (var1 != null && !var1.isEmpty()) {
         var5 = -204212;
      }

      boolean var9 = this.nameTags.getSetting("Background").asBoolean();
      ESPNameBarMode var10 = (ESPNameBarMode)this.nameTags.getSetting("Bar").getValue();
      boolean var11 = this.nameTags.getSetting("CFont").asBoolean() && !this.nameTags.getSetting("Outline").asBoolean();
      int var12 = client.getFontRenderer().getStringWidth(var2) / 2;
      int var13 = client.getFontRenderer().getStringWidth(var1) / 2;
      if (var11) {
         var12 = client.getCustomFontRenderer().getStringWidth(var2) / 2;
         var13 = client.getCustomFontRenderer().getStringWidth(var1) / 2;
      }

      if (var9) {
         RenderUtil.drawRect(var3 - var12 - 3.0F, var4 - 15.5F, var3 + var12 + 3.0F, var4 - 3.0F, this.nameBackgroundColor);
      }

      switch (var10) {
         case UP:
            RenderUtil.drawRect(var3 - var12 - 3.0F, var4 - 15.5F, var3 + var12 + 3.0F, var4 - 14.5F, var5);
            break;
         case DOWN:
            RenderUtil.drawRect(var3 - var12 - 3.0F, var4 - 4.0F, var3 + var12 + 3.0F, var4 - 3.0F, var5);
      }

      if (this.nameTags.getSetting("Outline").asBoolean()) {
         client.getFontRenderer().O(var2, (int)(var3 - var12), (int)(var4 - client.getFontRenderer().FONT_HEIGHT - 4.0F), var5);
      } else if (var11) {
         client.getCustomFontRenderer().drawStringWithShadow(var2, var3 - var12, var4 - client.getCustomFontRenderer().X().j() / 2.0F - 3.0F, var5);
      } else {
         client.getFontRenderer().drawStringWithShadow(var2, var3 - var12, var4 - client.getFontRenderer().FONT_HEIGHT - 4.0F, var5);
      }

      if (var1 != null) {
         if (var11) {
            client.getCustomFontRenderer().drawStringWithShadow("§7" + var1, var3 - var13, var4 - client.getCustomFontRenderer().X().j() - 16.0F, -1);
         } else {
            client.getFontRenderer().drawStringWithShadow("§7" + var1, var3 - var13, var4 - client.getFontRenderer().FONT_HEIGHT - 16.0F, -1);
         }
      }
   }

   public static void renderItem(ItemStack var0, int var1, int var2) {
      GlStateManager.pushMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.clear(256);
      RenderHelper.enableStandardItemLighting();
      mc.getRenderItem().zLevel = -150.0F;
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.disableTexture2D();
      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      mc.getRenderItem().renderItemAndEffectIntoGUI(var0, var1, var2);
      mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, var0, var1, var2);
      mc.getRenderItem().zLevel = 0.0F;
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableCull();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.disableLighting();
      GlStateManager.scale(0.5, 0.5, 0.5);
      GlStateManager.disableDepth();
      GlStateManager.enableDepth();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      GlStateManager.popMatrix();
   }

   private void drawTileEntity(RenderEvent var1, TileEntity var2, double var3, double var5, double var7) {
      float[] var12 = this.project((float)var3, (float)var5, (float)var7);
      if (var12 != null && !(var12[2] > 1.0F)) {
         if (this.isOnScreen(var12[0], var12[1])) {
            AxisAlignedBB var13 = AxisAlignedBB.fromBounds(var3, var5, var7, var3 + 1.0, var5 + 1.0, var7 + 1.0);
            if (var2 instanceof TileEntityChest) {
               TileEntityChest var14 = (TileEntityChest)var2;
               if (var14.adjacentChestZNeg != null) {
                  var13 = AxisAlignedBB.fromBounds(var3, var5, var7 + 1.0, var3 + 0.9375, var5 + 0.875, var7 - 1.0);
               } else if (var14.adjacentChestXPos != null) {
                  var13 = AxisAlignedBB.fromBounds(var3 + 0.0625, var5, var7 + 0.0625, var3 + 1.9375, var5 + 0.875, var7 + 0.9375);
               } else {
                  if (var14.adjacentChestZNeg != null || var14.adjacentChestXPos != null || var14.adjacentChestXNeg != null || var14.adjacentChestZPos != null) {
                     return;
                  }

                  var13 = AxisAlignedBB.fromBounds(var3 + 0.0625, var5, var7 + 0.0625, var3 + 0.9375, var5 + 0.875, var7 + 0.9375);
               }
            } else if (var2 instanceof TileEntityEnderChest) {
               var13 = AxisAlignedBB.fromBounds(var3 + 0.0625, var5, var7 + 0.0625, var3 + 0.9375, var5 + 0.875, var7 + 0.9375);
            }

            int var15 = Integer.MAX_VALUE;
            int var16 = Integer.MIN_VALUE;
            int var17 = Integer.MAX_VALUE;
            int var18 = Integer.MIN_VALUE;
            float[][] var19 = new float[][]{
               {(float)var13.minX, (float)var13.minY, (float)var13.minZ},
               {(float)var13.minX, (float)var13.maxY, (float)var13.minZ},
               {(float)var13.maxX, (float)var13.maxY, (float)var13.minZ},
               {(float)var13.maxX, (float)var13.minY, (float)var13.minZ},
               {(float)var13.maxX, (float)var13.maxY, (float)var13.maxZ},
               {(float)var13.minX, (float)var13.maxY, (float)var13.maxZ},
               {(float)var13.minX, (float)var13.minY, (float)var13.maxZ},
               {(float)var13.maxX, (float)var13.minY, (float)var13.maxZ}
            };

            for (int var20 = 0; var20 < 8; var20++) {
               float[] var23 = this.project(var19[var20][0], var19[var20][1], var19[var20][2]);
               if (var23 != null) {
                  var15 = Math.min(var15, (int)var23[0]);
                  var16 = Math.max(var16, (int)var23[0]);
                  var17 = Math.min(var17, (int)var23[1]);
                  var18 = Math.max(var18, (int)var23[1]);
               }
            }

            if (var15 != Integer.MAX_VALUE && var16 != Integer.MIN_VALUE && var17 != Integer.MAX_VALUE && var18 != Integer.MIN_VALUE) {
               int var24 = this.getTileEntityColor(var2);
               double var21 = var1.getResolution().getScaleFactor();
               GlStateManager.scale(1.0 / var21, 1.0 / var21, 1.0 / var21);
               if (this.box.getSetting("Enable").asBoolean()) {
                  this.drawBox(var15, var16, var17, var18, var24);
               }

               GlStateManager.scale(var21, var21, var21);
            }
         }
      }
   }

   public static Color pulsingColor(long var0, float var2) {
      float var5 = 1.0F - MathHelper.abs(MathHelper.sin((float)var0 % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long var6 = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(var5, 0.5F, 1.0F)), 16);
      Color var8 = new Color((int)var6);
      return new Color(var8.getRed() / 255.0F * var2, var8.getGreen() / 255.0F * var2, var8.getBlue() / 255.0F * var2, var8.getAlpha() / 255.0F);
   }

   private void drawBox(int var1, int var2, int var3, int var4, int var5) {
      ESPBoxMode var9 = (ESPBoxMode)this.box.getSetting("Mode").getValue();
      boolean var10 = this.box.getSetting("Outline").asBoolean();
      switch (var9) {
         case FULL:
            if (var10) {
               Gui.v(var1 - 1.0, var2 + 1.0, var3 - 1.0, var3 + 2.0, this.outlineColor);
            }

            Gui.S(var1, var2, var3, var5);
            if (var10) {
               Gui.v(var1 - 1.0, var2 + 1.0, var4 - 1.0, var4 + 2.0, this.outlineColor);
            }

            Gui.S(var1, var2, var4, var5);
            if (var10) {
               Gui.f(var1 - 1.0, var1 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
            }

            Gui.J(var1, var4, var3, var5);
            if (var10) {
               Gui.f(var2 - 1.0, var2 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
            }

            Gui.J(var2, var4, var3, var5);
            break;
         case RAINBOW:
            long var11 = Minecraft.getSystemTime();
            RenderUtil.drawRect(var1 - 1, var3 - 1, var2 + 2, var3 + 2, this.outlineColor);
            RenderUtil.drawVerticalGradient(var1, var3, var2, var3 + 1, pulsingColor(var11, 1.0F).getRGB(), pulsingColor(var11, 1.0F).getRGB());
            RenderUtil.drawRect(var1 - 1, var4 - 1, var2 + 2, var4 + 2, this.outlineColor);
            RenderUtil.drawVerticalGradient(
               var1, var4, var2 + 1, var4 + 1, pulsingColor(var11 - 1600L, 1.0F).getRGB(), pulsingColor(var11 - 1600L, 1.0F).getRGB()
            );
            RenderUtil.drawRect(var1 - 1, var3 + 1, var1 + 2, var4 - 1, this.outlineColor);
            RenderUtil.drawVerticalGradient(var1, var3, var1 + 1, var4, pulsingColor(var11 - 1600L, 1.0F).getRGB(), pulsingColor(var11, 1.0F).getRGB());
            RenderUtil.drawRect(var2 - 1, var3 + 1, var2 + 2, var4, this.outlineColor);
            RenderUtil.drawVerticalGradient(var2, var3, var2 + 1, var4 + 1, pulsingColor(var11 - 1600L, 1.0F).getRGB(), pulsingColor(var11, 1.0F).getRGB());
            break;
         case CUSTOM:
            int var13 = (int)this.box.getSetting("Width").asFloat();
            int var14 = (int)this.box.getSetting("Height").asFloat();
            int var15 = (var2 - var1) / var13;
            int var16 = (var1 - var2) / var13;
            int var17 = (var4 - var3) / var14;
            int var18 = (var3 - var4) / var14;
            if (var13 != 1) {
               this.drawHorizontalCorners(var1, var2, var3, var5, var10, var15, var16);
            } else {
               if (var10) {
                  Gui.v(var1 - 1.0, var2 + 1.0, var3 - 1.0, var3 + 2.0, this.outlineColor);
               }

               Gui.S(var1, var2, var3, var5);
               if (var10) {
                  Gui.v(var1 - 1.0, var2 + 1.0, var4 - 1.0, var4 + 2.0, this.outlineColor);
               }

               Gui.S(var1, var2, var4, var5);
            }

            if (var14 != 1) {
               if (var10) {
                  Gui.f(var1 - 1.0, var1 + 2.0, var3 + 1, var3 + var17 + 1, this.outlineColor);
               }

               Gui.J(var1, var3, var3 + var17, var5);
               if (var10) {
                  Gui.f(var2 - 1.0, var2 + 2.0, var3 + 1, var3 + var17 + 1, this.outlineColor);
               }

               Gui.J(var2, var3, var3 + var17, var5);
            } else {
               if (var10) {
                  Gui.f(var1 - 1.0, var1 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
               }

               Gui.J(var1, var4, var3, var5);
               if (var10) {
                  Gui.f(var2 - 1.0, var2 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
               }

               Gui.J(var2, var4, var3, var5);
            }

            if (var13 != 1) {
               this.drawHorizontalCorners(var1, var2, var4, var5, var10, var15, var16);
            }

            if (var14 != 1) {
               if (var10) {
                  Gui.f(var1 - 1.0, var1 + 2.0, var4 - 1, var4 + var18, this.outlineColor);
               }

               Gui.J(var1, var4, var4 + var18, var5);
               if (var10) {
                  Gui.f(var2 - 1.0, var2 + 2.0, var4 - 1, var4 + var18, this.outlineColor);
               }

               Gui.J(var2, var4, var4 + var18, var5);
            }
            break;
         case TRANS:
            int var19 = (var4 - var3) / 3;
            int var20 = (var3 - var4) / 3;
            if (var10) {
               Gui.v(var1 - 1.0, var2 + 1.0, var3 - 1.0, var3 + 2.0, this.outlineColor);
            }

            Gui.S(var1, var2, var3, HUDFlagColor.BLUE.m().getRGB());
            if (var10) {
               Gui.f(var1 - 1.0, var1 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
            }

            Gui.J(var1, var4, var3, Color.WHITE.getRGB());
            if (var10) {
               Gui.f(var2 - 1.0, var2 + 2.0, var4 - 1.0, var3 + 1.0, this.outlineColor);
            }

            Gui.J(var2, var4, var3, Color.WHITE.getRGB());
            Gui.J(var1, var3, var3 + var19, HUDFlagColor.PINK.m().getRGB());
            Gui.J(var2, var3, var3 + var19, HUDFlagColor.PINK.m().getRGB());
            if (var10) {
               Gui.v(var1 - 1.0, var2 + 1.0, var4 - 1.0, var4 + 2.0, this.outlineColor);
            }

            Gui.S(var1, var2, var4, HUDFlagColor.BLUE.m().getRGB());
            Gui.J(var1, var4, var4 + var20, HUDFlagColor.PINK.m().getRGB());
            Gui.J(var2, var4, var4 + var20, HUDFlagColor.PINK.m().getRGB());
      }
   }

   private void drawHorizontalCorners(int var1, int var2, int var3, int var4, boolean var5, int var6, int var7) {
      if (var5) {
         Gui.v(var1 - 1, var1 + var6 + 1.0, var3 - 1.0, var3 + 2.0, this.outlineColor);
      }

      Gui.S(var1, var1 + var6, var3, var4);
      if (var5) {
         Gui.v(var2 + 1, var2 + var7 - 1.0, var3 - 1.0, var3 + 2.0, this.outlineColor);
      }

      Gui.S(var2, var2 + var7, var3, var4);
   }

   private int getTileEntityColor(TileEntity var1) {
      if (var1 instanceof TileEntityChest) {
         return ((TileEntityChest)var1).getChestType() == 1 ? this.trappedChestColor : this.chestColor;
      } else if (var1 instanceof TileEntityEnderChest) {
         return this.enderChestColor;
      } else {
         return var1 instanceof TileEntityHopper ? this.hopperColor : Color.WHITE.getRGB();
      }
   }

   private double getHealthValue(EntityLivingBase var1) {
      switch ((ESPHealthMode)this.health.getValue()) {
         case HEARTS:
            return var1.getHealth() / 2.0F;
         case PERCENTAGE:
            return var1.getHealth() * 100.0F / var1.getMaxHealth();
         case HEALTH:
            return var1.getHealth();
         default:
            return 0.0;
      }
   }

   private boolean shouldRender(Entity var1) {
      long var2 = cb ^ 105193055106273L;
      long var4 = var2 ^ 27577897621281L;
      if ((!(var1 instanceof EntityPlayer) || var1 == mc.thePlayer)
         && (var1 != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())) {
         return GameUtil.isHostile(var1) && this.entities.getSetting("Hostiles").asBoolean()
            || GameUtil.isNeutral(var1) && this.entities.getSetting("Neutrals").asBoolean()
            || GameUtil.isPassive(var1) && this.entities.getSetting("Passives").asBoolean()
            || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean()
            || GameUtil.isVehicle(var1) && this.entities.getSetting("Vehicles").asBoolean()
            || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean()
            || GameUtil.isObject(var1) && this.entities.getSetting("Others").asBoolean();
      }

      if (GameUtil.S(var1) && !this.entities.getSetting("Invisibles").asBoolean()) {
         return false;
      }

      if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && AutoHypixel.pitSpawnY != 0.0 && var1.posY > AutoHypixel.pitSpawnY) {
         return false;
      }

      if (GameUtil.O(mc.thePlayer, (EntityLivingBase)var1) && this.teams.getValue()) {
         AstolfoUser var7 = client.getUserManager().g(var1.getName());
         if (var7 == null) {
            return false;
         }
      }

      return BotDetector.w.contains(var1) && !this.entities.getSetting("Bots").asBoolean() ? false : this.entities.getSetting("Players").asBoolean();
   }

   private boolean isHighlightedItem(EntityItem var1) {
      return var1.getEntityItem() != null
         && HypixelGameTracker.game == 3
         && (
            var1.getEntityItem().getDisplayName() != null
                  && OldInventoryManager.uhcItemNames.contains(StringUtils.stripControlCodes(var1.getEntityItem().getDisplayName()))
               || var1.getEntityItem().getItem() instanceof ItemSkull && OldInventoryManager.isHead(var1.getEntityItem())
               || var1.getEntityItem().getItem() instanceof ItemAppleGold
         );
   }

   private boolean shouldRenderTileEntity(TileEntity var1) {
      return var1 instanceof TileEntityEnderChest && this.blocks.getSetting("Ender Chest").asBoolean()
         || var1 instanceof TileEntityChest && this.blocks.getSetting("Chest").asBoolean()
         || var1 instanceof TileEntityHopper && this.blocks.getSetting("Hopper").asBoolean();
   }

   private int getBoxColor(Entity var1) {
      long var2 = cb ^ 138792793910601L;
      long var10001 = var2 ^ 52303409343278L;
      int var4 = (int)((var2 ^ 52303409343278L) >>> 56);
      int var5 = (int)((var2 ^ 52303409343278L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      long var7 = var2 ^ 62139172228745L;
      AstolfoUser var10 = client.getUserManager().g(var1.getName());
      if (var10 != null) {
         return var10.K == AstolfoUserStatus.C
            ? Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F)
            : ((ColorSetting)this.groupColor.getSetting("Astolfo")).getValue().getRGB();
      } else if (client.getRelationManager().z(var1)) {
         return ((ColorSetting)this.groupColor.getSetting("Friends")).getValue().getRGB();
      } else if (Denicker.nicks.get(var1.getName()) != null) {
         return -204212;
      } else if (client.getRelationManager().D(var1)) {
         return ((ColorSetting)this.groupColor.getSetting("Enemies")).getValue().getRGB();
      } else if (BotDetector.w.contains(var1)) {
         return this.botColor;
      } else if (var1 instanceof EntityItem) {
         return this.isHighlightedItem((EntityItem)var1) ? Color.YELLOW.getRGB() : Color.WHITE.getRGB();
      } else if (AutoHypixel.bountiedPlayers.contains(var1)) {
         return Color.YELLOW.getRGB();
      } else {
         return var1 == KillAura.target && this.highlight.getValue()
            ? client.getColorSetting().getValue().getRGB()
            : ((ColorSetting)this.box.getSetting("Color")).getValue().getRGB();
      }
   }

   private float[] project(float var1, float var2, float var3) {
      ((Buffer)projected).clear();
      boolean var7 = GLU.gluProject(var1, var2, var3, modelView, projection, viewport, projected);
      return var7 ? new float[]{projected.get(0), viewport.get(3) - projected.get(1), projected.get(2)} : null;
   }

   private void saveMatrices(float var1) {
      GL11.glPushAttrib(4096);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      mc.entityRenderer.setupCameraTransform(var1, 0);
      GlStateManager.getFloat(2983, projection);
      GlStateManager.getFloat(2982, modelView);
      GL11.glGetInteger(2978, viewport);
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
   }

   private boolean isOnScreen(double var1, double var3) {
      return var1 > 0.0 && var3 > 0.0 && var1 < mc.displayWidth && var3 < mc.displayHeight;
   }
}
