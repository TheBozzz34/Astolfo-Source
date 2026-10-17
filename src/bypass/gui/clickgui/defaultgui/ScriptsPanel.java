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
   private DefaultClickGuiScreen screen;
   private Stopwatch clickTimer = new Stopwatch();
   private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

   public ScriptsPanel(DefaultClickGuiScreen defaultClickGuiScreen) {
      super("scripts", 0.0F, 0.0F, 107, 17);
      this.screen = defaultClickGuiScreen;
   }

   @Override
   public void render(int i, int j) {
      int k = this.getScriptListHeight();
      RenderUtil.drawRect(
         this.getX() - 0.5F,
         this.getYPos() - k - 0.5F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 0.5F,
         new Color(128, 128, 128).getRGB()
      );
      RenderUtil.drawRect(this.getX(), this.getYPos() - k, this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.getX() + 2.0F,
         this.getYPos() - k + 2.0F,
         this.getX() + this.getWidth() - 2.0F,
         this.getYPos() + this.getHeight() - 17.0F,
         new Color(37, 37, 37).getRGB()
      );
      int l = (int)(this.getX() + this.getWidth() - 11.0F);
      int m = (int)this.getYPos() + 4;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/lua.png"));
      Gui.drawModalRectWithCustomSizedTexture(l, m, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPushMatrix();
      if (this.isMouseInRect(l - 16, m, 12, m + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.clickTimer.hasElapsed(1500.0F)) {
            SettingsCommand.openFolder(ScriptManager.SCRIPTS_DIR);
            this.clickTimer.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(l - 12, m, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      RenderUtil.setColor(Color.WHITE.getRGB());
      int n = (int)(this.getX() + this.getWidth() - 24.0F);
      int i2 = (int)this.getYPos() + 4;
      GL11.glPushMatrix();
      if (this.isMouseInRect(n - 16, i2, 12, i2 + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.clickTimer.hasElapsed(1500.0F)) {
            mc.thePlayer.sendChatMessage(".script reload");
            this.updateConfigComponents();
            this.screen.refreshScriptModules();
            this.clickTimer.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/reload.png"));
      Gui.drawModalRectWithCustomSizedTexture(n - 12, i2, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.latoBoldFont15.drawStringWithShadow(this.getName(), this.getX() + 4.0F, this.getYPos() + 3.5F, -1);
      if (this.isExpanded()) {
         byte b = 30;

         for (Element element : this.getChildren()) {
            if (element instanceof ScriptsPanel.ScriptEntry) {
               ScriptsPanel.ScriptEntry scriptEntry = (ScriptsPanel.ScriptEntry)element;
               scriptEntry.setPosition(this.getX() + 2.0F, this.getYPos() - b + 17.0F);
               scriptEntry.render(i, j);
               b += 15;
            }
         }
      }
   }

   private int getScriptListHeight() {
      byte b = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof ScriptsPanel.ScriptEntry) {
               b += 15;
            }
         }
      }

      return b;
   }

   private void updateConfigComponents() {
      this.getChildren().clear();
      File[] files = ScriptManager.SCRIPTS_DIR.listFiles();
      Arrays.sort(files, (file, file2) -> -file.getName().compareTo(file2.getName()));

      for (File file3 : files) {
         if (!file3.isDirectory() && file3.getName().endsWith(".lua")) {
            this.addChildren(new ScriptsPanel.ScriptEntry(file3.getName(), file3, 103, 15));
            System.out.println(file3.getName());
         }
      }
   }

   @Override
   public void onClick(int i) {
      if (i == 1) {
         this.setExpanded(!this.isExpanded());
         this.updateConfigComponents();
      }
   }

   public static DefaultClickGuiScreen getScreenOf(ScriptsPanel scriptsPanel) {
      return scriptsPanel.screen;
   }

   public class ScriptEntry extends Component {
      private File file;

      public ScriptEntry(String string, File file, int i, int j) {
         super(string, 0.0F, 0.0F, i, j);
         this.file = file;
      }

      @Override
      public void render(int i, int j) {
         Script script = client.getScriptManager().getScript(this.getName());
         if (script != null && script.isEnabled()) {
            RenderUtil.drawRect(
               this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(128, 128, 128).getRGB()
            );
         }

         GuiMainMenu.latoBoldFont15.drawString(this.getName(), this.getX() + 2.0F, this.getYPos() + 3.0F, -1);
      }

      @Override
      public void onClick(int i) {
         if (i == 0) {
            Script script2 = client.getScriptManager().getScript(this.getName());
            if (script2 != null) {
               if (script2.isEnabled()) {
                  mc.thePlayer.sendChatMessage(".script disable " + this.getName());
                  ScriptsPanel.getScreenOf(ScriptsPanel.this).refreshScriptModules();
               } else {
                  mc.thePlayer.sendChatMessage(".script enable " + this.getName());
                  ScriptsPanel.getScreenOf(ScriptsPanel.this).refreshScriptModules();
               }
            }
         }
      }
   }
}
