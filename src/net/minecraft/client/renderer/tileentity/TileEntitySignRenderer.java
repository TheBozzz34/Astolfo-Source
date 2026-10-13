package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer<TileEntitySign> {
   private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
   private final ModelSign model = new ModelSign();
   private static final long b = 140119004339953L;

   public void renderTileEntityAt(TileEntitySign var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = b ^ 87904020361471L;
      Block var13 = var1.getBlockType();
      GlStateManager.pushMatrix();
      float var14 = 0.6666667F;
      if (var13 == Blocks.standing_sign) {
         GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 0.75F * var14, (float)var6 + 0.5F);
         float var15 = var1.getBlockMetadata() * 360 / 16.0F;
         GlStateManager.rotate(-var15, 0.0F, 1.0F, 0.0F);
         this.model.signStick.showModel = true;
      } else {
         int var22 = var1.getBlockMetadata();
         float var16 = 0.0F;
         if (var22 == 2) {
            var16 = 180.0F;
         }

         if (var22 == 4) {
            var16 = 90.0F;
         }

         if (var22 == 5) {
            var16 = -90.0F;
         }

         GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 0.75F * var14, (float)var6 + 0.5F);
         GlStateManager.rotate(-var16, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.model.signStick.showModel = false;
      }

      if (var9 >= 0) {
         this.bindTexture(DESTROY_STAGES[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         long var10000 = 4255248082457419965L ^ var10;
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(SIGN_TEXTURE);
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      GlStateManager.scale(var14, -var14, -var14);
      this.model.renderSign();
      GlStateManager.popMatrix();
      FontRenderer var23 = this.getFontRenderer();
      float var24 = 0.015625F * var14;
      GlStateManager.translate(0.0F, 0.5F * var14, 0.07F * var14);
      GlStateManager.scale(var24, -var24, var24);
      GL11.glNormal3f(0.0F, 0.0F, -1.0F * var24);
      GlStateManager.depthMask(false);
      int var17 = 0;
      if (Config.isCustomColors()) {
         var17 = CustomColors.E(var17);
      }

      if (var9 < 0) {
         for (int var18 = 0; var18 < var1.signText.length; var18++) {
            if (var1.signText[var18] != null) {
               IChatComponent var19 = var1.signText[var18];
               List var20 = GuiUtilRenderComponents.splitText(var19, 90, var23, false, true);
               String var21 = var20 != null && var20.size() > 0 ? ((IChatComponent)var20.get(0)).getFormattedText() : "";
               if (var18 == var1.lineBeingEdited) {
                  var21 = "> " + var21 + " <";
                  var23.D(var21, -var23.getStringWidth(var21) / 2, var18 * 10 - var1.signText.length * 5, var17);
               } else {
                  var23.D(var21, -var23.getStringWidth(var21) / 2, var18 * 10 - var1.signText.length * 5, var17);
               }
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      if (var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         long var26 = 4427808520587393407L ^ var10;
         GlStateManager.matrixMode(5888);
      }
   }
}
