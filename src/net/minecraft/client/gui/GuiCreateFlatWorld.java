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

   public GuiCreateFlatWorld(GuiCreateWorld createWorldGuiIn, String string) {
      this.createWorldGui = createWorldGuiIn;
      this.func_146383_a(string);
   }

   public String func_146384_e() {
      return this.theFlatGeneratorInfo.toString();
   }

   public void func_146383_a(String string) {
      this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(string);
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
   public void actionPerformed(GuiButton button) throws IOException {
      int i = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.field_148228_k - 1;
      if (button.id == 1) {
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 0) {
         this.createWorldGui.chunkProviderSettingsJson = this.func_146384_e();
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 5) {
         this.mc.displayGuiScreen(new GuiFlatPresets(this));
      } else if (button.id == 4 && this.func_146382_i()) {
         this.theFlatGeneratorInfo.getFlatLayers().remove(i);
         this.createFlatWorldListSlotGui.field_148228_k = Math.min(
            this.createFlatWorldListSlotGui.field_148228_k, this.theFlatGeneratorInfo.getFlatLayers().size() - 1
         );
      }

      this.theFlatGeneratorInfo.func_82645_d();
      this.func_146375_g();
   }

   public void func_146375_g() {
      boolean bl = this.func_146382_i();
      this.field_146386_v.enabled = bl;
      this.field_146388_u.enabled = bl;
      this.field_146388_u.enabled = false;
      this.field_146389_t.enabled = false;
   }

   private boolean func_146382_i() {
      return this.createFlatWorldListSlotGui.field_148228_k > -1
         && this.createFlatWorldListSlotGui.field_148228_k < this.theFlatGeneratorInfo.getFlatLayers().size();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.fontRendererObj, this.field_146394_i, i, 32, 16777215);
      this.drawString(this.fontRendererObj, this.field_146391_r, i + 2 + 213 - this.fontRendererObj.getStringWidth(this.field_146391_r), 32, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public class Details extends GuiSlot {
      public int field_148228_k = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      private void func_148225_a(int i, int j, ItemStack itemStack) {
         this.func_148226_e(i + 1, j + 1);
         GlStateManager.enableRescaleNormal();
         if (itemStack != null && itemStack.getItem() != null) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(itemStack, i + 2, j + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      private void func_148226_e(int i, int j) {
         this.func_148224_c(i, j, 0, 0);
      }

      private void func_148224_c(int i, int j, int k, int l) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(i + 0, j + 18, GuiCreateFlatWorld.this.zLevel).tex((k + 0) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, j + 18, GuiCreateFlatWorld.this.zLevel).tex((k + 18) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 18, j + 0, GuiCreateFlatWorld.this.zLevel).tex((k + 18) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
         worldRenderer.pos(i + 0, j + 0, GuiCreateFlatWorld.this.zLevel).tex((k + 0) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
         tessellator.draw();
      }

      @Override
      public int getSize() {
         return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.field_148228_k = slotIndex;
         GuiCreateFlatWorld.this.func_146375_g();
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.field_148228_k;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         FlatLayerInfo flatLayerInfo = GuiCreateFlatWorld.this.theFlatGeneratorInfo
            .getFlatLayers()
            .get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - entryID - 1);
         IBlockState iBlockState = flatLayerInfo.getLayerMaterial();
         Block block = iBlockState.getBlock();
         Item item = Item.getItemFromBlock(block);
         ItemStack itemStack = block != Blocks.air && item != null ? new ItemStack(item, 1, block.getMetaFromState(iBlockState)) : null;
         String string = itemStack == null ? "Air" : item.getItemStackDisplayName(itemStack);
         if (item == null) {
            if (block == Blocks.water || block == Blocks.flowing_water) {
               item = Items.water_bucket;
            } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
               item = Items.lava_bucket;
            }

            if (item != null) {
               itemStack = new ItemStack(item, 1, block.getMetaFromState(iBlockState));
               string = block.getLocalizedName();
            }
         }

         this.func_148225_a(i, j, itemStack);
         GuiCreateFlatWorld.this.fontRendererObj.drawStringNoShadow(string, i + 18 + 5, j + 3, 16777215);
         String string2;
         if (entryID == 0) {
            string2 = I18n.format("createWorld.customize.flat.layer.top", flatLayerInfo.getLayerCount());
         } else if (entryID == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
            string2 = I18n.format("createWorld.customize.flat.layer.bottom", flatLayerInfo.getLayerCount());
         } else {
            string2 = I18n.format("createWorld.customize.flat.layer", flatLayerInfo.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRendererObj
            .drawStringNoShadow(string2, i + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getStringWidth(string2), j + 3, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 70;
      }
   }
}
