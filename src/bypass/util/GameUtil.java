package bypass.util;

import bypass.ClientAccess;
import bypass.event.player.MoveEvent;
import bypass.module.hidden.ServerRotationTracker;
import bypass.module.movement.Flight;
import bypass.module.movement.Speed;
import bypass.server.ServerManager;
import bypass.server.ServerType;
import com.google.common.base.Predicates;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;

public final class GameUtil implements ClientAccess {
   private static final Pattern HYPIXEL_ADDRESS_PATTERN = Pattern.compile(
      "^(?:(?:(?:.+\\.)?hypixel\\.net)|(?:209\\.222\\.115\\.\\d{1,3})|(?:99\\.198\\.123\\.[123]?\\d?))\\.?(?::\\d{1,5}\\.?)?$", 2
   );
   private static Pattern colorCodePattern = Pattern.compile("§(.).*§r");
   public static String cachedTabDisplayName = null;

   public static boolean isPassive(Entity entity) {
      return entity instanceof EntityPig
         || entity instanceof EntityCow
         || entity instanceof EntitySheep
         || entity instanceof EntityChicken
         || entity instanceof EntitySquid
         || entity instanceof EntityBat
         || entity instanceof EntityVillager
         || entity instanceof EntityOcelot
         || entity instanceof EntityHorse
         || entity instanceof EntityRabbit && isNormalRabbit(entity);
   }

   public static boolean isNeutral(Entity entity) {
      return entity instanceof EntityWolf && isCalmWolf(entity)
         || entity instanceof EntityIronGolem && isCalmIronGolem(entity)
         || entity instanceof EntityEnderman && isCalmEnderman(entity)
         || entity instanceof EntityPigZombie && isCalmPigZombie(entity);
   }

   public static boolean isHostile(Entity entity) {
      return entity instanceof EntityCreeper
         || entity instanceof EntitySkeleton
         || entity instanceof EntityZombie && !(entity instanceof EntityPigZombie)
         || entity instanceof EntityBlaze
         || entity instanceof EntitySpider
         || entity instanceof EntityWitch
         || entity instanceof EntitySlime
         || entity instanceof EntitySilverfish
         || entity instanceof EntityGuardian
         || entity instanceof EntityEndermite
         || entity instanceof EntityGhast
         || entity instanceof EntityWolf && !isCalmWolf(entity)
         || entity instanceof EntityPigZombie && !isCalmPigZombie(entity)
         || entity instanceof EntityEnderman && !isCalmEnderman(entity)
         || entity instanceof EntityRabbit && !isNormalRabbit(entity)
         || entity instanceof EntityIronGolem && !isCalmIronGolem(entity);
   }

   public static boolean isBoss(Entity entity) {
      return entity instanceof EntityDragon || entity instanceof EntityWither || entity instanceof EntityGiantZombie;
   }

   public static boolean isVehicle(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart;
   }

   public static boolean isObject(Entity entity) {
      return entity instanceof EntityEnderCrystal
         || entity instanceof EntityFallingBlock
         || entity instanceof EntityFireball
         || entity instanceof EntityArmorStand;
   }

   public static boolean isCalmWolf(Entity entity) {
      return entity instanceof EntityWolf && !((EntityWolf)entity).isAngry();
   }

   public static boolean isCalmPigZombie(Entity entity) {
      return entity instanceof EntityPigZombie && entity.rotationPitch == 0.0F && ((EntityPigZombie)entity).getRevengeTimer() <= 0;
   }

   public static boolean isCalmIronGolem(Entity entity) {
      return entity instanceof EntityIronGolem && entity.rotationPitch == 0.0F;
   }

   public static boolean isCalmEnderman(Entity entity) {
      return entity instanceof EntityEnderman && !((EntityEnderman)entity).isScreaming();
   }

   public static boolean isNormalRabbit(Entity entity) {
      return entity instanceof EntityRabbit && ((EntityRabbit)entity).getRabbitType() != 99;
   }

   public static boolean isRemoved(Entity entity) {
      return entity == null || !mc.theWorld.loadedEntityList.contains(entity);
   }

   public static boolean isDead(EntityLivingBase entityLivingBase) {
      return entityLivingBase.getHealth() == 0.0F || entityLivingBase.deathTime > 0;
   }

   public static boolean canSee(Entity entity) {
      for (int i = -1; i < 2; i++) {
         for (int j = -1; j < 2; j++) {
            for (int k = -1; k < 2; k++) {
               double d = i / 1.8;
               double e = j / 1.8;
               double d2 = k / 1.8;
               if (mc.theWorld
                     .rayTraceBlocks(
                        new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                        new Vec3(entity.posX + d, entity.posY + e + entity.getEyeHeight(), d2 + entity.posZ)
                     )
                  == null) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isOverLiquid(AxisAlignedBB axisAlignedBB) {
      AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(
         axisAlignedBB.minX + 0.305,
         axisAlignedBB.minY - 0.1,
         axisAlignedBB.minZ + 0.305,
         axisAlignedBB.maxX - 0.305,
         axisAlignedBB.maxY,
         axisAlignedBB.maxZ - 0.305
      );
      Block block = mc.theWorld.getBlockState(new BlockPos(axisAlignedBB2.minX, axisAlignedBB2.minY, axisAlignedBB2.minZ)).getBlock();
      return block instanceof BlockLiquid;
   }

   public static boolean isHoldingSword(EntityPlayer entityPlayer) {
      return entityPlayer.getCurrentEquippedItem() != null ? entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemSword : false;
   }

   public static int getLatencyAdjustedHurtTime(EntityLivingBase entityLivingBase) {
      return entityLivingBase.hurtTime - MathHelper.ceiling_float_int((float)ServerRotationTracker.getTimeSinceLastPacket() / 50.0F);
   }

   public static boolean canCriticalHit() {
      return !mc.thePlayer.isOnLadder()
         && !mc.thePlayer.isInWater()
         && !mc.thePlayer.isPotionActive(Potion.blindness)
         && !mc.thePlayer.isRiding()
         && mc.thePlayer.isCollidedVertically
         && !mc.playerController.isHittingBlock
         && !client.getModuleManager().get(Speed.class).isEnabled()
         && !client.getModuleManager().get(Flight.class).isEnabled()
         && !isOverLiquid(mc.thePlayer.getEntityBoundingBox());
   }

   public static boolean isOnServer(String string) {
      return mc.isSingleplayer()
         ? false
         : mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains(string)
            || mc.thePlayer.getClientBrand() != null && mc.thePlayer.getClientBrand().toLowerCase().contains(string);
   }

   public static boolean isOnHypixel() {
      return ServerManager.currentServerType == ServerType.HYPIXEL && !Minecraft.hypixelInHostsFile;
   }

   public static boolean isInHypixelGame() {
      return ServerManager.currentServerType == ServerType.HYPIXEL && !Minecraft.hypixelInHostsFile && hasSidebar();
   }

   public static boolean isOnHypixelAddress() {
      ServerData serverData = mc.getCurrentServerData();
      return serverData != null && HYPIXEL_ADDRESS_PATTERN.matcher(serverData.serverIP).find();
   }

   public static boolean isMoving(Entity entity) {
      return entity == mc.thePlayer
         ? mc.gameSettings.keyBindForward.pressed
            || mc.gameSettings.keyBindBack.pressed
            || mc.gameSettings.keyBindRight.pressed
            || mc.gameSettings.keyBindLeft.pressed
         : entity.posX != entity.lastTickPosX || entity.posY != entity.lastTickPosY || entity.posZ != entity.lastTickPosZ;
   }

   public static boolean isFullyInvisible(Entity entity) {
      if (!(entity instanceof EntityLivingBase)) {
         return false;
      }

      EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
      if (entity instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entity;
         boolean bl = false;

         for (ItemStack itemStack : entityPlayer.inventory.armorInventory) {
            if (itemStack != null) {
               bl = true;
            }
         }

         return r_boolean(entityLivingBase) && !isHoldingItem(entityLivingBase) && !bl;
      } else {
         return r_boolean(entityLivingBase) && !isHoldingItem(entityLivingBase);
      }
   }

   public static boolean isHoldingItem(EntityLivingBase entityLivingBase) {
      return entityLivingBase != null && entityLivingBase.getHeldItem() != null;
   }

   public static boolean r_boolean(EntityLivingBase entityLivingBase) {
      return entityLivingBase.isInvisibleToPlayer(mc.thePlayer);
   }

   public static String getScoreboardTitle() {
      if (mc.theWorld == null) {
         return "";
      }

      Scoreboard scoreboard = mc.theWorld.getScoreboard();
      if (scoreboard == null) {
         return "";
      }

      ScoreObjective scoreObjective = null;
      ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
      if (scorePlayerTeam != null && scorePlayerTeam.getChatFormat() != null) {
         int i = scorePlayerTeam.getChatFormat().getColorIndex();
         if (i >= 0) {
            scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + i);
         }
      }

      ScoreObjective scoreObjective2 = scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);
      return scoreObjective2 == null ? "" : StringUtils.stripControlCodes(scoreObjective2.getDisplayName());
   }

   public static boolean scoreboardTitleContains(String string) {
      return getScoreboardTitle().contains(string);
   }

   private static boolean hasSidebar() {
      if (mc.theWorld == null) {
         return false;
      }

      Scoreboard scoreboard = mc.theWorld.getScoreboard();
      if (scoreboard == null) {
         return false;
      }

      ScoreObjective scoreObjective = null;
      ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
      if (scorePlayerTeam != null && scorePlayerTeam.getChatFormat() != null) {
         int i = scorePlayerTeam.getChatFormat().getColorIndex();
         if (i >= 0) {
            scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + i);
         }
      }

      ScoreObjective scoreObjective2 = scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);
      return scoreObjective2 != null;
   }

   public static MovingObjectPosition rayTrace(float f, float g, double d) {
      Entity entity = null;
      double e = d;
      MovingObjectPosition movingObjectPosition;
      MovingObjectPosition movingObjectPosition2 = movingObjectPosition = rayTraceBlocks(d, f, g);
      Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
      if (movingObjectPosition2 != null) {
         e = movingObjectPosition2.hitVec.distanceTo(vec3);
      }

      Vec3 vec32 = mc.thePlayer.getVectorForRotation(g, f);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d);
      Vec3 vec34 = null;
      List list = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double d2 = e;

      for (Object object : list) {
         Entity entity2 = (Entity)object;
         float h = entity2.getCollisionBorderSize();
         AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(h, h, h);
         MovingObjectPosition movingObjectPosition3 = axisAlignedBB.calculateIntercept(vec3, vec33);
         if (axisAlignedBB.isVecInside(vec3)) {
            if (d2 >= 0.0) {
               entity = entity2;
               vec34 = movingObjectPosition3 == null ? vec3 : movingObjectPosition3.hitVec;
               d2 = 0.0;
            }
         } else if (movingObjectPosition3 != null) {
            double d3 = vec3.distanceTo(movingObjectPosition3.hitVec);
            if (d3 < d2 || d2 == 0.0) {
               boolean bl = false;
               if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                  bl = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract);
               }

               if (entity2 != entity2.ridingEntity || bl) {
                  entity = entity2;
                  vec34 = movingObjectPosition3.hitVec;
                  d2 = d3;
               } else if (d2 == 0.0) {
                  entity = entity2;
                  vec34 = movingObjectPosition3.hitVec;
               }
            }
         }
      }

      if (entity != null && vec3.distanceTo(vec34) > d) {
         entity = null;
         movingObjectPosition = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec34, null, new BlockPos(vec34));
      }

      if ((entity instanceof EntityLivingBase || entity instanceof EntityFallingBlock) && (d2 < e || movingObjectPosition2 == null)) {
         movingObjectPosition = new MovingObjectPosition(entity, vec34);
      }

      return movingObjectPosition;
   }

   public static MovingObjectPosition getMouseOverAt(float f, float g, double d) {
      Entity entity = null;
      double e = mc.playerController.getBlockReachDistance();
      double d2 = e;
      boolean bl = false;
      MovingObjectPosition movingObjectPosition;
      MovingObjectPosition movingObjectPosition2 = movingObjectPosition = rayTraceBlocks(e, f, g);
      Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
      if (mc.playerController.extendedReach()) {
         e = 6.0;
         d2 = 6.0;
      } else if (e > 3.0) {
         bl = true;
      }

      if (movingObjectPosition2 != null) {
         d2 = movingObjectPosition2.hitVec.distanceTo(vec3);
      }

      Vec3 vec32 = mc.thePlayer.getVectorForRotation(g, f);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * e, vec32.yCoord * e, vec32.zCoord * e);
      Vec3 vec34 = null;
      List list = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(vec32.xCoord * e, vec32.yCoord * e, vec32.zCoord * e).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double d3 = d2;

      for (Object object : list) {
         Entity entity2 = (Entity)object;
         float h = entity2.getCollisionBorderSize();
         AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(h, h, h);
         MovingObjectPosition movingObjectPosition3 = axisAlignedBB.calculateIntercept(vec3, vec33);
         if (axisAlignedBB.isVecInside(vec3)) {
            if (d3 >= 0.0) {
               entity = entity2;
               vec34 = movingObjectPosition3 == null ? vec3 : movingObjectPosition3.hitVec;
               d3 = 0.0;
            }
         } else if (movingObjectPosition3 != null) {
            double d4 = vec3.distanceTo(movingObjectPosition3.hitVec);
            if (d4 < d3 || d3 == 0.0) {
               boolean bl2 = false;
               if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                  bl2 = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract);
               }

               if (entity2 != entity2.ridingEntity || bl2) {
                  entity = entity2;
                  vec34 = movingObjectPosition3.hitVec;
                  d3 = d4;
               } else if (d3 == 0.0) {
                  entity = entity2;
                  vec34 = movingObjectPosition3.hitVec;
               }
            }
         }
      }

      if (entity != null && bl && vec3.distanceTo(vec34) > d) {
         entity = null;
         movingObjectPosition = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec34, null, new BlockPos(vec34));
      }

      if (entity != null && (d3 < d2 || movingObjectPosition2 == null)) {
         movingObjectPosition = new MovingObjectPosition(entity, vec34);
      }

      return movingObjectPosition;
   }

   private static MovingObjectPosition rayTraceBlocks(double d, float f, float g) {
      Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 vec32 = mc.thePlayer.getVectorForRotation(g, f);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d);
      return mc.theWorld.rayTraceBlocks(vec3, vec33, false, false, true);
   }

   public static void setMoveSpeed(MoveEvent moveEvent, double d, float f) {
      double e = mc.thePlayer.movementInput.moveForward;
      double d2 = mc.thePlayer.movementInput.moveStrafe;
      float g = mc.thePlayer.rotationYaw;
      if (e == 0.0 && d2 == 0.0) {
         moveEvent.setX(0.0);
         moveEvent.setZ(0.0);
      } else {
         if (e != 0.0) {
            if (d2 > 0.0) {
               g += e > 0.0 ? -45 : 45;
            } else if (d2 < 0.0) {
               g += e > 0.0 ? 45 : -45;
            }

            d2 = 0.0;
            if (e > 0.0) {
               e = 1.0;
            } else if (e < 0.0) {
               e = -1.0;
            }
         }

         double d3 = Math.cos(Math.toRadians(g + f));
         double d4 = Math.sin(Math.toRadians(g + f));
         moveEvent.setX((e * d * d3 + d2 * d * d4) * 0.99);
         moveEvent.setZ((e * d * d4 - d2 * d * d3) * 0.99);
      }
   }

   public static int getArmorColor(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemArmor) {
         ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
         if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
            return itemArmor.getColor(itemStack);
         }

         switch (itemArmor.getArmorMaterial()) {
            case DIAMOND:
               return -7740176;
            case IRON:
               return -5131342;
            case CHAIN:
               return -6908266;
            case GOLD:
               return -988591;
         }
      }

      return -1;
   }

   public static Block getBlock(BlockPos blockPos) {
      return mc.theWorld.getBlockState(blockPos).getBlock();
   }

   public static Block getBlock(double d, double e, double d2) {
      return mc.theWorld.getBlockState(new BlockPos((int)Math.floor(d), (int)Math.floor(e), (int)Math.floor(d2))).getBlock();
   }

   public static Block getBlockAtPlayer() {
      return getBlockAtPlayer(0.0);
   }

   public static Block getBlockAtPlayer(double d) {
      return getBlock(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
   }

   public static int getHealthColorRGB(EntityLivingBase entityLivingBase) {
      int i = 100 - (int)entityLivingBase.getHealth() * 100 / (int)entityLivingBase.getMaxHealth();
      int j = 255 * i / 100;
      int k = 255 * (100 - i) / 100;
      j = MathHelper.clamp_int(j, 0, 255);
      k = MathHelper.clamp_int(k, 0, 255);
      return new Color(j, k, 0).getRGB();
   }

   public static Color getHealthColor(EntityLivingBase entityLivingBase) {
      int i = 100 - (int)entityLivingBase.getHealth() * 100 / (int)entityLivingBase.getMaxHealth();
      int j = 255 * i / 100;
      int k = 255 * (100 - i) / 100;
      j = MathHelper.clamp_int(j, 0, 255);
      k = MathHelper.clamp_int(k, 0, 255);
      return new Color(j, k, 0);
   }

   private static String getColorCode(EntityPlayer entityPlayer) {
      Matcher matcher2 = colorCodePattern.matcher(entityPlayer.getDisplayName().getFormattedText());
      return matcher2.find() ? matcher2.group(1) : "f";
   }

   private static String getColorCode(String string) {
      Matcher matcher2 = colorCodePattern.matcher(string);
      return matcher2.find() ? matcher2.group(1) : "f";
   }

   public static boolean isOnSameTeam(EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
      if (entityLivingBase.getDisplayName() == null) {
         return false;
      }

      if (cachedTabDisplayName == null || mc.thePlayer.ticksExisted % 214 == 0) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityPlayer;
         if (abstractClientPlayer.getPlayerInfo() != null && !Objects.equals(abstractClientPlayer.getPlayerInfo().getGameProfile().getName(), mc.thePlayer.getName())) {
            for (NetworkPlayerInfo networkPlayerInfo : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
               if (networkPlayerInfo.getGameProfile().getName().equals(abstractClientPlayer.getPlayerInfo().getGameProfile().getName())) {
                  cachedTabDisplayName = networkPlayerInfo.getDisplayName() != null
                     ? networkPlayerInfo.getDisplayName().getFormattedText()
                     : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
                  cachedTabDisplayName = cachedTabDisplayName + "§r";
                  break;
               }
            }
         }
      }

      return cachedTabDisplayName != null
         ? cachedTabDisplayName.contains("§" + getColorCode(cachedTabDisplayName))
            && entityLivingBase.getDisplayName().getFormattedText().contains("§" + getColorCode(cachedTabDisplayName))
         : entityPlayer.getDisplayName().getFormattedText().contains("§" + getColorCode(entityPlayer))
            && entityLivingBase.getDisplayName().getFormattedText().contains("§" + getColorCode(entityPlayer));
   }
}
