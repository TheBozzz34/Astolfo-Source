package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import org.lwjgl.opengl.GL11;
import shadersmod.client.SVertexBuilder;

public class WorldVertexBufferUploader {
   private static final long b = 61201061751505L;

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void draw(WorldRenderer var1) {
      long var2 = b ^ 95818232398051L;
      if (var1.getVertexCount() > 0) {
         VertexFormat var5 = var1.getVertexFormat();
         int var6 = var5.getNextOffset();
         ByteBuffer var7 = var1.getByteBuffer();
         List var8 = var5.getElements();
         boolean var9 = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
         boolean var10 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

         for (int var11 = 0; var11 < var8.size(); var11++) {
            VertexFormatElement var12 = (VertexFormatElement)var8.get(var11);
            VertexFormatElement.EnumUsage var13 = var12.getUsage();
            if (var9) {
               Reflector.callVoid(var13, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, var5, var11, var6, var7);
            } else {
               int var14 = var12.getType().getGlConstant();
               int var15 = var12.getIndex();
               ((Buffer)var7).position(var5.getOffset(var11));
               switch (var13) {
                  case POSITION:
                     GL11.glVertexPointer(var12.getElementCount(), var14, var6, var7);
                     GL11.glEnableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + var15);
                     GL11.glTexCoordPointer(var12.getElementCount(), var14, var6, var7);
                     long var10000 = 8353501295803406929L ^ var2;
                     GL11.glEnableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glColorPointer(var12.getElementCount(), var14, var6, var7);
                     GL11.glEnableClientState(32886);
                     break;
                  case NORMAL:
                     GL11.glNormalPointer(var14, var6, var7);
                     GL11.glEnableClientState(32885);
               }
            }
         }

         if (var1.isMultiTexture()) {
            var1.drawMultiTexture();
         } else if (Config.isShaders()) {
            SVertexBuilder.drawArrays(var1.getDrawMode(), 0, var1.getVertexCount(), var1);
         } else {
            GL11.glDrawArrays(var1.getDrawMode(), 0, var1.getVertexCount());
         }

         int var16 = 0;

         for (int var17 = var8.size(); var16 < var17; var16++) {
            VertexFormatElement var18 = (VertexFormatElement)var8.get(var16);
            VertexFormatElement.EnumUsage var19 = var18.getUsage();
            if (var10) {
               Reflector.callVoid(var19, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, var5, var16, var6, var7);
            } else {
               int var20 = var18.getIndex();
               switch (var19) {
                  case POSITION:
                     GL11.glDisableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + var20);
                     GL11.glDisableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glDisableClientState(32886);
                     GlStateManager.resetColor();
                     break;
                  case NORMAL:
                     GL11.glDisableClientState(32885);
               }
            }
         }
      }

      var1.reset();
   }
}
