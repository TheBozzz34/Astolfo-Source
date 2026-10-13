package bypass.module.combat;

import bypass.ClientChat;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.hidden.BotDetector;
import bypass.module.hidden.HypixelGameTracker;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.user.AstolfoUser;
import bypass.user.AstolfoUserStatus;
import bypass.util.GameUtil;
import bypass.util.Rotation;
import bypass.util.Rotator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.WorldSettings;

public class Aimbot extends Module {
   private static final Rotator rotator = new Rotator(false, 0.0F);
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("killaura entiti")
      .add("Players", new BooleanSetting("Players", "Add players to Entities", true))
      .add("Hostiles", new BooleanSetting("Hostiles", "Add monsters to Entities", false))
      .add("Passives", new BooleanSetting("Passives", "Add passives to Entities", false))
      .add("Neutrals", new BooleanSetting("Neutrals", "Add neutrals to Entities", false))
      .add("Bosses", new BooleanSetting("Bosses", "Add bosses to Entities", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Add invisibles to Entities", false))
      .add("Friends", new BooleanSetting("Friends", "Add friends to Entities", false))
      .add("Teams", new BooleanSetting("Teams", "Add teams to Entities", false))
      .add("Astolfo-Users", new BooleanSetting("Astolfo-Users", "Hits astolfo users", true))
      .build();
   private double gravity = 0.05;
   private double velocity = 1.8;
   private EntityLivingBase unusedTarget;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private Listener<MotionEvent> onMotion = var1 -> this.findTargets();
   private static final long c = 123357317965043L;

   public Aimbot() {
      super("Aimbot", Category.COMBAT);
      this.addSettings(this.entities);
   }

   private ArrayList<EntityLivingBase> findTargets() {
      long var1 = c ^ 43126422368545L;
      long var10001 = var1 ^ 42086716010639L;
      int var3 = (int)((var1 ^ 42086716010639L) >>> 56);
      int var4 = (int)((var1 ^ 42086716010639L) << 8 >>> 32);
      int var5 = (int)(var10001 << 40 >>> 40);
      long var6 = var1 ^ 54241723553064L;
      ArrayList var9 = new ArrayList(10);
      List var10 = mc.theWorld.loadedEntityList;
      int var11 = 0;

      for (int var12 = var10.size(); var11 < var12; var11++) {
         Entity var13 = (Entity)var10.get(var11);
         if (var13 instanceof EntityLivingBase) {
            EntityLivingBase var14 = (EntityLivingBase)var13;
            if (var9.size() > 10) {
               break;
            }

            if (!BotDetector.w.contains(var14) && (!client.getRelationManager().z(var13) || this.entities.getSetting("Friends").asBoolean())) {
               AstolfoUser var15 = client.getUserManager().g(var13.getName());
               if ((var15 == null || var15.K != AstolfoUserStatus.C) && this.isValidTarget(var14) && this.canHit(var14)) {
                  ClientChat.CHAT.I(String.valueOf(var14));
                  var9.add(var14);
               }
            }
         }
      }

      return var9;
   }

   private boolean isValidTarget(EntityLivingBase var1) {
      long var2 = c ^ 125985551913258L;
      long var4 = var2 ^ 111021845804323L;
      if (mc.thePlayer == null || mc.theWorld == null || var1 == mc.thePlayer || GameUtil.isRemoved(var1)) {
         return false;
      }

      if (mc.thePlayer.isSpectator()) {
         return false;
      }

      if (var1 instanceof EntityPlayer && !mc.thePlayer.canAttackPlayer((EntityPlayer)var1)) {
         return false;
      }

      if ((!GameUtil.isDead(var1) || Double.isNaN(var1.getHealth())) && (!GameUtil.S(var1) || this.entities.getSetting("Invisibles").asBoolean())) {
         if (var1 instanceof EntityPlayer && this.entities.getSetting("Players").asBoolean()) {
            EntityPlayer var8 = (EntityPlayer)var1;
            if (GameUtil.O(mc.thePlayer, var8) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            } else if (((AbstractClientPlayer)var8).getPlayerInfo() != null
               && ((AbstractClientPlayer)var8).getPlayerInfo().getGameType() == WorldSettings.GameType.CREATIVE) {
               return false;
            } else {
               return !this.entities.getSetting("Astolfo-Users").asBoolean() && client.getUserManager().g(var1.getName()) != null
                  ? false
                  : !var8.isPlayerSleeping() && var8.getEntityId() < 1000000000 && var8.getEntityId() > -1;
            }
         } else {
            if (GameUtil.isBoss(var1) && GameUtil.O(mc.thePlayer, var1) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            }

            if (HypixelGameTracker.game == 3 && var1 instanceof EntityZombie && GameUtil.isOnHypixel()) {
               ItemStack var7 = var1.getCurrentArmor(3);
               if (var7 != null && var7.getItem() != null && var7.getItem() == Items.skull) {
                  return true;
               }
            }

            return GameUtil.isHostile(var1) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(var1) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(var1) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean();
         }
      } else {
         return false;
      }
   }

   private boolean canHit(EntityLivingBase var1) {
      double var5 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX);
      double var7 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY);
      double var9 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
      double[] var11 = this.predictPosition(var1);
      float[] var12 = this.getBowRotations(this.gravity, this.velocity, var11[0], var11[1], var11[2]);
      if (var12 == null) {
         return false;
      }

      mc.thePlayer.rotationYaw = var12[0];
      mc.thePlayer.rotationPitch = var12[1];
      chat.I(String.format("y: %s p: %s", var12[0], var12[1]));
      double var13 = var5 - MathHelper.cos(var12[0] / 180.0F * (float) Math.PI) * 0.16F;
      double var15 = var7 + mc.thePlayer.getEyeHeight() - 0.1000000014901161;
      double var17 = var9 - MathHelper.sin(var12[0] / 180.0F * (float) Math.PI) * 0.16F;
      double var19 = -MathHelper.sin(var12[0] / 180.0F * (float) Math.PI) * MathHelper.cos(var12[1] / 180.0F * (float) Math.PI) * 1.0;
      double var21 = -MathHelper.sin(var12[1] / 180.0F * (float) Math.PI) * 1.0;
      double var23 = MathHelper.cos(var12[0] / 180.0F * (float) Math.PI) * MathHelper.cos(var12[1] / 180.0F * (float) Math.PI) * 1.0;
      float var25 = 1.0F;
      float var26 = MathHelper.sqrt_double(var19 * var19 + var21 * var21 + var23 * var23);
      var19 /= var26;
      var21 /= var26;
      var23 /= var26;
      float var27 = var25 * 2.0F;
      var19 *= var27 * 1.5F;
      var21 *= var27 * 1.5F;
      var23 *= var27 * 1.5F;
      boolean var28 = false;
      MovingObjectPosition var29 = null;

      while (!var28 && var15 > 0.0) {
         Vec3 var30 = new Vec3(var13, var15, var17);
         Vec3 var31 = new Vec3(var13 + var19, var15 + var21, var17 + var23);
         MovingObjectPosition var32 = mc.theWorld.rayTraceBlocks(var30, var31, false, true, false);
         if (var32 != null && var32.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
            var29 = var32;
            var28 = true;
         }

         var13 += var19;
         var15 += var21;
         var17 += var23;
         float var33 = 0.99F;
         var19 *= var33;
         var21 *= var33;
         var23 *= var33;
         var21 -= 0.05;
      }

      if (var29 != null && var29.getBlockPos() != null) {
         double var41 = this.distance(var11[0], var11[1], var11[2], var29.getBlockPos().getX(), var29.getBlockPos().getY(), var29.getBlockPos().getZ());
         ClientChat.CHAT.I(String.valueOf(var41));
         if (var41 < 5.0) {
            return true;
         }
      }

      return false;
   }

   public double distance(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = var1 - var7;
      double var15 = var3 - var9;
      double var17 = var5 - var11;
      return MathHelper.sqrt_double(var13 * var13 + var15 * var15 + var17 * var17);
   }

   private double[] predictPosition(EntityLivingBase var1) {
      double var5 = mc.thePlayer.posY;
      double var7 = var1.posX;
      double var9 = var1.posY;
      double var11 = var1.posZ;
      double var13 = var1.prevPosX;
      double var15 = var1.prevPosY;
      double var17 = var1.prevPosZ;
      double var19 = 0.0;
      if (mc.thePlayer.getDistance(var13, var15, var17) > mc.thePlayer.getDistance(var7, var9, var11)) {
         var19 = this.velocity * mc.thePlayer.getDistanceToEntity(var1) * 1.2 + 5.0;
      }

      float[] var21 = this.getBowRotations(this.gravity, this.velocity, var7, var9, var11);
      if (var21 != null) {
         double var22 = -1.0F * var21[1] * (Math.PI / 180.0);
         var19 = this.flightTime(this.gravity, this.velocity, var22, var5 - var9, var7, var9, var11);
      }

      double var32 = 0.0;
      double var24 = 0.0;
      double var26 = 0.0;
      var32 = (var7 - var13) * (var19 * 1.0 + 5.0);
      var26 = (var11 - var17) * (var19 * 1.0 + 5.0);
      var24 -= this.heightAboveGround(var1);
      double var28 = var1.getEntityBoundingBox().minY;
      double var30 = var1.getEntityBoundingBox().maxY;
      var24 += (var30 - var28) * 0.75;
      return new double[]{var7 + var32, var9 + var24, var11 + var26};
   }

   private float[] getBowRotations(double var1, double var3, double var5, double var7, double var9) {
      Vector3d var14 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      double var15 = mc.thePlayer.posX;
      double var17 = mc.thePlayer.posY;
      var17 += mc.thePlayer.getEyeHeight();
      double var19 = mc.thePlayer.posZ;
      double var21 = this.horizontalDistance(var5, var9, var15, var19);
      double var23 = var7 - var17;
      double var25 = Math.pow(var3, 4.0) - var1 * (var1 * Math.pow(var21, 2.0) + 2.0 * var23 * Math.pow(var3, 2.0));
      if (var25 >= 0.0) {
         double var27 = (Math.pow(var3, 2.0) - Math.sqrt(var25)) / (var1 * var21);
         double var29 = Math.atan(var27);
         double var31 = var29 * 180.0 / Math.PI;
         Vector3d var33 = new Vector3d(var5, var7, var9);
         Rotation var34 = rotator.rotationTo(var33, var14);
         float var35 = MathHelper.wrapAngleTo180_float(var34.getYaw() - mc.thePlayer.rotationYaw);
         float var36 = mc.thePlayer.rotationYaw + var35;
         float var37 = (float)(0.0 - var31);
         return new float[]{var36, var37};
      } else {
         return null;
      }
   }

   private double horizontalDistance(double var1, double var3, double var5, double var7) {
      return Math.sqrt(Math.pow(var5 - var1, 2.0) + Math.pow(var7 - var3, 2.0));
   }

   private double flightTime(double var1, double var3, double var5, double var7, double var9, double var11, double var13) {
      double var15 = mc.thePlayer.posX;
      double var17 = mc.thePlayer.posZ;
      return this.horizontalDistance(var15, var17, var9, var13) / (Math.cos(var5) * var3);
   }

   private double heightAboveGround(EntityLivingBase var1) {
      double var5 = var1.getEntityBoundingBox().minX;
      double var7 = var1.getEntityBoundingBox().minY;
      double var9 = var1.getEntityBoundingBox().minZ;
      double var11 = var1.getEntityBoundingBox().maxX;
      double var13 = var1.getEntityBoundingBox().maxZ;

      for (double var15 = var7; var15 > 0.0; var15 -= 0.1) {
         BlockPos var17 = new BlockPos(var5, var15, var9);
         BlockPos var18 = new BlockPos(var5, var15, var13);
         BlockPos var19 = new BlockPos(var11, var15, var9);
         BlockPos var20 = new BlockPos(var11, var15, var13);
         IBlockState var21 = mc.theWorld.getBlockState(new BlockPos(var17));
         IBlockState var22 = mc.theWorld.getBlockState(new BlockPos(var18));
         IBlockState var23 = mc.theWorld.getBlockState(new BlockPos(var19));
         IBlockState var24 = mc.theWorld.getBlockState(new BlockPos(var20));
         if (this.isSolid(var22.getBlock()) && this.isSolid(var21.getBlock()) && this.isSolid(var23.getBlock()) && this.isSolid(var24.getBlock())) {
            return var7 - var15;
         }
      }

      return 1.0;
   }

   private boolean isSolid(Block var1) {
      return var1 == null
         ? false
         : var1.getMaterial().blocksMovement() && var1.isOpaqueCube() && !var1.getMaterial().isLiquid() && !(var1 instanceof BlockFalling);
   }
}
