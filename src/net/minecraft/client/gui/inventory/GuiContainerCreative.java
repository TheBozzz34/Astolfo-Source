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

   public GuiContainerCreative(EntityPlayer entityPlayer) {
      super(new GuiContainerCreative.ContainerCreative(entityPlayer));
      entityPlayer.openContainer = this.inventorySlots;
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
   public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      this.field_147057_D = true;
      boolean bl = clickType == 1;
      clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
      if (slotIn == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && clickType != 5) {
         InventoryPlayer inventoryPlayer = this.mc.thePlayer.inventory;
         if (inventoryPlayer.getItemStack() != null) {
            if (clickedButton == 0) {
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack(), true);
               this.mc.playerController.sendPacketDropItem(inventoryPlayer.getItemStack());
               inventoryPlayer.setItemStack((ItemStack)null);
            }

            if (clickedButton == 1) {
               ItemStack itemStack = inventoryPlayer.getItemStack().splitStack(1);
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemStack, true);
               this.mc.playerController.sendPacketDropItem(itemStack);
               if (inventoryPlayer.getItemStack().stackSize == 0) {
                  inventoryPlayer.setItemStack((ItemStack)null);
               }
            }
         }
      } else if (slotIn == this.field_147064_C && bl) {
         for (int i = 0; i < this.mc.thePlayer.inventoryContainer.getInventory().size(); i++) {
            this.mc.playerController.sendSlotPacket((ItemStack)null, i);
         }
      } else if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
         if (slotIn == this.field_147064_C) {
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else if (clickType == 4 && slotIn != null && slotIn.getHasStack()) {
            ItemStack itemStack2 = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemStack2, true);
            this.mc.playerController.sendPacketDropItem(itemStack2);
         } else if (clickType == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack(), true);
            this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else {
            this.mc
               .thePlayer
               .inventoryContainer
               .slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
            this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
         }
      } else if (clickType != 5 && slotIn.inventory == field_147060_v) {
         InventoryPlayer inventoryPlayer2 = this.mc.thePlayer.inventory;
         ItemStack itemStack3 = inventoryPlayer2.getItemStack();
         ItemStack itemStack4 = slotIn.getStack();
         if (clickType == 2) {
            if (itemStack4 != null && clickedButton >= 0 && clickedButton < 9) {
               ItemStack itemStack5 = itemStack4.copy();
               itemStack5.stackSize = itemStack5.getMaxStackSize();
               this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, itemStack5);
               this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }

            return;
         }

         if (clickType == 3) {
            if (inventoryPlayer2.getItemStack() == null && slotIn.getHasStack()) {
               ItemStack itemStack6 = slotIn.getStack().copy();
               itemStack6.stackSize = itemStack6.getMaxStackSize();
               inventoryPlayer2.setItemStack(itemStack6);
            }

            return;
         }

         if (clickType == 4) {
            if (itemStack4 != null) {
               ItemStack itemStack7 = itemStack4.copy();
               itemStack7.stackSize = clickedButton == 0 ? 1 : itemStack7.getMaxStackSize();
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemStack7, true);
               this.mc.playerController.sendPacketDropItem(itemStack7);
            }

            return;
         }

         if (itemStack3 != null && itemStack4 != null && itemStack3.isItemEqual(itemStack4)) {
            if (clickedButton == 0) {
               if (bl) {
                  itemStack3.stackSize = itemStack3.getMaxStackSize();
               } else if (itemStack3.stackSize < itemStack3.getMaxStackSize()) {
                  itemStack3.stackSize++;
               }
            } else if (itemStack3.stackSize <= 1) {
               inventoryPlayer2.setItemStack((ItemStack)null);
            } else {
               itemStack3.stackSize--;
            }
         } else if (itemStack4 != null && itemStack3 == null) {
            inventoryPlayer2.setItemStack(ItemStack.copyItemStack(itemStack4));
            itemStack3 = inventoryPlayer2.getItemStack();
            if (bl) {
               itemStack3.stackSize = itemStack3.getMaxStackSize();
            }
         } else {
            inventoryPlayer2.setItemStack((ItemStack)null);
         }
      } else {
         this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);
         if (Container.getDragEvent(clickedButton) == 2) {
            for (int j = 0; j < 9; j++) {
               this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + j).getStack(), 36 + j);
            }
         } else if (slotIn != null) {
            ItemStack itemStack8 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
            this.mc.playerController.sendSlotPacket(itemStack8, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
         }
      }
   }

   @Override
   public void updateActivePotionEffects() {
      int i = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != i) {
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
         int i = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[i]);
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
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
         if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
         } else {
            super.keyTyped(typedChar, keyCode);
         }
      } else {
         if (this.field_147057_D) {
            this.field_147057_D = false;
            this.searchField.setText("");
         }

         if (!this.checkHotbarKeys(keyCode)) {
            if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
               this.updateCreativeSearch();
            } else {
               super.keyTyped(typedChar, keyCode);
            }
         }
      }
   }

   private void updateCreativeSearch() {
      GuiContainerCreative.ContainerCreative containerCreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      containerCreative.itemList.clear();

      for (Item item : Item.itemRegistry) {
         if (item != null && item.getCreativeTab() != null) {
            item.getSubItems(item, (CreativeTabs)null, containerCreative.itemList);
         }
      }

      for (Enchantment enchantment : Enchantment.enchantmentsBookList) {
         if (enchantment != null && enchantment.type != null) {
            Items.enchanted_book.getAll(enchantment, containerCreative.itemList);
         }
      }

      Iterator iterator2 = containerCreative.itemList.iterator();
      String string = this.searchField.getText().toLowerCase();

      while (iterator2.hasNext()) {
         ItemStack itemStack = (ItemStack)iterator2.next();
         boolean bl = false;

         for (String string2 : itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)) {
            if (EnumChatFormatting.getTextWithoutFormattingCodes(string2).toLowerCase().contains(string)) {
               bl = true;
               break;
            }
         }

         if (!bl) {
            iterator2.remove();
         }
      }

      this.currentScroll = 0.0F;
      containerCreative.scrollTo(0.0F);
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (creativeTabs.drawInForegroundOfTab()) {
         GlStateManager.disableBlend();
         this.fontRendererObj.D(I18n.format(creativeTabs.getTranslatedTabLabel()), 8.0F, 6.0F, 4210752);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         int i = mouseX - this.guiLeft;
         int j = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.func_147049_a(creativeTabs, i, j)) {
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state == 0) {
         int i = mouseX - this.guiLeft;
         int j = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.func_147049_a(creativeTabs, i, j)) {
               this.setCurrentCreativeTab(creativeTabs);
               return;
            }
         }
      }

      super.mouseReleased(mouseX, mouseY, state);
   }

   private boolean needsScrollBars() {
      return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex()
         && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory()
         && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).func_148328_e();
   }

   private void setCurrentCreativeTab(CreativeTabs creativeTabs) {
      int i = selectedTabIndex;
      selectedTabIndex = creativeTabs.getTabIndex();
      GuiContainerCreative.ContainerCreative containerCreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      this.dragSplittingSlots.clear();
      containerCreative.itemList.clear();
      creativeTabs.displayAllReleventItems(containerCreative.itemList);
      if (creativeTabs == CreativeTabs.tabInventory) {
         Container container = this.mc.thePlayer.inventoryContainer;
         if (this.field_147063_B == null) {
            this.field_147063_B = containerCreative.inventorySlots;
         }

         containerCreative.inventorySlots = Lists.newArrayList();

         for (int j = 0; j < container.inventorySlots.size(); j++) {
            GuiContainerCreative.CreativeSlot creativeSlot = new GuiContainerCreative.CreativeSlot(container.inventorySlots.get(j), j);
            containerCreative.inventorySlots.add(creativeSlot);
            if (j >= 5 && j < 9) {
               int k = j - 5;
               int l = k / 2;
               int m = k % 2;
               creativeSlot.xDisplayPosition = 9 + l * 54;
               creativeSlot.yDisplayPosition = 6 + m * 27;
            } else if (j >= 0 && j < 5) {
               creativeSlot.yDisplayPosition = -2000;
               creativeSlot.xDisplayPosition = -2000;
            } else if (j < container.inventorySlots.size()) {
               int n = j - 9;
               int i2 = n % 9;
               int i3 = n / 9;
               creativeSlot.xDisplayPosition = 9 + i2 * 18;
               if (j >= 36) {
                  creativeSlot.yDisplayPosition = 112;
               } else {
                  creativeSlot.yDisplayPosition = 54 + i3 * 18;
               }
            }
         }

         this.field_147064_C = new Slot(field_147060_v, 0, 173, 112);
         containerCreative.inventorySlots.add(this.field_147064_C);
      } else if (i == CreativeTabs.tabInventory.getTabIndex()) {
         containerCreative.inventorySlots = this.field_147063_B;
         this.field_147063_B = null;
      }

      if (this.searchField != null) {
         if (creativeTabs == CreativeTabs.tabAllSearch) {
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
      containerCreative.scrollTo(0.0F);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int i = Mouse.getEventDWheel();
      if (i != 0 && this.needsScrollBars()) {
         int j = ((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5;
         if (i > 0) {
            i = 1;
         }

         if (i < 0) {
            i = -1;
         }

         this.currentScroll = (float)(this.currentScroll - (double)i / j);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean bl = Mouse.isButtonDown(0);
      int i = this.guiLeft;
      int j = this.guiTop;
      int k = i + 175;
      int l = j + 18;
      int m = k + 14;
      int n = l + 112;
      if (!this.wasClicking && bl && mouseX >= k && mouseY >= l && mouseX < m && mouseY < n) {
         this.isScrolling = this.needsScrollBars();
      }

      if (!bl) {
         this.isScrolling = false;
      }

      this.wasClicking = bl;
      if (this.isScrolling) {
         this.currentScroll = (mouseY - l - 7.5F) / (n - l - 15.0F);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);

      for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
         if (this.renderCreativeInventoryHoveringText(creativeTabs, mouseX, mouseY)) {
            break;
         }
      }

      if (this.field_147064_C != null
         && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()
         && this.isPointInRegion(this.field_147064_C.xDisplayPosition, this.field_147064_C.yDisplayPosition, 16, 16, mouseX, mouseY)) {
         this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
   }

   @Override
   public void renderToolTip(ItemStack stack, int x, int y) {
      if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
         List list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
         CreativeTabs creativeTabs = stack.getItem().getCreativeTab();
         if (creativeTabs == null && stack.getItem() == Items.enchanted_book) {
            Map map = EnchantmentHelper.getEnchantments(stack);
            if (map.size() == 1) {
               Enchantment enchantment = Enchantment.getEnchantmentById((Integer)map.keySet().iterator().next());

               for (CreativeTabs creativeTabs2 : CreativeTabs.creativeTabArray) {
                  if (creativeTabs2.hasRelevantEnchantmentType(enchantment.type)) {
                     creativeTabs = creativeTabs2;
                     break;
                  }
               }
            }
         }

         if (creativeTabs != null) {
            list.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(creativeTabs.getTranslatedTabLabel()));
         }

         for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
               list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            } else {
               list.set(i, EnumChatFormatting.GRAY + (String)list.get(i));
            }
         }

         this.drawHoveringText(list, x, y);
      } else {
         super.renderToolTip(stack, x, y);
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];

      for (CreativeTabs creativeTabs2 : CreativeTabs.creativeTabArray) {
         this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
         if (creativeTabs2.getTabIndex() != selectedTabIndex) {
            this.func_147051_a(creativeTabs2);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativeTabs.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextBox();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.guiLeft + 175;
      int j = this.guiTop + 18;
      int k = j + 112;
      this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
      if (creativeTabs.shouldHidePlayerInventory()) {
         this.drawTexturedModalRect(i, j + (int)((k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.func_147051_a(creativeTabs);
      if (creativeTabs == CreativeTabs.tabInventory) {
         GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, this.guiLeft + 43 - mouseX, this.guiTop + 45 - 30 - mouseY, this.mc.thePlayer);
      }
   }

   public boolean func_147049_a(CreativeTabs creativeTabs, int i, int j) {
      int k = creativeTabs.getTabColumn();
      int l = 28 * k;
      int m = 0;
      if (k == 5) {
         l = this.xSize - 28 + 2;
      } else if (k > 0) {
         l += k;
      }

      if (creativeTabs.isTabInFirstRow()) {
         m -= 32;
      } else {
         m += this.ySize;
      }

      return i >= l && i <= l + 28 && j >= m && j <= m + 32;
   }

   public boolean renderCreativeInventoryHoveringText(CreativeTabs creativeTabs, int i, int j) {
      int k = creativeTabs.getTabColumn();
      int l = 28 * k;
      int m = 0;
      if (k == 5) {
         l = this.xSize - 28 + 2;
      } else if (k > 0) {
         l += k;
      }

      if (creativeTabs.isTabInFirstRow()) {
         m -= 32;
      } else {
         m += this.ySize;
      }

      if (this.isPointInRegion(l + 3, m + 3, 23, 27, i, j)) {
         this.drawCreativeTabHoveringText(I18n.format(creativeTabs.getTranslatedTabLabel()), i, j);
         return true;
      } else {
         return false;
      }
   }

   public void func_147051_a(CreativeTabs creativeTabs) {
      boolean bl = creativeTabs.getTabIndex() == selectedTabIndex;
      boolean bl2 = creativeTabs.isTabInFirstRow();
      int i = creativeTabs.getTabColumn();
      int j = i * 28;
      byte b = 0;
      int k = this.guiLeft + 28 * i;
      int l = this.guiTop;
      byte b2 = 32;
      if (bl) {
         b += 32;
      }

      if (i == 5) {
         k = this.guiLeft + this.xSize - 28;
      } else if (i > 0) {
         k += i;
      }

      if (bl2) {
         l -= 28;
      } else {
         b += 64;
         l += this.ySize - 4;
      }

      GlStateManager.disableLighting();
      this.drawTexturedModalRect(k, l, j, b, 28, b2);
      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      k += 6;
      l = l + 8 + (bl2 ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack itemStack = creativeTabs.getIconItemStack();
      this.itemRender.renderItemAndEffectIntoGUI(itemStack, k, l);
      this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, k, l);
      GlStateManager.disableLighting();
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   public static class ContainerCreative extends Container {
      public List<ItemStack> itemList = Lists.newArrayList();

      public ContainerCreative(EntityPlayer entityPlayer) {
         InventoryPlayer inventoryPlayer = entityPlayer.inventory;

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
               this.addSlotToContainer(new Slot(GuiContainerCreative.field_147060_v, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      @Override
      public boolean canInteractWith(EntityPlayer playerIn) {
         return true;
      }

      public void scrollTo(float f) {
         int i = (this.itemList.size() + 9 - 1) / 9 - 5;
         int j = (int)(f * i + 0.5);
         if (j < 0) {
            j = 0;
         }

         for (int k = 0; k < 5; k++) {
            for (int l = 0; l < 9; l++) {
               int m = l + (k + j) * 9;
               if (m >= 0 && m < this.itemList.size()) {
                  GuiContainerCreative.field_147060_v.setInventorySlotContents(l + k * 9, this.itemList.get(m));
               } else {
                  GuiContainerCreative.field_147060_v.setInventorySlotContents(l + k * 9, (ItemStack)null);
               }
            }
         }
      }

      public boolean func_148328_e() {
         return this.itemList.size() > 45;
      }

      @Override
      public void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
         if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
               slot.putStack((ItemStack)null);
            }
         }

         return null;
      }

      @Override
      public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
         return slotIn.yDisplayPosition > 90;
      }

      @Override
      public boolean canDragIntoSlot(Slot slot) {
         return slot.inventory instanceof InventoryPlayer || slot.yDisplayPosition > 90 && slot.xDisplayPosition <= 162;
      }
   }

   public class CreativeSlot extends Slot {
      private final Slot slot;

      public CreativeSlot(Slot slot, int j) {
         super(slot.inventory, j, 0, 0);
         this.slot = slot;
      }

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
         this.slot.onPickupFromSlot(playerIn, stack);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return this.slot.isItemValid(stack);
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
      public void putStack(ItemStack stack) {
         this.slot.putStack(stack);
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
      public int getItemStackLimit(ItemStack stack) {
         return this.slot.getItemStackLimit(stack);
      }

      @Override
      public String getSlotTexture() {
         return this.slot.getSlotTexture();
      }

      @Override
      public ItemStack decrStackSize(int amount) {
         return this.slot.decrStackSize(amount);
      }

      @Override
      public boolean isHere(IInventory inv, int slotIn) {
         return this.slot.isHere(inv, slotIn);
      }
   }
}
