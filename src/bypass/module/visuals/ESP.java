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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      switch (renderEvent.getType()) {
         case WORLD:
            this.saveMatrices(renderEvent.getPartialTicks());
            break;
         case SCREEN:
            int i = renderEvent.getResolution().getScaledHeight();
            int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
            int k = i - Mouse.getY() * i / mc.displayHeight - 1;
            if (this.entities.getSetting("Items").asBoolean()) {
               for (Entity entity : mc.theWorld.loadedEntityList) {
                  if (entity instanceof EntityItem
                     && (
                        ((EntityItem)entity).getEntityItem().isItemEnchanted()
                           || this.isHighlightedItem((EntityItem)entity)
                           || !this.items.getSetting("Highlighted-Only").asBoolean()
                     )) {
                     EntityItem entityItem = (EntityItem)entity;
                     if (!this.groupedItems.contains(entityItem)) {
                        ArrayList arrayList = new ArrayList();

                        for (Object object : mc.theWorld.loadedEntityList) {
                           if (object instanceof EntityItem && object != entityItem) {
                              EntityItem entityItem2 = (EntityItem)object;
                              if ((
                                    entityItem2.getEntityItem().isItemEnchanted()
                                       || this.isHighlightedItem(entityItem2)
                                       || !this.items.getSetting("Highlighted-Only").asBoolean()
                                 )
                                 && !(entityItem2.getDistanceToEntity(entityItem) >= 3.0F)) {
                                 arrayList.add(entityItem2);
                                 if (!this.groupedItems.contains(entityItem2)) {
                                    this.groupedItems.add(entityItem2);
                                 }
                              }
                           }
                        }

                        arrayList.add(entityItem);
                        double d = MathUtil.interpolate(entity.lastTickPosX, entity.posX, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosX);
                        double e = MathUtil.interpolate(entity.lastTickPosY, entity.posY, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosY);
                        double d2 = MathUtil.interpolate(entity.lastTickPosZ, entity.posZ, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosZ);
                        this.drawItemGroup(renderEvent, entityItem, arrayList, (float)d, (float)e, (float)d2);
                     }
                  }
               }
            }

            List list = mc.theWorld.loadedTileEntityList;
            int l = 0;

            for (int m = list.size(); l < m; l++) {
               TileEntity tileEntity = (TileEntity)list.get(l);
               if (this.shouldRenderTileEntity(tileEntity)) {
                  double d3 = tileEntity.getPos().getX() - mc.getRenderManager().renderPosX;
                  double d4 = tileEntity.getPos().getY() - mc.getRenderManager().renderPosY;
                  double d5 = tileEntity.getPos().getZ() - mc.getRenderManager().renderPosZ;
                  this.drawTileEntity(renderEvent, tileEntity, d3, d4, d5);
               }
            }

            List list2 = mc.theWorld.loadedEntityList;
            int n = 0;

            for (int i2 = list2.size(); n < i2; n++) {
               Entity entity2 = (Entity)list2.get(n);
               if (this.shouldRender(entity2)) {
                  double d6 = MathUtil.interpolate(entity2.lastTickPosX, entity2.posX, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosX);
                  double d7 = MathUtil.interpolate(entity2.lastTickPosY, entity2.posY, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosY);
                  double d8 = MathUtil.interpolate(entity2.lastTickPosZ, entity2.posZ, renderEvent.getPartialTicks(), mc.getRenderManager().viewerPosZ);
                  this.drawEntity(renderEvent, entity2, (float)d6, (float)d7, (float)d8, j, k);
               }
            }

            if (this.menuTarget != null) {
               float f = renderEvent.getResolution().getScaledWidth() / 2.0F;
               float g = renderEvent.getResolution().getScaledHeight() / 2.0F;
               this.menuX = this.menuX - mc.mouseHelper.a();
               this.menuY = this.menuY + mc.mouseHelper.z();
               int i3 = mc.fontRendererObj.getStringWidth(this.menuTarget.getName());
               RenderUtil.drawRect(
                  f - i3 + this.menuX,
                  g - 100.0F + this.menuY,
                  f + i3 + this.menuX,
                  g - 90.0F + this.menuY + 9.0F,
                  new Color(1, 1, 1, 120).getRGB()
               );
               mc.fontRendererObj.drawStringWithShadow(this.menuTarget.getName(), f - i3 / 2.0F + this.menuX, g - 90.0F + this.menuY - 5.0F, -1);
               this.drawMenuButton("Custom", f + 20.0F, g - 75.0F, f + 75.0F, g - 20.0F);
               this.drawMenuButton("Enemy", f - 75.0F, g + 20.0F, f - 20.0F, g + 75.0F);
               this.drawMenuButton("Friend", f + 20.0F, g + 20.0F, f + 75.0F, g + 75.0F);
               this.drawMenuButton("Teleport", f - 75.0F, g - 75.0F, f - 20.0F, g - 20.0F);
               RenderUtil.drawLine(1.0F, f + this.menuX, g + this.menuY, f, g, -1);
               if (this.clickESP.getSetting("Right-Click").asBoolean()) {
                  mc.gameSettings.keyBindUseItem.pressed = false;
               } else {
                  mc.gameSettings.keyBindAttack.pressed = false;
               }

               this.menuOpen = true;
            }

            if (this.menuOpen && !Mouse.isButtonDown(this.clickESP.getSetting("Right-Click").asBoolean() ? 1 : 0) && this.menuTarget != null) {
               if (this.menuX >= 20.0F && this.menuY >= 20.0F && this.menuX <= 75.0F && this.menuY <= 75.0F) {
                  mc.thePlayer.sendClientChatMessage(".teleport " + this.menuTarget.getName());
               }

               if (this.menuX >= 20.0F && this.menuY <= -20.0F && this.menuY >= -75.0F && this.menuX <= 75.0F) {
                  if (client.getRelationManager().isEnemy(this.menuTarget)) {
                     client.getRelationManager().getRelations().remove(this.menuTarget.getName());
                     client.getRelationManager().getRelationMap().remove(this.menuTarget.getName());
                  } else {
                     client.getRelationManager().getRelations().remove(this.menuTarget.getName());
                     client.getRelationManager().getRelationMap().remove(this.menuTarget.getName());
                     client.getRelationManager().add(new Relation(this.menuTarget.getName(), this.menuTarget.getName(), false));
                  }
               }

               if (this.menuX <= -20.0F && this.menuY <= -20.0F && this.menuY >= -75.0F && this.menuX >= -75.0F) {
                  if (client.getRelationManager().isFriend(this.menuTarget)) {
                     client.getRelationManager().getRelations().remove(this.menuTarget.getName());
                     client.getRelationManager().getRelationMap().remove(this.menuTarget.getName());
                  } else {
                     client.getRelationManager().getRelations().remove(this.menuTarget.getName());
                     client.getRelationManager().getRelationMap().remove(this.menuTarget.getName());
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
   private final Listener<RenderNameTagEvent> onRenderNameTag = renderNameTagEvent -> {
      if (this.nameTags.getSetting("Enable").asBoolean()) {
         renderNameTagEvent.cancel();
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      this.displayedHealth.clear();
      this.groupedItems.clear();
   };
   private final int trappedChestColor = new Color(204, 0, 0).getRGB();
   private final int chestColor = new Color(255, 153, 0).getRGB();
   private final int enderChestColor = new Color(204, 51, 153).getRGB();
   private final int hopperColor = new Color(128, 128, 128).getRGB();
   private final int unusedGray = new Color(128, 128, 128).getRGB();

   public ESP(Category category) {
      super(category);
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

   private void drawMenuButton(String string, float f, float g, float h, float f2) {
      float f3 = f + this.menuX;
      float f4 = g + this.menuY;
      float f5 = h + this.menuX;
      float f6 = f2 + this.menuY;
      int i = mc.fontRendererObj.getStringWidth(string) / 2;
      RenderUtil.drawRect(f3, f4, f5, f6, new Color(1, 1, 1, 120).getRGB());
      mc.fontRendererObj.drawStringWithShadow(string, (f5 + f3) / 2.0F - i, f6 - 13.0F, -1);
      switch (string) {
         case "Teleport":
            GuiMainMenu.G(new ResourceLocation("client/icon/teleport.png"), 16.0F, (f5 + f3) / 2.0F - 16.0F, f4 + 5.0F);
            break;
         case "Friend":
            GuiMainMenu.G(new ResourceLocation("client/icon/friend.png"), 16.0F, (f5 + f3) / 2.0F - 16.0F, f4 + 5.0F);
            break;
         case "Enemy":
            GuiMainMenu.G(new ResourceLocation("client/icon/enemy.png"), 16.0F, (f5 + f3) / 2.0F - 16.0F, f4 + 5.0F);
            break;
         case "Custom":
            GuiMainMenu.G(new ResourceLocation("client/icon/options.png"), 16.0F, (f5 + f3) / 2.0F - 16.0F, f4 + 5.0F);
      }
   }

   private void drawItemGroup(RenderEvent renderEvent, EntityItem entityItem, List<EntityItem> list, float f, float g, float h) {
      float[] floats = this.project(f, g, h);
      if (floats != null) {
         if (!(floats[2] > 1.0F) && this.isOnScreen(floats[0], floats[1])) {
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(
               f - entityItem.width + 0.2,
               g,
               h - entityItem.width + 0.2,
               f + entityItem.width - 0.2,
               g + entityItem.height + (entityItem.isSneaking() ? 0.02 : 0.2),
               h + entityItem.width - 0.2
            );
            int i = Integer.MAX_VALUE;
            int j = Integer.MIN_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            float[][] floatss = new float[][]{
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ}
            };

            for (int m = 0; m < 8; m++) {
               float[] floats2 = this.project(floatss[m][0], floatss[m][1], floatss[m][2]);
               if (floats2 != null) {
                  i = Math.min(i, (int)floats2[0]);
                  j = Math.max(j, (int)floats2[0]);
                  k = Math.min(k, (int)floats2[1]);
                  l = Math.max(l, (int)floats2[1]);
               }
            }

            if (i != Integer.MAX_VALUE && j != Integer.MIN_VALUE && k != Integer.MAX_VALUE && l != Integer.MIN_VALUE) {
               GlStateManager.pushMatrix();
               int n = renderEvent.getResolution().getScaleFactor();
               double d = n / Math.pow(n, 2.0);
               GlStateManager.scale(d, d, d);
               float f2 = this.items.getSetting("Item-Scale").asFloat();
               GlStateManager.popMatrix();
               GlStateManager.pushMatrix();
               GlStateManager.scale(d * f2, d * f2, d * f2);
               float f3 = i / f2;
               float f4 = k / f2;
               RenderUtil.drawTriangle(f3, f4 + 1.0F, f3 - 8.0F, f4 - 6.0F, f3 + 8.0F, f4 - 6.0F, -872415232);
               ArrayList arrayList = new ArrayList();

               for (EntityItem entityItem2 : list) {
                  ItemStack itemStack = entityItem2.getEntityItem();
                  boolean bl = false;
                  if (entityItem2.getEntityItem().getItem() instanceof ItemArmor) {
                     ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
                     ArmorSlot armorSlot = AutoArmor.getArmorSlotByType(itemArmor.armorType);
                     if (armorSlot != null) {
                        int i2 = InventoryUtil.getArmorScore(itemStack);
                        Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(armorSlot.slot);
                        int i3 = InventoryUtil.getArmorScore(slot2.getStack());
                        if (i3 < i2) {
                           bl = true;
                        }
                     }
                  }

                  NBTTagList nbtTagList = entityItem2.getEntityItem().getEnchantmentTagList();
                  if (nbtTagList == null) {
                     arrayList.add(
                        (bl ? EnumChatFormatting.AQUA : "")
                           + entityItem2.getEntityItem().getDisplayName()
                           + (entityItem2.getEntityItem().stackSize > 1 ? EnumChatFormatting.GRAY + " x" + entityItem2.getEntityItem().stackSize : "")
                     );
                  } else {
                     StringBuilder stringBuilder = new StringBuilder(
                        (bl ? EnumChatFormatting.AQUA : EnumChatFormatting.RED) + entityItem2.getEntityItem().getDisplayName() + "§f:"
                     );

                     for (int i4 = 0; i4 < nbtTagList.tagCount(); i4++) {
                        short s = nbtTagList.getCompoundTagAt(i4).getShort("id");
                        short s2 = nbtTagList.getCompoundTagAt(i4).getShort("lvl");
                        Enchantment enchantment = Enchantment.getEnchantmentById(s);
                        if (enchantment != null) {
                           String string = I18n.format(enchantment.getName()).substring(0, 3).toUpperCase() + " " + s2;
                           stringBuilder.append(" ").append(string);
                        }
                     }

                     arrayList.add(stringBuilder.toString());
                  }
               }

               ArrayList arrayList2 = new ArrayList();
               int i5 = 0;

               for (int i6 = 0; i6 < arrayList.size(); i6++) {
                  String string2 = (String)arrayList.get(i6);
                  int i7 = 0;
                  if (!arrayList2.contains(string2)) {
                     for (Object object : arrayList) {
                        String string3 = (String)object;
                        if (string3.equals(string2)) {
                           i7++;
                           arrayList2.add(string3);
                        }
                     }

                     if (i7 > 1) {
                        string2 = string2 + "(x" + i7 + ")";
                     }

                     float f5 = client.getFontRenderer().getStringWidth(string2) / 2 + 4;
                     RenderUtil.drawRect(f3 - f5, f4 - i5 * 11 - 17.0F, f3 + f5, f4 - 11 * i5 - 6.0F, -872415232);
                     client.getFontRenderer().drawStringWithShadow(string2, f3 - f5 + 4.0F, f4 - i5 * 11 - 15.0F, -1);
                     i5++;
                  }
               }

               GL11.glPopMatrix();
            }
         }
      }
   }

   private void drawEntity(RenderEvent renderEvent, Entity entity, float f, float g, float h, int i, int j) {
      float[] floats = this.project(f, g, h);
      if (floats != null) {
         if (!(floats[2] > 1.0F) && this.isOnScreen(floats[0], floats[1])) {
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(
               f - entity.width + 0.2,
               g,
               h - entity.width + 0.2,
               f + entity.width - 0.2,
               g + entity.height + (entity.isSneaking() ? 0.02 : 0.2),
               h + entity.width - 0.2
            );
            if (entity instanceof EntityHorse) {
               axisAlignedBB = AxisAlignedBB.fromBounds(
                  f - entity.width + 0.6, g + 0.1, h - entity.width + 0.2, f + entity.width - 0.6, g + entity.height + 0.6, h + entity.width - 0.2
               );
            } else if (entity instanceof EntityCreeper) {
               axisAlignedBB = AxisAlignedBB.fromBounds(
                  f - entity.width + 0.2, g, h - entity.width + 0.2, f + entity.width - 0.2, g + entity.height - 0.1, h + entity.width - 0.2
               );
            } else if (entity instanceof EntityItem) {
               axisAlignedBB = AxisAlignedBB.fromBounds(
                  f - entity.width + 0.2, g + 0.2, h - entity.width + 0.05, f + entity.width - 0.1, g + entity.height + 0.3, h + entity.width - 0.1
               );
            }

            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int m = Integer.MAX_VALUE;
            int n = Integer.MIN_VALUE;
            float[][] floatss = new float[][]{
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ}
            };

            for (int i2 = 0; i2 < 8; i2++) {
               float[] floats2 = this.project(floatss[i2][0], floatss[i2][1], floatss[i2][2]);
               if (floats2 != null) {
                  k = Math.min(k, (int)floats2[0]);
                  l = Math.max(l, (int)floats2[0]);
                  m = Math.min(m, (int)floats2[1]);
                  n = Math.max(n, (int)floats2[1]);
               }
            }

            if (k != Integer.MAX_VALUE && l != Integer.MIN_VALUE && m != Integer.MAX_VALUE && n != Integer.MIN_VALUE) {
               GlStateManager.pushMatrix();
               int i3 = renderEvent.getResolution().getScaleFactor();
               double d = i3 / Math.pow(i3, 2.0);
               GlStateManager.scale(d, d, d);
               int i4 = (k + l) / 2;
               if (this.box.getSetting("Enable").asBoolean()) {
                  this.drawBox(k, l, m, n, this.getBoxColor(entity));
               }

               if (entity instanceof EntityLivingBase) {
                  EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                  if (!Double.isNaN(entityLivingBase.getHealth()) && this.healthBar.getSetting("Enable").asBoolean()) {
                     Float float2 = this.displayedHealth.get(entityLivingBase.getEntityId());
                     if (float2 != null && this.healthBar.getSetting("Animated").asBoolean()) {
                        if (float2 > entityLivingBase.getHealth()) {
                           float2 = float2 - this.healthBar.getSetting("Speed").asFloat() / Minecraft.getDebugFPS();
                        } else {
                           float2 = entityLivingBase.getHealth();
                        }
                     } else {
                        float2 = entityLivingBase.getHealth();
                     }

                     this.displayedHealth.put(entityLivingBase.getEntityId(), float2);
                     int i5 = n + 1;
                     if (float2.floatValue() != 0.0) {
                        i5 = (int)(n + (m - n) * (float2 / entityLivingBase.getMaxHealth() * 100.0F * 0.01F));
                     }

                     switch ((ESPHealthBarColorMode)this.healthBar.getSetting("Color-Mode").getValue()) {
                        case NORMAL:
                           RenderUtil.drawRect(k - 6, m - 1, k - 2, n + 2, this.outlineColor);
                           RenderUtil.drawRect(k - 5, i5, k - 3, n + 1, GameUtil.getHealthColorRGB(entityLivingBase));
                           break;
                        case GRADIENT:
                           Color color2 = (Color)this.healthBar.getSetting("Bottom-Color").getValue();
                           int i6 = color2.darker().darker().getRGB();
                           int i7 = color2.darker().darker().darker().getRGB();
                           Color color3 = (Color)this.healthBar.getSetting("Color").getValue();
                           int i8 = color3.darker().darker().getRGB();
                           int i9 = color3.darker().darker().darker().darker().getRGB();
                           RenderUtil.drawVerticalGradient(k - 6, m - 1, k - 2, n + 2, i7, i9);
                           RenderUtil.drawVerticalGradient(k - 5, m, k - 3, n + 1, i6, i8);
                           RenderUtil.drawVerticalGradient(k - 5, m, k - 3, n + 1, color2.getRGB(), color3.getRGB());
                           RenderUtil.drawRect(k - 5, m, k - 3, i5, this.healthBarShadeColor);
                           break;
                        case CUSTOM:
                           RenderUtil.drawRect(k - 6, m - 1, k - 2, n + 2, this.outlineColor);
                           RenderUtil.drawRect(k - 5, i5, k - 3, n + 1, ((ColorSetting)this.healthBar.getSetting("Color")).getValue().getRGB());
                     }

                     if (entityLivingBase.getAbsorptionAmount() > 0.0F) {
                        int i10 = (int)(n + (m - n) * (entityLivingBase.getAbsorptionAmount() / entityLivingBase.getMaxHealth() * 100.0F * 0.01F));
                        if (i10 > n) {
                           i10 = m;
                        }

                        Gui.drawRect(k - 5, n + 1, k - 3, i10, Color.YELLOW.getRGB());
                     }

                     if (this.hPos.getValue().equals(ESPHPosMode.HEALTH_BAR) && entityLivingBase.getHealth() != entityLivingBase.getMaxHealth()) {
                        String string = this.health.getValue() == ESPHealthMode.HEARTS || this.health.getValue() == ESPHealthMode.HEALTH
                           ? MathUtil.ONE_DECIMAL.format(this.getHealthValue(entityLivingBase)) + "§c❤"
                           : (
                              this.health.getValue().equals(ESPHealthMode.PERCENTAGE)
                                 ? MathUtil.ONE_DECIMAL.format(this.getHealthValue(entityLivingBase)) + "%"
                                 : String.valueOf(entityLivingBase.getHealth())
                           );
                        client.getFontRenderer()
                           .O(
                              string,
                              (k - 4 + k - 2) / 2 - client.getFontRenderer().getStringWidth(string) - 4,
                              i5 - client.getFontRenderer().FONT_HEIGHT + 9,
                              Color.WHITE.getRGB()
                           );
                     }
                  }

                  if (this.clickESP.getSetting("Enable").asBoolean()
                     && entityLivingBase instanceof EntityPlayer
                     && Mouse.isButtonDown(this.clickESP.getSetting("Right-Click").asBoolean() ? 1 : 0)
                     && this.menuTarget == null) {
                     float f2 = Display.getWidth() / 2.0F;
                     float f3 = Display.getHeight() / 2.0F;
                     float f4 = f2 - k;
                     float f5 = f2 - l;
                     float f6 = f3 - m;
                     float f7 = f3 - n;
                     if (Keyboard.isKeyDown(29)
                        && (
                           this.isInside(k, m, l, n, (int)(i / d), (int)(j / d))
                              || f4 > 0.0F && f5 < 0.0F && f6 > 0.0F && f7 < 0.0F
                        )) {
                        this.menuTarget = entityLivingBase;
                     }
                  }

                  boolean bl = this.nameTags.getSetting("CFont").asBoolean() && !this.nameTags.getSetting("Outline").asBoolean();
                  float f8 = this.nameTags.getSetting("Scale").asFloat() + (bl ? 0.5F : 0.0F);
                  int i11 = Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F);
                  GlStateManager.popMatrix();
                  GlStateManager.pushMatrix();
                  GlStateManager.scale(d * f8, d * f8, d * f8);
                  float f9 = i4 / f8;
                  float f10 = m / f8;
                  if (entityLivingBase instanceof EntityPlayer) {
                     boolean bl2 = false;
                     if (this.nameTags.getSetting("Armor").asBoolean()) {
                        EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
                        ArrayList arrayList = new ArrayList();

                        for (int i12 = 4; i12 >= 0; i12--) {
                           ItemStack itemStack = i12 == 4 ? entityLivingBase.getHeldItem() : entityPlayer.inventory.armorInventory[i12];
                           if (itemStack != null) {
                              arrayList.add(itemStack);
                           }
                        }

                        float f11 = f9;
                        String string2 = Denicker.nicks.get(entity.getName());
                        float f12 = f10 - 34.0F - (string2 == null ? 0 : 12);

                        for (Object object : arrayList) {
                           ItemStack itemStack2 = (ItemStack)object;
                           if (itemStack2 != null) {
                              int i13 = 9 * arrayList.size();
                              GlStateManager.clear(256);
                              GlStateManager.disableDepth();
                              GlStateManager.disableTexture2D();
                              GlStateManager.enableTexture2D();
                              GlStateManager.enableDepth();
                              float f13 = 1.0F;
                              GlStateManager.scale(f13, f13, 0.0F);
                              float f14 = -i13 + f11;
                              mc.getRenderItem().K(itemStack2, f14 / f13, f12 / f13);
                              mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack2, (int)(f14 / f13), (int)(f12 / f13));
                              GlStateManager.scale(1.0F / f13, 1.0F / f13, 0.0F);
                              GlStateManager.disableLighting();
                              this.drawEnchantments(itemStack2, -i13 + f11 + 7.0F, f10 - 34.0F);
                              bl2 = true;
                              f11 += 16.0F;
                           }
                        }
                     }

                     if (this.nameTags.getSetting("Potions").asBoolean()) {
                        EntityPlayer entityPlayer2 = (EntityPlayer)entityLivingBase;
                        List list = HypixelGameTracker.getEffects(entityPlayer2);
                        if (!list.isEmpty()) {
                           float f15 = 0.0F;

                           for (Object object2 : list) {
                              PotionEffect potionEffect = (PotionEffect)object2;
                              Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                              byte b = 18;
                              boolean bl3 = false;
                              short s2 = 198;
                              byte b2 = 8;
                              GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                              mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
                              int i14 = potion.getStatusIconIndex();
                              float f16 = 1.0F;
                              String string3 = Denicker.nicks.get(entity.getName());
                              GlStateManager.scale(f16, f16, 0.0F);
                              mc.ingameGUI
                                 .drawTexturedModalRect(
                                    (float)(f9 + f15 - list.size() * 18 / 2.0F),
                                    (float)(f10 - (bl2 ? 52 + (string3 == null ? 0 : 12) : 34)),
                                    0 + i14 % 8 * 18,
                                    198 + i14 / 8 * 18,
                                    18,
                                    18
                                 );
                              GlStateManager.disableLighting();
                              GlStateManager.scale(1.0F / f16, 1.0F / f16, 0.0F);
                              f15 += 18.0F;
                           }
                        }
                     }
                  }

                  if (this.nameTags.getSetting("Enable").asBoolean()) {
                     AstolfoUser astolfoUser = client.getUserManager().getUser(entity.getName());
                     int i15 = astolfoUser == null
                        ? ((ColorSetting)this.nameTags.getSetting("Color")).getValue().getRGB()
                        : (astolfoUser.status == AstolfoUserStatus.C ? i11 : ((ColorSetting)this.groupColor.getSetting("Astolfo")).getValue().getRGB());
                     Relation relation = client.getRelationManager().getRelationMap().get(entity.getName());
                     String string4 = Denicker.nicks.get(entity.getName());
                     String string5 = string4 != null
                        ? string4
                        : (
                           relation != null
                              ? (relation.isFriend() ? "§3" : "§4") + relation.getNickname()
                              : (
                                 astolfoUser == null
                                    ? (
                                       this.nameTags.getSetting("Mode").getValue() == ESPNameMode.NAME
                                          ? entity.getName()
                                          : entity.getDisplayName().getUnformattedTextForChat()
                                    )
                                    : astolfoUser.clientName
                              )
                        );
                     if (this.hPos.getValue().equals(ESPHPosMode.STANDARD)) {
                        String string6 = this.health.getValue().equals(ESPHealthMode.HEARTS)
                           ? String.format(
                              "§7[§f%s§c❤§7]", Double.isNaN(this.getHealthValue(entityLivingBase)) ? "NaN" : MathUtil.ONE_DECIMAL.format(this.getHealthValue(entityLivingBase))
                           )
                           : String.format(
                              "§7[§f%s%%§7]", Double.isNaN(this.getHealthValue(entityLivingBase)) ? "NaN" : MathUtil.ONE_DECIMAL.format(this.getHealthValue(entityLivingBase))
                           );
                        this.drawNameTag(string4 == null ? "" : entity.getName(), string5 + " " + string6, f9, f10, i15);
                     } else {
                        this.drawNameTag(string4 == null ? "" : entity.getName(), string5, f9, f10, i15);
                     }
                  }

                  GlStateManager.popMatrix();
                  GlStateManager.pushMatrix();
                  float f17 = 1.0F + (bl ? 0.35F : 0.0F);
                  GlStateManager.scale(d * f17, d * f17, d * f17);
                  float f18 = i4 / f17;
                  float f19 = n / f17;
                  if (entityLivingBase instanceof EntityPlayer && entityLivingBase.getHeldItem() != null && this.itemName.getValue()) {
                     if (this.pitItemPerks.getValue() && GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && entityLivingBase.getHeldItem().V() != null) {
                        byte b3 = 32;
                        if (entityLivingBase.getHeldItem().V().hasKey("display", 10)) {
                           NBTTagCompound nbtTagCompound = entityLivingBase.getHeldItem().V().getCompoundTag("display");
                           NBTTagList nbtTagList = nbtTagCompound.getTagList("Lore", 8);
                           if (nbtTagList.tagCount() > 0) {
                              for (int i16 = 0; i16 < nbtTagList.tagCount(); i16++) {
                                 String string7 = nbtTagList.getStringTagAt(i16);
                                 if (!string7.contains("Unbreakable")
                                    && !string7.contains("Attack Damage")
                                    && (string7.startsWith("§dRARE!") || string7.startsWith("§9"))) {
                                    this.drawNameTag("", nbtTagList.getStringTagAt(i16), f18, f19 + b3, -1);
                                    b3 += 12;
                                 }
                              }
                           }
                        }
                     }

                     this.drawNameTag(
                        "",
                        entityLivingBase.getHeldItem().getDisplayName() + (entityLivingBase.getHeldItem().stackSize > 1 ? " §7x" + entityLivingBase.getHeldItem().stackSize : ""),
                        f18,
                        f19 + 20.0F,
                        -1
                     );
                  }
               }

               GlStateManager.popMatrix();
            }
         } else {
            if (entity instanceof EntityLivingBase && this.offScreen.getSetting("Enable").asBoolean()) {
               this.drawOffScreenArrow(renderEvent, (EntityLivingBase)entity, floats[0], floats[1], floats[2]);
            }
         }
      }
   }

   private int drawEnchantments(ItemStack itemStack, float f, float g) {
      GlStateManager.disableDepth();
      int i = 0;
      if (itemStack.isItemEnchanted()) {
         NBTTagList nbtTagList = itemStack.getEnchantmentTagList();
         byte b = -4;

         for (int j = 0; j < Objects.requireNonNull(nbtTagList).tagCount(); j++) {
            short s = nbtTagList.getCompoundTagAt(j).getShort("id");
            short s2 = nbtTagList.getCompoundTagAt(j).getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantmentById(s);
            if ((
                  enchantment == Enchantment.protection
                     || enchantment == Enchantment.sharpness
                     || enchantment == Enchantment.unbreaking
                     || enchantment == Enchantment.fireAspect
                     || enchantment == Enchantment.efficiency
                     || enchantment == Enchantment.flame
                     || enchantment == Enchantment.knockback
               )
               && (!GameUtil.isOnHypixel() || enchantment != Enchantment.unbreaking)) {
               String string = enchantment.getTranslatedName(s2).substring(0, 2).trim() + s2;
               string = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
               this.drawScaledString(string, f, g + b, 0.5F);
               b -= 8;
               i++;
            }
         }
      }

      GlStateManager.disableLighting();
      return i;
   }

   private void drawScaledString(String string, float f, float g, float h) {
      f *= 1.0F / h;
      g *= 1.0F / h;
      GlStateManager.scale(h, h, 0.0F);
      f -= 6.0F;
      g -= 3.0F;
      mc.fontRendererObj.drawString(string, f + 0.7F, g + 0.7F, 0, false);
      mc.fontRendererObj.drawString(string, f, g, -1, false);
      GlStateManager.scale(1.0F / h, 1.0F / h, 0.0F);
   }

   private void drawOffScreenArrow(RenderEvent renderEvent, EntityLivingBase entityLivingBase, float f, float g, float h) {
      float f2 = renderEvent.getResolution().getScaledWidth() / 2.0F;
      float f3 = renderEvent.getResolution().getScaledHeight() / 2.0F;
      double d = f / renderEvent.getResolution().getScaleFactor();
      double e = g / renderEvent.getResolution().getScaleFactor();
      double d2 = f3 * this.offScreen.getSetting("Radius").asFloat();
      float f4 = this.angleTo(f2, d, f3, e) + (h > 1.0 ? 180 : 0);
      double d3 = d2 * Math.cos(Math.toRadians(f4));
      double d4 = d2 * Math.sin(Math.toRadians(f4));
      GL11.glPushMatrix();
      GL11.glTranslated(d3 + f2, d4 + f3, 0.0);
      GL11.glRotatef(f4 - 90.0F, 0.0F, 0.0F, 1.0F);
      float f5 = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 3000.0F / 3000.0F * (float) Math.PI * 2.0F) * 0.6F);
      Color color = client.getRelationManager().isFriend(entityLivingBase)
         ? Color.BLUE.brighter()
         : ((ColorSetting)this.offScreen.getSetting("Color")).getValue();
      RenderUtil.drawArrow(
         0.0,
         0.0,
         this.offScreen.getSetting("Scale").asInt(),
         false,
         this.offScreen.getSetting("Pulsing").asBoolean()
            ? new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp_int((int)(color.getAlpha() * f5), 0, 255)).getRGB()
            : color.getRGB()
      );
      GL11.glPopMatrix();
   }

   private float angleTo(double d, double e, double d2, double d3) {
      return (float)Math.toDegrees(Math.atan2(d3 - d2, e - d));
   }

   private boolean isInside(int i, int j, int k, int l, int m, int n) {
      return m >= i && m <= k && n >= j && n <= l;
   }

   private void drawNameTag(String string, String string2, float f, float g, int i) {
      if (string != null && !string.isEmpty()) {
         i = -204212;
      }

      boolean bl = this.nameTags.getSetting("Background").asBoolean();
      ESPNameBarMode espNameBarMode = (ESPNameBarMode)this.nameTags.getSetting("Bar").getValue();
      boolean bl2 = this.nameTags.getSetting("CFont").asBoolean() && !this.nameTags.getSetting("Outline").asBoolean();
      int j = client.getFontRenderer().getStringWidth(string2) / 2;
      int k = client.getFontRenderer().getStringWidth(string) / 2;
      if (bl2) {
         j = client.getCustomFontRenderer().getStringWidth(string2) / 2;
         k = client.getCustomFontRenderer().getStringWidth(string) / 2;
      }

      if (bl) {
         RenderUtil.drawRect(f - j - 3.0F, g - 15.5F, f + j + 3.0F, g - 3.0F, this.nameBackgroundColor);
      }

      switch (espNameBarMode) {
         case UP:
            RenderUtil.drawRect(f - j - 3.0F, g - 15.5F, f + j + 3.0F, g - 14.5F, i);
            break;
         case DOWN:
            RenderUtil.drawRect(f - j - 3.0F, g - 4.0F, f + j + 3.0F, g - 3.0F, i);
      }

      if (this.nameTags.getSetting("Outline").asBoolean()) {
         client.getFontRenderer().O(string2, (int)(f - j), (int)(g - client.getFontRenderer().FONT_HEIGHT - 4.0F), i);
      } else if (bl2) {
         client.getCustomFontRenderer()
            .drawStringWithShadow(string2, f - j, g - client.getCustomFontRenderer().getGlyphPage().getMaxGlyphHeight() / 2.0F - 3.0F, i);
      } else {
         client.getFontRenderer().drawStringWithShadow(string2, f - j, g - client.getFontRenderer().FONT_HEIGHT - 4.0F, i);
      }

      if (string != null) {
         if (bl2) {
            client.getCustomFontRenderer()
               .drawStringWithShadow("§7" + string, f - k, g - client.getCustomFontRenderer().getGlyphPage().getMaxGlyphHeight() - 16.0F, -1);
         } else {
            client.getFontRenderer().drawStringWithShadow("§7" + string, f - k, g - client.getFontRenderer().FONT_HEIGHT - 16.0F, -1);
         }
      }
   }

   public static void renderItem(ItemStack itemStack, int i, int j) {
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
      mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, i, j);
      mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack, i, j);
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

   private void drawTileEntity(RenderEvent renderEvent, TileEntity tileEntity, double d, double e, double d2) {
      float[] floats = this.project((float)d, (float)e, (float)d2);
      if (floats != null && !(floats[2] > 1.0F)) {
         if (this.isOnScreen(floats[0], floats[1])) {
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(d, e, d2, d + 1.0, e + 1.0, d2 + 1.0);
            if (tileEntity instanceof TileEntityChest) {
               TileEntityChest tileEntityChest = (TileEntityChest)tileEntity;
               if (tileEntityChest.adjacentChestZNeg != null) {
                  axisAlignedBB = AxisAlignedBB.fromBounds(d, e, d2 + 1.0, d + 0.9375, e + 0.875, d2 - 1.0);
               } else if (tileEntityChest.adjacentChestXPos != null) {
                  axisAlignedBB = AxisAlignedBB.fromBounds(d + 0.0625, e, d2 + 0.0625, d + 1.9375, e + 0.875, d2 + 0.9375);
               } else {
                  if (tileEntityChest.adjacentChestZNeg != null || tileEntityChest.adjacentChestXPos != null || tileEntityChest.adjacentChestXNeg != null || tileEntityChest.adjacentChestZPos != null) {
                     return;
                  }

                  axisAlignedBB = AxisAlignedBB.fromBounds(d + 0.0625, e, d2 + 0.0625, d + 0.9375, e + 0.875, d2 + 0.9375);
               }
            } else if (tileEntity instanceof TileEntityEnderChest) {
               axisAlignedBB = AxisAlignedBB.fromBounds(d + 0.0625, e, d2 + 0.0625, d + 0.9375, e + 0.875, d2 + 0.9375);
            }

            int i = Integer.MAX_VALUE;
            int j = Integer.MIN_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            float[][] floatss = new float[][]{
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.minZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.maxY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.minX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ},
               {(float)axisAlignedBB.maxX, (float)axisAlignedBB.minY, (float)axisAlignedBB.maxZ}
            };

            for (int m = 0; m < 8; m++) {
               float[] floats2 = this.project(floatss[m][0], floatss[m][1], floatss[m][2]);
               if (floats2 != null) {
                  i = Math.min(i, (int)floats2[0]);
                  j = Math.max(j, (int)floats2[0]);
                  k = Math.min(k, (int)floats2[1]);
                  l = Math.max(l, (int)floats2[1]);
               }
            }

            if (i != Integer.MAX_VALUE && j != Integer.MIN_VALUE && k != Integer.MAX_VALUE && l != Integer.MIN_VALUE) {
               int n = this.getTileEntityColor(tileEntity);
               double d3 = renderEvent.getResolution().getScaleFactor();
               GlStateManager.scale(1.0 / d3, 1.0 / d3, 1.0 / d3);
               if (this.box.getSetting("Enable").asBoolean()) {
                  this.drawBox(i, j, k, l, n);
               }

               GlStateManager.scale(d3, d3, d3);
            }
         }
      }
   }

   public static Color pulsingColor(long l, float f) {
      float g = 1.0F - MathHelper.abs(MathHelper.sin((float)l % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long m = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(g, 0.5F, 1.0F)), 16);
      Color color = new Color((int)m);
      return new Color(color.getRed() / 255.0F * f, color.getGreen() / 255.0F * f, color.getBlue() / 255.0F * f, color.getAlpha() / 255.0F);
   }

   private void drawBox(int i, int j, int k, int l, int m) {
      ESPBoxMode espBoxMode = (ESPBoxMode)this.box.getSetting("Mode").getValue();
      boolean bl = this.box.getSetting("Outline").asBoolean();
      switch (espBoxMode) {
         case FULL:
            if (bl) {
               Gui.v(i - 1.0, j + 1.0, k - 1.0, k + 2.0, this.outlineColor);
            }

            Gui.S(i, j, k, m);
            if (bl) {
               Gui.v(i - 1.0, j + 1.0, l - 1.0, l + 2.0, this.outlineColor);
            }

            Gui.S(i, j, l, m);
            if (bl) {
               Gui.f(i - 1.0, i + 2.0, l - 1.0, k + 1.0, this.outlineColor);
            }

            Gui.J(i, l, k, m);
            if (bl) {
               Gui.f(j - 1.0, j + 2.0, l - 1.0, k + 1.0, this.outlineColor);
            }

            Gui.J(j, l, k, m);
            break;
         case RAINBOW:
            long n = Minecraft.getSystemTime();
            RenderUtil.drawRect(i - 1, k - 1, j + 2, k + 2, this.outlineColor);
            RenderUtil.drawVerticalGradient(i, k, j, k + 1, pulsingColor(n, 1.0F).getRGB(), pulsingColor(n, 1.0F).getRGB());
            RenderUtil.drawRect(i - 1, l - 1, j + 2, l + 2, this.outlineColor);
            RenderUtil.drawVerticalGradient(i, l, j + 1, l + 1, pulsingColor(n - 1600L, 1.0F).getRGB(), pulsingColor(n - 1600L, 1.0F).getRGB());
            RenderUtil.drawRect(i - 1, k + 1, i + 2, l - 1, this.outlineColor);
            RenderUtil.drawVerticalGradient(i, k, i + 1, l, pulsingColor(n - 1600L, 1.0F).getRGB(), pulsingColor(n, 1.0F).getRGB());
            RenderUtil.drawRect(j - 1, k + 1, j + 2, l, this.outlineColor);
            RenderUtil.drawVerticalGradient(j, k, j + 1, l + 1, pulsingColor(n - 1600L, 1.0F).getRGB(), pulsingColor(n, 1.0F).getRGB());
            break;
         case CUSTOM:
            int i2 = (int)this.box.getSetting("Width").asFloat();
            int i3 = (int)this.box.getSetting("Height").asFloat();
            int i4 = (j - i) / i2;
            int i5 = (i - j) / i2;
            int i6 = (l - k) / i3;
            int i7 = (k - l) / i3;
            if (i2 != 1) {
               this.drawHorizontalCorners(i, j, k, m, bl, i4, i5);
            } else {
               if (bl) {
                  Gui.v(i - 1.0, j + 1.0, k - 1.0, k + 2.0, this.outlineColor);
               }

               Gui.S(i, j, k, m);
               if (bl) {
                  Gui.v(i - 1.0, j + 1.0, l - 1.0, l + 2.0, this.outlineColor);
               }

               Gui.S(i, j, l, m);
            }

            if (i3 != 1) {
               if (bl) {
                  Gui.f(i - 1.0, i + 2.0, k + 1, k + i6 + 1, this.outlineColor);
               }

               Gui.J(i, k, k + i6, m);
               if (bl) {
                  Gui.f(j - 1.0, j + 2.0, k + 1, k + i6 + 1, this.outlineColor);
               }

               Gui.J(j, k, k + i6, m);
            } else {
               if (bl) {
                  Gui.f(i - 1.0, i + 2.0, l - 1.0, k + 1.0, this.outlineColor);
               }

               Gui.J(i, l, k, m);
               if (bl) {
                  Gui.f(j - 1.0, j + 2.0, l - 1.0, k + 1.0, this.outlineColor);
               }

               Gui.J(j, l, k, m);
            }

            if (i2 != 1) {
               this.drawHorizontalCorners(i, j, l, m, bl, i4, i5);
            }

            if (i3 != 1) {
               if (bl) {
                  Gui.f(i - 1.0, i + 2.0, l - 1, l + i7, this.outlineColor);
               }

               Gui.J(i, l, l + i7, m);
               if (bl) {
                  Gui.f(j - 1.0, j + 2.0, l - 1, l + i7, this.outlineColor);
               }

               Gui.J(j, l, l + i7, m);
            }
            break;
         case TRANS:
            int i8 = (l - k) / 3;
            int i9 = (k - l) / 3;
            if (bl) {
               Gui.v(i - 1.0, j + 1.0, k - 1.0, k + 2.0, this.outlineColor);
            }

            Gui.S(i, j, k, HUDFlagColor.BLUE.getColor().getRGB());
            if (bl) {
               Gui.f(i - 1.0, i + 2.0, l - 1.0, k + 1.0, this.outlineColor);
            }

            Gui.J(i, l, k, Color.WHITE.getRGB());
            if (bl) {
               Gui.f(j - 1.0, j + 2.0, l - 1.0, k + 1.0, this.outlineColor);
            }

            Gui.J(j, l, k, Color.WHITE.getRGB());
            Gui.J(i, k, k + i8, HUDFlagColor.PINK.getColor().getRGB());
            Gui.J(j, k, k + i8, HUDFlagColor.PINK.getColor().getRGB());
            if (bl) {
               Gui.v(i - 1.0, j + 1.0, l - 1.0, l + 2.0, this.outlineColor);
            }

            Gui.S(i, j, l, HUDFlagColor.BLUE.getColor().getRGB());
            Gui.J(i, l, l + i9, HUDFlagColor.PINK.getColor().getRGB());
            Gui.J(j, l, l + i9, HUDFlagColor.PINK.getColor().getRGB());
      }
   }

   private void drawHorizontalCorners(int i, int j, int k, int l, boolean bl, int m, int n) {
      if (bl) {
         Gui.v(i - 1, i + m + 1.0, k - 1.0, k + 2.0, this.outlineColor);
      }

      Gui.S(i, i + m, k, l);
      if (bl) {
         Gui.v(j + 1, j + n - 1.0, k - 1.0, k + 2.0, this.outlineColor);
      }

      Gui.S(j, j + n, k, l);
   }

   private int getTileEntityColor(TileEntity tileEntity) {
      if (tileEntity instanceof TileEntityChest) {
         return ((TileEntityChest)tileEntity).getChestType() == 1 ? this.trappedChestColor : this.chestColor;
      } else if (tileEntity instanceof TileEntityEnderChest) {
         return this.enderChestColor;
      } else {
         return tileEntity instanceof TileEntityHopper ? this.hopperColor : Color.WHITE.getRGB();
      }
   }

   private double getHealthValue(EntityLivingBase entityLivingBase) {
      switch ((ESPHealthMode)this.health.getValue()) {
         case HEARTS:
            return entityLivingBase.getHealth() / 2.0F;
         case PERCENTAGE:
            return entityLivingBase.getHealth() * 100.0F / entityLivingBase.getMaxHealth();
         case HEALTH:
            return entityLivingBase.getHealth();
         default:
            return 0.0;
      }
   }

   private boolean shouldRender(Entity entity) {
      if ((!(entity instanceof EntityPlayer) || entity == mc.thePlayer)
         && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())) {
         return GameUtil.isHostile(entity) && this.entities.getSetting("Hostiles").asBoolean()
            || GameUtil.isNeutral(entity) && this.entities.getSetting("Neutrals").asBoolean()
            || GameUtil.isPassive(entity) && this.entities.getSetting("Passives").asBoolean()
            || GameUtil.isBoss(entity) && this.entities.getSetting("Bosses").asBoolean()
            || GameUtil.isVehicle(entity) && this.entities.getSetting("Vehicles").asBoolean()
            || GameUtil.isBoss(entity) && this.entities.getSetting("Bosses").asBoolean()
            || GameUtil.isObject(entity) && this.entities.getSetting("Others").asBoolean();
      }

      if (GameUtil.isFullyInvisible(entity) && !this.entities.getSetting("Invisibles").asBoolean()) {
         return false;
      }

      if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && AutoHypixel.pitSpawnY != 0.0 && entity.posY > AutoHypixel.pitSpawnY) {
         return false;
      }

      if (GameUtil.isOnSameTeam(mc.thePlayer, (EntityLivingBase)entity) && this.teams.getValue()) {
         AstolfoUser astolfoUser = client.getUserManager().getUser(entity.getName());
         if (astolfoUser == null) {
            return false;
         }
      }

      return BotDetector.bots.contains(entity) && !this.entities.getSetting("Bots").asBoolean() ? false : this.entities.getSetting("Players").asBoolean();
   }

   private boolean isHighlightedItem(EntityItem entityItem) {
      return entityItem.getEntityItem() != null
         && HypixelGameTracker.game == 3
         && (
            entityItem.getEntityItem().getDisplayName() != null
                  && OldInventoryManager.uhcItemNames.contains(StringUtils.stripControlCodes(entityItem.getEntityItem().getDisplayName()))
               || entityItem.getEntityItem().getItem() instanceof ItemSkull && OldInventoryManager.isHead(entityItem.getEntityItem())
               || entityItem.getEntityItem().getItem() instanceof ItemAppleGold
         );
   }

   private boolean shouldRenderTileEntity(TileEntity tileEntity) {
      return tileEntity instanceof TileEntityEnderChest && this.blocks.getSetting("Ender Chest").asBoolean()
         || tileEntity instanceof TileEntityChest && this.blocks.getSetting("Chest").asBoolean()
         || tileEntity instanceof TileEntityHopper && this.blocks.getSetting("Hopper").asBoolean();
   }

   private int getBoxColor(Entity entity) {
      AstolfoUser astolfoUser = client.getUserManager().getUser(entity.getName());
      if (astolfoUser != null) {
         return astolfoUser.status == AstolfoUserStatus.C
            ? Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F)
            : ((ColorSetting)this.groupColor.getSetting("Astolfo")).getValue().getRGB();
      } else if (client.getRelationManager().isFriend(entity)) {
         return ((ColorSetting)this.groupColor.getSetting("Friends")).getValue().getRGB();
      } else if (Denicker.nicks.get(entity.getName()) != null) {
         return -204212;
      } else if (client.getRelationManager().isEnemy(entity)) {
         return ((ColorSetting)this.groupColor.getSetting("Enemies")).getValue().getRGB();
      } else if (BotDetector.bots.contains(entity)) {
         return this.botColor;
      } else if (entity instanceof EntityItem) {
         return this.isHighlightedItem((EntityItem)entity) ? Color.YELLOW.getRGB() : Color.WHITE.getRGB();
      } else if (AutoHypixel.bountiedPlayers.contains(entity)) {
         return Color.YELLOW.getRGB();
      } else {
         return entity == KillAura.target && this.highlight.getValue()
            ? client.getColorSetting().getValue().getRGB()
            : ((ColorSetting)this.box.getSetting("Color")).getValue().getRGB();
      }
   }

   private float[] project(float f, float g, float h) {
      ((Buffer)projected).clear();
      boolean bl = GLU.gluProject(f, g, h, modelView, projection, viewport, projected);
      return bl ? new float[]{projected.get(0), viewport.get(3) - projected.get(1), projected.get(2)} : null;
   }

   private void saveMatrices(float f) {
      GL11.glPushAttrib(4096);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      mc.entityRenderer.setupCameraTransform(f, 0);
      GlStateManager.getFloat(2983, projection);
      GlStateManager.getFloat(2982, modelView);
      GL11.glGetInteger(2978, viewport);
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
   }

   private boolean isOnScreen(double d, double e) {
      return d > 0.0 && e > 0.0 && d < mc.displayWidth && e < mc.displayHeight;
   }
}
