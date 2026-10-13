package net.minecraft.src;

import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class BlockModelUtils {
   private static final long a = 5133602769716L;

   public static IBakedModel G(String var0, int var1) {
      TextureAtlasSprite var2 = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(var0);
      return Z(var2, var1);
   }

   public static IBakedModel Z(TextureAtlasSprite var0, int var1) {
      ArrayList var5 = new ArrayList();
      EnumFacing[] var6 = EnumFacing.VALUES;
      ArrayList var7 = new ArrayList(var6.length);

      for (int var8 = 0; var8 < var6.length; var8++) {
         EnumFacing var9 = var6[var8];
         ArrayList var10 = new ArrayList();
         var10.add(F(var9, var0, var1));
         var7.add(var10);
      }

      return new SimpleBakedModel(var5, var7, true, true, var0, ItemCameraTransforms.DEFAULT);
   }

   private static BakedQuad F(EnumFacing var0, TextureAtlasSprite var1, int var2) {
      Vector3f var3 = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f var4 = new Vector3f(16.0F, 16.0F, 16.0F);
      BlockFaceUV var5 = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      BlockPartFace var6 = new BlockPartFace(var0, var2, "#" + var0.getName(), var5);
      ModelRotation var7 = ModelRotation.X0_Y0;
      Object var8 = null;
      boolean var9 = false;
      boolean var10 = true;
      FaceBakery var11 = new FaceBakery();
      return var11.makeBakedQuad(var3, var4, var6, var1, var0, var7, (BlockPartRotation)var8, var9, var10);
   }
}
