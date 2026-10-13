package bypass.gui.clickgui.defaultgui;

import bypass.Astolfo;
import bypass.command.SettingsCommand;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ConfigsPanel extends Container {
   private Stopwatch K = new Stopwatch();
   private SimpleDateFormat T = new SimpleDateFormat("MM/dd/yyyy");
   private static final long d = 72679344459400L;

   public ConfigsPanel() {
      super("configs", 0.0F, 0.0F, 107, 17);
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = this.f();
      RenderUtil.drawRect(
         this.M() - 0.5F, this.E_float() - var6 - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 0.5F, new Color(128, 128, 128).getRGB()
      );
      RenderUtil.drawRect(this.M(), this.E_float() - var6, this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() - var6 + 2.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 17.0F, new Color(37, 37, 37).getRGB()
      );
      int var7 = (int)(this.M() + this.x() - 15.0F);
      int var8 = (int)this.E_float() + 2;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/options.png"));
      Gui.drawModalRectWithCustomSizedTexture(var7, var8, 0.0F, 0.0F, 12, 12, 12.0F, 12.0F);
      int var9 = (int)(this.M() + this.x() - 11.0F);
      int var10 = (int)this.E_float() + 4;
      GL11.glPushMatrix();
      if (this.u(var9 - 16, var10, 12, var10 + 4, var1, var2)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.K.hasElapsed(1500.0F)) {
            SettingsCommand.m(client.getDirectory());
            this.K.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(var9 - 14, var10, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.m.drawStringWithShadow(this.J(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean()) {
         byte var11 = 30;

         for (Element var13 : this.M_ArrayList()) {
            if (var13 instanceof ConfigsPanel.ax4) {
               ConfigsPanel.ax4 var14 = (ConfigsPanel.ax4)var13;
               var14.r(this.M() + 2.0F, this.E_float() - var11 + 17.0F);
               var14.render(var1, var2);
               var11 += 15;
            }
         }
      }
   }

   private int f() {
      byte var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof ConfigsPanel.ax4) {
               var4 += 15;
            }
         }
      }

      return var4;
   }

   private void updateConfigComponents() {
      this.M_ArrayList().clear();
      File[] var4 = client.getDirectory().listFiles();
      Arrays.sort(var4, (var0, var1) -> -var0.getName().compareTo(var1.getName()));

      for (File var8 : var4) {
         if (!var8.isDirectory()
            && var8.getName().endsWith(".json")
            && !var8.getName().equals("altening.json")
            && !var8.getName().equals("alts.json")
            && !var8.getName().equals("fonts.json")
            && !var8.getName().equals("chat.json")
            && !var8.getName().equals("colors.json")
            && !var8.getName().equals("relations.json")) {
            this.a(new ConfigsPanel.ax4(var8.getName().split("\\.json")[0], var8, 103, 15));
            System.out.println(var8.getName());
         }
      }
   }

   @Override
   public void l(int var1) {
      if (var1 == 1) {
         this.u(!this.s_boolean());
         this.updateConfigComponents();
      }
   }

   public static SimpleDateFormat b(ConfigsPanel var0) {
      return var0.T;
   }

   public class ax4 extends Component {
      private File L;
      private static final long c = 91075761623407L;

      public ax4(String var2, File var3, int var4, int var5) {
         super(var2, 0.0F, 0.0F, var4, var5);
         this.L = var3;
      }

      @Override
      public void render(int var1, int var2) {
         if (Astolfo.INSTANCE.getConfigName().equals(this.J())) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
         }

         GuiMainMenu.m.drawString(this.J(), this.M() + 2.0F, this.E_float() + 3.0F, -1);
         String var6 = ConfigsPanel.b(ConfigsPanel.this).format(this.L.lastModified());
         RenderUtil.drawRect(
            this.M() + this.x() - GuiMainMenu.m.getStringWidth(var6, true) - 3.0F,
            this.E_float() + 3.0F,
            this.M() + this.x(),
            this.E_float() + 12.0F,
            Astolfo.INSTANCE.getConfigName().equals(this.J()) ? new Color(1710618).getRGB() : new Color(37, 37, 37).getRGB()
         );
         GuiMainMenu.m
            .drawString(var6, this.M() + this.x() - GuiMainMenu.m.getStringWidth(var6, true) - 3.0F, this.E_float() + 3.0F, new Color(127, 127, 127).getRGB());
      }

      @Override
      public void l(int var1) {
         if (var1 == 0) {
            mc.thePlayer.sendChatMessage(".config load " + this.J());
         }
      }
   }
}
