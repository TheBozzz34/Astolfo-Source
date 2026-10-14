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
   public void draw(WorldRenderer worldRenderer) {
      long l = b ^ 95818232398051L;
      if (worldRenderer.getVertexCount() > 0) {
         VertexFormat vertexFormat = worldRenderer.getVertexFormat();
         int i = vertexFormat.getNextOffset();
         ByteBuffer byteBuffer = worldRenderer.getByteBuffer();
         List list = vertexFormat.getElements();
         boolean bl = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
         boolean bl2 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

         for (int j = 0; j < list.size(); j++) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
            VertexFormatElement.EnumUsage enumUsage = vertexFormatElement.getUsage();
            if (bl) {
               Reflector.callVoid(enumUsage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexFormat, j, i, byteBuffer);
            } else {
               int k = vertexFormatElement.getType().getGlConstant();
               int m = vertexFormatElement.getIndex();
               ((Buffer)byteBuffer).position(vertexFormat.getOffset(j));
               switch (enumUsage) {
                  case POSITION:
                     GL11.glVertexPointer(vertexFormatElement.getElementCount(), k, i, byteBuffer);
                     GL11.glEnableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + m);
                     GL11.glTexCoordPointer(vertexFormatElement.getElementCount(), k, i, byteBuffer);
                     long n = 8353501295803406929L ^ l;
                     GL11.glEnableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glColorPointer(vertexFormatElement.getElementCount(), k, i, byteBuffer);
                     GL11.glEnableClientState(32886);
                     break;
                  case NORMAL:
                     GL11.glNormalPointer(k, i, byteBuffer);
                     GL11.glEnableClientState(32885);
               }
            }
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.drawMultiTexture();
         } else if (Config.isShaders()) {
            SVertexBuilder.drawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount(), worldRenderer);
         } else {
            GL11.glDrawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount());
         }

         int i2 = 0;

         for (int i3 = list.size(); i2 < i3; i2++) {
            VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(i2);
            VertexFormatElement.EnumUsage enumUsage2 = vertexFormatElement2.getUsage();
            if (bl2) {
               Reflector.callVoid(enumUsage2, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, vertexFormat, i2, i, byteBuffer);
            } else {
               int i4 = vertexFormatElement2.getIndex();
               switch (enumUsage2) {
                  case POSITION:
                     GL11.glDisableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i4);
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

      worldRenderer.reset();
   }
}
