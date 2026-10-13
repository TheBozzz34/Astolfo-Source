package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
   private IMerchant merchant;
   private GuiMerchant.MerchantButton nextButton;
   private GuiMerchant.MerchantButton previousButton;
   private int selectedMerchantRecipe;
   private IChatComponent chatComponent;
   private static final long bb = 75551353706711L;

   public GuiMerchant(InventoryPlayer var1, IMerchant var2, World var3) {
      super(new ContainerMerchant(var1, var2, var3));
      this.merchant = var2;
      this.chatComponent = var2.getDisplayName();
   }

   @Override
   public void initGui() {
      super.initGui();
      int var3 = (this.width - this.xSize) / 2;
      int var4 = (this.height - this.ySize) / 2;
      this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, var3 + 120 + 27, var4 + 24 - 1, true));
      this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, var3 + 36 - 19, var4 + 24 - 1, false));
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      String var5 = this.chatComponent.getUnformattedText();
      this.fontRendererObj.D(var5, this.xSize / 2 - this.fontRendererObj.getStringWidth(var5) / 2, 6.0F, 4210752);
      this.fontRendererObj.D(I18n.format("container.inventory"), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      MerchantRecipeList var4 = this.merchant.getRecipes(this.mc.thePlayer);
      if (var4 != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < var4.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      boolean var5 = false;
      if (var1 == this.nextButton) {
         this.selectedMerchantRecipe++;
         MerchantRecipeList var6 = this.merchant.getRecipes(this.mc.thePlayer);
         if (var6 != null && this.selectedMerchantRecipe >= var6.size()) {
            this.selectedMerchantRecipe = var6.size() - 1;
         }

         var5 = true;
      } else if (var1 == this.previousButton) {
         this.selectedMerchantRecipe--;
         if (this.selectedMerchantRecipe < 0) {
            this.selectedMerchantRecipe = 0;
         }

         var5 = true;
      }

      if (var5) {
         ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
         PacketBuffer var7 = new PacketBuffer(Unpooled.buffer());
         var7.writeInt(this.selectedMerchantRecipe);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", var7));
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList var9 = this.merchant.getRecipes(this.mc.thePlayer);
      if (var9 != null && !var9.isEmpty()) {
         int var10 = this.selectedMerchantRecipe;
         if (var10 < 0 || var10 >= var9.size()) {
            return;
         }

         MerchantRecipe var11 = var9.get(var10);
         if (var11.isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
      MerchantRecipeList var7 = this.merchant.getRecipes(this.mc.thePlayer);
      if (var7 != null && !var7.isEmpty()) {
         int var8 = (this.width - this.xSize) / 2;
         int var9 = (this.height - this.ySize) / 2;
         int var10 = this.selectedMerchantRecipe;
         MerchantRecipe var11 = var7.get(var10);
         ItemStack var12 = var11.getItemToBuy();
         ItemStack var13 = var11.getSecondItemToBuy();
         ItemStack var14 = var11.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(var12, var8 + 36, var9 + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, var12, var8 + 36, var9 + 24);
         if (var13 != null) {
            this.itemRender.renderItemAndEffectIntoGUI(var13, var8 + 62, var9 + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, var13, var8 + 62, var9 + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(var14, var8 + 120, var9 + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, var14, var8 + 120, var9 + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, var1, var2) && var12 != null) {
            this.renderToolTip(var12, var1, var2);
         } else if (var13 != null && this.isPointInRegion(62, 24, 16, 16, var1, var2) && var13 != null) {
            this.renderToolTip(var13, var1, var2);
         } else if (var14 != null && this.isPointInRegion(120, 24, 16, 16, var1, var2) && var14 != null) {
            this.renderToolTip(var14, var1, var2);
         } else if (var11.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, var1, var2) || this.isPointInRegion(83, 51, 28, 21, var1, var2))) {
            this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated"), var1, var2);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
      }
   }

   public IMerchant getMerchant() {
      return this.merchant;
   }

   public static class MerchantButton extends GuiButton {
      private final boolean field_146157_o;
      private static final long c = 123763091282544L;

      public MerchantButton(int var1, int var2, int var3, boolean var4) {
         super(var1, var2, var3, 12, 19, "");
         this.field_146157_o = var4;
      }

      @Override
      public void drawButton(Minecraft var1, int var2, int var3) {
         long var4 = c ^ 38811996112940L;
         if (this.visible) {
            var1.getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var7 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
            int var8 = 0;
            long var10000 = 3973695186554498735L ^ var4;
            int var9 = 176;
            if (!this.enabled) {
               var9 += this.width * 2;
            } else if (var7) {
               var9 += this.width;
            }

            if (!this.field_146157_o) {
               var8 += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, var9, var8, this.width, this.height);
         }
      }
   }
}
