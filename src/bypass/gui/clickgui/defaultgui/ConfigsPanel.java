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
   public void render(int i, int j) {
      int k = this.f();
      RenderUtil.drawRect(
         this.M() - 0.5F, this.E_float() - k - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 0.5F, new Color(128, 128, 128).getRGB()
      );
      RenderUtil.drawRect(this.M(), this.E_float() - k, this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() - k + 2.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 17.0F, new Color(37, 37, 37).getRGB()
      );
      int l = (int)(this.M() + this.x() - 15.0F);
      int n = (int)this.E_float() + 2;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/options.png"));
      Gui.drawModalRectWithCustomSizedTexture(l, n, 0.0F, 0.0F, 12, 12, 12.0F, 12.0F);
      int i2 = (int)(this.M() + this.x() - 11.0F);
      int i3 = (int)this.E_float() + 4;
      GL11.glPushMatrix();
      if (this.u(i2 - 16, i3, 12, i3 + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.K.hasElapsed(1500.0F)) {
            SettingsCommand.m(client.getDirectory());
            this.K.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(i2 - 14, i3, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.m.drawStringWithShadow(this.J(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean()) {
         byte b = 30;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ConfigsPanel.ax4) {
               ConfigsPanel.ax4 ax42 = (ConfigsPanel.ax4)element;
               ax42.r(this.M() + 2.0F, this.E_float() - b + 17.0F);
               ax42.render(i, j);
               b += 15;
            }
         }
      }
   }

   private int f() {
      byte b = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof ConfigsPanel.ax4) {
               b += 15;
            }
         }
      }

      return b;
   }

   private void updateConfigComponents() {
      this.M_ArrayList().clear();
      File[] files = client.getDirectory().listFiles();
      Arrays.sort(files, (file, file2) -> -file.getName().compareTo(file2.getName()));

      for (File file3 : files) {
         if (!file3.isDirectory()
            && file3.getName().endsWith(".json")
            && !file3.getName().equals("altening.json")
            && !file3.getName().equals("alts.json")
            && !file3.getName().equals("fonts.json")
            && !file3.getName().equals("chat.json")
            && !file3.getName().equals("colors.json")
            && !file3.getName().equals("relations.json")) {
            this.a(new ConfigsPanel.ax4(file3.getName().split("\\.json")[0], file3, 103, 15));
            System.out.println(file3.getName());
         }
      }
   }

   @Override
   public void l(int i) {
      if (i == 1) {
         this.u(!this.s_boolean());
         this.updateConfigComponents();
      }
   }

   public static SimpleDateFormat b(ConfigsPanel configsPanel) {
      return configsPanel.T;
   }

   public class ax4 extends Component {
      private File L;
      private static final long c = 91075761623407L;

      public ax4(String string, File file, int i, int j) {
         super(string, 0.0F, 0.0F, i, j);
         this.L = file;
      }

      @Override
      public void render(int i, int j) {
         if (Astolfo.INSTANCE.getConfigName().equals(this.J())) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
         }

         GuiMainMenu.m.drawString(this.J(), this.M() + 2.0F, this.E_float() + 3.0F, -1);
         String string = ConfigsPanel.b(ConfigsPanel.this).format(this.L.lastModified());
         RenderUtil.drawRect(
            this.M() + this.x() - GuiMainMenu.m.getStringWidth(string, true) - 3.0F,
            this.E_float() + 3.0F,
            this.M() + this.x(),
            this.E_float() + 12.0F,
            Astolfo.INSTANCE.getConfigName().equals(this.J()) ? new Color(1710618).getRGB() : new Color(37, 37, 37).getRGB()
         );
         GuiMainMenu.m
            .drawString(string, this.M() + this.x() - GuiMainMenu.m.getStringWidth(string, true) - 3.0F, this.E_float() + 3.0F, new Color(127, 127, 127).getRGB());
      }

      @Override
      public void l(int i) {
         if (i == 0) {
            mc.thePlayer.sendChatMessage(".config load " + this.J());
         }
      }
   }
}
