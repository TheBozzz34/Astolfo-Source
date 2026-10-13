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

   public GuiContainer(Container var1) {
      this.inventorySlots = var1;
      this.ignoreMouseUp = true;
   }

   @Override
   public void initGui() {
      long var1 = db ^ 15687370448167L;
      long var3 = var1 ^ 29722429960183L;
      super.initGui();
      this.mc.thePlayer.openContainer = this.inventorySlots;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.buttonList.add(this.d = new GuiButton(32, 2, 1, 140, 20, "Disable KillAura"));
      this.buttonList.add(this.i = new GuiButton(33, 2, 20, 140, 20, "Disable InventoryManager"));
      this.buttonList.add(this.Xk = new GuiButton(34, 2, 40, 140, 20, "Disable Stealer"));
      KillAura var5 = Astolfo.INSTANCE.getModuleManager().get(KillAura.class);
      InventoryManager var6 = Astolfo.INSTANCE.getModuleManager().get(InventoryManager.class);
      Stealer var7 = Astolfo.INSTANCE.getModuleManager().get(Stealer.class);
      this.d.enabled = var5.isEnabled();
      this.i.enabled = var6.isEnabled();
      this.Xk.enabled = var7.isEnabled();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      int var7 = this.guiLeft;
      int var8 = this.guiTop;
      this.drawGuiContainerBackgroundLayer(var3, var1, var2);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      super.drawScreen(var1, var2, var3);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate(var7, var8, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.theSlot = null;
      short var9 = 240;
      short var10 = 240;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var9 / 1.0F, var10 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int var11 = 0; var11 < this.inventorySlots.inventorySlots.size(); var11++) {
         Slot var12 = this.inventorySlots.inventorySlots.get(var11);
         this.drawSlot(var12);
         if (this.isMouseOverSlot(var12, var1, var2) && var12.canBeHovered()) {
            this.theSlot = var12;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int var13 = var12.xDisplayPosition;
            int var14 = var12.yDisplayPosition;
            GlStateManager.colorMask(true, true, true, false);
            this.drawGradientRect(var13, var14, var13 + 16, var14 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(var1, var2);
      RenderHelper.enableGUIStandardItemLighting();
      InventoryPlayer var18 = this.mc.thePlayer.inventory;
      ItemStack var19 = this.draggedStack == null ? var18.getItemStack() : this.draggedStack;
      if (var19 != null) {
         byte var20 = 8;
         int var23 = this.draggedStack == null ? 8 : 16;
         String var15 = null;
         if (this.draggedStack != null && this.isRightMouseClick) {
            var19 = var19.copy();
            var19.stackSize = MathHelper.ceiling_float_int(var19.stackSize / 2.0F);
         } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
            var19 = var19.copy();
            var19.stackSize = this.dragSplittingRemnant;
            if (var19.stackSize == 0) {
               var15 = "" + EnumChatFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(var19, var1 - var7 - var20, var2 - var8 - var23, var15);
      }

      if (this.returningStack != null) {
         float var21 = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
         if (var21 >= 1.0F) {
            var21 = 1.0F;
            this.returningStack = null;
         }

         int var24 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
         int var25 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
         int var16 = this.touchUpX + (int)(var24 * var21);
         int var17 = this.touchUpY + (int)(var25 * var21);
         this.drawItemStack(this.returningStack, var16, var17, (String)null);
      }

      GlStateManager.popMatrix();
      if (var18.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
         ItemStack var22 = this.theSlot.getStack();
         this.renderToolTip(var22, var1, var2);
      }

      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
   }

   private void drawItemStack(ItemStack var1, int var2, int var3, String var4) {
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      this.itemRender.renderItemAndEffectIntoGUI(var1, var2, var3);
      this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, var1, var2, var3 - (this.draggedStack == null ? 0 : 8), var4);
      this.zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
   }

   public void drawGuiContainerForegroundLayer(int var1, int var2) {
   }

   public abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

   private void drawSlot(Slot var1) {
      int var5 = var1.xDisplayPosition;
      int var6 = var1.yDisplayPosition;
      ItemStack var7 = var1.getStack();
      boolean var8 = false;
      boolean var9 = var1 == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
      ItemStack var10 = this.mc.thePlayer.inventory.getItemStack();
      String var11 = null;
      if (var1 == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && var7 != null) {
         var7 = var7.copy();
         var7.stackSize /= 2;
      } else if (this.dragSplitting && this.dragSplittingSlots.contains(var1) && var10 != null) {
         if (this.dragSplittingSlots.size() == 1) {
            return;
         }

         if (Container.canAddItemToSlot(var1, var10, true) && this.inventorySlots.canDragIntoSlot(var1)) {
            var7 = var10.copy();
            var8 = true;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, var7, var1.getStack() == null ? 0 : var1.getStack().stackSize);
            if (var7.stackSize > var7.getMaxStackSize()) {
               var11 = EnumChatFormatting.YELLOW + "" + var7.getMaxStackSize();
               var7.stackSize = var7.getMaxStackSize();
            }

            if (var7.stackSize > var1.getItemStackLimit(var7)) {
               var11 = EnumChatFormatting.YELLOW + "" + var1.getItemStackLimit(var7);
               var7.stackSize = var1.getItemStackLimit(var7);
            }
         } else {
            this.dragSplittingSlots.remove(var1);
            this.updateDragSplitting();
         }
      }

      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      if (var7 == null) {
         String var12 = var1.getSlotTexture();
         if (var12 != null) {
            TextureAtlasSprite var13 = this.mc.getTextureMapBlocks().getAtlasSprite(var12);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(var5, var6, var13, 16, 16);
            GlStateManager.enableLighting();
            var9 = true;
         }
      }

      if (!var9) {
         if (var8) {
            drawRect(var5, var6, var5 + 16, var6 + 16, -2130706433);
         }

         GlStateManager.enableDepth();
         this.itemRender.renderItemAndEffectIntoGUI(var7, var5, var6);
         this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, var7, var5, var6, var11);
      }

      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   private void updateDragSplitting() {
      ItemStack var4 = this.mc.thePlayer.inventory.getItemStack();
      if (var4 != null && this.dragSplitting) {
         this.dragSplittingRemnant = var4.stackSize;

         for (Slot var6 : this.dragSplittingSlots) {
            ItemStack var7 = var4.copy();
            int var8 = var6.getStack() == null ? 0 : var6.getStack().stackSize;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, var7, var8);
            if (var7.stackSize > var7.getMaxStackSize()) {
               var7.stackSize = var7.getMaxStackSize();
            }

            if (var7.stackSize > var6.getItemStackLimit(var7)) {
               var7.stackSize = var6.getItemStackLimit(var7);
            }

            this.dragSplittingRemnant = this.dragSplittingRemnant - (var7.stackSize - var8);
         }
      }
   }

   private Slot getSlotAtPosition(int var1, int var2) {
      for (int var6 = 0; var6 < this.inventorySlots.inventorySlots.size(); var6++) {
         Slot var7 = this.inventorySlots.inventorySlots.get(var6);
         if (this.isMouseOverSlot(var7, var1, var2)) {
            return var7;
         }
      }

      return null;
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      ContainerClickEvent var7 = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(var7);
      if (!var7.isCancelled()) {
         this.checkHotbarKeys(var3 - 100);
         super.mouseClicked(var1, var2, var3);
         boolean var8 = var3 == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
         Slot var9 = this.getSlotAtPosition(var1, var2);
         long var10 = Minecraft.getSystemTime();
         this.doubleClick = this.lastClickSlot == var9 && var10 - this.lastClickTime < 250L && this.lastClickButton == var3;
         this.ignoreMouseUp = false;
         if (var3 == 0 || var3 == 1 || var8) {
            int var12 = this.guiLeft;
            int var13 = this.guiTop;
            boolean var14 = var1 < var12 || var2 < var13 || var1 >= var12 + this.xSize || var2 >= var13 + this.ySize;
            int var15 = -1;
            if (var9 != null) {
               var15 = var9.slotNumber;
            }

            if (var14) {
               var15 = -999;
            }

            if (this.mc.gameSettings.touchscreen && var14 && this.mc.thePlayer.inventory.getItemStack() == null) {
               this.mc.displayGuiScreen((GuiScreen)null);
               return;
            }

            if (var15 != -1) {
               if (this.mc.gameSettings.touchscreen) {
                  if (var9 != null && var9.getHasStack()) {
                     this.clickedSlot = var9;
                     this.draggedStack = null;
                     this.isRightMouseClick = var3 == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.dragSplitting) {
                  if (this.mc.thePlayer.inventory.getItemStack() == null) {
                     if (var3 == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.handleMouseClick(var9, var15, var3, 3);
                     } else {
                        boolean var16 = var15 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                        byte var17 = 0;
                        if (var16) {
                           this.shiftClickedSlot = var9 != null && var9.getHasStack() ? var9.getStack() : null;
                           var17 = 1;
                        } else if (var15 == -999) {
                           var17 = 4;
                        }

                        this.handleMouseClick(var9, var15, var3, var17);
                     }

                     this.ignoreMouseUp = true;
                  } else {
                     this.dragSplitting = true;
                     this.dragSplittingButton = var3;
                     this.dragSplittingSlots.clear();
                     if (var3 == 0) {
                        this.dragSplittingLimit = 0;
                     } else if (var3 == 1) {
                        this.dragSplittingLimit = 1;
                     } else if (var3 == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.dragSplittingLimit = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = var9;
         this.lastClickTime = var10;
         this.lastClickButton = var3;
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      long var2 = db ^ 2195983386247L;
      long var4 = var2 ^ 22761179903063L;
      switch (var1.id) {
         case 32:
            KillAura var7 = Astolfo.INSTANCE.getModuleManager().get(KillAura.class);
            var7.setEnabled(false);
            this.d.enabled = false;
            break;
         case 33:
            InventoryManager var8 = Astolfo.INSTANCE.getModuleManager().get(InventoryManager.class);
            var8.setEnabled(false);
            this.i.enabled = false;
            break;
         case 34:
            Stealer var9 = Astolfo.INSTANCE.getModuleManager().get(Stealer.class);
            var9.setEnabled(false);
            this.Xk.enabled = false;
      }

      super.actionPerformed(var1);
   }

   @Override
   public void mouseClickMove(int var1, int var2, int var3, long var4) {
      ContainerClickEvent var9 = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(var9);
      if (!var9.isCancelled()) {
         Slot var10 = this.getSlotAtPosition(var1, var2);
         ItemStack var11 = this.mc.thePlayer.inventory.getItemStack();
         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (var3 == 0 || var3 == 1) {
               if (this.draggedStack == null) {
                  if (var10 != this.clickedSlot && this.clickedSlot.getStack() != null) {
                     this.draggedStack = this.clickedSlot.getStack().copy();
                  }
               } else if (this.draggedStack.stackSize > 1 && var10 != null && Container.canAddItemToSlot(var10, this.draggedStack, false)) {
                  long var12 = Minecraft.getSystemTime();
                  if (this.currentDragTargetSlot == var10) {
                     if (var12 - this.dragItemDropDelay > 500L) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                        this.handleMouseClick(var10, var10.slotNumber, 1, 0);
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                        this.dragItemDropDelay = var12 + 750L;
                        this.draggedStack.stackSize--;
                     }
                  } else {
                     this.currentDragTargetSlot = var10;
                     this.dragItemDropDelay = var12;
                  }
               }
            }
         } else if (this.dragSplitting
            && var10 != null
            && var11 != null
            && var11.stackSize > this.dragSplittingSlots.size()
            && Container.canAddItemToSlot(var10, var11, true)
            && var10.isItemValid(var11)
            && this.inventorySlots.canDragIntoSlot(var10)) {
            this.dragSplittingSlots.add(var10);
            this.updateDragSplitting();
         }
      }
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      ContainerClickEvent var7 = new ContainerClickEvent();
      Astolfo.INSTANCE.getEventBus().post(var7);
      if (!var7.isCancelled()) {
         Slot var8 = this.getSlotAtPosition(var1, var2);
         int var9 = this.guiLeft;
         int var10 = this.guiTop;
         boolean var11 = var1 < var9 || var2 < var10 || var1 >= var9 + this.xSize || var2 >= var10 + this.ySize;
         int var12 = -1;
         if (var8 != null) {
            var12 = var8.slotNumber;
         }

         if (var11) {
            var12 = -999;
         }

         if (this.doubleClick && var8 != null && var3 == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, var8)) {
            if (isShiftKeyDown()) {
               if (var8 != null && var8.inventory != null && this.shiftClickedSlot != null) {
                  for (Slot var18 : this.inventorySlots.inventorySlots) {
                     if (var18 != null
                        && var18.canTakeStack(this.mc.thePlayer)
                        && var18.getHasStack()
                        && var18.inventory == var8.inventory
                        && Container.canAddItemToSlot(var18, this.shiftClickedSlot, true)) {
                        this.handleMouseClick(var18, var18.slotNumber, var3, 1);
                     }
                  }
               }
            } else {
               this.handleMouseClick(var8, var12, var3, 6);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
         } else {
            if (this.dragSplitting && this.dragSplittingButton != var3) {
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
               if (var3 == 0 || var3 == 1) {
                  if (this.draggedStack == null && var8 != this.clickedSlot) {
                     this.draggedStack = this.clickedSlot.getStack();
                  }

                  boolean var16 = Container.canAddItemToSlot(var8, this.draggedStack, false);
                  if (var12 != -1 && this.draggedStack != null && var16) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, var3, 0);
                     this.handleMouseClick(var8, var12, 0, 0);
                     if (this.mc.thePlayer.inventory.getItemStack() != null) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, var3, 0);
                        this.touchUpX = var1 - var9;
                        this.touchUpY = var2 - var10;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                     } else {
                        this.returningStack = null;
                     }
                  } else if (this.draggedStack != null) {
                     this.touchUpX = var1 - var9;
                     this.touchUpY = var2 - var10;
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Minecraft.getSystemTime();
                  }

                  this.draggedStack = null;
                  this.clickedSlot = null;
               }
            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
               this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.dragSplittingLimit), 5);

               for (Slot var14 : this.dragSplittingSlots) {
                  this.handleMouseClick(var14, var14.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), 5);
               }

               this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.dragSplittingLimit), 5);
            } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
               if (var3 == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                  this.handleMouseClick(var8, var12, var3, 3);
               } else {
                  boolean var13 = var12 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                  if (var13) {
                     this.shiftClickedSlot = var8 != null && var8.getHasStack() ? var8.getStack() : null;
                  }

                  this.handleMouseClick(var8, var12, var3, var13 ? 1 : 0);
               }
            }
         }

         if (this.mc.thePlayer.inventory.getItemStack() == null) {
            this.lastClickTime = 0L;
         }

         this.dragSplitting = false;
      }
   }

   private boolean isMouseOverSlot(Slot var1, int var2, int var3) {
      return this.isPointInRegion(var1.xDisplayPosition, var1.yDisplayPosition, 16, 16, var2, var3);
   }

   public boolean isPointInRegion(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var10 = this.guiLeft;
      int var11 = this.guiTop;
      var5 -= var10;
      var6 -= var11;
      return var5 >= var1 - 1 && var5 < var1 + var3 + 1 && var6 >= var2 - 1 && var6 < var2 + var4 + 1;
   }

   public void handleMouseClick(Slot var1, int var2, int var3, int var4) {
      if (var1 != null) {
         var2 = var1.slotNumber;
      }

      this.mc.playerController.windowClick(this.inventorySlots.windowId, var2, var3, var4, this.mc.thePlayer);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == 1 || var2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.thePlayer.closeScreen();
      }

      this.checkHotbarKeys(var2);
      if (this.theSlot != null && this.theSlot.getHasStack()) {
         if (var2 == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
         } else if (var2 == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
         }
      }
   }

   public boolean checkHotbarKeys(int var1) {
      if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
         for (int var5 = 0; var5 < 9; var5++) {
            if (var1 == this.mc.gameSettings.keyBindsHotbar[var5].getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, var5, 2);
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
