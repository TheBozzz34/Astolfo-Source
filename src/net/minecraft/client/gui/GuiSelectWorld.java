package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
   private static final Logger logger = LogManager.getLogger();
   private final DateFormat field_146633_h = new SimpleDateFormat();
   public GuiScreen parentScreen;
   public String screenTitle = "Select world";
   private boolean field_146634_i;
   private int selectedIndex;
   private java.util.List<SaveFormatComparator> field_146639_s;
   private GuiSelectWorld.List availableWorlds;
   private String field_146637_u;
   private String field_146636_v;
   private String[] field_146635_w = new String[4];
   private boolean confirmingDelete;
   private GuiButton deleteButton;
   private GuiButton selectButton;
   private GuiButton renameButton;
   private GuiButton recreateButton;
   private static final long e = 101737716579130L;

   public GuiSelectWorld(GuiScreen var1) {
      this.parentScreen = var1;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("selectWorld.title");

      try {
         this.loadLevelList();
      } catch (AnvilConverterException var4) {
         logger.error("Couldn't load level list", var4);
         this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", var4.getMessage()));
         return;
      }

      this.field_146637_u = I18n.format("selectWorld.world");
      this.field_146636_v = I18n.format("selectWorld.conversion");
      this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
      this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
      this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
      this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
      this.availableWorlds = new GuiSelectWorld.List(this.mc);
      this.availableWorlds.U(4, 5);
      this.addWorldSelectionButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.availableWorlds.handleMouseInput();
   }

   private void loadLevelList() throws AnvilConverterException {
      ISaveFormat var1 = this.mc.getSaveLoader();
      this.field_146639_s = var1.getSaveList();
      Collections.sort(this.field_146639_s);
      this.selectedIndex = -1;
   }

   public String func_146621_a(int var1) {
      return this.field_146639_s.get(var1).getFileName();
   }

   public String func_146614_d(int var1) {
      String var5 = this.field_146639_s.get(var1).getDisplayName();
      if (StringUtils.isEmpty(var5)) {
         var5 = I18n.format("selectWorld.world") + " " + (var1 + 1);
      }

      return var5;
   }

   public void addWorldSelectionButtons() {
      this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
      this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
      this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
      this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.recreateButton.enabled = false;
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 2) {
            String var5 = this.func_146614_d(this.selectedIndex);
            if (var5 != null) {
               this.confirmingDelete = true;
               GuiYesNo var6 = makeDeleteWorldYesNo(this, var5, this.selectedIndex);
               this.mc.displayGuiScreen(var6);
            }
         } else if (var1.id == 1) {
            this.func_146615_e(this.selectedIndex);
         } else if (var1.id == 3) {
            this.mc.displayGuiScreen(new GuiCreateWorld(this));
         } else if (var1.id == 6) {
            this.mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.selectedIndex)));
         } else if (var1.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (var1.id == 7) {
            GuiCreateWorld var8 = new GuiCreateWorld(this);
            ISaveHandler var9 = this.mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
            WorldInfo var7 = var9.loadWorldInfo();
            var9.flush();
            var8.recreateFromExistingWorld(var7);
            this.mc.displayGuiScreen(var8);
         } else {
            this.availableWorlds.actionPerformed(var1);
         }
      }
   }

   public void func_146615_e(int var1) {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.field_146634_i) {
         this.field_146634_i = true;
         String var5 = this.func_146621_a(var1);
         if (var5 == null) {
            var5 = "World" + var1;
         }

         String var6 = this.func_146614_d(var1);
         if (var6 == null) {
            var6 = "World" + var1;
         }

         if (this.mc.getSaveLoader().canLoadWorld(var5)) {
            this.mc.launchIntegratedServer(var5, var6, (WorldSettings)null);
         }
      }
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
      if (this.confirmingDelete) {
         this.confirmingDelete = false;
         if (var1) {
            ISaveFormat var6 = this.mc.getSaveLoader();
            var6.flushCache();
            var6.deleteWorldDirectory(this.func_146621_a(var2));

            try {
               this.loadLevelList();
            } catch (AnvilConverterException var8) {
               logger.error("Couldn't load level list", var8);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.availableWorlds.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback var0, String var1, int var2) {
      String var5 = I18n.format("selectWorld.deleteQuestion");
      String var6 = "'" + var1 + "' " + I18n.format("selectWorld.deleteWarning");
      String var7 = I18n.format("selectWorld.deleteButton");
      String var8 = I18n.format("gui.cancel");
      return new GuiYesNo(var0, var5, var6, var7, var8, var2);
   }

   public class List extends GuiSlot {
      private static final long d = 126369446155060L;

      public List(Minecraft var2) {
         super(var2, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
      }

      @Override
      public int getSize() {
         return GuiSelectWorld.this.field_146639_s.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         GuiSelectWorld.this.selectedIndex = var1;
         boolean var8 = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
         GuiSelectWorld.this.selectButton.enabled = var8;
         GuiSelectWorld.this.deleteButton.enabled = var8;
         GuiSelectWorld.this.renameButton.enabled = var8;
         GuiSelectWorld.this.recreateButton.enabled = var8;
         if (var2 && var8) {
            GuiSelectWorld.this.func_146615_e(var1);
         }
      }

      @Override
      public boolean isSelected(int var1) {
         return var1 == GuiSelectWorld.this.selectedIndex;
      }

      @Override
      public int getContentHeight() {
         return GuiSelectWorld.this.field_146639_s.size() * 36;
      }

      @Override
      public void drawBackground() {
         GuiSelectWorld.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         SaveFormatComparator var10 = GuiSelectWorld.this.field_146639_s.get(var1);
         String var11 = var10.getDisplayName();
         if (StringUtils.isEmpty(var11)) {
            var11 = GuiSelectWorld.this.field_146637_u + " " + (var1 + 1);
         }

         String var12 = var10.getFileName();
         var12 = var12 + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(var10.getLastTimePlayed()));
         var12 = var12 + ")";
         String var13 = "";
         if (var10.requiresConversion()) {
            var13 = GuiSelectWorld.this.field_146636_v + " " + var13;
         } else {
            var13 = GuiSelectWorld.this.field_146635_w[var10.getEnumGameType().getID()];
            if (var10.isHardcoreModeEnabled()) {
               var13 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore") + EnumChatFormatting.RESET;
            }

            if (var10.getCheatsEnabled()) {
               var13 = var13 + ", " + I18n.format("selectWorld.cheats");
            }
         }

         GuiSelectWorld.this.I(GuiSelectWorld.this.fontRendererObj, var11, var2 + 2, var3 + 1, 16777215);
         GuiSelectWorld.this.I(GuiSelectWorld.this.fontRendererObj, var12, var2 + 2, var3 + 12, 8421504);
         GuiSelectWorld.this.I(GuiSelectWorld.this.fontRendererObj, var13, var2 + 2, var3 + 12 + 10, 8421504);
      }
   }
}
