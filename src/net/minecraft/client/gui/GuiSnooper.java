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
   private static final long a = 122429170815509L;

   public GuiSnooper(GuiScreen var1, GameSettings var2) {
      this.field_146608_a = var1;
      this.game_settings_2 = var2;
   }

   @Override
   public void initGui() {
      this.field_146610_i = I18n.format("options.snooper.title");
      String var4 = I18n.format("options.snooper.desc");
      ArrayList var5 = Lists.newArrayList();

      for (Object var7 : this.fontRendererObj.listFormattedStringToWidth(var4, this.width - 30)) {
         var5.add((String)var7);
      }

      this.field_146607_r = (java.lang.String[])(var5.toArray(new String[var5.size()]));
      this.field_146604_g.clear();
      this.field_146609_h.clear();
      this.buttonList
         .add(
            this.field_146605_t = new GuiButton(
               1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)
            )
         );
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
      boolean var9 = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

      for (Entry var8 : new TreeMap<>(this.mc.getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
         this.field_146604_g.add((var9 ? "C " : "") + (String)var8.getKey());
         this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String)var8.getValue(), this.width - 220));
      }

      if (var9) {
         for (Entry var12 : new TreeMap<>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
            this.field_146604_g.add("S " + (String)var12.getKey());
            this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String)var12.getValue(), this.width - 220));
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
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 2) {
            this.game_settings_2.saveOptions();
            this.game_settings_2.saveOptions();
            this.mc.displayGuiScreen(this.field_146608_a);
         }

         if (var1.id == 1) {
            this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
            this.field_146605_t.displayString = this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.field_146606_s.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, this.field_146610_i, this.width / 2, 8, 16777215);
      int var7 = 22;

      for (String var11 : this.field_146607_r) {
         this.d(this.fontRendererObj, var11, this.width / 2, var7, 8421504);
         var7 += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(var1, var2, var3);
   }

   public static java.util.List i(GuiSnooper var0) {
      return var0.field_146609_h;
   }

   public class List extends GuiSlot {
      private static final long c = 116483877071671L;

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
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
      }

      @Override
      public boolean isSelected(int var1) {
         return false;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         GuiSnooper.this.fontRendererObj.D(GuiSnooper.this.field_146604_g.get(var1), 10.0F, var3, 16777215);
         GuiSnooper.this.fontRendererObj.D((String)GuiSnooper.i(GuiSnooper.this).get(var1), 230.0F, var3, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 10;
      }
   }
}
