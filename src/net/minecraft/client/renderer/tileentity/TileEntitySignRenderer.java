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

   public void renderTileEntityAt(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage) {
      Block block = te.getBlockType();
      GlStateManager.pushMatrix();
      float f = 0.6666667F;
      if (block == Blocks.standing_sign) {
         GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
         float g = te.getBlockMetadata() * 360 / 16.0F;
         GlStateManager.rotate(-g, 0.0F, 1.0F, 0.0F);
         this.model.signStick.showModel = true;
      } else {
         int i = te.getBlockMetadata();
         float h = 0.0F;
         if (i == 2) {
            h = 180.0F;
         }

         if (i == 4) {
            h = 90.0F;
         }

         if (i == 5) {
            h = -90.0F;
         }

         GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
         GlStateManager.rotate(-h, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.model.signStick.showModel = false;
      }

      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(SIGN_TEXTURE);
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      GlStateManager.scale(f, -f, -f);
      this.model.renderSign();
      GlStateManager.popMatrix();
      FontRenderer fontRenderer = this.getFontRenderer();
      float f2 = 0.015625F * f;
      GlStateManager.translate(0.0F, 0.5F * f, 0.07F * f);
      GlStateManager.scale(f2, -f2, f2);
      GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
      GlStateManager.depthMask(false);
      int j = 0;
      if (Config.isCustomColors()) {
         j = CustomColors.getSignTextColor(j);
      }

      if (destroyStage < 0) {
         for (int k = 0; k < te.signText.length; k++) {
            if (te.signText[k] != null) {
               IChatComponent iChatComponent = te.signText[k];
               List list = GuiUtilRenderComponents.splitText(iChatComponent, 90, fontRenderer, false, true);
               String string = list != null && list.size() > 0 ? ((IChatComponent)list.get(0)).getFormattedText() : "";
               if (k == te.lineBeingEdited) {
                  string = "> " + string + " <";
                  fontRenderer.drawStringNoShadow(string, -fontRenderer.getStringWidth(string) / 2, k * 10 - te.signText.length * 5, j);
               } else {
                  fontRenderer.drawStringNoShadow(string, -fontRenderer.getStringWidth(string) / 2, k * 10 - te.signText.length * 5, j);
               }
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
