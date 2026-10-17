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
   public static IBakedModel makeModelCube(String spriteName, int tintIndex) {
      TextureAtlasSprite sprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(spriteName);
      return makeModelCubeFromSprite(sprite, tintIndex);
   }

   public static IBakedModel makeModelCubeFromSprite(TextureAtlasSprite textureAtlasSprite, int i) {
      ArrayList arrayList = new ArrayList();
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList arrayList2 = new ArrayList(enumFacings.length);

      for (int j = 0; j < enumFacings.length; j++) {
         EnumFacing enumFacing = enumFacings[j];
         ArrayList arrayList3 = new ArrayList();
         arrayList3.add(makeBakedQuad(enumFacing, textureAtlasSprite, i));
         arrayList2.add(arrayList3);
      }

      return new SimpleBakedModel(arrayList, arrayList2, true, true, textureAtlasSprite, ItemCameraTransforms.DEFAULT);
   }

   private static BakedQuad makeBakedQuad(EnumFacing facing, TextureAtlasSprite sprite, int tintIndex) {
      Vector3f posFrom = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f posTo = new Vector3f(16.0F, 16.0F, 16.0F);
      BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      BlockPartFace face = new BlockPartFace(facing, tintIndex, "#" + facing.getName(), uv);
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      BlockPartRotation partRotation = null;
      boolean uvLocked = false;
      boolean shade = true;
      FaceBakery faceBakery = new FaceBakery();
      return faceBakery.makeBakedQuad(posFrom, posTo, face, sprite, facing, modelRotation, partRotation, uvLocked, shade);
   }
}
