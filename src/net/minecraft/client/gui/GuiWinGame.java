package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private int field_146581_h;
   private List<String> field_146582_i;
   private int field_146579_r;
   private float field_146578_s = 0.5F;
   private static final long b = 61435793279235L;

   @Override
   public void updateScreen() {
      MusicTicker musicTicker = this.mc.getMusicTicker();
      SoundHandler soundHandler = this.mc.getSoundHandler();
      if (this.field_146581_h == 0) {
         musicTicker.func_181557_a();
         musicTicker.func_181558_a(MusicTicker.MusicType.CREDITS);
         soundHandler.resumeSounds();
      }

      soundHandler.update();
      this.field_146581_h++;
      float f = (this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
      if (this.field_146581_h > f) {
         this.sendRespawnPacket();
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.sendRespawnPacket();
      }
   }

   private void sendRespawnPacket() {
      this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return true;
   }

   @Override
   public void initGui() {
      long l = b ^ 30372592802631L;
      if (this.field_146582_i == null) {
         this.field_146582_i = Lists.newArrayList();

         try {
            String string = "";
            String string2 = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
            long m = 7164191114002988431L ^ l;
            short s = 274;
            InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
            Random random = new Random(8124371L);

            while ((string = bufferedReader.readLine()) != null) {
               string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());

               while (string.contains(string2)) {
                  int i = string.indexOf(string2);
                  String string3 = string.substring(0, i);
                  String string4 = string.substring(i + string2.length());
                  string = string3 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
               }

               this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, s));
               this.field_146582_i.add("");
            }

            inputStream.close();

            for (int j = 0; j < 8; j++) {
               this.field_146582_i.add("");
            }

            inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));

            while ((string = bufferedReader.readLine()) != null) {
               string = string.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
               string = string.replaceAll("\t", "    ");
               this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(string, s));
               this.field_146582_i.add("");
            }

            inputStream.close();
            this.field_146579_r = this.field_146582_i.size() * 12;
         } catch (Exception exception) {
            logger.error("Couldn't load credits", exception);
         }
      }
   }

   private void drawWinGameScreen(int i, int j, float f) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      int k = this.width;
      float g = 0.0F - (this.field_146581_h + f) * 0.5F * this.field_146578_s;
      float h = this.height - (this.field_146581_h + f) * 0.5F * this.field_146578_s;
      float f2 = 0.015625F;
      float f3 = (this.field_146581_h + f - 0.0F) * 0.02F;
      float f4 = (this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
      float f5 = (f4 - 20.0F - (this.field_146581_h + f)) * 0.005F;
      if (f5 < f3) {
         f3 = f5;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      f3 *= f3;
      f3 = f3 * 96.0F / 255.0F;
      worldRenderer.pos(0.0, this.height, this.zLevel).tex(0.0, g * f2).color(f3, f3, f3, 1.0F).endVertex();
      worldRenderer.pos(k, this.height, this.zLevel).tex(k * f2, g * f2).color(f3, f3, f3, 1.0F).endVertex();
      worldRenderer.pos(k, 0.0, this.zLevel).tex(k * f2, h * f2).color(f3, f3, f3, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.0, h * f2).color(f3, f3, f3, 1.0F).endVertex();
      tessellator.draw();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWinGameScreen(mouseX, mouseY, partialTicks);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      short s = 274;
      int i = this.width / 2 - s / 2;
      int j = this.height + 50;
      float f = -(this.field_146581_h + partialTicks) * this.field_146578_s;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, f, 0.0F);
      this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(i, j, 0, 0, 155, 44);
      this.drawTexturedModalRect(i + 155, j, 0, 45, 155, 44);
      int k = j + 200;

      for (int l = 0; l < this.field_146582_i.size(); l++) {
         if (l == this.field_146582_i.size() - 1) {
            float g = k + f - (this.height / 2 - 6);
            if (g < 0.0F) {
               GlStateManager.translate(0.0F, -g, 0.0F);
            }
         }

         if (k + f + 12.0F + 8.0F > 0.0F && k + f < this.height) {
            String string = this.field_146582_i.get(l);
            if (string.startsWith("[C]")) {
               this.fontRendererObj
                  .drawStringWithShadow(string.substring(3), i + (s - this.fontRendererObj.getStringWidth(string.substring(3))) / 2, k, 16777215);
            } else {
               this.fontRendererObj.fontRandom.setSeed(l * 4238972211L + this.field_146581_h / 4);
               this.fontRendererObj.drawStringWithShadow(string, i, k, 16777215);
            }
         }

         k += 12;
      }

      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(0, 769);
      int m = this.width;
      int n = this.height;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, n, this.zLevel).tex(0.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(m, n, this.zLevel).tex(1.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(m, 0.0, this.zLevel).tex(1.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
