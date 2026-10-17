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

   public GuiMerchant(InventoryPlayer inventoryPlayer, IMerchant iMerchant, World worldIn) {
      super(new ContainerMerchant(inventoryPlayer, iMerchant, worldIn));
      this.merchant = iMerchant;
      this.chatComponent = iMerchant.getDisplayName();
   }

   @Override
   public void initGui() {
      super.initGui();
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
      this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String string = this.chatComponent.getUnformattedText();
      this.fontRendererObj.drawStringNoShadow(string, this.xSize / 2 - this.fontRendererObj.getStringWidth(string) / 2, 6.0F, 4210752);
      this.fontRendererObj.drawStringNoShadow(I18n.format("container.inventory"), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
      if (merchantRecipeList != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < merchantRecipeList.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      boolean bl = false;
      if (button == this.nextButton) {
         this.selectedMerchantRecipe++;
         MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
         if (merchantRecipeList != null && this.selectedMerchantRecipe >= merchantRecipeList.size()) {
            this.selectedMerchantRecipe = merchantRecipeList.size() - 1;
         }

         bl = true;
      } else if (button == this.previousButton) {
         this.selectedMerchantRecipe--;
         if (this.selectedMerchantRecipe < 0) {
            this.selectedMerchantRecipe = 0;
         }

         bl = true;
      }

      if (bl) {
         ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.selectedMerchantRecipe);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", packetBuffer));
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
      if (merchantRecipeList != null && !merchantRecipeList.isEmpty()) {
         int k = this.selectedMerchantRecipe;
         if (k < 0 || k >= merchantRecipeList.size()) {
            return;
         }

         MerchantRecipe merchantRecipe = merchantRecipeList.get(k);
         if (merchantRecipe.isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
      if (merchantRecipeList != null && !merchantRecipeList.isEmpty()) {
         int i = (this.width - this.xSize) / 2;
         int j = (this.height - this.ySize) / 2;
         int k = this.selectedMerchantRecipe;
         MerchantRecipe merchantRecipe = merchantRecipeList.get(k);
         ItemStack itemStack = merchantRecipe.getItemToBuy();
         ItemStack itemStack2 = merchantRecipe.getSecondItemToBuy();
         ItemStack itemStack3 = merchantRecipe.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(itemStack, i + 36, j + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, i + 36, j + 24);
         if (itemStack2 != null) {
            this.itemRender.renderItemAndEffectIntoGUI(itemStack2, i + 62, j + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack2, i + 62, j + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(itemStack3, i + 120, j + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack3, i + 120, j + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && itemStack != null) {
            this.renderToolTip(itemStack, mouseX, mouseY);
         } else if (itemStack2 != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY) && itemStack2 != null) {
            this.renderToolTip(itemStack2, mouseX, mouseY);
         } else if (itemStack3 != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY) && itemStack3 != null) {
            this.renderToolTip(itemStack3, mouseX, mouseY);
         } else if (merchantRecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY))) {
            this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated"), mouseX, mouseY);
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

      public MerchantButton(int buttonID, int x, int y, boolean bl) {
         super(buttonID, x, y, 12, 19, "");
         this.field_146157_o = bl;
      }

      @Override
      public void drawButton(Minecraft mcIn, int mouseX, int mouseY) {
         if (this.visible) {
            mcIn.getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean bl = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            int i = 0;
            int j = 176;
            if (!this.enabled) {
               j += this.width * 2;
            } else if (bl) {
               j += this.width;
            }

            if (!this.field_146157_o) {
               i += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, j, i, this.width, this.height);
         }
      }
   }
}
