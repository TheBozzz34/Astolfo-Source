package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiRepair extends GuiContainer implements ICrafting {
   private static final ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
   private ContainerRepair anvil;
   private GuiTextField nameField;
   private InventoryPlayer playerInventory;
   private static final long bb = 136872546293604L;

   public GuiRepair(InventoryPlayer var1, World var2) {
      super(new ContainerRepair(var1, var2, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = var1;
      this.anvil = (ContainerRepair)this.inventorySlots;
   }

   @Override
   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      int var3 = (this.width - this.xSize) / 2;
      int var4 = (this.height - this.ySize) / 2;
      this.nameField = new GuiTextField(0, this.fontRendererObj, var3 + 62, var4 + 24, 103, 12);
      this.nameField.setTextColor(-1);
      this.nameField.setDisabledTextColour(-1);
      this.nameField.setEnableBackgroundDrawing(false);
      this.nameField.setMaxStringLength(30);
      this.inventorySlots.removeCraftingFromCrafters(this);
      this.inventorySlots.onCraftGuiOpened(this);
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
      this.inventorySlots.removeCraftingFromCrafters(this);
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      long var3 = bb ^ 119476666371005L;
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.fontRendererObj.D(I18n.format("container.repair"), 60.0F, 6.0F, 4210752);
      if (this.anvil.maximumCost > 0) {
         int var6 = 8453920;
         boolean var7 = true;
         long var10000 = 7048619347340121451L ^ var3;
         String var8 = I18n.format("container.repair.cost", this.anvil.maximumCost);
         if (this.anvil.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode) {
            var8 = I18n.format("container.repair.expensive");
            var6 = 16736352;
         } else if (!this.anvil.getSlot(2).getHasStack()) {
            var7 = false;
         } else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player)) {
            var6 = 16736352;
         }

         if (var7) {
            int var9 = 0xFF000000 | (var6 & 16579836) >> 2 | var6 & 0xFF000000;
            int var10 = this.xSize - 8 - this.fontRendererObj.getStringWidth(var8);
            byte var11 = 67;
            if (this.fontRendererObj.n()) {
               drawRect(var10 - 3, var11 - 2, this.xSize - 7, var11 + 10, -16777216);
               drawRect(var10 - 2, var11 - 1, this.xSize - 8, var11 + 9, -12895429);
            } else {
               this.fontRendererObj.D(var8, var10, var11 + 1, var9);
               this.fontRendererObj.D(var8, var10 + 1, var11, var9);
               this.fontRendererObj.D(var8, var10 + 1, var11 + 1, var9);
            }

            this.fontRendererObj.D(var8, var10, var11, var6);
         }
      }

      GlStateManager.enableLighting();
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (this.nameField.textboxKeyTyped(var1, var2)) {
         this.renameItem();
      } else {
         super.keyTyped(var1, var2);
      }
   }

   private void renameItem() {
      String var4 = this.nameField.getText();
      Slot var5 = this.anvil.getSlot(0);
      if (var5 != null && var5.getHasStack() && !var5.getStack().hasDisplayName() && var4.equals(var5.getStack().getDisplayName())) {
         var4 = "";
      }

      this.anvil.updateItemName(var4);
      this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", new PacketBuffer(Unpooled.buffer()).writeString(var4)));
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.nameField.mouseClicked(var1, var2, var3);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.nameField.drawTextBox();
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(anvilResource);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      this.drawTexturedModalRect(var7 + 59, var8 + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
      if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
         this.drawTexturedModalRect(var7 + 99, var8 + 45, this.xSize, 0, 28, 21);
      }
   }

   @Override
   public void updateCraftingInventory(Container var1, List<ItemStack> var2) {
      this.sendSlotContents(var1, 0, var1.getSlot(0).getStack());
   }

   @Override
   public void sendSlotContents(Container var1, int var2, ItemStack var3) {
      if (var2 == 0) {
         this.nameField.setText(var3 == null ? "" : var3.getDisplayName());
         this.nameField.setEnabled(var3 != null);
         if (var3 != null) {
            this.renameItem();
         }
      }
   }

   @Override
   public void sendProgressBarUpdate(Container var1, int var2, int var3) {
   }

   @Override
   public void sendAllWindowProperties(Container var1, IInventory var2) {
   }
}
