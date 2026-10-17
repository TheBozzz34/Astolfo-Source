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

   public GuiFlatPresets(GuiCreateFlatWorld guiCreateFlatWorld) {
      this.parentScreen = guiCreateFlatWorld;
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
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.field_146433_u.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.field_146433_u.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0 && this.func_146430_p()) {
         this.parentScreen.func_146383_a(this.field_146433_u.getText());
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.field_146435_s.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.presetsTitle, this.width / 2, 8, 16777215);
      this.drawString(this.fontRendererObj, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.fontRendererObj, this.field_146436_r, 50, 70, 10526880);
      this.field_146433_u.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      this.field_146433_u.updateCursorCounter();
      super.updateScreen();
   }

   public void func_146426_g() {
      boolean bl = this.func_146430_p();
      this.field_146434_t.enabled = bl;
   }

   private boolean func_146430_p() {
      return this.field_146435_s.field_148175_k > -1 && this.field_146435_s.field_148175_k < FLAT_WORLD_PRESETS.size()
         || this.field_146433_u.getText().length() > 1;
   }

   private static void func_146425_a(String string, Item item, BiomeGenBase biomeGenBase, FlatLayerInfo... flatLayerInfos) {
      func_175354_a(string, item, 0, biomeGenBase, (List<String>)null, flatLayerInfos);
   }

   private static void func_146421_a(String string, Item item, BiomeGenBase biomeGenBase, List<String> list, FlatLayerInfo... flatLayerInfos) {
      func_175354_a(string, item, 0, biomeGenBase, list, flatLayerInfos);
   }

   private static void func_175354_a(String string, Item item, int i, BiomeGenBase biomeGenBase, List<String> list, FlatLayerInfo... flatLayerInfos) {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();

      for (int j = flatLayerInfos.length - 1; j >= 0; j--) {
         flatGeneratorInfo.getFlatLayers().add(flatLayerInfos[j]);
      }

      flatGeneratorInfo.setBiome(biomeGenBase.biomeID);
      flatGeneratorInfo.func_82645_d();
      if (list != null) {
         for (String string2 : list) {
            flatGeneratorInfo.getWorldFeatures().put(string2, Maps.newHashMap());
         }
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(item, i, string, flatGeneratorInfo.toString()));
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

      public LayerItem(Item item, int i, String string, String string2) {
         this.field_148234_a = item;
         this.field_179037_b = i;
         this.field_148232_b = string;
         this.field_148233_c = string2;
      }
   }

   public class ListSlot extends GuiSlot {
      public int field_148175_k = -1;

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }

      private void func_178054_a(int i, int j, Item item, int k) {
         this.func_148173_e(i + 1, j + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(item, 1, k), i + 2, j + 2);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }

      private void func_148173_e(int i, int j) {
         this.func_148171_c(i, j, 0, 0);
      }

      private void func_148171_c(int i, int j, int k, int l) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(i + 0, j + 18, GuiFlatPresets.this.zLevel).tex((k + 0) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, j + 18, GuiFlatPresets.this.zLevel).tex((k + 18) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, j + 0, GuiFlatPresets.this.zLevel).tex((k + 18) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 0, j + 0, GuiFlatPresets.this.zLevel).tex((k + 0) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
         tessellator.draw();
      }

      @Override
      public int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.field_148175_k = slotIndex;
         GuiFlatPresets.this.func_146426_g();
         GuiFlatPresets.this.field_146433_u.setText(GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.field_146435_s.field_148175_k).field_148233_c);
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.field_148175_k;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         GuiFlatPresets.LayerItem layerItem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(entryID);
         this.func_178054_a(i, j, layerItem.field_148234_a, layerItem.field_179037_b);
         GuiFlatPresets.this.fontRendererObj.drawStringNoShadow(layerItem.field_148232_b, i + 18 + 5, j + 6, 16777215);
      }
   }
}
