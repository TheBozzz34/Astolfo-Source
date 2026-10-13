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
      MusicTicker var4 = this.mc.getMusicTicker();
      SoundHandler var5 = this.mc.getSoundHandler();
      if (this.field_146581_h == 0) {
         var4.func_181557_a();
         var4.func_181558_a(MusicTicker.MusicType.CREDITS);
         var5.resumeSounds();
      }

      var5.update();
      this.field_146581_h++;
      float var6 = (this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
      if (this.field_146581_h > var6) {
         this.sendRespawnPacket();
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == 1) {
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
      long var1 = b ^ 30372592802631L;
      if (this.field_146582_i == null) {
         this.field_146582_i = Lists.newArrayList();

         try {
            String var4 = "";
            String var5 = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
            long var10000 = 7164191114002988431L ^ var1;
            short var6 = 274;
            InputStream var7 = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
            BufferedReader var8 = new BufferedReader(new InputStreamReader(var7, Charsets.UTF_8));
            Random var9 = new Random(8124371L);

            while ((var4 = var8.readLine()) != null) {
               var4 = var4.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());

               while (var4.contains(var5)) {
                  int var12 = var4.indexOf(var5);
                  String var10 = var4.substring(0, var12);
                  String var11 = var4.substring(var12 + var5.length());
                  var4 = var10 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var9.nextInt(4) + 3) + var11;
               }

               this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(var4, var6));
               this.field_146582_i.add("");
            }

            var7.close();

            for (int var21 = 0; var21 < 8; var21++) {
               this.field_146582_i.add("");
            }

            var7 = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            var8 = new BufferedReader(new InputStreamReader(var7, Charsets.UTF_8));

            while ((var4 = var8.readLine()) != null) {
               var4 = var4.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
               var4 = var4.replaceAll("\t", "    ");
               this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(var4, var6));
               this.field_146582_i.add("");
            }

            var7.close();
            this.field_146579_r = this.field_146582_i.size() * 12;
         } catch (Exception var13) {
            logger.error("Couldn't load credits", var13);
         }
      }
   }

   private void drawWinGameScreen(int var1, int var2, float var3) {
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      var8.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      int var9 = this.width;
      float var10 = 0.0F - (this.field_146581_h + var3) * 0.5F * this.field_146578_s;
      float var11 = this.height - (this.field_146581_h + var3) * 0.5F * this.field_146578_s;
      float var12 = 0.015625F;
      float var13 = (this.field_146581_h + var3 - 0.0F) * 0.02F;
      float var14 = (this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
      float var15 = (var14 - 20.0F - (this.field_146581_h + var3)) * 0.005F;
      if (var15 < var13) {
         var13 = var15;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      var13 *= var13;
      var13 = var13 * 96.0F / 255.0F;
      var8.pos(0.0, this.height, this.zLevel).tex(0.0, var10 * var12).color(var13, var13, var13, 1.0F).endVertex();
      var8.pos(var9, this.height, this.zLevel).tex(var9 * var12, var10 * var12).color(var13, var13, var13, 1.0F).endVertex();
      var8.pos(var9, 0.0, this.zLevel).tex(var9 * var12, var11 * var12).color(var13, var13, var13, 1.0F).endVertex();
      var8.pos(0.0, 0.0, this.zLevel).tex(0.0, var11 * var12).color(var13, var13, var13, 1.0F).endVertex();
      var7.draw();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawWinGameScreen(var1, var2, var3);
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      short var9 = 274;
      int var10 = this.width / 2 - var9 / 2;
      int var11 = this.height + 50;
      float var12 = -(this.field_146581_h + var3) * this.field_146578_s;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, var12, 0.0F);
      this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(var10, var11, 0, 0, 155, 44);
      this.drawTexturedModalRect(var10 + 155, var11, 0, 45, 155, 44);
      int var13 = var11 + 200;

      for (int var14 = 0; var14 < this.field_146582_i.size(); var14++) {
         if (var14 == this.field_146582_i.size() - 1) {
            float var15 = var13 + var12 - (this.height / 2 - 6);
            if (var15 < 0.0F) {
               GlStateManager.translate(0.0F, -var15, 0.0F);
            }
         }

         if (var13 + var12 + 12.0F + 8.0F > 0.0F && var13 + var12 < this.height) {
            String var17 = this.field_146582_i.get(var14);
            if (var17.startsWith("[C]")) {
               this.fontRendererObj
                  .drawStringWithShadow(var17.substring(3), var10 + (var9 - this.fontRendererObj.getStringWidth(var17.substring(3))) / 2, var13, 16777215);
            } else {
               this.fontRendererObj.fontRandom.setSeed(var14 * 4238972211L + this.field_146581_h / 4);
               this.fontRendererObj.drawStringWithShadow(var17, var10, var13, 16777215);
            }
         }

         var13 += 12;
      }

      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(0, 769);
      int var16 = this.width;
      int var18 = this.height;
      var8.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      var8.pos(0.0, var18, this.zLevel).tex(0.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var8.pos(var16, var18, this.zLevel).tex(1.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var8.pos(var16, 0.0, this.zLevel).tex(1.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var8.pos(0.0, 0.0, this.zLevel).tex(0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var7.draw();
      GlStateManager.disableBlend();
      super.drawScreen(var1, var2, var3);
   }
}
