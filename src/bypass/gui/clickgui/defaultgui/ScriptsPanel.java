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

   public ScriptsPanel(DefaultClickGuiScreen var1) {
      super("scripts", 0.0F, 0.0F, 107, 17);
      this.Q = var1;
   }

   @Override
   public void render(int var1, int var2) {
      int var7 = this.a();
      RenderUtil.drawRect(
         this.M() - 0.5F, this.E_float() - var7 - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 0.5F, new Color(128, 128, 128).getRGB()
      );
      RenderUtil.drawRect(this.M(), this.E_float() - var7, this.M() + this.x(), this.E_float() + this.Y(), new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() - var7 + 2.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 17.0F, new Color(37, 37, 37).getRGB()
      );
      int var8 = (int)(this.M() + this.x() - 11.0F);
      int var9 = (int)this.E_float() + 4;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/lua.png"));
      Gui.drawModalRectWithCustomSizedTexture(var8, var9, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPushMatrix();
      if (this.u(var8 - 16, var9, 12, var9 + 4, var1, var2)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.r.hasElapsed(1500.0F)) {
            SettingsCommand.m(ScriptManager.B);
            this.r.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(var8 - 12, var9, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      RenderUtil.setColor(Color.WHITE.getRGB());
      int var10 = (int)(this.M() + this.x() - 24.0F);
      int var11 = (int)this.E_float() + 4;
      GL11.glPushMatrix();
      if (this.u(var10 - 16, var11, 12, var11 + 4, var1, var2)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.r.hasElapsed(1500.0F)) {
            mc.thePlayer.sendChatMessage(".script reload");
            this.updateConfigComponents();
            this.Q.S();
            this.r.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/reload.png"));
      Gui.drawModalRectWithCustomSizedTexture(var10 - 12, var11, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.m.drawStringWithShadow(this.J(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean()) {
         byte var12 = 30;

         for (Element var14 : this.M_ArrayList()) {
            if (var14 instanceof ScriptsPanel.ax_) {
               ScriptsPanel.ax_ var15 = (ScriptsPanel.ax_)var14;
               var15.r(this.M() + 2.0F, this.E_float() - var12 + 17.0F);
               var15.render(var1, var2);
               var12 += 15;
            }
         }
      }
   }

   private int a() {
      byte var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof ScriptsPanel.ax_) {
               var4 += 15;
            }
         }
      }

      return var4;
   }

   private void updateConfigComponents() {
      this.M_ArrayList().clear();
      File[] var4 = ScriptManager.B.listFiles();
      Arrays.sort(var4, (var0, var1) -> -var0.getName().compareTo(var1.getName()));

      for (File var8 : var4) {
         if (!var8.isDirectory() && var8.getName().endsWith(".lua")) {
            this.a(new ScriptsPanel.ax_(var8.getName(), var8, 103, 15));
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

   public static DefaultClickGuiScreen t(ScriptsPanel var0) {
      return var0.Q;
   }

   public class ax_ extends Component {
      private File w;
      private static final long c = 65388371031681L;

      public ax_(String var2, File var3, int var4, int var5) {
         super(var2, 0.0F, 0.0F, var4, var5);
         this.w = var3;
      }

      @Override
      public void render(int var1, int var2) {
         long var3 = c ^ 48892510316787L;
         long var5 = var3 ^ 113435321281841L;
         Script var8 = client.getScriptManager().T(this.J());
         if (var8 != null && var8.t()) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(128, 128, 128).getRGB());
         }

         GuiMainMenu.m.drawString(this.J(), this.M() + 2.0F, this.E_float() + 3.0F, -1);
      }

      @Override
      public void l(int var1) {
         long var2 = c ^ 100495993858119L;
         long var4 = var2 ^ 17841708461445L;
         if (var1 == 0) {
            Script var7 = client.getScriptManager().T(this.J());
            if (var7 != null) {
               if (var7.t()) {
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
