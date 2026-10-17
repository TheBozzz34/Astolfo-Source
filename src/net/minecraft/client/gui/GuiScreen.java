package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.gui.screen.LoginScreen;
import bypass.loader.GuiStage;
import bypass.loader.LoadStage;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
   private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
   public Minecraft mc;
   public RenderItem itemRender;
   public int width;
   public int height;
   public List<GuiButton> buttonList = Lists.newArrayList();
   public List<GuiLabel> labelList = Lists.newArrayList();
   public boolean allowUserInput;
   public FontRenderer fontRendererObj;
   private GuiButton selectedButton;
   private int eventButton;
   private long lastMouseEvent;
   private int touchValue;
   private URI clickedLinkURI;
   private static final int BACKGROUND_GRADIENT_TOP = new Color(7, 7, 7).getRGB();
   private static final int BACKGROUND_GRADIENT_BOTTOM = new Color(17, 17, 17).getRGB();

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      for (LoadStage loadStage : Astolfo.INSTANCE.getLoaderGuard().getStageList().getStageContainer().getOwnedContainer().getValue()) {
         if (loadStage instanceof GuiStage) {
            GuiStage guiStage = (GuiStage)loadStage;
            if (guiStage.getIntegrityCheckHolder().getIntegrityCheck().isFlag5() && guiStage.getIntegrityCheckHolder().getIntegrityCheck().isFlag2()) {
               return;
            }
         }
      }

      for (int i = 0; i < this.buttonList.size(); i++) {
         this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
      }

      for (int j = 0; j < this.labelList.size(); j++) {
         this.labelList.get(j).drawLabel(this.mc, mouseX, mouseY);
      }
   }

   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }
   }

   public static String getClipboardString() {
      try {
         Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)transferable.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception exception) {
      }

      return "";
   }

   public static void setClipboardString(String copyText) {
      if (!StringUtils.isEmpty(copyText)) {
         try {
            StringSelection stringSelection = new StringSelection(copyText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, (ClipboardOwner)null);
         } catch (Exception exception) {
         }
      }
   }

   public void renderToolTip(ItemStack stack, int x, int y) {
      List list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

      for (int i = 0; i < list.size(); i++) {
         if (i == 0) {
            list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
         } else {
            list.set(i, EnumChatFormatting.GRAY + (String)list.get(i));
         }
      }

      this.drawHoveringText(list, x, y);
   }

   public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
      this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
   }

   public void drawHoveringText(List<String> textLines, int x, int y) {
      if (!textLines.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         int i = 0;

         for (String string : textLines) {
            int j = this.fontRendererObj.getStringWidth(string);
            if (j > i) {
               i = j;
            }
         }

         int k = x + 12;
         int l = y - 12;
         int m = 8;
         if (textLines.size() > 1) {
            m += 2 + (textLines.size() - 1) * 10;
         }

         if (k + i > this.width) {
            k -= 28 + i;
         }

         if (l + m + 6 > this.height) {
            l = this.height - m - 6;
         }

         this.zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         int n = -15592942;
         this.drawGradientRect(k - 3, l - 4, k + i + 3, l - 3, n, n);
         this.drawGradientRect(k - 3, l + m + 3, k + i + 3, l + m + 4, n, n);
         this.drawGradientRect(k - 3, l - 3, k + i + 3, l + m + 3, n, n);
         this.drawGradientRect(k - 4, l - 3, k - 3, l + m + 3, n, n);
         this.drawGradientRect(k + i + 3, l - 3, k + i + 4, l + m + 3, n, n);
         int i2 = -14606047;
         int i3 = -14606047;
         this.drawGradientRect(k - 3, l - 3 + 1, k - 3 + 1, l + m + 3 - 1, i2, i3);
         this.drawGradientRect(k + i + 2, l - 3 + 1, k + i + 3, l + m + 3 - 1, i2, i3);
         this.drawGradientRect(k - 3, l - 3, k + i + 3, l - 3 + 1, i2, i2);
         this.drawGradientRect(k - 3, l + m + 2, k + i + 3, l + m + 3, i3, i3);

         for (int i4 = 0; i4 < textLines.size(); i4++) {
            String string2 = (String)textLines.get(i4);
            this.fontRendererObj.drawStringWithShadow(string2, k, l, -1);
            if (i4 == 0) {
               l += 2;
            }

            l += 10;
         }

         this.zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   public void handleComponentHover(IChatComponent component, int x, int y) {
      if (component != null && component.getChatStyle().getChatHoverEvent() != null) {
         HoverEvent hoverEvent = component.getChatStyle().getChatHoverEvent();
         if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = null;

            try {
               NBTTagCompound nbtTagCompound = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
               if (nbtTagCompound instanceof NBTTagCompound) {
                  itemStack = ItemStack.loadItemStackFromNBT(nbtTagCompound);
               }
            } catch (NBTException nbtException) {
            }

            if (itemStack != null) {
               this.renderToolTip(itemStack, x, y);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
            }
         } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound nbtTagCompound2 = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
                  if (nbtTagCompound2 instanceof NBTTagCompound) {
                     ArrayList arrayList = Lists.newArrayList();
                     NBTTagCompound nbtTagCompound3 = nbtTagCompound2;
                     arrayList.add(nbtTagCompound3.getString("name"));
                     if (nbtTagCompound3.hasKey("type", 8)) {
                        String string = nbtTagCompound3.getString("type");
                        arrayList.add("Type: " + string + " (" + EntityList.getIDFromString(string) + ")");
                     }

                     arrayList.add(nbtTagCompound3.getString("id"));
                     this.drawHoveringText(arrayList, x, y);
                  } else {
                     this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                  }
               } catch (NBTException nbtException2) {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
               }
            }
         } else if (hoverEvent.getAction() != HoverEvent.Action.SHOW_TEXT) {
            if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
               StatBase statBase = StatList.getOneShotStat(hoverEvent.getValue().getUnformattedText());
               if (statBase != null) {
                  IChatComponent iChatComponent = statBase.getStatName();
                  ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (statBase.isAchievement() ? "achievement" : "statistic"));
                  chatComponentTranslation.getChatStyle().setItalic(true);
                  String string2 = statBase instanceof Achievement ? ((Achievement)statBase).getDescription() : null;
                  ArrayList arrayList2 = Lists.newArrayList(iChatComponent.getFormattedText(), chatComponentTranslation.getFormattedText());
                  if (string2 != null) {
                     arrayList2.addAll(this.fontRendererObj.listFormattedStringToWidth(string2, 150));
                  }

                  this.drawHoveringText(arrayList2, x, y);
               } else {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
               }
            }
         } else {
            ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();
            this.drawHoveringText(
               NEWLINE_SPLITTER.splitToList(
                  hoverEvent.getValue().getFormattedText() + (clickEvent != null && !clickEvent.getValue().contains("TP") ? "\n" + clickEvent.getValue() : "")
               ),
               x,
               y
            );
         }

         GlStateManager.disableLighting();
      }
   }

   public void setText(String newChatText, boolean shouldOverwrite) {
   }

   public boolean handleComponentClick(IChatComponent component) {
      if (component == null) {
         return false;
      }

      ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();
      if (isShiftKeyDown()) {
         if (component.getChatStyle().getInsertion() != null) {
            this.setText(component.getChatStyle().getInsertion(), false);
         }
      } else if (clickEvent != null) {
         if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            if (!this.mc.gameSettings.chatLinks) {
               return false;
            }

            try {
               URI uri = new URI(clickEvent.getValue());
               String string = uri.getScheme();
               if (string == null) {
                  throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
               }

               if (!PROTOCOLS.contains(string.toLowerCase())) {
                  throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase());
               }

               if (this.mc.gameSettings.chatLinksPrompt) {
                  this.clickedLinkURI = uri;
                  this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickEvent.getValue(), 31102009, false));
               } else {
                  this.openWebLink(uri);
               }
            } catch (URISyntaxException uriSyntaxException) {
               LOGGER.error("Can't open url for " + clickEvent, uriSyntaxException);
            }
         } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
            URI uri2 = new File(clickEvent.getValue()).toURI();
            this.openWebLink(uri2);
         } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
            this.setText(clickEvent.getValue(), true);
         } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.sendChatMessage(clickEvent.getValue(), false);
         } else if (clickEvent.getAction() != ClickEvent.Action.TWITCH_USER_INFO) {
            LOGGER.error("Don't know how to handle " + clickEvent);
         }

         return true;
      }

      return false;
   }

   public void sendChatMessage(String msg) {
      this.sendChatMessage(msg, true);
   }

   public void sendChatMessage(String msg, boolean addToChat) {
      if (addToChat) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
      }

      this.mc.thePlayer.sendChatMessage(msg);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         for (int i = 0; i < this.buttonList.size(); i++) {
            GuiButton guiButton = this.buttonList.get(i);
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               this.selectedButton = guiButton;
               guiButton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guiButton);
            }
         }
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.selectedButton != null && state == 0) {
         this.selectedButton.mouseReleased(mouseX, mouseY);
         this.selectedButton = null;
      }
   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
   }

   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case -88888:
            LoginScreen.loginFailed = false;
            this.mc
               .mouseHelper
               .getAuthSession()
               .getEndpoint()
               .getConnection()
               .connect(this.mc.mouseHelper.getAuthSession().getEndpoint().getHost(), this.mc.mouseHelper.getAuthSession().getEndpoint().getPort().getPort());
      }
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.mc = mc;
      this.itemRender = mc.getRenderItem();
      this.fontRendererObj = mc.fontRendererObj;
      this.width = width;
      this.height = height;
      this.buttonList.clear();
      this.initGui();
   }

   public void initGui() {
   }

   public void handleInput() throws IOException {
      if (Mouse.isCreated()) {
         while (Mouse.next()) {
            this.handleMouseInput();
         }
      }

      if (Keyboard.isCreated()) {
         while (Keyboard.next()) {
            this.handleKeyboardEvent();
         }
      }
   }

   public void handleMouseInput() throws IOException {
      int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int k = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
            return;
         }

         this.eventButton = k;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(i, j, this.eventButton);
      } else if (k != -1) {
         if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseReleased(i, j, k);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long l = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.mouseClickMove(i, j, this.eventButton, l);
      }
   }

   public void handleKeyboardEvent() throws IOException {
      switch (Util.getOSType()) {
         case WINDOWS:
            this.handleKeyboardInput();
            break;
         case LINUX:
         case OSX:
            try {
               this.handleKeyboardInputWithPaste();
            } catch (Exception exception) {
               this.handleKeyboardInput();
            }
      }

      this.mc.dispatchKeypresses();
   }

   private void handleKeyboardInput() throws IOException {
      char c = Keyboard.getEventCharacter();
      int i = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState() || i == 0 && Character.isDefined(c)) {
         this.keyTyped(c, i);
      }
   }

   private void handleKeyboardInputWithPaste() throws IOException {
      char c = Keyboard.getEventCharacter();
      int i = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState() || i == 0 && Character.isDefined(c)) {
         if (i == 88) {
            char[] chars = Strings.nullToEmpty(JOptionPane.showInputDialog("")).toCharArray();

            for (char c2 : chars) {
               this.keyTyped(c2, 0);
            }

            return;
         }

         this.keyTyped(c, i);
      }
   }

   public void updateScreen() {
   }

   public void onGuiClosed() {
   }

   public void drawDefaultBackground() {
      this.drawWorldBackground(0);
   }

   public void drawWorldBackground(int tint) {
      if (this.mc.theWorld != null) {
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.drawBackground(tint);
      }
   }

   public void drawBackground(int tint) {
      this.drawGradientRect(0, 0, this.width, this.height, BACKGROUND_GRADIENT_TOP, BACKGROUND_GRADIENT_BOTTOM);
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (id == 31102009) {
         if (result) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }
   }

   private void openWebLink(URI url) {
      try {
         Class class2 = Class.forName("java.awt.Desktop");
         Object object = class2.getMethod("getDesktop").invoke(null);
         class2.getMethod("browse", URI.class).invoke(object, url);
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't open link", throwable);
      }
   }

   public static boolean isCtrlKeyDown() {
      return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
   }

   public static boolean isShiftKeyDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public static boolean isAltKeyDown() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }

   public static boolean isKeyComboCtrlX(int keyID) {
      return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlV(int keyID) {
      return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlC(int keyID) {
      return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlA(int keyID) {
      return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public void onResize(Minecraft mcIn, int w, int h) {
      this.setWorldAndResolution(mcIn, w, h);
   }
}
