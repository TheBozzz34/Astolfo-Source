package bypass.gui.clickgui.defaultgui;

import bypass.command.SettingsCommand;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.script.Script;
import bypass.script.ScriptManager;
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

public class ScriptsPanel extends Container {
   private DefaultClickGuiScreen Q;
   private Stopwatch r = new Stopwatch();
   private SimpleDateFormat K = new SimpleDateFormat("MM/dd/yyyy");
   private static final long d = 34096501654596L;

   public ScriptsPanel(DefaultClickGuiScreen defaultClickGuiScreen) {
      super("scripts", 0.0F, 0.0F, 107, 17);
      this.Q = defaultClickGuiScreen;
   }

   @Override
   public void render(int i, int j) {
      int k = this.a();
      RenderUtil.drawRect(
         this.M() - 0.5F, this.E_float() - k - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 0.5F, new Color(128, 128, 128).getRGB()
      );
      RenderUtil.drawRect(this.M(), this.E_float() - k, this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() - k + 2.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 17.0F, new Color(37, 37, 37).getRGB()
      );
      int l = (int)(this.M() + this.x() - 11.0F);
      int n = (int)this.E_float() + 4;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/lua.png"));
      Gui.drawModalRectWithCustomSizedTexture(l, n, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPushMatrix();
      if (this.u(l - 16, n, 12, n + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.r.hasElapsed(1500.0F)) {
            SettingsCommand.m(ScriptManager.B);
            this.r.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(l - 12, n, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      RenderUtil.setColor(Color.WHITE.getRGB());
      int i2 = (int)(this.M() + this.x() - 24.0F);
      int i3 = (int)this.E_float() + 4;
      GL11.glPushMatrix();
      if (this.u(i2 - 16, i3, 12, i3 + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.r.hasElapsed(1500.0F)) {
            mc.thePlayer.sendChatMessage(".script reload");
            this.updateConfigComponents();
            this.Q.S();
            this.r.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/reload.png"));
      Gui.drawModalRectWithCustomSizedTexture(i2 - 12, i3, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.m.drawStringWithShadow(this.J(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean()) {
         byte b = 30;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ScriptsPanel.ax_) {
               ScriptsPanel.ax_ ax_2 = (ScriptsPanel.ax_)element;
               ax_2.r(this.M() + 2.0F, this.E_float() - b + 17.0F);
               ax_2.render(i, j);
               b += 15;
            }
         }
      }
   }

   private int a() {
      byte b = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof ScriptsPanel.ax_) {
               b += 15;
            }
         }
      }

      return b;
   }

   private void updateConfigComponents() {
      this.M_ArrayList().clear();
      File[] files = ScriptManager.B.listFiles();
      Arrays.sort(files, (file, file2) -> -file.getName().compareTo(file2.getName()));

      for (File file3 : files) {
         if (!file3.isDirectory() && file3.getName().endsWith(".lua")) {
            this.a(new ScriptsPanel.ax_(file3.getName(), file3, 103, 15));
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

   public static DefaultClickGuiScreen t(ScriptsPanel scriptsPanel) {
      return scriptsPanel.Q;
   }

   public class ax_ extends Component {
      private File w;
      private static final long c = 65388371031681L;

      public ax_(String string, File file, int i, int j) {
         super(string, 0.0F, 0.0F, i, j);
         this.w = file;
      }

      @Override
      public void render(int i, int j) {
         long l = c ^ 48892510316787L;
         long n = l ^ 113435321281841L;
         Script script = client.getScriptManager().T(this.J());
         if (script != null && script.t()) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(128, 128, 128).getRGB());
         }

         GuiMainMenu.m.drawString(this.J(), this.M() + 2.0F, this.E_float() + 3.0F, -1);
      }

      @Override
      public void l(int i) {
         long m = c ^ 100495993858119L;
         long n = m ^ 17841708461445L;
         if (i == 0) {
            Script script2 = client.getScriptManager().T(this.J());
            if (script2 != null) {
               if (script2.t()) {
                  mc.thePlayer.sendChatMessage(".script disable " + this.J());
                  ScriptsPanel.t(ScriptsPanel.this).S();
               } else {
                  mc.thePlayer.sendChatMessage(".script enable " + this.J());
                  ScriptsPanel.t(ScriptsPanel.this).S();
               }
            }
         }
      }
   }
}
