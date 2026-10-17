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
   private Listener<MotionEvent> onMotion = motionEvent -> this.findTargets();

   public Aimbot() {
      super("Aimbot", Category.COMBAT);
      this.addSettings(this.entities);
   }

   private ArrayList<EntityLivingBase> findTargets() {
      ArrayList arrayList = new ArrayList(10);
      List list = mc.theWorld.loadedEntityList;
      int i = 0;

      for (int j = list.size(); i < j; i++) {
         Entity entity = (Entity)list.get(i);
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            if (arrayList.size() > 10) {
               break;
            }

            if (!BotDetector.bots.contains(entityLivingBase) && (!client.getRelationManager().isFriend(entity) || this.entities.getSetting("Friends").asBoolean())) {
               AstolfoUser astolfoUser = client.getUserManager().getUser(entity.getName());
               if ((astolfoUser == null || astolfoUser.status != AstolfoUserStatus.C) && this.isValidTarget(entityLivingBase) && this.canHit(entityLivingBase)) {
                  ClientChat.CHAT.print(String.valueOf(entityLivingBase));
                  arrayList.add(entityLivingBase);
               }
            }
         }
      }

      return arrayList;
   }

   private boolean isValidTarget(EntityLivingBase entityLivingBase) {
      if (mc.thePlayer == null || mc.theWorld == null || entityLivingBase == mc.thePlayer || GameUtil.isRemoved(entityLivingBase)) {
         return false;
      }

      if (mc.thePlayer.isSpectator()) {
         return false;
      }

      if (entityLivingBase instanceof EntityPlayer && !mc.thePlayer.canAttackPlayer((EntityPlayer)entityLivingBase)) {
         return false;
      }

      if ((!GameUtil.isDead(entityLivingBase) || Double.isNaN(entityLivingBase.getHealth()))
         && (!GameUtil.isFullyInvisible(entityLivingBase) || this.entities.getSetting("Invisibles").asBoolean())) {
         if (entityLivingBase instanceof EntityPlayer && this.entities.getSetting("Players").asBoolean()) {
            EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
            if (GameUtil.isOnSameTeam(mc.thePlayer, entityPlayer) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            } else if (((AbstractClientPlayer)entityPlayer).getPlayerInfo() != null
               && ((AbstractClientPlayer)entityPlayer).getPlayerInfo().getGameType() == WorldSettings.GameType.CREATIVE) {
               return false;
            } else {
               return !this.entities.getSetting("Astolfo-Users").asBoolean() && client.getUserManager().getUser(entityLivingBase.getName()) != null
                  ? false
                  : !entityPlayer.isPlayerSleeping() && entityPlayer.getEntityId() < 1000000000 && entityPlayer.getEntityId() > -1;
            }
         } else {
            if (GameUtil.isBoss(entityLivingBase) && GameUtil.isOnSameTeam(mc.thePlayer, entityLivingBase) && this.entities.getSetting("Teams").asBoolean()) {
               return false;
            }

            if (HypixelGameTracker.game == 3 && entityLivingBase instanceof EntityZombie && GameUtil.isOnHypixel()) {
               ItemStack itemStack = entityLivingBase.getCurrentArmor(3);
               if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() == Items.skull) {
                  return true;
               }
            }

            return GameUtil.isHostile(entityLivingBase) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(entityLivingBase) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(entityLivingBase) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(entityLivingBase) && this.entities.getSetting("Bosses").asBoolean();
         }
      } else {
         return false;
      }
   }

   private boolean canHit(EntityLivingBase entityLivingBase) {
      double d = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX);
      double e = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY);
      double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
      double[] doubles = this.predictPosition(entityLivingBase);
      float[] floats = this.getBowRotations(this.gravity, this.velocity, doubles[0], doubles[1], doubles[2]);
      if (floats == null) {
         return false;
      }

      mc.thePlayer.rotationYaw = floats[0];
      mc.thePlayer.rotationPitch = floats[1];
      chat.print(String.format("y: %s p: %s", floats[0], floats[1]));
      double d3 = d - MathHelper.cos(floats[0] / 180.0F * (float) Math.PI) * 0.16F;
      double d4 = e + mc.thePlayer.getEyeHeight() - 0.1000000014901161;
      double d5 = d2 - MathHelper.sin(floats[0] / 180.0F * (float) Math.PI) * 0.16F;
      double d6 = -MathHelper.sin(floats[0] / 180.0F * (float) Math.PI) * MathHelper.cos(floats[1] / 180.0F * (float) Math.PI) * 1.0;
      double d7 = -MathHelper.sin(floats[1] / 180.0F * (float) Math.PI) * 1.0;
      double d8 = MathHelper.cos(floats[0] / 180.0F * (float) Math.PI) * MathHelper.cos(floats[1] / 180.0F * (float) Math.PI) * 1.0;
      float f = 1.0F;
      float g = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
      d6 /= g;
      d7 /= g;
      d8 /= g;
      float h = f * 2.0F;
      d6 *= h * 1.5F;
      d7 *= h * 1.5F;
      d8 *= h * 1.5F;
      boolean bl = false;
      MovingObjectPosition movingObjectPosition = null;

      while (!bl && d4 > 0.0) {
         Vec3 vec3 = new Vec3(d3, d4, d5);
         Vec3 vec32 = new Vec3(d3 + d6, d4 + d7, d5 + d8);
         MovingObjectPosition movingObjectPosition2 = mc.theWorld.rayTraceBlocks(vec3, vec32, false, true, false);
         if (movingObjectPosition2 != null && movingObjectPosition2.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
            movingObjectPosition = movingObjectPosition2;
            bl = true;
         }

         d3 += d6;
         d4 += d7;
         d5 += d8;
         float f2 = 0.99F;
         d6 *= f2;
         d7 *= f2;
         d8 *= f2;
         d7 -= 0.05;
      }

      if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null) {
         double d9 = this.distance(doubles[0], doubles[1], doubles[2], movingObjectPosition.getBlockPos().getX(), movingObjectPosition.getBlockPos().getY(), movingObjectPosition.getBlockPos().getZ());
         ClientChat.CHAT.print(String.valueOf(d9));
         if (d9 < 5.0) {
            return true;
         }
      }

      return false;
   }

   public double distance(double d, double e, double d2, double d3, double d4, double d5) {
      double d6 = d - d3;
      double d7 = e - d4;
      double d8 = d2 - d5;
      return MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
   }

   private double[] predictPosition(EntityLivingBase entityLivingBase) {
      double d = mc.thePlayer.posY;
      double e = entityLivingBase.posX;
      double d2 = entityLivingBase.posY;
      double d3 = entityLivingBase.posZ;
      double d4 = entityLivingBase.prevPosX;
      double d5 = entityLivingBase.prevPosY;
      double d6 = entityLivingBase.prevPosZ;
      double d7 = 0.0;
      if (mc.thePlayer.getDistance(d4, d5, d6) > mc.thePlayer.getDistance(e, d2, d3)) {
         d7 = this.velocity * mc.thePlayer.getDistanceToEntity(entityLivingBase) * 1.2 + 5.0;
      }

      float[] floats = this.getBowRotations(this.gravity, this.velocity, e, d2, d3);
      if (floats != null) {
         double d8 = -1.0F * floats[1] * (Math.PI / 180.0);
         d7 = this.flightTime(this.gravity, this.velocity, d8, d - d2, e, d2, d3);
      }

      double d9 = 0.0;
      double d10 = 0.0;
      double d11 = 0.0;
      d9 = (e - d4) * (d7 * 1.0 + 5.0);
      d11 = (d3 - d6) * (d7 * 1.0 + 5.0);
      d10 -= this.heightAboveGround(entityLivingBase);
      double d12 = entityLivingBase.getEntityBoundingBox().minY;
      double d13 = entityLivingBase.getEntityBoundingBox().maxY;
      d10 += (d13 - d12) * 0.75;
      return new double[]{e + d9, d2 + d10, d3 + d11};
   }

   private float[] getBowRotations(double d, double e, double d2, double d3, double d4) {
      Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      double d5 = mc.thePlayer.posX;
      double d6 = mc.thePlayer.posY;
      d6 += mc.thePlayer.getEyeHeight();
      double d7 = mc.thePlayer.posZ;
      double d8 = this.horizontalDistance(d2, d4, d5, d7);
      double d9 = d3 - d6;
      double d10 = Math.pow(e, 4.0) - d * (d * Math.pow(d8, 2.0) + 2.0 * d9 * Math.pow(e, 2.0));
      if (d10 >= 0.0) {
         double d11 = (Math.pow(e, 2.0) - Math.sqrt(d10)) / (d * d8);
         double d12 = Math.atan(d11);
         double d13 = d12 * 180.0 / Math.PI;
         Vector3d vector3d2 = new Vector3d(d2, d3, d4);
         Rotation rotation = rotator.rotationTo(vector3d2, vector3d);
         float f = MathHelper.wrapAngleTo180_float(rotation.getYaw() - mc.thePlayer.rotationYaw);
         float g = mc.thePlayer.rotationYaw + f;
         float h = (float)(0.0 - d13);
         return new float[]{g, h};
      } else {
         return null;
      }
   }

   private double horizontalDistance(double d, double e, double d2, double d3) {
      return Math.sqrt(Math.pow(d2 - d, 2.0) + Math.pow(d3 - e, 2.0));
   }

   private double flightTime(double d, double e, double d2, double d3, double d4, double d5, double d6) {
      double d7 = mc.thePlayer.posX;
      double d8 = mc.thePlayer.posZ;
      return this.horizontalDistance(d7, d8, d4, d6) / (Math.cos(d2) * e);
   }

   private double heightAboveGround(EntityLivingBase entityLivingBase) {
      double d = entityLivingBase.getEntityBoundingBox().minX;
      double e = entityLivingBase.getEntityBoundingBox().minY;
      double d2 = entityLivingBase.getEntityBoundingBox().minZ;
      double d3 = entityLivingBase.getEntityBoundingBox().maxX;
      double d4 = entityLivingBase.getEntityBoundingBox().maxZ;

      for (double d5 = e; d5 > 0.0; d5 -= 0.1) {
         BlockPos blockPos = new BlockPos(d, d5, d2);
         BlockPos blockPos2 = new BlockPos(d, d5, d4);
         BlockPos blockPos3 = new BlockPos(d3, d5, d2);
         BlockPos blockPos4 = new BlockPos(d3, d5, d4);
         IBlockState iBlockState = mc.theWorld.getBlockState(new BlockPos(blockPos));
         IBlockState iBlockState2 = mc.theWorld.getBlockState(new BlockPos(blockPos2));
         IBlockState iBlockState3 = mc.theWorld.getBlockState(new BlockPos(blockPos3));
         IBlockState iBlockState4 = mc.theWorld.getBlockState(new BlockPos(blockPos4));
         if (this.isSolid(iBlockState2.getBlock()) && this.isSolid(iBlockState.getBlock()) && this.isSolid(iBlockState3.getBlock()) && this.isSolid(iBlockState4.getBlock())) {
            return e - d5;
         }
      }

      return 1.0;
   }

   private boolean isSolid(Block block) {
      return block == null
         ? false
         : block.getMaterial().blocksMovement() && block.isOpaqueCube() && !block.getMaterial().isLiquid() && !(block instanceof BlockFalling);
   }
}
