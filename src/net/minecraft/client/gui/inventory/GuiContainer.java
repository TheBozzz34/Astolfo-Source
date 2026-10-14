package net.minecraft.client.gui.inventory;

import bypass.Astolfo;
import bypass.event.game.ContainerClickEvent;
import bypass.module.combat.KillAura;
import bypass.module.player.InventoryManager;
import bypass.module.player.Stealer;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public abstract class GuiContainer extends GuiScreen {
   public static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
   public int xSize = 176;
   public int ySize = 166;
   public Container inventorySlots;
   public int guiLeft;
   public int guiTop;
   private Slot theSlot;
   private Slot clickedSlot;
   private boolean isRightMouseClick;
   private ItemStack draggedStack;
   private int touchUpX;
   private int touchUpY;
   private Slot returningStackDestSlot;
   private long returningStackTime;
   private ItemStack returningStack;
   private Slot currentDragTargetSlot;
   private long dragItemDropDelay;
   public final Set<Slot> dragSplittingSlots = Sets.newHashSet();
   public boolean dragSplitting;
   private int dragSplittingLimit;
   private int dragSplittingButton;
   private boolean ignoreMouseUp;
   private int dragSplittingRemnant;
   private long lastClickTime;
   private Slot lastClickSlot;
   private int lastClickButton;
   private boolean doubleClick;
   private ItemStack shiftClickedSlot;
   private GuiButton d;
   private GuiButton i;
   private GuiButton Xk;
   private static final long db = 37636951297891L;

   public GuiContainer(Container inventorySlotsIn) {
      this.inventorySlots = inventorySlotsIn;
      this.ignoreMouseUp = true;
   }

   @Override
   public void initGui() {
      long l = db ^ 15687370448167L;
      long m = l ^ 29722429960183L;
      super.initGui();
      this.mc.thePlayer.openContainer = this.inventorySlots;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.buttonList.add(this.d = new GuiButton(32, 2, 1, 140, 20, "Disable KillAura"));
      this.buttonList.add(this.i = new GuiButton(33, 2, 20, 140, 20, "Disable InventoryManager"));
      this.buttonList.add(this.Xk = new GuiButton(34, 2, 40, 140, 20, "Disable Stealer"));
      KillAura killAura = Astolfo.INSTANCE.getModuleManager().get(KillAura.class);
      InventoryManager inventoryManager = Astolfo.INSTANCE.getModuleManager().get(InventoryManager.class);
      Stealer stealer = Astolfo.INSTANCE.getModuleManager().get(Stealer.class);
      this.d.enabled = killAura.isEnabled();
      this.i.enabled = inventoryManager.isEnabled();
      this.Xk.enabled = stealer.isEnabled();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate(i, j, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.theSlot = null;
      short s = 240;
      short s2 = 240;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, s / 1.0F, s2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int k = 0; k < this.inventorySlots.inventorySlots.size(); k++) {
         Slot slot = this.inventorySlots.inventorySlots.get(k);
         this.drawSlot(slot);
         if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered()) {
            this.theSlot = slot;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int l = slot.xDisplayPosition;
            int m = slot.yDisplayPosition;
            GlStateManager.colorMask(true, true, true, false);
            this.drawGradientRect(l, m, l + 16, m + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(mouseX, mouseY);
      RenderHelper.enableGUIStandardItemLighting();
      InventoryPlayer inventoryPlayer = this.mc.thePlayer.inventory;
      ItemStack itemStack = this.draggedStack == null ? inventoryPlayer.getItemStack() : this.draggedStack;
      if (itemStack != null) {
         byte b = 8;
         int n = this.draggedStack == null ? 8 : 16;
         String string = null;
         if (this.draggedStack != null && this.isRightMouseClick) {
            itemStack = itemStack.copy();
            itemStack.stackSize = MathHelper.ceiling_float_int(itemStack.stackSize / 2.0F);
         } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
            itemStack = itemStack.copy();
            itemStack.stackSize = this.dragSplittingRemnant;
            if (itemStack.stackSize == 0) {
               string = "" + EnumChatFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(itemStack, mouseX - i - b, mouseY - j - n, string);
      }

      if (this.returningStack != null) {
         float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.returningStack = null;
         }

         int i2 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
         int i3 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
         int i4 = this.touchUpX + (int)(i2 * f);
         int i5 = this.touchUpY + (int)(i3 * f);
         this.drawItemStack(this.returningStack, i4, i5, (String)null);
      }

      GlStateManager.popMatrix();
      if (inventoryPlayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
         ItemStack itemStack2 = this.theSlot.getStack();
         this.renderToolTip(itemStack2, mouseX, mouseY);
      }

      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
   }

   private void drawItemStack(ItemStack stack, int x, int y, String altText) {
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
      this.zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
   }

   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
   }

   public abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

   private void drawSlot(Slot slotIn) {
      int i = slotIn.xDisplayPosition;
      int j = slotIn.yDisplayPosition;
      ItemStack itemStack = slotIn.getStack();
      boolean bl = false;
      boolean bl2 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
      ItemStack itemStack2 = this.mc.thePlayer.inventory.getItemStack();
      String string = null;
      if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemStack != null) {
         itemStack = itemStack.copy();
         itemStack.stackSize /= 2;
      } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemStack2 != null) {
         if (this.dragSplittingSlots.size() == 1) {
            return;
         }

         if (Container.canAddItemToSlot(slotIn, itemStack2, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
            itemStack = itemStack2.copy();
            bl = true;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);
            if (itemStack.stackSize > itemStack.getMaxStackSize()) {
               string = EnumChatFormatting.YELLOW + "" + itemStack.getMaxStackSize();
               itemStack.stackSize = itemStack.getMaxStackSize();
            }

            if (itemStack.stackSize > slotIn.getItemStackLimit(itemStack)) {
               string = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemStack);
               itemStack.stackSize = slotIn.getItemStackLimit(itemStack);
            }
         } else {
            this.dragSplittingSlots.remove(slotIn);
            this.updateDragSplitting();
         }
      }

      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      if (itemStack == null) {
         String string2 = slotIn.getSlotTexture();
         if (string2 != null) {
            TextureAtlasSprite textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(string2);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(i, j, textureAtlasSprite, 16, 16);
            GlStateManager.enableLighting();
            bl2 = true;
         }
      }

      if (!bl2) {
         if (bl) {
            drawRect(i, j, i + 16, j + 16, -2130706433);
         }

         GlStateManager.enableDepth();
         this.itemRender.renderItemAndEffectIntoGUI(itemStack, i, j);
         this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemStack, i, j, string);
      }

      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   private void updateDragSplitting() {
      ItemStack itemStack = this.mc.thePlayer.inventory.getItemStack();
      if (itemStack != null && this.dragSplitting) {
         this.dragSplittingRemnant = itemStack.stackSize;

         for (Slot slot : this.dragSplittingSlots) {
            ItemStack itemStack2 = itemStack.copy();
            int i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack2, i);
            if (itemStack2.stackSize > itemStack2.getMaxStackSize()) {
               itemStack2.stackSize = itemStack2.getMaxStackSize();
            }

            if (itemStack2.stackSize > slot.getItemStackLimit(itemStack2)) {
               itemStack2.stackSize = slot.getItemStackLimit(itemStack2);
            }

            this.dragSplittingRemnant = this.dragSplittingRemnant - (itemStack2.stackSize - i);
         }
      }
   }

   private Slot getSlotAtPosition(int x, int y) {
      for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
         Slot slot = this.inventorySlots.inventorySlots.get(i);
         if (this.isMouseOverSlot(slot, x, y)) {
            return slot;
         }
      }

      return null;
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      ContainerClickEvent containerClickEvent = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(containerClickEvent);
      if (!containerClickEvent.isCancelled()) {
         this.checkHotbarKeys(mouseButton - 100);
         super.mouseClicked(mouseX, mouseY, mouseButton);
         boolean bl = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         long l = Minecraft.getSystemTime();
         this.doubleClick = this.lastClickSlot == slot && l - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
         this.ignoreMouseUp = false;
         if (mouseButton == 0 || mouseButton == 1 || bl) {
            int i = this.guiLeft;
            int j = this.guiTop;
            boolean bl2 = mouseX < i || mouseY < j || mouseX >= i + this.xSize || mouseY >= j + this.ySize;
            int k = -1;
            if (slot != null) {
               k = slot.slotNumber;
            }

            if (bl2) {
               k = -999;
            }

            if (this.mc.gameSettings.touchscreen && bl2 && this.mc.thePlayer.inventory.getItemStack() == null) {
               this.mc.displayGuiScreen((GuiScreen)null);
               return;
            }

            if (k != -1) {
               if (this.mc.gameSettings.touchscreen) {
                  if (slot != null && slot.getHasStack()) {
                     this.clickedSlot = slot;
                     this.draggedStack = null;
                     this.isRightMouseClick = mouseButton == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.dragSplitting) {
                  if (this.mc.thePlayer.inventory.getItemStack() == null) {
                     if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.handleMouseClick(slot, k, mouseButton, 3);
                     } else {
                        boolean bl3 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                        byte b = 0;
                        if (bl3) {
                           this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                           b = 1;
                        } else if (k == -999) {
                           b = 4;
                        }

                        this.handleMouseClick(slot, k, mouseButton, b);
                     }

                     this.ignoreMouseUp = true;
                  } else {
                     this.dragSplitting = true;
                     this.dragSplittingButton = mouseButton;
                     this.dragSplittingSlots.clear();
                     if (mouseButton == 0) {
                        this.dragSplittingLimit = 0;
                     } else if (mouseButton == 1) {
                        this.dragSplittingLimit = 1;
                     } else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.dragSplittingLimit = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slot;
         this.lastClickTime = l;
         this.lastClickButton = mouseButton;
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      long l = db ^ 2195983386247L;
      long m = l ^ 22761179903063L;
      switch (button.id) {
         case 32:
            KillAura killAura = Astolfo.INSTANCE.getModuleManager().get(KillAura.class);
            killAura.setEnabled(false);
            this.d.enabled = false;
            break;
         case 33:
            InventoryManager inventoryManager = Astolfo.INSTANCE.getModuleManager().get(InventoryManager.class);
            inventoryManager.setEnabled(false);
            this.i.enabled = false;
            break;
         case 34:
            Stealer stealer = Astolfo.INSTANCE.getModuleManager().get(Stealer.class);
            stealer.setEnabled(false);
            this.Xk.enabled = false;
      }

      super.actionPerformed(button);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      ContainerClickEvent containerClickEvent = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(containerClickEvent);
      if (!containerClickEvent.isCancelled()) {
         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         ItemStack itemStack = this.mc.thePlayer.inventory.getItemStack();
         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (clickedMouseButton == 0 || clickedMouseButton == 1) {
               if (this.draggedStack == null) {
                  if (slot != this.clickedSlot && this.clickedSlot.getStack() != null) {
                     this.draggedStack = this.clickedSlot.getStack().copy();
                  }
               } else if (this.draggedStack.stackSize > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
                  long l = Minecraft.getSystemTime();
                  if (this.currentDragTargetSlot == slot) {
                     if (l - this.dragItemDropDelay > 500L) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                        this.handleMouseClick(slot, slot.slotNumber, 1, 0);
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                        this.dragItemDropDelay = l + 750L;
                        this.draggedStack.stackSize--;
                     }
                  } else {
                     this.currentDragTargetSlot = slot;
                     this.dragItemDropDelay = l;
                  }
               }
            }
         } else if (this.dragSplitting
            && slot != null
            && itemStack != null
            && itemStack.stackSize > this.dragSplittingSlots.size()
            && Container.canAddItemToSlot(slot, itemStack, true)
            && slot.isItemValid(itemStack)
            && this.inventorySlots.canDragIntoSlot(slot)) {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
         }
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      ContainerClickEvent containerClickEvent = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(containerClickEvent);
      if (!containerClickEvent.isCancelled()) {
         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         int i = this.guiLeft;
         int j = this.guiTop;
         boolean bl = mouseX < i || mouseY < j || mouseX >= i + this.xSize || mouseY >= j + this.ySize;
         int k = -1;
         if (slot != null) {
            k = slot.slotNumber;
         }

         if (bl) {
            k = -999;
         }

         if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slot)) {
            if (isShiftKeyDown()) {
               if (slot != null && slot.inventory != null && this.shiftClickedSlot != null) {
                  for (Slot slot2 : this.inventorySlots.inventorySlots) {
                     if (slot2 != null
                        && slot2.canTakeStack(this.mc.thePlayer)
                        && slot2.getHasStack()
                        && slot2.inventory == slot.inventory
                        && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
                        this.handleMouseClick(slot2, slot2.slotNumber, state, 1);
                     }
                  }
               }
            } else {
               this.handleMouseClick(slot, k, state, 6);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
         } else {
            if (this.dragSplitting && this.dragSplittingButton != state) {
               this.dragSplitting = false;
               this.dragSplittingSlots.clear();
               this.ignoreMouseUp = true;
               return;
            }

            if (this.ignoreMouseUp) {
               this.ignoreMouseUp = false;
               return;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
               if (state == 0 || state == 1) {
                  if (this.draggedStack == null && slot != this.clickedSlot) {
                     this.draggedStack = this.clickedSlot.getStack();
                  }

                  boolean bl2 = Container.canAddItemToSlot(slot, this.draggedStack, false);
                  if (k != -1 && this.draggedStack != null && bl2) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                     this.handleMouseClick(slot, k, 0, 0);
                     if (this.mc.thePlayer.inventory.getItemStack() != null) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                        this.touchUpX = mouseX - i;
                        this.touchUpY = mouseY - j;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                     } else {
                        this.returningStack = null;
                     }
                  } else if (this.draggedStack != null) {
                     this.touchUpX = mouseX - i;
                     this.touchUpY = mouseY - j;
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Minecraft.getSystemTime();
                  }

                  this.draggedStack = null;
                  this.clickedSlot = null;
               }
            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
               this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.dragSplittingLimit), 5);

               for (Slot slot3 : this.dragSplittingSlots) {
                  this.handleMouseClick(slot3, slot3.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), 5);
               }

               this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.dragSplittingLimit), 5);
            } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
               if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                  this.handleMouseClick(slot, k, state, 3);
               } else {
                  boolean bl3 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                  if (bl3) {
                     this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                  }

                  this.handleMouseClick(slot, k, state, bl3 ? 1 : 0);
               }
            }
         }

         if (this.mc.thePlayer.inventory.getItemStack() == null) {
            this.lastClickTime = 0L;
         }

         this.dragSplitting = false;
      }
   }

   private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
      return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
   }

   public boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
      int i = this.guiLeft;
      int j = this.guiTop;
      pointX -= i;
      pointY -= j;
      return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
   }

   public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      if (slotIn != null) {
         slotId = slotIn.slotNumber;
      }

      this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, this.mc.thePlayer);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.thePlayer.closeScreen();
      }

      this.checkHotbarKeys(keyCode);
      if (this.theSlot != null && this.theSlot.getHasStack()) {
         if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
         } else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
         }
      }
   }

   public boolean checkHotbarKeys(int keyCode) {
      if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
         for (int i = 0; i < 9; i++) {
            if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void onGuiClosed() {
      if (this.mc.thePlayer != null) {
         this.inventorySlots.onContainerClosed(this.mc.thePlayer);
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
         this.mc.thePlayer.closeScreen();
      }
   }
}
