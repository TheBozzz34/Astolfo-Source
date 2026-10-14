package net.minecraft.client.network;

import bypass.Astolfo;
import bypass.auth.DisconnectHandler;
import bypass.module.ModuleManager;
import bypass.util.GameUtil;
import bypass.via.ViaFabric;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   private static final Logger logger = LogManager.getLogger();
   private final NetworkManager netManager;
   private final GameProfile profile;
   private final GuiScreen guiScreenServer;
   private Minecraft gameController;
   private WorldClient clientWorldController;
   public boolean doneLoadingTerrain;
   public final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   public int currentServerMaxPlayers = 20;
   private boolean field_147308_k = false;
   private final Random avRandomizer = new Random();
   private static final long a = 109445161998205L;

   public NetHandlerPlayClient(Minecraft mcIn, GuiScreen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
      this.gameController = mcIn;
      this.guiScreenServer = guiScreen;
      this.netManager = networkManager;
      this.profile = gameProfile;
   }

   public void cleanup() {
      this.clientWorldController = null;
   }

   @Override
   public void handleJoinGame(S01PacketJoinGame packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
      this.clientWorldController = new WorldClient(
         this,
         new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()),
         packetIn.getDimension(),
         packetIn.getDifficulty(),
         this.gameController.mcProfiler
      );
      this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
      this.gameController.loadWorld(this.clientWorldController);
      this.gameController.thePlayer.dimension = packetIn.getDimension();
      this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
      this.currentServerMaxPlayers = packetIn.getMaxPlayers();
      this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
      this.gameController.playerController.setGameType(packetIn.getGameType());
      this.gameController.gameSettings.sendSettingsToServer();
      this.netManager
         .sendPacket(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
   }

   @Override
   public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / ModuleManager.v;
      double e = packetIn.getY() / ModuleManager.v;
      double d2 = packetIn.getZ() / ModuleManager.v;
      Entity entity = null;
      if (packetIn.getType() == 10) {
         entity = EntityMinecart.getMinecart(this.clientWorldController, d, e, d2, EntityMinecart.EnumMinecartType.byNetworkID(packetIn.func_149009_m()));
      } else if (packetIn.getType() == 90) {
         Entity entity2 = this.clientWorldController.getEntityByID(packetIn.func_149009_m());
         if (entity2 instanceof EntityPlayer) {
            entity = new EntityFishHook(this.clientWorldController, d, e, d2, (EntityPlayer)entity2);
         }

         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 60) {
         entity = new EntityArrow(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 61) {
         entity = new EntitySnowball(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 71) {
         entity = new EntityItemFrame(
            this.clientWorldController,
            new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(e), MathHelper.floor_double(d2)),
            EnumFacing.getHorizontal(packetIn.func_149009_m())
         );
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 77) {
         entity = new EntityLeashKnot(
            this.clientWorldController, new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(e), MathHelper.floor_double(d2))
         );
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 65) {
         entity = new EntityEnderPearl(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 72) {
         entity = new EntityEnderEye(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 76) {
         entity = new EntityFireworkRocket(this.clientWorldController, d, e, d2, (ItemStack)null);
      } else if (packetIn.getType() == 63) {
         entity = new EntityLargeFireball(
            this.clientWorldController, d, e, d2, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 64) {
         entity = new EntitySmallFireball(
            this.clientWorldController, d, e, d2, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 66) {
         entity = new EntityWitherSkull(
            this.clientWorldController, d, e, d2, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 62) {
         entity = new EntityEgg(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 73) {
         entity = new EntityPotion(this.clientWorldController, d, e, d2, packetIn.func_149009_m());
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 75) {
         entity = new EntityExpBottle(this.clientWorldController, d, e, d2);
         packetIn.func_149002_g(0);
      } else if (packetIn.getType() == 1) {
         entity = new EntityBoat(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 50) {
         entity = new EntityTNTPrimed(this.clientWorldController, d, e, d2, (EntityLivingBase)null);
      } else if (packetIn.getType() == 78) {
         entity = new EntityArmorStand(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 51) {
         entity = new EntityEnderCrystal(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 2) {
         entity = new EntityItem(this.clientWorldController, d, e, d2);
      } else if (packetIn.getType() == 70) {
         entity = new EntityFallingBlock(this.clientWorldController, d, e, d2, Block.getStateById(packetIn.func_149009_m() & 65535));
         packetIn.func_149002_g(0);
      }

      if (entity != null) {
         entity.serverPosX = packetIn.getX();
         entity.serverPosY = packetIn.getY();
         entity.serverPosZ = packetIn.getZ();
         entity.rotationPitch = packetIn.getPitch() * 360 / 256.0F;
         entity.rotationYaw = packetIn.getYaw() * 360 / 256.0F;
         Entity[] entitys = entity.getParts();
         if (entitys != null) {
            int i = packetIn.getEntityID() - entity.getEntityId();

            for (int j = 0; j < entitys.length; j++) {
               entitys[j].setEntityId(entitys[j].getEntityId() + i);
            }
         }

         entity.setEntityId(packetIn.getEntityID());
         this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
         if (packetIn.func_149009_m() > 0) {
            if (packetIn.getType() == 60) {
               Entity entity3 = this.clientWorldController.getEntityByID(packetIn.func_149009_m());
               if (entity3 instanceof EntityLivingBase && entity instanceof EntityArrow) {
                  ((EntityArrow)entity).shootingEntity = entity3;
               }
            }

            entity.setVelocity(packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0);
         }
      }
   }

   @Override
   public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityXPOrb entityXPOrb = new EntityXPOrb(
         this.clientWorldController,
         packetIn.getX() / ModuleManager.v,
         packetIn.getY() / ModuleManager.v,
         packetIn.getZ() / ModuleManager.v,
         packetIn.getXPValue()
      );
      entityXPOrb.serverPosX = packetIn.getX();
      entityXPOrb.serverPosY = packetIn.getY();
      entityXPOrb.serverPosZ = packetIn.getZ();
      entityXPOrb.rotationYaw = 0.0F;
      entityXPOrb.rotationPitch = 0.0F;
      entityXPOrb.setEntityId(packetIn.getEntityID());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityXPOrb);
   }

   @Override
   public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.func_149051_d() / ModuleManager.v;
      double e = packetIn.func_149050_e() / ModuleManager.v;
      double d2 = packetIn.func_149049_f() / ModuleManager.v;
      EntityLightningBolt entityLightningBolt = null;
      if (packetIn.func_149053_g() == 1) {
         entityLightningBolt = new EntityLightningBolt(this.clientWorldController, d, e, d2);
      }

      if (entityLightningBolt != null) {
         entityLightningBolt.serverPosX = packetIn.func_149051_d();
         entityLightningBolt.serverPosY = packetIn.func_149050_e();
         entityLightningBolt.serverPosZ = packetIn.func_149049_f();
         entityLightningBolt.rotationYaw = 0.0F;
         entityLightningBolt.rotationPitch = 0.0F;
         entityLightningBolt.setEntityId(packetIn.func_149052_c());
         this.clientWorldController.addWeatherEffect(entityLightningBolt);
      }
   }

   @Override
   public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPainting entityPainting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityPainting);
   }

   @Override
   public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         entity.setVelocity(packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
      }
   }

   @Override
   public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity != null && packetIn.func_149376_c() != null) {
         entity.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
      }
   }

   @Override
   public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / ModuleManager.v;
      double e = packetIn.getY() / ModuleManager.v;
      double d2 = packetIn.getZ() / ModuleManager.v;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float g = packetIn.getPitch() * 360 / 256.0F;
      EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
      entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.serverPosX = packetIn.getX();
      entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.serverPosY = packetIn.getY();
      entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.serverPosZ = packetIn.getZ();
      int i = packetIn.getCurrentItemID();
      if (i == 0) {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = null;
      } else {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = new ItemStack(Item.getItemById(i), 1, 0);
      }

      entityOtherPlayerMP.setPositionAndRotation(d, e, d2, f, g);
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityOtherPlayerMP);
      List list = packetIn.func_148944_c();
      if (list != null) {
         entityOtherPlayerMP.getDataWatcher().updateWatchedObjectsFromList(list);
      }
   }

   @Override
   public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity != null) {
         entity.serverPosX = packetIn.getX();
         entity.serverPosY = packetIn.getY();
         entity.serverPosZ = packetIn.getZ();
         double d = entity.serverPosX / ModuleManager.v;
         double e = entity.serverPosY / ModuleManager.v;
         double d2 = entity.serverPosZ / ModuleManager.v;
         float f = packetIn.getYaw() * 360 / 256.0F;
         float g = packetIn.getPitch() * 360 / 256.0F;
         if (Math.abs(entity.posX - d) < 0.03125 && Math.abs(entity.posY - e) < 0.015625 && Math.abs(entity.posZ - d2) < 0.03125) {
            entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, g, 3, true);
         } else {
            entity.setPositionAndRotation2(d, e, d2, f, g, 3, true);
         }

         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
         this.gameController.thePlayer.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
      }
   }

   @Override
   public void handleEntityMovement(S14PacketEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.serverPosX = entity.serverPosX + packetIn.func_149062_c();
         entity.serverPosY = entity.serverPosY + packetIn.func_149061_d();
         entity.serverPosZ = entity.serverPosZ + packetIn.func_149064_e();
         double d = entity.serverPosX / ModuleManager.v;
         double e = entity.serverPosY / ModuleManager.v;
         double d2 = entity.serverPosZ / ModuleManager.v;
         float f = packetIn.func_149060_h() ? packetIn.func_149066_f() * 360 / 256.0F : entity.rotationYaw;
         float g = packetIn.func_149060_h() ? packetIn.func_149063_g() * 360 / 256.0F : entity.rotationPitch;
         entity.setPositionAndRotation2(d, e, d2, f, g, 3, false);
         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         float f = packetIn.getYaw() * 360 / 256.0F;
         entity.setRotationYawHead(f);
      }
   }

   @Override
   public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (int i = 0; i < packetIn.getEntityIDs().length; i++) {
         this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
      }
   }

   @Override
   public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      double d = packetIn.getX();
      double e = packetIn.getY();
      double d2 = packetIn.getZ();
      float f = packetIn.getYaw();
      float g = packetIn.getPitch();
      if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         d += entityPlayerSP.posX;
      } else {
         entityPlayerSP.motionX = 0.0;
      }

      if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         e += entityPlayerSP.posY;
      } else {
         entityPlayerSP.motionY = 0.0;
      }

      if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         d2 += entityPlayerSP.posZ;
      } else {
         entityPlayerSP.motionZ = 0.0;
      }

      if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         g += entityPlayerSP.rotationPitch;
      }

      if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         f += entityPlayerSP.rotationYaw;
      }

      entityPlayerSP.setPositionAndRotation(d, e, d2, f, g);
      this.netManager
         .sendPacket(
            new C03PacketPlayer.C06PacketPlayerPosLook(entityPlayerSP.posX, entityPlayerSP.getEntityBoundingBox().minY, entityPlayerSP.posZ, entityPlayerSP.rotationYaw, entityPlayerSP.rotationPitch, false)
         );
      if (!this.doneLoadingTerrain) {
         this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
         this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
         this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
         this.doneLoadingTerrain = true;
         this.gameController.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (S22PacketMultiBlockChange.BlockUpdateData blockUpdateData : packetIn.getChangedBlocks()) {
         this.clientWorldController.invalidateRegionAndSetBlock(blockUpdateData.getPos(), blockUpdateData.getBlockState());
      }
   }

   @Override
   public void handleChunkData(S21PacketChunkData packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.func_149274_i()) {
         if (packetIn.getExtractedSize() == 0) {
            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
            return;
         }

         this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
      }

      this.clientWorldController
         .invalidateBlockReceiveRegion(
            packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15
         );
      Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
      chunk.fillChunk(packetIn.getExtractedDataBytes(), packetIn.getExtractedSize(), packetIn.func_149274_i());
      this.clientWorldController
         .markBlockRangeForRenderUpdate(
            packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15
         );
      if (!packetIn.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
         chunk.resetRelightChecks();
      }
   }

   @Override
   public void handleBlockChange(S23PacketBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
   }

   @Override
   public void handleDisconnect(S40PacketDisconnect packetIn) {
      this.netManager.closeChannel(packetIn.getReason());
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.gameController.loadWorld((WorldClient)null);
      if (GameUtil.isOnHypixel()) {
         for (IChatComponent iChatComponent : reason.getSiblings()) {
            if (iChatComponent.getFormattedText().startsWith("§fCheating through the use of unfair game advantages")
               || iChatComponent.getFormattedText().startsWith("§fBoosting your account to improve your stats.")
               || iChatComponent.getFormattedText().startsWith("§fDiscussing or acting in a manner which encourages")) {
               if (!DisconnectHandler.disconnected) {
               }
               break;
            }
         }

         Astolfo.INSTANCE.getServerManager().x();
      }

      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
            this.gameController
               .displayGuiScreen(
                  new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).func_154321_a(), "disconnect.lost", reason).getProxy()
               );
         } else {
            this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
         }
      } else {
         this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
      }
   }

   public void addToSendQueue(Packet packet) {
      this.netManager.sendPacket(packet);
   }

   public void u(Packet packet) {
      this.netManager.B(packet);
   }

   @Override
   public void handleCollectItem(S0DPacketCollectItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
      EntityLivingBase entityLivingBase = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityLivingBase == null) {
         entityLivingBase = this.gameController.thePlayer;
      }

      if (entity != null) {
         if (entity instanceof EntityXPOrb) {
            this.clientWorldController
               .playSoundAtEntity(entity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.clientWorldController
               .playSoundAtEntity(entity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entity, entityLivingBase, 0.5F));
         this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
      }
   }

   @Override
   public void handleChat(S02PacketChat packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getType() == 2) {
         this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
      } else {
         this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
      }
   }

   @Override
   public void handleAnimation(S0BPacketAnimation packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         if (packetIn.getAnimationType() == 0) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            entityLivingBase.swingItem();
         } else if (packetIn.getAnimationType() == 1) {
            entity.performHurtAnimation();
         } else if (packetIn.getAnimationType() == 2) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            entityPlayer.wakeUpPlayer(false, false, false);
         } else if (packetIn.getAnimationType() == 4) {
            this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
         } else if (packetIn.getAnimationType() == 5) {
            this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
         }
      }
   }

   @Override
   public void handleUseBed(S0APacketUseBed packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
   }

   @Override
   public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / ModuleManager.v;
      double e = packetIn.getY() / ModuleManager.v;
      double d2 = packetIn.getZ() / ModuleManager.v;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float g = packetIn.getPitch() * 360 / 256.0F;
      EntityLivingBase entityLivingBase = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.theWorld);
      entityLivingBase.serverPosX = packetIn.getX();
      entityLivingBase.serverPosY = packetIn.getY();
      entityLivingBase.serverPosZ = packetIn.getZ();
      entityLivingBase.renderYawOffset = entityLivingBase.rotationYawHead = packetIn.getHeadPitch() * 360 / 256.0F;
      Entity[] entitys = entityLivingBase.getParts();
      if (entitys != null) {
         int i = packetIn.getEntityID() - entityLivingBase.getEntityId();

         for (int j = 0; j < entitys.length; j++) {
            entitys[j].setEntityId(entitys[j].getEntityId() + i);
         }
      }

      entityLivingBase.setEntityId(packetIn.getEntityID());
      entityLivingBase.setPositionAndRotation(d, e, d2, f, g);
      entityLivingBase.motionX = packetIn.getVelocityX() / 8000.0F;
      entityLivingBase.motionY = packetIn.getVelocityY() / 8000.0F;
      entityLivingBase.motionZ = packetIn.getVelocityZ() / 8000.0F;
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityLivingBase);
      List list = packetIn.func_149027_c();
      if (list != null) {
         entityLivingBase.getDataWatcher().updateWatchedObjectsFromList(list);
      }
   }

   @Override
   public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.m(packetIn.getTotalWorldTime());
      this.gameController.theWorld.setWorldTime(packetIn.getWorldTime());
   }

   @Override
   public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPos(), true);
      this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPos());
   }

   @Override
   public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      Entity entity2 = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());
      if (packetIn.getLeash() == 0) {
         boolean bl = false;
         if (packetIn.getEntityId() == this.gameController.thePlayer.getEntityId()) {
            entity = this.gameController.thePlayer;
            if (entity2 instanceof EntityBoat) {
               ((EntityBoat)entity2).setIsBoatEmpty(false);
            }

            bl = entity.ridingEntity == null && entity2 != null;
         } else if (entity2 instanceof EntityBoat) {
            ((EntityBoat)entity2).setIsBoatEmpty(true);
         }

         if (entity == null) {
            return;
         }

         entity.mountEntity(entity2);
         if (bl) {
            GameSettings gameSettings2 = this.gameController.gameSettings;
            this.gameController
               .ingameGUI
               .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(gameSettings2.keyBindSneak.getKeyCode())), false);
         }
      } else if (packetIn.getLeash() == 1 && entity instanceof EntityLiving) {
         if (entity2 != null) {
            ((EntityLiving)entity).setLeashedToEntity(entity2, false);
         } else {
            ((EntityLiving)entity).clearLeashed(false, false);
         }
      }
   }

   @Override
   public void handleEntityStatus(S19PacketEntityStatus packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         if (packetIn.getOpCode() == 21) {
            this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
         } else {
            entity.handleStatusUpdate(packetIn.getOpCode());
         }
      }
   }

   @Override
   public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
      this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
      this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
   }

   @Override
   public void handleSetExperience(S1FPacketSetExperience packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setXPStats(packetIn.func_149397_c(), packetIn.getTotalExperience(), packetIn.getLevel());
   }

   @Override
   public void handleRespawn(S07PacketRespawn packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.clientWorldController.getScoreboard();
         this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()),
            packetIn.getDimensionID(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
         );
         this.clientWorldController.setWorldScoreboard(scoreboard);
         this.gameController.loadWorld(this.clientWorldController);
         this.gameController.thePlayer.dimension = packetIn.getDimensionID();
         this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
      this.gameController.playerController.setGameType(packetIn.getGameType());
   }

   @Override
   public void handleExplosion(S27PacketExplosion packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Explosion explosion = new Explosion(
         this.gameController.theWorld,
         (Entity)null,
         packetIn.getX(),
         packetIn.getY(),
         packetIn.getZ(),
         packetIn.getStrength(),
         packetIn.getAffectedBlockPositions()
      );
      explosion.doExplosionB(true);
      this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + packetIn.func_149149_c();
      this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + packetIn.func_149144_d();
      this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + packetIn.func_149147_e();
   }

   @Override
   public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if ("minecraft:container".equals(packetIn.getGuiId())) {
         entityPlayerSP.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
         entityPlayerSP.displayVillagerTradeGui(new NpcMerchant(entityPlayerSP, packetIn.getWindowTitle()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else if ("EntityHorse".equals(packetIn.getGuiId())) {
         Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
         if (entity instanceof EntityHorse) {
            entityPlayerSP.displayGUIHorse((EntityHorse)entity, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
         }
      } else if (!packetIn.hasSlots()) {
         entityPlayerSP.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else {
         ContainerLocalMenu containerLocalMenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
         entityPlayerSP.displayGUIChest(containerLocalMenu);
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      }
   }

   @Override
   public void handleSetSlot(S2FPacketSetSlot packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if (packetIn.func_149175_c() == -1) {
         entityPlayerSP.inventory.setItemStack(packetIn.func_149174_e());
      } else {
         boolean bl = false;
         if (this.gameController.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative guiContainerCreative = (GuiContainerCreative)this.gameController.currentScreen;
            bl = guiContainerCreative.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
         }

         if (packetIn.func_149175_c() == 0 && packetIn.func_149173_d() >= 36 && packetIn.func_149173_d() < 45) {
            ItemStack itemStack = entityPlayerSP.inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();
            if (packetIn.func_149174_e() != null && (itemStack == null || itemStack.stackSize < packetIn.func_149174_e().stackSize)) {
               packetIn.func_149174_e().animationsToGo = 5;
            }

            entityPlayerSP.inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
         } else if (packetIn.func_149175_c() == entityPlayerSP.openContainer.windowId && (packetIn.func_149175_c() != 0 || !bl)) {
            entityPlayerSP.openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
         }
      }
   }

   @Override
   public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Container container = null;
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         container = entityPlayerSP.inventoryContainer;
      } else if (packetIn.getWindowId() == entityPlayerSP.openContainer.windowId) {
         container = entityPlayerSP.openContainer;
      }

      if (container != null && !packetIn.func_148888_e()) {
         this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
      }
   }

   @Override
   public void handleWindowItems(S30PacketWindowItems packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if (packetIn.func_148911_c() == 0) {
         entityPlayerSP.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
      } else if (packetIn.func_148911_c() == entityPlayerSP.openContainer.windowId) {
         entityPlayerSP.openContainer.putStacksInSlots(packetIn.getItemStacks());
      }
   }

   @Override
   public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      TileEntity tileEntity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());
      if (!(tileEntity instanceof TileEntitySign)) {
         tileEntity = new TileEntitySign();
         tileEntity.setWorldObj(this.clientWorldController);
         tileEntity.setPos(packetIn.getSignPosition());
      }

      this.gameController.thePlayer.openEditSign((TileEntitySign)tileEntity);
   }

   @Override
   public void handleUpdateSign(S33PacketUpdateSign packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean bl = false;
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         if (tileEntity instanceof TileEntitySign) {
            TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
            if (tileEntitySign.getIsEditable()) {
               System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
               tileEntitySign.markDirty();
            }

            bl = true;
         }
      }

      if (!bl && this.gameController.thePlayer != null) {
         this.gameController
            .thePlayer
            .addChatMessage(
               new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ())
            );
      }
   }

   @Override
   public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         int i = packetIn.getTileEntityType();
         if (i == 1 && tileEntity instanceof TileEntityMobSpawner
            || i == 2 && tileEntity instanceof TileEntityCommandBlock
            || i == 3 && tileEntity instanceof TileEntityBeacon
            || i == 4 && tileEntity instanceof TileEntitySkull
            || i == 5 && tileEntity instanceof TileEntityFlowerPot
            || i == 6 && tileEntity instanceof TileEntityBanner) {
            tileEntity.readFromNBT(packetIn.getNbtCompound());
         }
      }
   }

   @Override
   public void handleWindowProperty(S31PacketWindowProperty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if (entityPlayerSP.openContainer != null && entityPlayerSP.openContainer.windowId == packetIn.getWindowId()) {
         entityPlayerSP.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
      }
   }

   @Override
   public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         entity.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
      }
   }

   @Override
   public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.closeScreenAndDropStack();
   }

   @Override
   public void handleBlockAction(S24PacketBlockAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
   }

   @Override
   public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
   }

   @Override
   public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (int i = 0; i < packetIn.getChunkCount(); i++) {
         int j = packetIn.getChunkX(i);
         int k = packetIn.getChunkZ(i);
         this.clientWorldController.doPreChunk(j, k, true);
         this.clientWorldController.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
         Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(j, k);
         chunk.fillChunk(packetIn.getChunkBytes(i), packetIn.getChunkSize(i), true);
         this.clientWorldController.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
         if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
         }
      }
   }

   @Override
   public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      int i = packetIn.getGameState();
      float f = packetIn.func_149137_d();
      int j = MathHelper.floor_float(f + 0.5F);
      if (i >= 0 && i < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[i] != null) {
         entityPlayerSP.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[i]));
      }

      if (i == 1) {
         this.clientWorldController.getWorldInfo().setRaining(true);
         this.clientWorldController.V(0.0F);
      } else if (i == 2) {
         this.clientWorldController.getWorldInfo().setRaining(false);
         this.clientWorldController.V(1.0F);
      } else if (i == 3) {
         this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(j));
      } else if (i == 4) {
         this.gameController.displayGuiScreen(new GuiWinGame());
      } else if (i == 5) {
         GameSettings gameSettings2 = this.gameController.gameSettings;
         if (f == 0.0F) {
            this.gameController.displayGuiScreen(new GuiScreenDemo());
         } else if (f == 101.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.getKeyDisplayString(gameSettings2.keyBindForward.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings2.keyBindLeft.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings2.keyBindBack.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings2.keyBindRight.getKeyCode())
                  )
               );
         } else if (f == 102.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(gameSettings2.keyBindJump.getKeyCode())));
         } else if (f == 103.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(gameSettings2.keyBindInventory.getKeyCode())));
         }
      } else if (i == 6) {
         this.clientWorldController.playSound(entityPlayerSP.posX, entityPlayerSP.posY + entityPlayerSP.getEyeHeight(), entityPlayerSP.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (i == 7) {
         this.clientWorldController.V(f);
      } else if (i == 8) {
         this.clientWorldController.t(f);
      } else if (i == 10) {
         this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, entityPlayerSP.posX, entityPlayerSP.posY, entityPlayerSP.posZ, 0.0, 0.0, 0.0);
         this.clientWorldController.playSound(entityPlayerSP.posX, entityPlayerSP.posY, entityPlayerSP.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleMaps(S34PacketMaps packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      MapData mapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
      packetIn.setMapdataTo(mapData);
      this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapData);
   }

   @Override
   public void handleEffect(S28PacketEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.isSoundServerwide()) {
         this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      } else {
         this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      }
   }

   @Override
   public void handleStatistics(S37PacketStatistics packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean bl = false;

      for (Entry entry : packetIn.func_148974_c().entrySet()) {
         StatBase statBase = (StatBase)entry.getKey();
         int i = (Integer)entry.getValue();
         if (statBase.isAchievement() && i > 0) {
            if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().readStat(statBase) == 0) {
               Achievement achievement = (Achievement)statBase;
               this.gameController.guiAchievement.displayAchievement(achievement);
               if (statBase == AchievementList.openInventory) {
                  this.gameController.gameSettings.l8 = false;
                  this.gameController.gameSettings.saveOptions();
               }
            }

            bl = true;
         }

         this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, statBase, i);
      }

      if (!this.field_147308_k && !bl && this.gameController.gameSettings.l8) {
         this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
      }

      this.field_147308_k = true;
      if (this.gameController.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.gameController.currentScreen).doneLoading();
      }
   }

   @Override
   public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity instanceof EntityLivingBase) {
         PotionEffect potionEffect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.func_179707_f());
         potionEffect.setPotionDurationMax(packetIn.func_149429_c());
         ((EntityLivingBase)entity).addPotionEffect(potionEffect);
      }
   }

   @Override
   public void handleCombatEvent(S42PacketCombatEvent packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.field_179775_c);
      if (!(entity instanceof EntityLivingBase)) {
         Object object = null;
      }

      if (packetIn.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         long l = 1000 * packetIn.field_179772_d / 20;
      } else if (packetIn.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         Entity entity2 = this.clientWorldController.getEntityByID(packetIn.field_179774_b);
         if (entity2 instanceof EntityPlayer) {
         }
      }
   }

   @Override
   public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
      this.gameController.theWorld.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
   }

   @Override
   public void handleCamera(S43PacketCamera packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         this.gameController.setRenderViewEntity(entity);
      }
   }

   @Override
   public void handleWorldBorder(S44PacketWorldBorder packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.func_179788_a(this.clientWorldController.getWorldBorder());
   }

   @Override
   public void handleTitle(S45PacketTitle packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      S45PacketTitle.Type type = packetIn.getType();
      String string = null;
      String string2 = null;
      String string3 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
      switch (type) {
         case TITLE:
            string = string3;
            break;
         case SUBTITLE:
            string2 = string3;
            break;
         case RESET:
            this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
            this.gameController.ingameGUI.setDefaultTitlesTimes();
            return;
      }

      this.gameController.ingameGUI.displayTitle(string, string2, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
   }

   @Override
   public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
      if (!this.netManager.isLocalChannel()) {
         this.netManager.setCompressionTreshold(packetIn.getThreshold());
      }
   }

   @Override
   public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
      this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader());
      this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter());
   }

   @Override
   public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).removePotionEffectClient(packetIn.getEffectId());
      }
   }

   @Override
   public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packetIn.getEntries()) {
         if (packetIn.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(addPlayerData.getProfile().getId());
         } else {
            NetworkPlayerInfo networkPlayerInfo = this.playerInfoMap.get(addPlayerData.getProfile().getId());
            if (packetIn.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
               networkPlayerInfo = new NetworkPlayerInfo(addPlayerData);
               this.playerInfoMap.put(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo);
            }

            if (networkPlayerInfo != null) {
               switch (packetIn.getAction()) {
                  case ADD_PLAYER:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_GAME_MODE:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     break;
                  case UPDATE_LATENCY:
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     networkPlayerInfo.setDisplayName(addPlayerData.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleKeepAlive(S00PacketKeepAlive packetIn) {
      this.addToSendQueue(new C00PacketKeepAlive(packetIn.func_149134_c()));
   }

   @Override
   public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      entityPlayerSP.capabilities.isFlying = packetIn.isFlying();
      entityPlayerSP.capabilities.isCreativeMode = packetIn.isCreativeMode();
      entityPlayerSP.capabilities.disableDamage = packetIn.isInvulnerable();
      entityPlayerSP.capabilities.allowFlying = packetIn.isAllowFlying();
      entityPlayerSP.capabilities.setFlySpeed(packetIn.getFlySpeed());
      entityPlayerSP.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
   }

   @Override
   public void handleTabComplete(S3APacketTabComplete packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      String[] strings = packetIn.func_149630_c();
      if (this.gameController.currentScreen instanceof GuiChat) {
         GuiChat guiChat = (GuiChat)this.gameController.currentScreen;
         guiChat.onAutocompleteResponse(strings);
      }
   }

   @Override
   public void handleSoundEffect(S29PacketSoundEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController
         .theWorld
         .playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
   }

   @Override
   public void handleResourcePack(S48PacketResourcePackSend packetIn) {
      if (this.C(packetIn.R(), packetIn.r())) {
         final String string = packetIn.R();
         final String string2 = packetIn.r();
         if (string.startsWith("level://")) {
            String string3 = string.substring("level://".length());
            File file = new File(this.gameController.mcDataDir, "saves");
            File file2 = new File(file, string3);
            if (file2.isFile()) {
               this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.ACCEPTED));
               Futures.addCallback(
                  this.gameController.getResourcePackRepository().setResourcePackInstance(file2),
                  new FutureCallback<Object>() {
                     @Override
                     public void onSuccess(Object object) {
                        NetHandlerPlayClient.this.netManager
                           .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                     }

                     @Override
                     public void onFailure(Throwable throwable) {
                        NetHandlerPlayClient.this.netManager
                           .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                     }
                  }
               );
            } else {
               this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
         } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(
               this.gameController.getResourcePackRepository().downloadResourcePack(string, string2),
               new FutureCallback<Object>() {
                  @Override
                  public void onSuccess(Object object) {
                     NetHandlerPlayClient.this.netManager
                        .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                  }

                  @Override
                  public void onFailure(Throwable throwable) {
                     NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                  }
               }
            );
         } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.DECLINED));
         } else {
            this.gameController
               .addScheduledTask(
                  new Runnable() {
                     @Override
                     public void run() {
                        NetHandlerPlayClient.this.gameController
                           .displayGuiScreen(
                              new GuiYesNo(
                                 new GuiYesNoCallback() {
                                    @Override
                                    public void confirmClicked(boolean result, int id) {
                                       NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                                       if (result) {
                                          if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                             NetHandlerPlayClient.this.gameController
                                                .getCurrentServerData()
                                                .setResourceMode(ServerData.ServerResourceMode.ENABLED);
                                          }

                                          NetHandlerPlayClient.this.netManager
                                             .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.ACCEPTED));
                                          Futures.addCallback(
                                             NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(string, string2),
                                             new FutureCallback<Object>() {
                                                @Override
                                                public void onSuccess(Object object) {
                                                   NetHandlerPlayClient.this.netManager
                                                      .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                                }

                                                @Override
                                                public void onFailure(Throwable throwable) {
                                                   NetHandlerPlayClient.this.netManager
                                                      .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                                                }
                                             }
                                          );
                                       } else {
                                          if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                             NetHandlerPlayClient.this.gameController
                                                .getCurrentServerData()
                                                .setResourceMode(ServerData.ServerResourceMode.DISABLED);
                                          }

                                          NetHandlerPlayClient.this.netManager
                                             .sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.DECLINED));
                                       }

                                       ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                                       NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                                    }
                                 },
                                 I18n.format("multiplayer.texturePrompt.line1"),
                                 I18n.format("multiplayer.texturePrompt.line2"),
                                 0
                              )
                           );
                     }
                  }
               );
         }
      }
   }

   private boolean C(String string, String string2) {
      try {
         URI uri = new URI(string);
         String string3 = uri.getScheme();
         boolean bl = "level".equals(string3);
         if (!"http".equals(string3) && !"https".equals(string3) && !bl) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(string2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            throw new URISyntaxException(string, "Wrong protocol");
         }

         string = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
         if (bl && (string.contains("..") || !string.endsWith("/resources.zip"))) {
            System.out.println("Malicious server tried to access " + string);
            EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
            if (entityPlayerSP != null) {
               entityPlayerSP.addChatMessage(
                  new ChatComponentText(
                     EnumChatFormatting.RED
                        + EnumChatFormatting.BOLD.toString()
                        + "[WARNING] The current server has attempted to be malicious but we have stopped them."
                  )
               );
            }

            throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
         } else {
            return true;
         }
      } catch (URISyntaxException uriSyntaxException) {
         return false;
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
         unsupportedEncodingException.printStackTrace();
         return false;
      }
   }

   @Override
   public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.clientUpdateEntityNBT(packetIn.getTagCompound());
      }
   }

   @Override
   public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if ("MC|TrList".equals(packetIn.getChannelName())) {
         PacketBuffer packetBuffer = packetIn.getBufferData();

         try {
            int i = packetBuffer.readInt();
            GuiScreen guiScreen = this.gameController.currentScreen;
            if (guiScreen != null && guiScreen instanceof GuiMerchant && i == this.gameController.thePlayer.openContainer.windowId) {
               IMerchant iMerchant = ((GuiMerchant)guiScreen).getMerchant();
               MerchantRecipeList merchantRecipeList = MerchantRecipeList.readFromBuf(packetBuffer);
               iMerchant.setRecipes(merchantRecipeList);
            }
         } catch (IOException ioException) {
            logger.error("Couldn't load trade info", ioException);
         } finally {
            packetBuffer.release();
         }
      } else if ("MC|Brand".equals(packetIn.getChannelName())) {
         if (ViaFabric.INSTANCE.isAfter1_12_2()
            && this.gameController.thePlayer.getClientBrand() != null
            && this.gameController.thePlayer.getClientBrand().startsWith("Hypixel BungeeCord")) {
            return;
         }

         this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
         Astolfo.INSTANCE.getServerManager().x();
      } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
         ItemStack itemStack = this.gameController.thePlayer.getCurrentEquippedItem();
         if (itemStack != null && itemStack.getItem() == Items.written_book) {
            this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, itemStack, false));
         }
      }
   }

   @Override
   public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.func_149338_e() == 0) {
         ScoreObjective scoreObjective = scoreboard.addScoreObjective(packetIn.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
         scoreObjective.setDisplayName(packetIn.func_149337_d());
         scoreObjective.setRenderType(packetIn.func_179817_d());
      } else {
         ScoreObjective scoreObjective2 = scoreboard.getObjective(packetIn.func_149339_c());
         if (packetIn.func_149338_e() == 1) {
            scoreboard.removeObjective(scoreObjective2);
         } else if (packetIn.func_149338_e() == 2) {
            scoreObjective2.setDisplayName(packetIn.func_149337_d());
            scoreObjective2.setRenderType(packetIn.func_179817_d());
         }
      }
   }

   @Override
   public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScoreObjective scoreObjective = scoreboard.getObjective(packetIn.getObjectiveName());
      if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE) {
         Score score = scoreboard.getValueFromObjective(packetIn.getPlayerName(), scoreObjective);
         score.setScorePoints(packetIn.getScoreValue());
      } else if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE) {
         if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName())) {
            scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
         } else if (scoreObjective != null) {
            scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), scoreObjective);
         }
      }
   }

   @Override
   public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.func_149370_d().length() == 0) {
         scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), (ScoreObjective)null);
      } else {
         ScoreObjective scoreObjective = scoreboard.getObjective(packetIn.func_149370_d());
         scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), scoreObjective);
      }
   }

   @Override
   public void handleTeams(S3EPacketTeams packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScorePlayerTeam scorePlayerTeam;
      if (packetIn.getAction() == 0) {
         scorePlayerTeam = scoreboard.createTeam(packetIn.getName());
      } else {
         scorePlayerTeam = scoreboard.getTeam(packetIn.getName());
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {
         scorePlayerTeam.setTeamName(packetIn.getDisplayName());
         scorePlayerTeam.setNamePrefix(packetIn.getPrefix());
         scorePlayerTeam.setNameSuffix(packetIn.getSuffix());
         scorePlayerTeam.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.getColor()));
         scorePlayerTeam.func_98298_a(packetIn.getFriendlyFlags());
         Team.EnumVisible enumVisible = Team.EnumVisible.func_178824_a(packetIn.getNameTagVisibility());
         if (enumVisible != null) {
            scorePlayerTeam.setNameTagVisibility(enumVisible);
         }
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
         for (String string : packetIn.getPlayers()) {
            scoreboard.addPlayerToTeam(string, packetIn.getName());
         }
      }

      if (packetIn.getAction() == 4) {
         for (String string2 : packetIn.getPlayers()) {
            scoreboard.removePlayerFromTeam(string2, scorePlayerTeam);
         }
      }

      if (packetIn.getAction() == 1) {
         scoreboard.removeTeam(scorePlayerTeam);
      }
   }

   @Override
   public void handleParticles(S2APacketParticles packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getParticleCount() == 0) {
         double d = packetIn.getParticleSpeed() * packetIn.getXOffset();
         double e = packetIn.getParticleSpeed() * packetIn.getYOffset();
         double d2 = packetIn.getParticleSpeed() * packetIn.getZOffset();

         try {
            this.clientWorldController
               .spawnParticle(
                  packetIn.getParticleType(),
                  packetIn.isLongDistance(),
                  packetIn.getXCoordinate(),
                  packetIn.getYCoordinate(),
                  packetIn.getZCoordinate(),
                  d,
                  e,
                  d2,
                  packetIn.getParticleArgs()
               );
         } catch (Throwable throwable) {
            logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
         }
      } else {
         for (int i = 0; i < packetIn.getParticleCount(); i++) {
            double d3 = this.avRandomizer.nextGaussian() * packetIn.getXOffset();
            double d4 = this.avRandomizer.nextGaussian() * packetIn.getYOffset();
            double d5 = this.avRandomizer.nextGaussian() * packetIn.getZOffset();
            double d6 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d7 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d8 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();

            try {
               this.clientWorldController
                  .spawnParticle(
                     packetIn.getParticleType(),
                     packetIn.isLongDistance(),
                     packetIn.getXCoordinate() + d3,
                     packetIn.getYCoordinate() + d4,
                     packetIn.getZCoordinate() + d5,
                     d6,
                     d7,
                     d8,
                     packetIn.getParticleArgs()
                  );
            } catch (Throwable throwable2) {
               logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
               return;
            }
         }
      }
   }

   @Override
   public void handleEntityProperties(S20PacketEntityProperties packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity2 = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity2 != null) {
         if (!(entity2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity2 + ")");
         }

         BaseAttributeMap baseAttributeMap = ((EntityLivingBase)entity2).getAttributeMap();

         for (S20PacketEntityProperties.Snapshot snapshot : packetIn.func_149441_d()) {
            IAttributeInstance iAttributeInstance = baseAttributeMap.getAttributeInstanceByName(snapshot.func_151409_a());
            if (iAttributeInstance == null) {
               iAttributeInstance = baseAttributeMap.registerAttribute(new RangedAttribute((IAttribute)null, snapshot.func_151409_a(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }

            iAttributeInstance.setBaseValue(snapshot.func_151410_b());
            iAttributeInstance.removeAllModifiers();

            for (AttributeModifier attributeModifier : snapshot.func_151408_c()) {
               iAttributeInstance.applyModifier(attributeModifier);
            }
         }
      }
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   public NetworkPlayerInfo getPlayerInfo(UUID uuid) {
      return this.playerInfoMap.get(uuid);
   }

   public NetworkPlayerInfo getPlayerInfo(String string) {
      for (NetworkPlayerInfo networkPlayerInfo : this.playerInfoMap.values()) {
         if (networkPlayerInfo.getGameProfile().getName().equals(string)) {
            return networkPlayerInfo;
         }
      }

      return null;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }
}
