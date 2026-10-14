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

   public void renderTileEntityAt(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage) {
      EnumFacing enumFacing = EnumFacing.getFront(te.getBlockMetadata() & 7);
      this.renderSkull((float)x, (float)y, (float)z, enumFacing, te.getSkullRotation() * 360 / 16.0F, te.getSkullType(), te.getPlayerProfile(), destroyStage);
   }

   @Override
   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void renderSkull(float f, float g, float h, EnumFacing enumFacing, float f2, int i, GameProfile gameProfile, int j) {
      long l = a ^ 106583929973648L;
      ModelSkeletonHead modelSkeletonHead = this.skeletonHead;
      if (j >= 0) {
         this.bindTexture(DESTROY_STAGES[j]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         long m = 691122399483883479L ^ l;
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch (i) {
            case 0:
            default:
               this.bindTexture(SKELETON_TEXTURES);
               break;
            case 1:
               this.bindTexture(WITHER_SKELETON_TEXTURES);
               break;
            case 2:
               this.bindTexture(ZOMBIE_TEXTURES);
               modelSkeletonHead = this.humanoidHead;
               break;
            case 3:
               modelSkeletonHead = this.humanoidHead;
               ResourceLocation resourceLocation = DefaultPlayerSkin.getDefaultSkinLegacy();
               if (gameProfile != null) {
                  if (gameProfile.getName() != null && gameProfile.getName().equalsIgnoreCase("Epic Gamer Skull")) {
                     this.bindTexture(resourceLocation);
                     return;
                  }

                  Minecraft minecraft = Minecraft.getMinecraft();
                  Map map = minecraft.getSkinManager().loadSkinFromCache(gameProfile);
                  if (map.containsKey(Type.SKIN)) {
                     resourceLocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
                  } else {
                     UUID uuid = EntityPlayer.getUUID(gameProfile);
                     resourceLocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                  }
               }

               this.bindTexture(resourceLocation);
               break;
            case 4:
               this.bindTexture(CREEPER_TEXTURES);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (enumFacing != EnumFacing.UP) {
         switch (enumFacing) {
            case NORTH:
               GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.74F);
               break;
            case SOUTH:
               GlStateManager.translate(f + 0.5F, g + 0.25F, h + 0.26F);
               f2 = 180.0F;
               break;
            case WEST:
               GlStateManager.translate(f + 0.74F, g + 0.25F, h + 0.5F);
               f2 = 270.0F;
               break;
            case EAST:
            default:
               GlStateManager.translate(f + 0.26F, g + 0.25F, h + 0.5F);
               f2 = 90.0F;
         }
      } else {
         GlStateManager.translate(f + 0.5F, g, h + 0.5F);
      }

      float f3 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      modelSkeletonHead.render((Entity)null, 0.0F, 0.0F, 0.0F, f2, 0.0F, f3);
      GlStateManager.popMatrix();
      if (j >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         long n = 7892806324261564880L ^ l;
         GlStateManager.matrixMode(5888);
      }
   }
}
