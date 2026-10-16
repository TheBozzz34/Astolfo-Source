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
   private Stopwatch folderClickTimer = new Stopwatch();
   private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

   public ConfigsPanel() {
      super("configs", 0.0F, 0.0F, 107, 17);
   }

   @Override
   public void render(int i, int j) {
      int k = this.getConfigListHeight();
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
      int l = (int)(this.getX() + this.getWidth() - 15.0F);
      int n = (int)this.getYPos() + 2;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/options.png"));
      Gui.drawModalRectWithCustomSizedTexture(l, n, 0.0F, 0.0F, 12, 12, 12.0F, 12.0F);
      int i2 = (int)(this.getX() + this.getWidth() - 11.0F);
      int i3 = (int)this.getYPos() + 4;
      GL11.glPushMatrix();
      if (this.isMouseInRect(i2 - 16, i3, 12, i3 + 4, i, j)) {
         RenderUtil.setColor(new Color(127, 127, 127).getRGB());
         if (Mouse.isButtonDown(0) && this.folderClickTimer.hasElapsed(1500.0F)) {
            SettingsCommand.openFolder(client.getDirectory());
            this.folderClickTimer.reset();
         }
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/icon/folder.png"));
      Gui.drawModalRectWithCustomSizedTexture(i2 - 14, i3, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GL11.glPopMatrix();
      GuiMainMenu.m.drawStringWithShadow(this.getName(), this.getX() + 4.0F, this.getYPos() + 3.5F, -1);
      if (this.isExpanded()) {
         byte b = 30;

         for (Element element : this.getChildren()) {
            if (element instanceof ConfigsPanel.ax4) {
               ConfigsPanel.ax4 ax42 = (ConfigsPanel.ax4)element;
               ax42.setPosition(this.getX() + 2.0F, this.getYPos() - b + 17.0F);
               ax42.render(i, j);
               b += 15;
            }
         }
      }
   }

   private int getConfigListHeight() {
      byte b = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof ConfigsPanel.ax4) {
               b += 15;
            }
         }
      }

      return b;
   }

   private void updateConfigComponents() {
      this.getChildren().clear();
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
            this.addChildren(new ConfigsPanel.ax4(file3.getName().split("\\.json")[0], file3, 103, 15));
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

   public static SimpleDateFormat getDateFormatOf(ConfigsPanel configsPanel) {
      return configsPanel.dateFormat;
   }

   public class ax4 extends Component {
      private File configFile;

      public ax4(String string, File file, int i, int j) {
         super(string, 0.0F, 0.0F, i, j);
         this.configFile = file;
      }

      @Override
      public void render(int i, int j) {
         if (Astolfo.INSTANCE.getConfigName().equals(this.getName())) {
            RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(1710618).getRGB());
         }

         GuiMainMenu.m.drawString(this.getName(), this.getX() + 2.0F, this.getYPos() + 3.0F, -1);
         String string = ConfigsPanel.getDateFormatOf(ConfigsPanel.this).format(this.configFile.lastModified());
         RenderUtil.drawRect(
            this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(string, true) - 3.0F,
            this.getYPos() + 3.0F,
            this.getX() + this.getWidth(),
            this.getYPos() + 12.0F,
            Astolfo.INSTANCE.getConfigName().equals(this.getName()) ? new Color(1710618).getRGB() : new Color(37, 37, 37).getRGB()
         );
         GuiMainMenu.m
            .drawString(
               string, this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(string, true) - 3.0F, this.getYPos() + 3.0F, new Color(127, 127, 127).getRGB()
            );
      }

      @Override
      public void onClick(int i) {
         if (i == 0) {
            mc.thePlayer.sendChatMessage(".config load " + this.getName());
         }
      }
   }
}
