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
   private static final long a = 4276064630866L;

   public ModelBlock makeItemModel(TextureMap var1, ModelBlock var2) {
      HashMap var6 = Maps.newHashMap();
      ArrayList var7 = Lists.newArrayList();

      for (int var8 = 0; var8 < LAYERS.size(); var8++) {
         String var9 = LAYERS.get(var8);
         if (!var2.isTexturePresent(var9)) {
            break;
         }

         String var10 = var2.resolveTextureName(var9);
         var6.put(var9, var10);
         TextureAtlasSprite var11 = var1.getAtlasSprite(new ResourceLocation(var10).toString());
         var7.addAll(this.func_178394_a(var8, var9, var11));
      }

      if (var7.isEmpty()) {
         return null;
      }

      var6.put("particle", var2.isTexturePresent("particle") ? var2.resolveTextureName("particle") : (String)var6.get("layer0"));
      return new ModelBlock(var7, var6, false, false, var2.getAllTransforms());
   }

   private List<BlockPart> func_178394_a(int var1, String var2, TextureAtlasSprite var3) {
      HashMap var4 = Maps.newHashMap();
      var4.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, var1, var2, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      var4.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, var1, var2, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList var5 = Lists.newArrayList();
      var5.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4, (BlockPartRotation)null, true));
      var5.addAll(this.func_178397_a(var3, var2, var1));
      return var5;
   }

   private List<BlockPart> func_178397_a(TextureAtlasSprite var1, String var2, int var3) {
      float var7 = var1.getIconWidth();
      float var8 = var1.getIconHeight();
      ArrayList var9 = Lists.newArrayList();

      for (ItemModelGenerator.Span var11 : this.func_178393_a(var1)) {
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         float var17 = 0.0F;
         float var18 = 0.0F;
         float var19 = 0.0F;
         float var20 = 0.0F;
         float var21 = 0.0F;
         float var22 = var11.func_178385_b();
         float var23 = var11.func_178384_c();
         float var24 = var11.func_178381_d();
         ItemModelGenerator.SpanFacing var25 = var11.func_178383_a();
         switch (var25) {
            case UP:
               var16 = var22;
               var12 = var22;
               var14 = var17 = var23 + 1.0F;
               var18 = var24;
               var13 = var24;
               var19 = var24;
               var15 = var24;
               var20 = 16.0F / var7;
               var21 = 16.0F / (var8 - 1.0F);
               break;
            case DOWN:
               var19 = var24;
               var18 = var24;
               var16 = var22;
               var12 = var22;
               var14 = var17 = var23 + 1.0F;
               var13 = var24 + 1.0F;
               var15 = var24 + 1.0F;
               var20 = 16.0F / var7;
               var21 = 16.0F / (var8 - 1.0F);
               break;
            case LEFT:
               var16 = var24;
               var12 = var24;
               var17 = var24;
               var14 = var24;
               var19 = var22;
               var13 = var22;
               var15 = var18 = var23 + 1.0F;
               var20 = 16.0F / (var7 - 1.0F);
               var21 = 16.0F / var8;
               break;
            case RIGHT:
               var17 = var24;
               var16 = var24;
               var12 = var24 + 1.0F;
               var14 = var24 + 1.0F;
               var19 = var22;
               var13 = var22;
               var15 = var18 = var23 + 1.0F;
               var20 = 16.0F / (var7 - 1.0F);
               var21 = 16.0F / var8;
         }

         float var26 = 16.0F / var7;
         float var27 = 16.0F / var8;
         var12 *= var26;
         var14 *= var26;
         var13 *= var27;
         var15 *= var27;
         var13 = 16.0F - var13;
         var15 = 16.0F - var15;
         var16 *= var20;
         var17 *= var20;
         var18 *= var21;
         var19 *= var21;
         HashMap var28 = Maps.newHashMap();
         var28.put(var25.getFacing(), new BlockPartFace((EnumFacing)null, var3, var2, new BlockFaceUV(new float[]{var16, var18, var17, var19}, 0)));
         switch (var25) {
            case UP:
               var9.add(new BlockPart(new Vector3f(var12, var13, 7.5F), new Vector3f(var14, var13, 8.5F), var28, (BlockPartRotation)null, true));
               break;
            case DOWN:
               var9.add(new BlockPart(new Vector3f(var12, var15, 7.5F), new Vector3f(var14, var15, 8.5F), var28, (BlockPartRotation)null, true));
               break;
            case LEFT:
               var9.add(new BlockPart(new Vector3f(var12, var13, 7.5F), new Vector3f(var12, var15, 8.5F), var28, (BlockPartRotation)null, true));
               break;
            case RIGHT:
               var9.add(new BlockPart(new Vector3f(var14, var13, 7.5F), new Vector3f(var14, var15, 8.5F), var28, (BlockPartRotation)null, true));
         }
      }

      return var9;
   }

   private List<ItemModelGenerator.Span> func_178393_a(TextureAtlasSprite var1) {
      int var5 = var1.getIconWidth();
      int var6 = var1.getIconHeight();
      ArrayList var7 = Lists.newArrayList();

      for (int var8 = 0; var8 < var1.getFrameCount(); var8++) {
         int[] var9 = var1.getFrameTextureData(var8)[0];

         for (int var10 = 0; var10 < var6; var10++) {
            for (int var11 = 0; var11 < var5; var11++) {
               boolean var12 = !this.func_178391_a(var9, var11, var10, var5, var6);
               this.func_178396_a(ItemModelGenerator.SpanFacing.UP, var7, var9, var11, var10, var5, var6, var12);
               this.func_178396_a(ItemModelGenerator.SpanFacing.DOWN, var7, var9, var11, var10, var5, var6, var12);
               this.func_178396_a(ItemModelGenerator.SpanFacing.LEFT, var7, var9, var11, var10, var5, var6, var12);
               this.func_178396_a(ItemModelGenerator.SpanFacing.RIGHT, var7, var9, var11, var10, var5, var6, var12);
            }
         }
      }

      return var7;
   }

   private void func_178396_a(
      ItemModelGenerator.SpanFacing var1, List<ItemModelGenerator.Span> var2, int[] var3, int var4, int var5, int var6, int var7, boolean var8
   ) {
      boolean var12 = this.func_178391_a(var3, var4 + var1.func_178372_b(), var5 + var1.func_178371_c(), var6, var7) && var8;
      if (var12) {
         this.func_178395_a(var2, var1, var4, var5);
      }
   }

   private void func_178395_a(List<ItemModelGenerator.Span> var1, ItemModelGenerator.SpanFacing var2, int var3, int var4) {
      ItemModelGenerator.Span var8 = null;

      for (ItemModelGenerator.Span var10 : var1) {
         if (var10.func_178383_a() == var2) {
            int var11 = var2.func_178369_d() ? var4 : var3;
            if (var10.func_178381_d() == var11) {
               var8 = var10;
               break;
            }
         }
      }

      int var12 = var2.func_178369_d() ? var4 : var3;
      int var13 = var2.func_178369_d() ? var3 : var4;
      if (var8 == null) {
         var1.add(new ItemModelGenerator.Span(var2, var13, var12));
      } else {
         var8.func_178382_a(var13);
      }
   }

   private boolean func_178391_a(int[] var1, int var2, int var3, int var4, int var5) {
      return var2 >= 0 && var3 >= 0 && var2 < var4 && var3 < var5 ? (var1[var3 * var4 + var2] >> 24 & 0xFF) == 0 : true;
   }

   public static class Span {
      private final ItemModelGenerator.SpanFacing spanFacing;
      private int field_178387_b;
      private int field_178388_c;
      private final int field_178386_d;
      private static final long a = 56688461196566L;

      public Span(ItemModelGenerator.SpanFacing var1, int var2, int var3) {
         this.spanFacing = var1;
         this.field_178387_b = var2;
         this.field_178388_c = var2;
         this.field_178386_d = var3;
      }

      public void func_178382_a(int var1) {
         if (var1 < this.field_178387_b) {
            this.field_178387_b = var1;
         } else if (var1 > this.field_178388_c) {
            this.field_178388_c = var1;
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
      private static final long a = 140273940548790L;

      SpanFacing(EnumFacing var3, int var4, int var5) {
         this.facing = var3;
         this.field_178373_f = var4;
         this.field_178374_g = var5;
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
