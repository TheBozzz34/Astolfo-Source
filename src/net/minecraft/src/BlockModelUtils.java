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

   public static IBakedModel G(String string, int i) {
      TextureAtlasSprite textureAtlasSprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(string);
      return Z(textureAtlasSprite, i);
   }

   public static IBakedModel Z(TextureAtlasSprite textureAtlasSprite, int i) {
      ArrayList arrayList = new ArrayList();
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList arrayList2 = new ArrayList(enumFacings.length);

      for (int j = 0; j < enumFacings.length; j++) {
         EnumFacing enumFacing = enumFacings[j];
         ArrayList arrayList3 = new ArrayList();
         arrayList3.add(F(enumFacing, textureAtlasSprite, i));
         arrayList2.add(arrayList3);
      }

      return new SimpleBakedModel(arrayList, arrayList2, true, true, textureAtlasSprite, ItemCameraTransforms.DEFAULT);
   }

   private static BakedQuad F(EnumFacing enumFacing, TextureAtlasSprite textureAtlasSprite, int i) {
      Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f vector3f2 = new Vector3f(16.0F, 16.0F, 16.0F);
      BlockFaceUV blockFaceUV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      BlockPartFace blockPartFace = new BlockPartFace(enumFacing, i, "#" + enumFacing.getName(), blockFaceUV);
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      Object object = null;
      boolean bl = false;
      boolean bl2 = true;
      FaceBakery faceBakery = new FaceBakery();
      return faceBakery.makeBakedQuad(vector3f, vector3f2, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, (BlockPartRotation)object, bl, bl2);
   }
}
