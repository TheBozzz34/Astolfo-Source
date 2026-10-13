package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {
   private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
   public static TileEntitySkullRenderer instance;
   private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
   private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
   private static final long a = 38092187634674L;

   public void renderTileEntityAt(TileEntitySkull var1, double var2, double var4, double var6, float var8, int var9) {
      EnumFacing var12 = EnumFacing.getFront(var1.getBlockMetadata() & 7);
      this.renderSkull((float)var2, (float)var4, (float)var6, var12, var1.getSkullRotation() * 360 / 16.0F, var1.getSkullType(), var1.getPlayerProfile(), var9);
   }

   @Override
   public void setRendererDispatcher(TileEntityRendererDispatcher var1) {
      super.setRendererDispatcher(var1);
      instance = this;
   }

   public void renderSkull(float var1, float var2, float var3, EnumFacing var4, float var5, int var6, GameProfile var7, int var8) {
      long var9 = a ^ 106583929973648L;
      ModelSkeletonHead var12 = this.skeletonHead;
      if (var8 >= 0) {
         this.bindTexture(DESTROY_STAGES[var8]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         long var10000 = 691122399483883479L ^ var9;
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch (var6) {
            case 0:
            default:
               this.bindTexture(SKELETON_TEXTURES);
               break;
            case 1:
               this.bindTexture(WITHER_SKELETON_TEXTURES);
               break;
            case 2:
               this.bindTexture(ZOMBIE_TEXTURES);
               var12 = this.humanoidHead;
               break;
            case 3:
               var12 = this.humanoidHead;
               ResourceLocation var13 = DefaultPlayerSkin.getDefaultSkinLegacy();
               if (var7 != null) {
                  if (var7.getName() != null && var7.getName().equalsIgnoreCase("Epic Gamer Skull")) {
                     this.bindTexture(var13);
                     return;
                  }

                  Minecraft var14 = Minecraft.getMinecraft();
                  Map var15 = var14.getSkinManager().loadSkinFromCache(var7);
                  if (var15.containsKey(Type.SKIN)) {
                     var13 = var14.getSkinManager().loadSkin((MinecraftProfileTexture)var15.get(Type.SKIN), Type.SKIN);
                  } else {
                     UUID var16 = EntityPlayer.getUUID(var7);
                     var13 = DefaultPlayerSkin.getDefaultSkin(var16);
                  }
               }

               this.bindTexture(var13);
               break;
            case 4:
               this.bindTexture(CREEPER_TEXTURES);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (var4 != EnumFacing.UP) {
         switch (var4) {
            case NORTH:
               GlStateManager.translate(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
               break;
            case SOUTH:
               GlStateManager.translate(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
               var5 = 180.0F;
               break;
            case WEST:
               GlStateManager.translate(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
               var5 = 270.0F;
               break;
            case EAST:
            default:
               GlStateManager.translate(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
               var5 = 90.0F;
         }
      } else {
         GlStateManager.translate(var1 + 0.5F, var2, var3 + 0.5F);
      }

      float var17 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      var12.render((Entity)null, 0.0F, 0.0F, 0.0F, var5, 0.0F, var17);
      GlStateManager.popMatrix();
      if (var8 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         long var18 = 7892806324261564880L ^ var9;
         GlStateManager.matrixMode(5888);
      }
   }
}
