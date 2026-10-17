package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiSnooper extends GuiScreen {
   private final GuiScreen field_146608_a;
   private final GameSettings game_settings_2;
   private final java.util.List<String> field_146604_g = Lists.newArrayList();
   private final java.util.List<String> field_146609_h = Lists.newArrayList();
   private String field_146610_i;
   private String[] field_146607_r;
   private GuiSnooper.List field_146606_s;
   private GuiButton field_146605_t;

   public GuiSnooper(GuiScreen guiScreen, GameSettings gameSettings) {
      this.field_146608_a = guiScreen;
      this.game_settings_2 = gameSettings;
   }

   @Override
   public void initGui() {
      this.field_146610_i = I18n.format("options.snooper.title");
      String string = I18n.format("options.snooper.desc");
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : this.fontRendererObj.listFormattedStringToWidth(string, this.width - 30)) {
         arrayList.add((String)object);
      }

      this.field_146607_r = (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
      this.field_146604_g.clear();
      this.field_146609_h.clear();
      this.buttonList
         .add(
            this.field_146605_t = new GuiButton(
               1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)
            )
         );
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
      boolean bl = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

      for (Entry entry : new TreeMap<>(this.mc.getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
         this.field_146604_g.add((bl ? "C " : "") + (String)entry.getKey());
         this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
      }

      if (bl) {
         for (Entry entry2 : new TreeMap<>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
            this.field_146604_g.add("S " + (String)entry2.getKey());
            this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String)entry2.getValue(), this.width - 220));
         }
      }

      this.field_146606_s = new GuiSnooper.List();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.field_146606_s.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            this.game_settings_2.saveOptions();
            this.game_settings_2.saveOptions();
            this.mc.displayGuiScreen(this.field_146608_a);
         }

         if (button.id == 1) {
            this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
            this.field_146605_t.displayString = this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.field_146606_s.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.field_146610_i, this.width / 2, 8, 16777215);
      int i = 22;

      for (String string : this.field_146607_r) {
         this.drawCenteredString(this.fontRendererObj, string, this.width / 2, i, 8421504);
         i += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static java.util.List accessSnooperValues(GuiSnooper guiSnooper) {
      return guiSnooper.field_146609_h;
   }

   public class List extends GuiSlot {
      public List() {
         super(
            GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRendererObj.FONT_HEIGHT + 1
         );
      }

      @Override
      public int getSize() {
         return GuiSnooper.this.field_146604_g.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         GuiSnooper.this.fontRendererObj.drawStringNoShadow(GuiSnooper.this.field_146604_g.get(entryID), 10.0F, j, 16777215);
         GuiSnooper.this.fontRendererObj.drawStringNoShadow((String)GuiSnooper.accessSnooperValues(GuiSnooper.this).get(entryID), 230.0F, j, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 10;
      }
   }
}
