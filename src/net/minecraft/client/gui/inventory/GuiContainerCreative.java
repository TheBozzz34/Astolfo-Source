package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiContainerCreative extends InventoryEffectRenderer {
   private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static InventoryBasic field_147060_v = new InventoryBasic("tmp", true, 45);
   private static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();
   private float currentScroll;
   private boolean isScrolling;
   private boolean wasClicking;
   private GuiTextField searchField;
   private List<Slot> field_147063_B;
   private Slot field_147064_C;
   private boolean field_147057_D;
   private CreativeCrafting field_147059_E;
   private static final long bb = 38095947894873L;

   public GuiContainerCreative(EntityPlayer var1) {
      super(new GuiContainerCreative.ContainerCreative(var1));
      var1.openContainer = this.inventorySlots;
      this.allowUserInput = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   @Override
   public void updateScreen() {
      if (!this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   @Override
   public void handleMouseClick(Slot var1, int var2, int var3, int var4) {
      this.field_147057_D = true;
      boolean var8 = var4 == 1;
      var4 = var2 == -999 && var4 == 0 ? 4 : var4;
      if (var1 == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && var4 != 5) {
         InventoryPlayer var18 = this.mc.thePlayer.inventory;
         if (var18.getItemStack() != null) {
            if (var3 == 0) {
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(var18.getItemStack(), true);
               this.mc.playerController.sendPacketDropItem(var18.getItemStack());
               var18.setItemStack((ItemStack)null);
            }

            if (var3 == 1) {
               ItemStack var20 = var18.getItemStack().splitStack(1);
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(var20, true);
               this.mc.playerController.sendPacketDropItem(var20);
               if (var18.getItemStack().stackSize == 0) {
                  var18.setItemStack((ItemStack)null);
               }
            }
         }
      } else if (var1 == this.field_147064_C && var8) {
         for (int var17 = 0; var17 < this.mc.thePlayer.inventoryContainer.getInventory().size(); var17++) {
            this.mc.playerController.sendSlotPacket((ItemStack)null, var17);
         }
      } else if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
         if (var1 == this.field_147064_C) {
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else if (var4 == 4 && var1 != null && var1.getHasStack()) {
            ItemStack var9 = var1.decrStackSize(var3 == 0 ? 1 : var1.getStack().getMaxStackSize());
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(var9, true);
            this.mc.playerController.sendPacketDropItem(var9);
         } else if (var4 == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack(), true);
            this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else {
            this.mc
               .thePlayer
               .inventoryContainer
               .slotClick(var1 == null ? var2 : ((GuiContainerCreative.CreativeSlot)var1).slot.slotNumber, var3, var4, this.mc.thePlayer);
            this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
         }
      } else if (var4 != 5 && var1.inventory == field_147060_v) {
         InventoryPlayer var16 = this.mc.thePlayer.inventory;
         ItemStack var10 = var16.getItemStack();
         ItemStack var11 = var1.getStack();
         if (var4 == 2) {
            if (var11 != null && var3 >= 0 && var3 < 9) {
               ItemStack var22 = var11.copy();
               var22.stackSize = var22.getMaxStackSize();
               this.mc.thePlayer.inventory.setInventorySlotContents(var3, var22);
               this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }

            return;
         }

         if (var4 == 3) {
            if (var16.getItemStack() == null && var1.getHasStack()) {
               ItemStack var21 = var1.getStack().copy();
               var21.stackSize = var21.getMaxStackSize();
               var16.setItemStack(var21);
            }

            return;
         }

         if (var4 == 4) {
            if (var11 != null) {
               ItemStack var12 = var11.copy();
               var12.stackSize = var3 == 0 ? 1 : var12.getMaxStackSize();
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(var12, true);
               this.mc.playerController.sendPacketDropItem(var12);
            }

            return;
         }

         if (var10 != null && var11 != null && var10.isItemEqual(var11)) {
            if (var3 == 0) {
               if (var8) {
                  var10.stackSize = var10.getMaxStackSize();
               } else if (var10.stackSize < var10.getMaxStackSize()) {
                  var10.stackSize++;
               }
            } else if (var10.stackSize <= 1) {
               var16.setItemStack((ItemStack)null);
            } else {
               var10.stackSize--;
            }
         } else if (var11 != null && var10 == null) {
            var16.setItemStack(ItemStack.copyItemStack(var11));
            var10 = var16.getItemStack();
            if (var8) {
               var10.stackSize = var10.getMaxStackSize();
            }
         } else {
            var16.setItemStack((ItemStack)null);
         }
      } else {
         this.inventorySlots.slotClick(var1 == null ? var2 : var1.slotNumber, var3, var4, this.mc.thePlayer);
         if (Container.p(var3) == 2) {
            for (int var14 = 0; var14 < 9; var14++) {
               this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + var14).getStack(), 36 + var14);
            }
         } else if (var1 != null) {
            ItemStack var15 = this.inventorySlots.getSlot(var1.slotNumber).getStack();
            this.mc.playerController.sendSlotPacket(var15, var1.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
         }
      }
   }

   @Override
   public void updateActivePotionEffects() {
      int var4 = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != var4) {
         this.searchField.xPosition = this.guiLeft + 82;
      }
   }

   @Override
   public void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         super.initGui();
         this.buttonList.clear();
         Keyboard.enableRepeatEvents(true);
         this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
         this.searchField.setMaxStringLength(15);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         int var4 = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[var4]);
         this.field_147059_E = new CreativeCrafting(this.mc);
         this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.field_147059_E);
      } else {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
      if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
         this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_147059_E);
      }

      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
         if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
         } else {
            super.keyTyped(var1, var2);
         }
      } else {
         if (this.field_147057_D) {
            this.field_147057_D = false;
            this.searchField.setText("");
         }

         if (!this.checkHotbarKeys(var2)) {
            if (this.searchField.textboxKeyTyped(var1, var2)) {
               this.updateCreativeSearch();
            } else {
               super.keyTyped(var1, var2);
            }
         }
      }
   }

   private void updateCreativeSearch() {
      GuiContainerCreative.ContainerCreative var4 = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      var4.itemList.clear();

      for (Item var6 : Item.itemRegistry) {
         if (var6 != null && var6.getCreativeTab() != null) {
            var6.getSubItems(var6, (CreativeTabs)null, var4.itemList);
         }
      }

      for (Enchantment var8 : Enchantment.enchantmentsBookList) {
         if (var8 != null && var8.type != null) {
            Items.enchanted_book.getAll(var8, var4.itemList);
         }
      }

      Iterator var12 = var4.itemList.iterator();
      String var14 = this.searchField.getText().toLowerCase();

      while (var12.hasNext()) {
         ItemStack var15 = (ItemStack)var12.next();
         boolean var16 = false;

         for (String var10 : var15.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)) {
            if (EnumChatFormatting.getTextWithoutFormattingCodes(var10).toLowerCase().contains(var14)) {
               var16 = true;
               break;
            }
         }

         if (!var16) {
            var12.remove();
         }
      }

      this.currentScroll = 0.0F;
      var4.scrollTo(0.0F);
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      CreativeTabs var6 = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (var6.drawInForegroundOfTab()) {
         GlStateManager.disableBlend();
         this.fontRendererObj.D(I18n.format(var6.getTranslatedTabLabel()), 8.0F, 6.0F, 4210752);
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         int var7 = var1 - this.guiLeft;
         int var8 = var2 - this.guiTop;

         for (CreativeTabs var12 : CreativeTabs.creativeTabArray) {
            if (this.func_147049_a(var12, var7, var8)) {
               return;
            }
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      if (var3 == 0) {
         int var7 = var1 - this.guiLeft;
         int var8 = var2 - this.guiTop;

         for (CreativeTabs var12 : CreativeTabs.creativeTabArray) {
            if (this.func_147049_a(var12, var7, var8)) {
               this.setCurrentCreativeTab(var12);
               return;
            }
         }
      }

      super.mouseReleased(var1, var2, var3);
   }

   private boolean needsScrollBars() {
      return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex()
         && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory()
         && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).func_148328_e();
   }

   private void setCurrentCreativeTab(CreativeTabs var1) {
      int var5 = selectedTabIndex;
      selectedTabIndex = var1.getTabIndex();
      GuiContainerCreative.ContainerCreative var6 = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      this.dragSplittingSlots.clear();
      var6.itemList.clear();
      var1.displayAllReleventItems(var6.itemList);
      if (var1 == CreativeTabs.tabInventory) {
         Container var7 = this.mc.thePlayer.inventoryContainer;
         if (this.field_147063_B == null) {
            this.field_147063_B = var6.inventorySlots;
         }

         var6.inventorySlots = Lists.newArrayList();

         for (int var8 = 0; var8 < var7.inventorySlots.size(); var8++) {
            GuiContainerCreative.CreativeSlot var9 = new GuiContainerCreative.CreativeSlot(var7.inventorySlots.get(var8), var8);
            var6.inventorySlots.add(var9);
            if (var8 >= 5 && var8 < 9) {
               int var13 = var8 - 5;
               int var14 = var13 / 2;
               int var15 = var13 % 2;
               var9.xDisplayPosition = 9 + var14 * 54;
               var9.yDisplayPosition = 6 + var15 * 27;
            } else if (var8 >= 0 && var8 < 5) {
               var9.yDisplayPosition = -2000;
               var9.xDisplayPosition = -2000;
            } else if (var8 < var7.inventorySlots.size()) {
               int var10 = var8 - 9;
               int var11 = var10 % 9;
               int var12 = var10 / 9;
               var9.xDisplayPosition = 9 + var11 * 18;
               if (var8 >= 36) {
                  var9.yDisplayPosition = 112;
               } else {
                  var9.yDisplayPosition = 54 + var12 * 18;
               }
            }
         }

         this.field_147064_C = new Slot(field_147060_v, 0, 173, 112);
         var6.inventorySlots.add(this.field_147064_C);
      } else if (var5 == CreativeTabs.tabInventory.getTabIndex()) {
         var6.inventorySlots = this.field_147063_B;
         this.field_147063_B = null;
      }

      if (this.searchField != null) {
         if (var1 == CreativeTabs.tabAllSearch) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
         }
      }

      this.currentScroll = 0.0F;
      var6.scrollTo(0.0F);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int var4 = Mouse.getEventDWheel();
      if (var4 != 0 && this.needsScrollBars()) {
         int var5 = ((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5;
         if (var4 > 0) {
            var4 = 1;
         }

         if (var4 < 0) {
            var4 = -1;
         }

         this.currentScroll = (float)(this.currentScroll - (double)var4 / var5);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      boolean var7 = Mouse.isButtonDown(0);
      int var8 = this.guiLeft;
      int var9 = this.guiTop;
      int var10 = var8 + 175;
      int var11 = var9 + 18;
      int var12 = var10 + 14;
      int var13 = var11 + 112;
      if (!this.wasClicking && var7 && var1 >= var10 && var2 >= var11 && var1 < var12 && var2 < var13) {
         this.isScrolling = this.needsScrollBars();
      }

      if (!var7) {
         this.isScrolling = false;
      }

      this.wasClicking = var7;
      if (this.isScrolling) {
         this.currentScroll = (var2 - var11 - 7.5F) / (var13 - var11 - 15.0F);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

      super.drawScreen(var1, var2, var3);

      for (CreativeTabs var17 : CreativeTabs.creativeTabArray) {
         if (this.renderCreativeInventoryHoveringText(var17, var1, var2)) {
            break;
         }
      }

      if (this.field_147064_C != null
         && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()
         && this.isPointInRegion(this.field_147064_C.xDisplayPosition, this.field_147064_C.yDisplayPosition, 16, 16, var1, var2)) {
         this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot"), var1, var2);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
   }

   @Override
   public void renderToolTip(ItemStack var1, int var2, int var3) {
      if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
         List var7 = var1.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
         CreativeTabs var8 = var1.getItem().getCreativeTab();
         if (var8 == null && var1.getItem() == Items.enchanted_book) {
            Map var9 = EnchantmentHelper.getEnchantments(var1);
            if (var9.size() == 1) {
               Enchantment var10 = Enchantment.getEnchantmentById((Integer)var9.keySet().iterator().next());

               for (CreativeTabs var14 : CreativeTabs.creativeTabArray) {
                  if (var14.hasRelevantEnchantmentType(var10.type)) {
                     var8 = var14;
                     break;
                  }
               }
            }
         }

         if (var8 != null) {
            var7.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(var8.getTranslatedTabLabel()));
         }

         for (int var15 = 0; var15 < var7.size(); var15++) {
            if (var15 == 0) {
               var7.set(var15, var1.getRarity().rarityColor + (String)var7.get(var15));
            } else {
               var7.set(var15, EnumChatFormatting.GRAY + (String)var7.get(var15));
            }
         }

         this.drawHoveringText(var7, var2, var3);
      } else {
         super.renderToolTip(var1, var2, var3);
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      CreativeTabs var7 = CreativeTabs.creativeTabArray[selectedTabIndex];

      for (CreativeTabs var11 : CreativeTabs.creativeTabArray) {
         this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
         if (var11.getTabIndex() != selectedTabIndex) {
            this.func_147051_a(var11);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var7.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextBox();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int var12 = this.guiLeft + 175;
      int var13 = this.guiTop + 18;
      int var14 = var13 + 112;
      this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
      if (var7.shouldHidePlayerInventory()) {
         this.drawTexturedModalRect(var12, var13 + (int)((var14 - var13 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.func_147051_a(var7);
      if (var7 == CreativeTabs.tabInventory) {
         GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, this.guiLeft + 43 - var2, this.guiTop + 45 - 30 - var3, this.mc.thePlayer);
      }
   }

   public boolean func_147049_a(CreativeTabs var1, int var2, int var3) {
      int var7 = var1.getTabColumn();
      int var8 = 28 * var7;
      int var9 = 0;
      if (var7 == 5) {
         var8 = this.xSize - 28 + 2;
      } else if (var7 > 0) {
         var8 += var7;
      }

      if (var1.isTabInFirstRow()) {
         var9 -= 32;
      } else {
         var9 += this.ySize;
      }

      return var2 >= var8 && var2 <= var8 + 28 && var3 >= var9 && var3 <= var9 + 32;
   }

   public boolean renderCreativeInventoryHoveringText(CreativeTabs var1, int var2, int var3) {
      int var7 = var1.getTabColumn();
      int var8 = 28 * var7;
      int var9 = 0;
      if (var7 == 5) {
         var8 = this.xSize - 28 + 2;
      } else if (var7 > 0) {
         var8 += var7;
      }

      if (var1.isTabInFirstRow()) {
         var9 -= 32;
      } else {
         var9 += this.ySize;
      }

      if (this.isPointInRegion(var8 + 3, var9 + 3, 23, 27, var2, var3)) {
         this.drawCreativeTabHoveringText(I18n.format(var1.getTranslatedTabLabel()), var2, var3);
         return true;
      } else {
         return false;
      }
   }

   public void func_147051_a(CreativeTabs var1) {
      long var2 = bb ^ 77441089314959L;
      boolean var5 = var1.getTabIndex() == selectedTabIndex;
      boolean var6 = var1.isTabInFirstRow();
      int var7 = var1.getTabColumn();
      int var8 = var7 * 28;
      byte var9 = 0;
      int var10 = this.guiLeft + 28 * var7;
      int var11 = this.guiTop;
      long var10000 = 5442326765809611196L ^ var2;
      byte var12 = 32;
      if (var5) {
         var9 += 32;
      }

      if (var7 == 5) {
         var10 = this.guiLeft + this.xSize - 28;
      } else if (var7 > 0) {
         var10 += var7;
      }

      if (var6) {
         var11 -= 28;
      } else {
         var9 += 64;
         var11 += this.ySize - 4;
      }

      GlStateManager.disableLighting();
      this.drawTexturedModalRect(var10, var11, var8, var9, 28, var12);
      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      var10 += 6;
      var11 = var11 + 8 + (var6 ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack var13 = var1.getIconItemStack();
      this.itemRender.renderItemAndEffectIntoGUI(var13, var10, var11);
      this.itemRender.renderItemOverlays(this.fontRendererObj, var13, var10, var11);
      GlStateManager.disableLighting();
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (var1.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   public static class ContainerCreative extends Container {
      public List<ItemStack> itemList = Lists.newArrayList();
      private static final long a = 39788308248564L;

      public ContainerCreative(EntityPlayer var1) {
         InventoryPlayer var5 = var1.inventory;

         for (int var6 = 0; var6 < 5; var6++) {
            for (int var7 = 0; var7 < 9; var7++) {
               this.addSlotToContainer(new Slot(GuiContainerCreative.field_147060_v, var6 * 9 + var7, 9 + var7 * 18, 18 + var6 * 18));
            }
         }

         for (int var8 = 0; var8 < 9; var8++) {
            this.addSlotToContainer(new Slot(var5, var8, 9 + var8 * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      @Override
      public boolean canInteractWith(EntityPlayer var1) {
         return true;
      }

      public void scrollTo(float var1) {
         int var5 = (this.itemList.size() + 9 - 1) / 9 - 5;
         int var6 = (int)(var1 * var5 + 0.5);
         if (var6 < 0) {
            var6 = 0;
         }

         for (int var7 = 0; var7 < 5; var7++) {
            for (int var8 = 0; var8 < 9; var8++) {
               int var9 = var8 + (var7 + var6) * 9;
               if (var9 >= 0 && var9 < this.itemList.size()) {
                  GuiContainerCreative.field_147060_v.setInventorySlotContents(var8 + var7 * 9, this.itemList.get(var9));
               } else {
                  GuiContainerCreative.field_147060_v.setInventorySlotContents(var8 + var7 * 9, (ItemStack)null);
               }
            }
         }
      }

      public boolean func_148328_e() {
         return this.itemList.size() > 45;
      }

      @Override
      public void retrySlotClick(int var1, int var2, boolean var3, EntityPlayer var4) {
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
         if (var2 >= this.inventorySlots.size() - 9 && var2 < this.inventorySlots.size()) {
            Slot var6 = this.inventorySlots.get(var2);
            if (var6 != null && var6.getHasStack()) {
               var6.putStack((ItemStack)null);
            }
         }

         return null;
      }

      @Override
      public boolean canMergeSlot(ItemStack var1, Slot var2) {
         return var2.yDisplayPosition > 90;
      }

      @Override
      public boolean canDragIntoSlot(Slot var1) {
         return var1.inventory instanceof InventoryPlayer || var1.yDisplayPosition > 90 && var1.xDisplayPosition <= 162;
      }
   }

   public class CreativeSlot extends Slot {
      private final Slot slot;

      public CreativeSlot(Slot var2, int var3) {
         super(var2.inventory, var3, 0, 0);
         this.slot = var2;
      }

      @Override
      public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
         this.slot.onPickupFromSlot(var1, var2);
      }

      @Override
      public boolean isItemValid(ItemStack var1) {
         return this.slot.isItemValid(var1);
      }

      @Override
      public ItemStack getStack() {
         return this.slot.getStack();
      }

      @Override
      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      @Override
      public void putStack(ItemStack var1) {
         this.slot.putStack(var1);
      }

      @Override
      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      @Override
      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      @Override
      public int getItemStackLimit(ItemStack var1) {
         return this.slot.getItemStackLimit(var1);
      }

      @Override
      public String getSlotTexture() {
         return this.slot.getSlotTexture();
      }

      @Override
      public ItemStack decrStackSize(int var1) {
         return this.slot.decrStackSize(var1);
      }

      @Override
      public boolean isHere(IInventory var1, int var2) {
         return this.slot.isHere(var1, var2);
      }
   }
}
