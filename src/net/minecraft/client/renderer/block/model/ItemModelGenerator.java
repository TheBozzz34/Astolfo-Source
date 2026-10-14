package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

   public ModelBlock makeItemModel(TextureMap textureMapIn, ModelBlock blockModel) {
      HashMap hashMap = Maps.newHashMap();
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < LAYERS.size(); i++) {
         String string = LAYERS.get(i);
         if (!blockModel.isTexturePresent(string)) {
            break;
         }

         String string2 = blockModel.resolveTextureName(string);
         hashMap.put(string, string2);
         TextureAtlasSprite textureAtlasSprite = textureMapIn.getAtlasSprite(new ResourceLocation(string2).toString());
         arrayList.addAll(this.func_178394_a(i, string, textureAtlasSprite));
      }

      if (arrayList.isEmpty()) {
         return null;
      }

      hashMap.put("particle", blockModel.isTexturePresent("particle") ? blockModel.resolveTextureName("particle") : (String)hashMap.get("layer0"));
      return new ModelBlock(arrayList, hashMap, false, false, blockModel.getAllTransforms());
   }

   private List<BlockPart> func_178394_a(int i, String string, TextureAtlasSprite textureAtlasSprite) {
      HashMap hashMap = Maps.newHashMap();
      hashMap.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, i, string, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      hashMap.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, i, string, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList arrayList = Lists.newArrayList();
      arrayList.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), hashMap, (BlockPartRotation)null, true));
      arrayList.addAll(this.func_178397_a(textureAtlasSprite, string, i));
      return arrayList;
   }

   private List<BlockPart> func_178397_a(TextureAtlasSprite textureAtlasSprite, String string, int i) {
      float f = textureAtlasSprite.getIconWidth();
      float g = textureAtlasSprite.getIconHeight();
      ArrayList arrayList = Lists.newArrayList();

      for (ItemModelGenerator.Span span : this.func_178393_a(textureAtlasSprite)) {
         float h = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 0.0F;
         float f11 = span.func_178385_b();
         float f12 = span.func_178384_c();
         float f13 = span.func_178381_d();
         ItemModelGenerator.SpanFacing spanFacing = span.func_178383_a();
         switch (spanFacing) {
            case UP:
               f5 = f11;
               h = f11;
               f3 = f6 = f12 + 1.0F;
               f7 = f13;
               f2 = f13;
               f8 = f13;
               f4 = f13;
               f9 = 16.0F / f;
               f10 = 16.0F / (g - 1.0F);
               break;
            case DOWN:
               f8 = f13;
               f7 = f13;
               f5 = f11;
               h = f11;
               f3 = f6 = f12 + 1.0F;
               f2 = f13 + 1.0F;
               f4 = f13 + 1.0F;
               f9 = 16.0F / f;
               f10 = 16.0F / (g - 1.0F);
               break;
            case LEFT:
               f5 = f13;
               h = f13;
               f6 = f13;
               f3 = f13;
               f8 = f11;
               f2 = f11;
               f4 = f7 = f12 + 1.0F;
               f9 = 16.0F / (f - 1.0F);
               f10 = 16.0F / g;
               break;
            case RIGHT:
               f6 = f13;
               f5 = f13;
               h = f13 + 1.0F;
               f3 = f13 + 1.0F;
               f8 = f11;
               f2 = f11;
               f4 = f7 = f12 + 1.0F;
               f9 = 16.0F / (f - 1.0F);
               f10 = 16.0F / g;
         }

         float f14 = 16.0F / f;
         float f15 = 16.0F / g;
         h *= f14;
         f3 *= f14;
         f2 *= f15;
         f4 *= f15;
         f2 = 16.0F - f2;
         f4 = 16.0F - f4;
         f5 *= f9;
         f6 *= f9;
         f7 *= f10;
         f8 *= f10;
         HashMap hashMap = Maps.newHashMap();
         hashMap.put(spanFacing.getFacing(), new BlockPartFace((EnumFacing)null, i, string, new BlockFaceUV(new float[]{f5, f7, f6, f8}, 0)));
         switch (spanFacing) {
            case UP:
               arrayList.add(new BlockPart(new Vector3f(h, f2, 7.5F), new Vector3f(f3, f2, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case DOWN:
               arrayList.add(new BlockPart(new Vector3f(h, f4, 7.5F), new Vector3f(f3, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case LEFT:
               arrayList.add(new BlockPart(new Vector3f(h, f2, 7.5F), new Vector3f(h, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case RIGHT:
               arrayList.add(new BlockPart(new Vector3f(f3, f2, 7.5F), new Vector3f(f3, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
         }
      }

      return arrayList;
   }

   private List<ItemModelGenerator.Span> func_178393_a(TextureAtlasSprite textureAtlasSprite) {
      int i = textureAtlasSprite.getIconWidth();
      int j = textureAtlasSprite.getIconHeight();
      ArrayList arrayList = Lists.newArrayList();

      for (int k = 0; k < textureAtlasSprite.getFrameCount(); k++) {
         int[] ints = textureAtlasSprite.getFrameTextureData(k)[0];

         for (int l = 0; l < j; l++) {
            for (int m = 0; m < i; m++) {
               boolean bl = !this.func_178391_a(ints, m, l, i, j);
               this.func_178396_a(ItemModelGenerator.SpanFacing.UP, arrayList, ints, m, l, i, j, bl);
               this.func_178396_a(ItemModelGenerator.SpanFacing.DOWN, arrayList, ints, m, l, i, j, bl);
               this.func_178396_a(ItemModelGenerator.SpanFacing.LEFT, arrayList, ints, m, l, i, j, bl);
               this.func_178396_a(ItemModelGenerator.SpanFacing.RIGHT, arrayList, ints, m, l, i, j, bl);
            }
         }
      }

      return arrayList;
   }

   private void func_178396_a(ItemModelGenerator.SpanFacing spanFacing, List<ItemModelGenerator.Span> list, int[] ints, int i, int j, int k, int l, boolean bl) {
      boolean bl2 = this.func_178391_a(ints, i + spanFacing.func_178372_b(), j + spanFacing.func_178371_c(), k, l) && bl;
      if (bl2) {
         this.func_178395_a(list, spanFacing, i, j);
      }
   }

   private void func_178395_a(List<ItemModelGenerator.Span> list, ItemModelGenerator.SpanFacing spanFacing, int i, int j) {
      ItemModelGenerator.Span span = null;

      for (ItemModelGenerator.Span span2 : list) {
         if (span2.func_178383_a() == spanFacing) {
            int k = spanFacing.func_178369_d() ? j : i;
            if (span2.func_178381_d() == k) {
               span = span2;
               break;
            }
         }
      }

      int l = spanFacing.func_178369_d() ? j : i;
      int m = spanFacing.func_178369_d() ? i : j;
      if (span == null) {
         list.add(new ItemModelGenerator.Span(spanFacing, m, l));
      } else {
         span.func_178382_a(m);
      }
   }

   private boolean func_178391_a(int[] ints, int i, int j, int k, int l) {
      return i >= 0 && j >= 0 && i < k && j < l ? (ints[j * k + i] >> 24 & 0xFF) == 0 : true;
   }

   public static class Span {
      private final ItemModelGenerator.SpanFacing spanFacing;
      private int field_178387_b;
      private int field_178388_c;
      private final int field_178386_d;

      public Span(ItemModelGenerator.SpanFacing spanFacingIn, int i, int j) {
         this.spanFacing = spanFacingIn;
         this.field_178387_b = i;
         this.field_178388_c = i;
         this.field_178386_d = j;
      }

      public void func_178382_a(int i) {
         if (i < this.field_178387_b) {
            this.field_178387_b = i;
         } else if (i > this.field_178388_c) {
            this.field_178388_c = i;
         }
      }

      public ItemModelGenerator.SpanFacing func_178383_a() {
         return this.spanFacing;
      }

      public int func_178385_b() {
         return this.field_178387_b;
      }

      public int func_178384_c() {
         return this.field_178388_c;
      }

      public int func_178381_d() {
         return this.field_178386_d;
      }
   }

   public enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      private final EnumFacing facing;
      private final int field_178373_f;
      private final int field_178374_g;

      SpanFacing(EnumFacing facing, int j, int k) {
         this.facing = facing;
         this.field_178373_f = j;
         this.field_178374_g = k;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }

      public int func_178372_b() {
         return this.field_178373_f;
      }

      public int func_178371_c() {
         return this.field_178374_g;
      }

      private boolean func_178369_d() {
         return this == DOWN || this == UP;
      }
   }
}
