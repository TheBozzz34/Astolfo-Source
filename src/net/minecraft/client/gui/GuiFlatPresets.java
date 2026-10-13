package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
   private static final List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   private final GuiCreateFlatWorld parentScreen;
   private String presetsTitle;
   private String presetsShare;
   private String field_146436_r;
   private GuiFlatPresets.ListSlot field_146435_s;
   private GuiButton field_146434_t;
   private GuiTextField field_146433_u;
   private static final long d = 28396127275834L;

   public GuiFlatPresets(GuiCreateFlatWorld var1) {
      this.parentScreen = var1;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.presetsTitle = I18n.format("createWorld.customize.presets.title");
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.field_146436_r = I18n.format("createWorld.customize.presets.list");
      this.field_146433_u = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
      this.field_146435_s = new GuiFlatPresets.ListSlot();
      this.field_146433_u.setMaxStringLength(1230);
      this.field_146433_u.setText(this.parentScreen.func_146384_e());
      this.buttonList
         .add(this.field_146434_t = new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.func_146426_g();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.field_146435_s.handleMouseInput();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      this.field_146433_u.mouseClicked(var1, var2, var3);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (!this.field_146433_u.textboxKeyTyped(var1, var2)) {
         super.keyTyped(var1, var2);
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 0 && this.func_146430_p()) {
         this.parentScreen.func_146383_a(this.field_146433_u.getText());
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (var1.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.field_146435_s.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, this.presetsTitle, this.width / 2, 8, 16777215);
      this.I(this.fontRendererObj, this.presetsShare, 50, 30, 10526880);
      this.I(this.fontRendererObj, this.field_146436_r, 50, 70, 10526880);
      this.field_146433_u.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void updateScreen() {
      this.field_146433_u.updateCursorCounter();
      super.updateScreen();
   }

   public void func_146426_g() {
      boolean var1 = this.func_146430_p();
      this.field_146434_t.enabled = var1;
   }

   private boolean func_146430_p() {
      return this.field_146435_s.field_148175_k > -1 && this.field_146435_s.field_148175_k < FLAT_WORLD_PRESETS.size()
         || this.field_146433_u.getText().length() > 1;
   }

   private static void func_146425_a(String var0, Item var1, BiomeGenBase var2, FlatLayerInfo... var3) {
      func_175354_a(var0, var1, 0, var2, (List<String>)null, var3);
   }

   private static void func_146421_a(String var0, Item var1, BiomeGenBase var2, List<String> var3, FlatLayerInfo... var4) {
      func_175354_a(var0, var1, 0, var2, var3, var4);
   }

   private static void func_175354_a(String var0, Item var1, int var2, BiomeGenBase var3, List<String> var4, FlatLayerInfo... var5) {
      FlatGeneratorInfo var8 = new FlatGeneratorInfo();

      for (int var9 = var5.length - 1; var9 >= 0; var9--) {
         var8.getFlatLayers().add(var5[var9]);
      }

      var8.setBiome(var3.biomeID);
      var8.func_82645_d();
      if (var4 != null) {
         for (String var10 : var4) {
            var8.getWorldFeatures().put(var10, Maps.newHashMap());
         }
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(var1, var2, var0, var8.toString()));
   }

   static {
      func_146421_a(
         "Classic Flat",
         Item.getItemFromBlock(Blocks.grass),
         BiomeGenBase.plains,
         Arrays.asList("village"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(2, Blocks.dirt),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_146421_a(
         "Tunnelers' Dream",
         Item.getItemFromBlock(Blocks.stone),
         BiomeGenBase.extremeHills,
         Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(230, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_146421_a(
         "Water World",
         Items.water_bucket,
         BiomeGenBase.deepOcean,
         Arrays.asList("biome_1", "oceanmonument"),
         new FlatLayerInfo(90, Blocks.water),
         new FlatLayerInfo(5, Blocks.sand),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(5, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_175354_a(
         "Overworld",
         Item.getItemFromBlock(Blocks.tallgrass),
         BlockTallGrass.EnumType.GRASS.getMeta(),
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_146421_a(
         "Snowy Kingdom",
         Item.getItemFromBlock(Blocks.snow_layer),
         BiomeGenBase.icePlains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.snow_layer),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_146421_a(
         "Bottomless Pit",
         Items.feather,
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(2, Blocks.cobblestone)
      );
      func_146421_a(
         "Desert",
         Item.getItemFromBlock(Blocks.sand),
         BiomeGenBase.desert,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
         new FlatLayerInfo(8, Blocks.sand),
         new FlatLayerInfo(52, Blocks.sandstone),
         new FlatLayerInfo(3, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      func_146425_a(
         "Redstone Ready",
         Items.redstone,
         BiomeGenBase.desert,
         new FlatLayerInfo(52, Blocks.sandstone),
         new FlatLayerInfo(3, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
   }

   public static class LayerItem {
      public Item field_148234_a;
      public int field_179037_b;
      public String field_148232_b;
      public String field_148233_c;

      public LayerItem(Item var1, int var2, String var3, String var4) {
         this.field_148234_a = var1;
         this.field_179037_b = var2;
         this.field_148232_b = var3;
         this.field_148233_c = var4;
      }
   }

   public class ListSlot extends GuiSlot {
      public int field_148175_k = -1;
      private static final long c = 637629496036L;

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }

      private void func_178054_a(int var1, int var2, Item var3, int var4) {
         this.func_148173_e(var1 + 1, var2 + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(var3, 1, var4), var1 + 2, var2 + 2);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }

      private void func_148173_e(int var1, int var2) {
         this.func_148171_c(var1, var2, 0, 0);
      }

      private void func_148171_c(int var1, int var2, int var3, int var4) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator var11 = Tessellator.getInstance();
         WorldRenderer var12 = var11.getWorldRenderer();
         var12.begin(7, DefaultVertexFormats.POSITION_TEX);
         var12.pos(var1 + 0, var2 + 18, GuiFlatPresets.this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
         var12.pos(var1 + 18, var2 + 18, GuiFlatPresets.this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
         var12.pos(var1 + 18, var2 + 0, GuiFlatPresets.this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
         var12.pos(var1 + 0, var2 + 0, GuiFlatPresets.this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
         var11.draw();
      }

      @Override
      public int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         this.field_148175_k = var1;
         GuiFlatPresets.this.func_146426_g();
         GuiFlatPresets.this.field_146433_u.setText(GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.field_146435_s.field_148175_k).field_148233_c);
      }

      @Override
      public boolean isSelected(int var1) {
         return var1 == this.field_148175_k;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         GuiFlatPresets.LayerItem var9 = GuiFlatPresets.FLAT_WORLD_PRESETS.get(var1);
         this.func_178054_a(var2, var3, var9.field_148234_a, var9.field_179037_b);
         GuiFlatPresets.this.fontRendererObj.D(var9.field_148232_b, var2 + 18 + 5, var3 + 6, 16777215);
      }
   }
}
