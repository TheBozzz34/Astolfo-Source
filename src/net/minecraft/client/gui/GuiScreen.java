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
   private static final int I = new Color(7, 7, 7).getRGB();
   private static final int Z = new Color(17, 17, 17).getRGB();
   private static final long cb = 7985418319024L;

   public void drawScreen(int var1, int var2, float var3) {
      long var4 = cb ^ 6382636428709L;
      long var6 = var4 ^ 9065368047539L;

      for (LoadStage var10 : Astolfo.INSTANCE.x().e().a().L().getValue()) {
         if (var10 instanceof GuiStage) {
            GuiStage var11 = (GuiStage)var10;
            if (var11.C().z().F() && var11.C().z().a()) {
               return;
            }
         }
      }

      for (int var12 = 0; var12 < this.buttonList.size(); var12++) {
         this.buttonList.get(var12).drawButton(this.mc, var1, var2);
      }

      for (int var13 = 0; var13 < this.labelList.size(); var13++) {
         this.labelList.get(var13).drawLabel(this.mc, var1, var2);
      }
   }

   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }
   }

   public static String getClipboardString() {
      try {
         Transferable var3 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (var3 != null && var3.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)var3.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var4) {
      }

      return "";
   }

   public static void setClipboardString(String var0) {
      if (!StringUtils.isEmpty(var0)) {
         try {
            StringSelection var3 = new StringSelection(var0);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var3, (ClipboardOwner)null);
         } catch (Exception var4) {
         }
      }
   }

   public void renderToolTip(ItemStack var1, int var2, int var3) {
      List var7 = var1.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

      for (int var8 = 0; var8 < var7.size(); var8++) {
         if (var8 == 0) {
            var7.set(var8, var1.getRarity().rarityColor + (String)var7.get(var8));
         } else {
            var7.set(var8, EnumChatFormatting.GRAY + (String)var7.get(var8));
         }
      }

      this.drawHoveringText(var7, var2, var3);
   }

   public void drawCreativeTabHoveringText(String var1, int var2, int var3) {
      this.drawHoveringText(Arrays.asList(var1), var2, var3);
   }

   public void drawHoveringText(List<String> var1, int var2, int var3) {
      long var4 = cb ^ 103880243932332L;
      if (!var1.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         int var7 = 0;

         for (String var9 : var1) {
            int var10 = this.fontRendererObj.getStringWidth(var9);
            if (var10 > var7) {
               var7 = var10;
            }
         }

         int var16 = var2 + 12;
         int var17 = var3 - 12;
         int var18 = 8;
         if (var1.size() > 1) {
            var18 += 2 + (var1.size() - 1) * 10;
         }

         if (var16 + var7 > this.width) {
            var16 -= 28 + var7;
         }

         if (var17 + var18 + 6 > this.height) {
            var17 = this.height - var18 - 6;
         }

         this.zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         int var11 = -15592942;
         this.drawGradientRect(var16 - 3, var17 - 4, var16 + var7 + 3, var17 - 3, var11, var11);
         long var10000 = 7569057859365955873L ^ var4;
         this.drawGradientRect(var16 - 3, var17 + var18 + 3, var16 + var7 + 3, var17 + var18 + 4, var11, var11);
         this.drawGradientRect(var16 - 3, var17 - 3, var16 + var7 + 3, var17 + var18 + 3, var11, var11);
         this.drawGradientRect(var16 - 4, var17 - 3, var16 - 3, var17 + var18 + 3, var11, var11);
         this.drawGradientRect(var16 + var7 + 3, var17 - 3, var16 + var7 + 4, var17 + var18 + 3, var11, var11);
         int var12 = -14606047;
         int var13 = -14606047;
         this.drawGradientRect(var16 - 3, var17 - 3 + 1, var16 - 3 + 1, var17 + var18 + 3 - 1, var12, var13);
         this.drawGradientRect(var16 + var7 + 2, var17 - 3 + 1, var16 + var7 + 3, var17 + var18 + 3 - 1, var12, var13);
         this.drawGradientRect(var16 - 3, var17 - 3, var16 + var7 + 3, var17 - 3 + 1, var12, var12);
         this.drawGradientRect(var16 - 3, var17 + var18 + 2, var16 + var7 + 3, var17 + var18 + 3, var13, var13);

         for (int var14 = 0; var14 < var1.size(); var14++) {
            String var15 = (String)var1.get(var14);
            this.fontRendererObj.drawStringWithShadow(var15, var16, var17, -1);
            if (var14 == 0) {
               var17 += 2;
            }

            var17 += 10;
         }

         this.zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   public void handleComponentHover(IChatComponent var1, int var2, int var3) {
      if (var1 != null && var1.getChatStyle().getChatHoverEvent() != null) {
         HoverEvent var7 = var1.getChatStyle().getChatHoverEvent();
         if (var7.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var17 = null;

            try {
               NBTTagCompound var19 = JsonToNBT.getTagFromJson(var7.getValue().getUnformattedText());
               if (var19 instanceof NBTTagCompound) {
                  var17 = ItemStack.loadItemStackFromNBT(var19);
               }
            } catch (NBTException var14) {
            }

            if (var17 != null) {
               this.renderToolTip(var17, var2, var3);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", var2, var3);
            }
         } else if (var7.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound var16 = JsonToNBT.getTagFromJson(var7.getValue().getUnformattedText());
                  if (var16 instanceof NBTTagCompound) {
                     ArrayList var18 = Lists.newArrayList();
                     NBTTagCompound var20 = var16;
                     var18.add(var20.getString("name"));
                     if (var20.hasKey("type", 8)) {
                        String var21 = var20.getString("type");
                        var18.add("Type: " + var21 + " (" + EntityList.getIDFromString(var21) + ")");
                     }

                     var18.add(var20.getString("id"));
                     this.drawHoveringText(var18, var2, var3);
                  } else {
                     this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", var2, var3);
                  }
               } catch (NBTException var13) {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", var2, var3);
               }
            }
         } else if (var7.getAction() != HoverEvent.Action.SHOW_TEXT) {
            if (var7.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
               StatBase var15 = StatList.getOneShotStat(var7.getValue().getUnformattedText());
               if (var15 != null) {
                  IChatComponent var9 = var15.getStatName();
                  ChatComponentTranslation var10 = new ChatComponentTranslation("stats.tooltip.type." + (var15.isAchievement() ? "achievement" : "statistic"));
                  var10.getChatStyle().setItalic(true);
                  String var11 = var15 instanceof Achievement ? ((Achievement)var15).getDescription() : null;
                  ArrayList var12 = Lists.newArrayList(var9.getFormattedText(), var10.getFormattedText());
                  if (var11 != null) {
                     var12.addAll(this.fontRendererObj.listFormattedStringToWidth(var11, 150));
                  }

                  this.drawHoveringText(var12, var2, var3);
               } else {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", var2, var3);
               }
            }
         } else {
            ClickEvent var8 = var1.getChatStyle().getChatClickEvent();
            this.drawHoveringText(
               NEWLINE_SPLITTER.splitToList(
                  var7.getValue().getFormattedText() + (var8 != null && !var8.getValue().contains("TP") ? "\n" + var8.getValue() : "")
               ),
               var2,
               var3
            );
         }

         GlStateManager.disableLighting();
      }
   }

   public void setText(String var1, boolean var2) {
   }

   public boolean handleComponentClick(IChatComponent var1) {
      if (var1 == null) {
         return false;
      }

      ClickEvent var5 = var1.getChatStyle().getChatClickEvent();
      if (isShiftKeyDown()) {
         if (var1.getChatStyle().getInsertion() != null) {
            this.setText(var1.getChatStyle().getInsertion(), false);
         }
      } else if (var5 != null) {
         if (var5.getAction() == ClickEvent.Action.OPEN_URL) {
            if (!this.mc.gameSettings.chatLinks) {
               return false;
            }

            try {
               URI var6 = new URI(var5.getValue());
               String var7 = var6.getScheme();
               if (var7 == null) {
                  throw new URISyntaxException(var5.getValue(), "Missing protocol");
               }

               if (!PROTOCOLS.contains(var7.toLowerCase())) {
                  throw new URISyntaxException(var5.getValue(), "Unsupported protocol: " + var7.toLowerCase());
               }

               if (this.mc.gameSettings.chatLinksPrompt) {
                  this.clickedLinkURI = var6;
                  this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, var5.getValue(), 31102009, false));
               } else {
                  this.openWebLink(var6);
               }
            } catch (URISyntaxException var8) {
               LOGGER.error("Can't open url for " + var5, var8);
            }
         } else if (var5.getAction() == ClickEvent.Action.OPEN_FILE) {
            URI var9 = new File(var5.getValue()).toURI();
            this.openWebLink(var9);
         } else if (var5.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
            this.setText(var5.getValue(), true);
         } else if (var5.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.sendChatMessage(var5.getValue(), false);
         } else if (var5.getAction() != ClickEvent.Action.TWITCH_USER_INFO) {
            LOGGER.error("Don't know how to handle " + var5);
         }

         return true;
      }

      return false;
   }

   public void sendChatMessage(String var1) {
      this.sendChatMessage(var1, true);
   }

   public void sendChatMessage(String var1, boolean var2) {
      if (var2) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(var1);
      }

      this.mc.thePlayer.sendChatMessage(var1);
   }

   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         for (int var7 = 0; var7 < this.buttonList.size(); var7++) {
            GuiButton var8 = this.buttonList.get(var7);
            if (var8.mousePressed(this.mc, var1, var2)) {
               this.selectedButton = var8;
               var8.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(var8);
            }
         }
      }
   }

   public void mouseReleased(int var1, int var2, int var3) {
      if (this.selectedButton != null && var3 == 0) {
         this.selectedButton.mouseReleased(var1, var2);
         this.selectedButton = null;
      }
   }

   public void mouseClickMove(int var1, int var2, int var3, long var4) {
   }

   public void actionPerformed(GuiButton var1) throws IOException {
      switch (var1.id) {
         case -88888:
            LoginScreen.d = false;
            this.mc
               .mouseHelper
               .Y()
               .getEndpoint()
               .B()
               .J(this.mc.mouseHelper.Y().getEndpoint().getHost(), this.mc.mouseHelper.Y().getEndpoint().getPort().getPort());
      }
   }

   public void setWorldAndResolution(Minecraft var1, int var2, int var3) {
      this.mc = var1;
      this.itemRender = var1.getRenderItem();
      this.fontRendererObj = var1.fontRendererObj;
      this.width = var2;
      this.height = var3;
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
            this.I();
         }
      }
   }

   public void handleMouseInput() throws IOException {
      int var4 = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int var5 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int var6 = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
            return;
         }

         this.eventButton = var6;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(var4, var5, this.eventButton);
      } else if (var6 != -1) {
         if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseReleased(var4, var5, var6);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long var7 = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.mouseClickMove(var4, var5, this.eventButton, var7);
      }
   }

   public void I() throws IOException {
      switch (Util.getOSType()) {
         case WINDOWS:
            this.handleKeyboardInput();
            break;
         case LINUX:
         case OSX:
            try {
               this.j();
            } catch (Exception var5) {
               this.handleKeyboardInput();
            }
      }

      this.mc.dispatchKeypresses();
   }

   private void handleKeyboardInput() throws IOException {
      char var4 = Keyboard.getEventCharacter();
      int var5 = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState() || var5 == 0 && Character.isDefined(var4)) {
         this.keyTyped(var4, var5);
      }
   }

   private void j() throws IOException {
      char var4 = Keyboard.getEventCharacter();
      int var5 = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState() || var5 == 0 && Character.isDefined(var4)) {
         if (var5 == 88) {
            char[] var6 = Strings.nullToEmpty(JOptionPane.showInputDialog("")).toCharArray();

            for (char var10 : var6) {
               this.keyTyped(var10, 0);
            }

            return;
         }

         this.keyTyped(var4, var5);
      }
   }

   public void updateScreen() {
   }

   public void onGuiClosed() {
   }

   public void drawDefaultBackground() {
      this.drawWorldBackground(0);
   }

   public void drawWorldBackground(int var1) {
      if (this.mc.theWorld != null) {
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.drawBackground(var1);
      }
   }

   public void drawBackground(int var1) {
      this.drawGradientRect(0, 0, this.width, this.height, I, Z);
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
      if (var2 == 31102009) {
         if (var1) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }
   }

   private void openWebLink(URI var1) {
      try {
         Class var4 = Class.forName("java.awt.Desktop");
         Object var5 = var4.getMethod("getDesktop").invoke(null);
         var4.getMethod("browse", URI.class).invoke(var5, var1);
      } catch (Throwable var6) {
         LOGGER.error("Couldn't open link", var6);
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

   public static boolean isKeyComboCtrlX(int var0) {
      return var0 == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlV(int var0) {
      return var0 == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlC(int var0) {
      return var0 == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlA(int var0) {
      return var0 == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public void onResize(Minecraft var1, int var2, int var3) {
      this.setWorldAndResolution(var1, var2, var3);
   }
}
