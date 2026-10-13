package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   private final GuiCreateWorld createWorldGui;
   private FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();
   private String flatWorldTitle;
   private String field_146394_i;
   private String field_146391_r;
   private GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
   private GuiButton field_146389_t;
   private GuiButton field_146388_u;
   private GuiButton field_146386_v;
   private static final long a = 13255165786350L;

   public GuiCreateFlatWorld(GuiCreateWorld var1, String var2) {
      this.createWorldGui = var1;
      this.func_146383_a(var2);
   }

   public String func_146384_e() {
      return this.theFlatGeneratorInfo.toString();
   }

   public void func_146383_a(String var1) {
      this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(var1);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.flatWorldTitle = I18n.format("createWorld.customize.flat.title");
      this.field_146394_i = I18n.format("createWorld.customize.flat.tile");
      this.field_146391_r = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
      this.buttonList
         .add(
            this.field_146389_t = new GuiButton(
               2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)"
            )
         );
      this.buttonList
         .add(
            this.field_146388_u = new GuiButton(
               3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)"
            )
         );
      this.buttonList
         .add(this.field_146386_v = new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.field_146389_t.visible = this.field_146388_u.visible = false;
      this.theFlatGeneratorInfo.func_82645_d();
      this.func_146375_g();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.createFlatWorldListSlotGui.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      int var5 = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.field_148228_k - 1;
      if (var1.id == 1) {
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (var1.id == 0) {
         this.createWorldGui.chunkProviderSettingsJson = this.func_146384_e();
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (var1.id == 5) {
         this.mc.displayGuiScreen(new GuiFlatPresets(this));
      } else if (var1.id == 4 && this.func_146382_i()) {
         this.theFlatGeneratorInfo.getFlatLayers().remove(var5);
         this.createFlatWorldListSlotGui.field_148228_k = Math.min(
            this.createFlatWorldListSlotGui.field_148228_k, this.theFlatGeneratorInfo.getFlatLayers().size() - 1
         );
      }

      this.theFlatGeneratorInfo.func_82645_d();
      this.func_146375_g();
   }

   public void func_146375_g() {
      boolean var1 = this.func_146382_i();
      this.field_146386_v.enabled = var1;
      this.field_146388_u.enabled = var1;
      this.field_146388_u.enabled = false;
      this.field_146389_t.enabled = false;
   }

   private boolean func_146382_i() {
      return this.createFlatWorldListSlotGui.field_148228_k > -1
         && this.createFlatWorldListSlotGui.field_148228_k < this.theFlatGeneratorInfo.getFlatLayers().size();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int var6 = this.width / 2 - 92 - 16;
      this.I(this.fontRendererObj, this.field_146394_i, var6, 32, 16777215);
      this.I(this.fontRendererObj, this.field_146391_r, var6 + 2 + 213 - this.fontRendererObj.getStringWidth(this.field_146391_r), 32, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   public class Details extends GuiSlot {
      public int field_148228_k = -1;
      private static final long c = 89057583998757L;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      private void func_148225_a(int var1, int var2, ItemStack var3) {
         this.func_148226_e(var1 + 1, var2 + 1);
         GlStateManager.enableRescaleNormal();
         if (var3 != null && var3.getItem() != null) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(var3, var1 + 2, var2 + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      private void func_148226_e(int var1, int var2) {
         this.func_148224_c(var1, var2, 0, 0);
      }

      private void func_148224_c(int var1, int var2, int var3, int var4) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator var11 = Tessellator.getInstance();
         WorldRenderer var12 = var11.getWorldRenderer();
         var12.begin(7, DefaultVertexFormats.POSITION_TEX);
         var12.pos(var1 + 0, var2 + 18, GuiCreateFlatWorld.this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
         var12.pos(var1 + 18, var2 + 18, GuiCreateFlatWorld.this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
         var12.pos(var1 + 18, var2 + 0, GuiCreateFlatWorld.this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
         var12.pos(var1 + 0, var2 + 0, GuiCreateFlatWorld.this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
         var11.draw();
      }

      @Override
      public int getSize() {
         return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         this.field_148228_k = var1;
         GuiCreateFlatWorld.this.func_146375_g();
      }

      @Override
      public boolean isSelected(int var1) {
         return var1 == this.field_148228_k;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         FlatLayerInfo var10 = GuiCreateFlatWorld.this.theFlatGeneratorInfo
            .getFlatLayers()
            .get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - var1 - 1);
         IBlockState var11 = var10.getLayerMaterial();
         Block var12 = var11.getBlock();
         Item var13 = Item.getItemFromBlock(var12);
         ItemStack var14 = var12 != Blocks.air && var13 != null ? new ItemStack(var13, 1, var12.getMetaFromState(var11)) : null;
         String var15 = var14 == null ? "Air" : var13.getItemStackDisplayName(var14);
         if (var13 == null) {
            if (var12 == Blocks.water || var12 == Blocks.flowing_water) {
               var13 = Items.water_bucket;
            } else if (var12 == Blocks.lava || var12 == Blocks.flowing_lava) {
               var13 = Items.lava_bucket;
            }

            if (var13 != null) {
               var14 = new ItemStack(var13, 1, var12.getMetaFromState(var11));
               var15 = var12.getLocalizedName();
            }
         }

         this.func_148225_a(var2, var3, var14);
         GuiCreateFlatWorld.this.fontRendererObj.D(var15, var2 + 18 + 5, var3 + 3, 16777215);
         String var16;
         if (var1 == 0) {
            var16 = I18n.format("createWorld.customize.flat.layer.top", var10.getLayerCount());
         } else if (var1 == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
            var16 = I18n.format("createWorld.customize.flat.layer.bottom", var10.getLayerCount());
         } else {
            var16 = I18n.format("createWorld.customize.flat.layer", var10.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRendererObj.D(var16, var2 + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getStringWidth(var16), var3 + 3, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 70;
      }
   }
}
