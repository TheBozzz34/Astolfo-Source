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

   public NetHandlerPlayClient(Minecraft var1, GuiScreen var2, NetworkManager var3, GameProfile var4) {
      this.gameController = var1;
      this.guiScreenServer = var2;
      this.netManager = var3;
      this.profile = var4;
   }

   public void cleanup() {
      this.clientWorldController = null;
   }

   @Override
   public void handleJoinGame(S01PacketJoinGame var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
      this.clientWorldController = new WorldClient(
         this,
         new WorldSettings(0L, var1.getGameType(), false, var1.isHardcoreMode(), var1.getWorldType()),
         var1.getDimension(),
         var1.getDifficulty(),
         this.gameController.mcProfiler
      );
      this.gameController.gameSettings.difficulty = var1.getDifficulty();
      this.gameController.loadWorld(this.clientWorldController);
      this.gameController.thePlayer.dimension = var1.getDimension();
      this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      this.gameController.thePlayer.setEntityId(var1.getEntityId());
      this.currentServerMaxPlayers = var1.getMaxPlayers();
      this.gameController.thePlayer.setReducedDebug(var1.isReducedDebugInfo());
      this.gameController.playerController.setGameType(var1.getGameType());
      this.gameController.gameSettings.sendSettingsToServer();
      this.netManager
         .sendPacket(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
   }

   @Override
   public void handleSpawnObject(S0EPacketSpawnObject var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      double var4 = var1.getX() / ModuleManager.v;
      double var6 = var1.getY() / ModuleManager.v;
      double var8 = var1.getZ() / ModuleManager.v;
      Entity var10 = null;
      if (var1.getType() == 10) {
         var10 = EntityMinecart.getMinecart(this.clientWorldController, var4, var6, var8, EntityMinecart.EnumMinecartType.byNetworkID(var1.func_149009_m()));
      } else if (var1.getType() == 90) {
         Entity var11 = this.clientWorldController.getEntityByID(var1.func_149009_m());
         if (var11 instanceof EntityPlayer) {
            var10 = new EntityFishHook(this.clientWorldController, var4, var6, var8, (EntityPlayer)var11);
         }

         var1.func_149002_g(0);
      } else if (var1.getType() == 60) {
         var10 = new EntityArrow(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 61) {
         var10 = new EntitySnowball(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 71) {
         var10 = new EntityItemFrame(
            this.clientWorldController,
            new BlockPos(MathHelper.floor_double(var4), MathHelper.floor_double(var6), MathHelper.floor_double(var8)),
            EnumFacing.getHorizontal(var1.func_149009_m())
         );
         var1.func_149002_g(0);
      } else if (var1.getType() == 77) {
         var10 = new EntityLeashKnot(
            this.clientWorldController, new BlockPos(MathHelper.floor_double(var4), MathHelper.floor_double(var6), MathHelper.floor_double(var8))
         );
         var1.func_149002_g(0);
      } else if (var1.getType() == 65) {
         var10 = new EntityEnderPearl(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 72) {
         var10 = new EntityEnderEye(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 76) {
         var10 = new EntityFireworkRocket(this.clientWorldController, var4, var6, var8, (ItemStack)null);
      } else if (var1.getType() == 63) {
         var10 = new EntityLargeFireball(
            this.clientWorldController, var4, var6, var8, var1.getSpeedX() / 8000.0, var1.getSpeedY() / 8000.0, var1.getSpeedZ() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.getType() == 64) {
         var10 = new EntitySmallFireball(
            this.clientWorldController, var4, var6, var8, var1.getSpeedX() / 8000.0, var1.getSpeedY() / 8000.0, var1.getSpeedZ() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.getType() == 66) {
         var10 = new EntityWitherSkull(
            this.clientWorldController, var4, var6, var8, var1.getSpeedX() / 8000.0, var1.getSpeedY() / 8000.0, var1.getSpeedZ() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.getType() == 62) {
         var10 = new EntityEgg(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 73) {
         var10 = new EntityPotion(this.clientWorldController, var4, var6, var8, var1.func_149009_m());
         var1.func_149002_g(0);
      } else if (var1.getType() == 75) {
         var10 = new EntityExpBottle(this.clientWorldController, var4, var6, var8);
         var1.func_149002_g(0);
      } else if (var1.getType() == 1) {
         var10 = new EntityBoat(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 50) {
         var10 = new EntityTNTPrimed(this.clientWorldController, var4, var6, var8, (EntityLivingBase)null);
      } else if (var1.getType() == 78) {
         var10 = new EntityArmorStand(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 51) {
         var10 = new EntityEnderCrystal(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 2) {
         var10 = new EntityItem(this.clientWorldController, var4, var6, var8);
      } else if (var1.getType() == 70) {
         var10 = new EntityFallingBlock(this.clientWorldController, var4, var6, var8, Block.getStateById(var1.func_149009_m() & 65535));
         var1.func_149002_g(0);
      }

      if (var10 != null) {
         var10.serverPosX = var1.getX();
         var10.serverPosY = var1.getY();
         var10.serverPosZ = var1.getZ();
         var10.rotationPitch = var1.getPitch() * 360 / 256.0F;
         var10.rotationYaw = var1.getYaw() * 360 / 256.0F;
         Entity[] var14 = var10.getParts();
         if (var14 != null) {
            int var12 = var1.getEntityID() - var10.getEntityId();

            for (int var13 = 0; var13 < var14.length; var13++) {
               var14[var13].setEntityId(var14[var13].getEntityId() + var12);
            }
         }

         var10.setEntityId(var1.getEntityID());
         this.clientWorldController.addEntityToWorld(var1.getEntityID(), var10);
         if (var1.func_149009_m() > 0) {
            if (var1.getType() == 60) {
               Entity var15 = this.clientWorldController.getEntityByID(var1.func_149009_m());
               if (var15 instanceof EntityLivingBase && var10 instanceof EntityArrow) {
                  ((EntityArrow)var10).shootingEntity = var15;
               }
            }

            var10.setVelocity(var1.getSpeedX() / 8000.0, var1.getSpeedY() / 8000.0, var1.getSpeedZ() / 8000.0);
         }
      }
   }

   @Override
   public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityXPOrb var2 = new EntityXPOrb(
         this.clientWorldController, var1.getX() / ModuleManager.v, var1.getY() / ModuleManager.v, var1.getZ() / ModuleManager.v, var1.getXPValue()
      );
      var2.serverPosX = var1.getX();
      var2.serverPosY = var1.getY();
      var2.serverPosZ = var1.getZ();
      var2.rotationYaw = 0.0F;
      var2.rotationPitch = 0.0F;
      var2.setEntityId(var1.getEntityID());
      this.clientWorldController.addEntityToWorld(var1.getEntityID(), var2);
   }

   @Override
   public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      double var2 = var1.func_149051_d() / ModuleManager.v;
      double var4 = var1.func_149050_e() / ModuleManager.v;
      double var6 = var1.func_149049_f() / ModuleManager.v;
      EntityLightningBolt var8 = null;
      if (var1.func_149053_g() == 1) {
         var8 = new EntityLightningBolt(this.clientWorldController, var2, var4, var6);
      }

      if (var8 != null) {
         var8.serverPosX = var1.func_149051_d();
         var8.serverPosY = var1.func_149050_e();
         var8.serverPosZ = var1.func_149049_f();
         var8.rotationYaw = 0.0F;
         var8.rotationPitch = 0.0F;
         var8.setEntityId(var1.func_149052_c());
         this.clientWorldController.addWeatherEffect(var8);
      }
   }

   @Override
   public void handleSpawnPainting(S10PacketSpawnPainting var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPainting var2 = new EntityPainting(this.clientWorldController, var1.getPosition(), var1.getFacing(), var1.getTitle());
      this.clientWorldController.addEntityToWorld(var1.getEntityID(), var2);
   }

   @Override
   public void handleEntityVelocity(S12PacketEntityVelocity var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityID());
      if (var2 != null) {
         var2.setVelocity(var1.getMotionX() / 8000.0, var1.getMotionY() / 8000.0, var1.getMotionZ() / 8000.0);
      }
   }

   @Override
   public void handleEntityMetadata(S1CPacketEntityMetadata var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityId());
      if (var2 != null && var1.func_149376_c() != null) {
         var2.getDataWatcher().updateWatchedObjectsFromList(var1.func_149376_c());
      }
   }

   @Override
   public void handleSpawnPlayer(S0CPacketSpawnPlayer var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      double var4 = var1.getX() / ModuleManager.v;
      double var6 = var1.getY() / ModuleManager.v;
      double var8 = var1.getZ() / ModuleManager.v;
      float var10 = var1.getYaw() * 360 / 256.0F;
      float var11 = var1.getPitch() * 360 / 256.0F;
      EntityOtherPlayerMP var12 = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(var1.getPlayer()).getGameProfile());
      var12.prevPosX = var12.lastTickPosX = var12.serverPosX = var1.getX();
      var12.prevPosY = var12.lastTickPosY = var12.serverPosY = var1.getY();
      var12.prevPosZ = var12.lastTickPosZ = var12.serverPosZ = var1.getZ();
      int var13 = var1.getCurrentItemID();
      if (var13 == 0) {
         var12.inventory.mainInventory[var12.inventory.currentItem] = null;
      } else {
         var12.inventory.mainInventory[var12.inventory.currentItem] = new ItemStack(Item.getItemById(var13), 1, 0);
      }

      var12.setPositionAndRotation(var4, var6, var8, var10, var11);
      this.clientWorldController.addEntityToWorld(var1.getEntityID(), var12);
      List var14 = var1.func_148944_c();
      if (var14 != null) {
         var12.getDataWatcher().updateWatchedObjectsFromList(var14);
      }
   }

   @Override
   public void handleEntityTeleport(S18PacketEntityTeleport var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var4 = this.clientWorldController.getEntityByID(var1.getEntityId());
      if (var4 != null) {
         var4.serverPosX = var1.getX();
         var4.serverPosY = var1.getY();
         var4.serverPosZ = var1.getZ();
         double var5 = var4.serverPosX / ModuleManager.v;
         double var7 = var4.serverPosY / ModuleManager.v;
         double var9 = var4.serverPosZ / ModuleManager.v;
         float var11 = var1.getYaw() * 360 / 256.0F;
         float var12 = var1.getPitch() * 360 / 256.0F;
         if (Math.abs(var4.posX - var5) < 0.03125 && Math.abs(var4.posY - var7) < 0.015625 && Math.abs(var4.posZ - var9) < 0.03125) {
            var4.setPositionAndRotation2(var4.posX, var4.posY, var4.posZ, var11, var12, 3, true);
         } else {
            var4.setPositionAndRotation2(var5, var7, var9, var11, var12, 3, true);
         }

         var4.onGround = var1.getOnGround();
      }
   }

   @Override
   public void handleHeldItemChange(S09PacketHeldItemChange var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.getHeldItemHotbarIndex() >= 0 && var1.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
         this.gameController.thePlayer.inventory.currentItem = var1.getHeldItemHotbarIndex();
      }
   }

   @Override
   public void handleEntityMovement(S14PacketEntity var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var4 = var1.getEntity(this.clientWorldController);
      if (var4 != null) {
         var4.serverPosX = var4.serverPosX + var1.func_149062_c();
         var4.serverPosY = var4.serverPosY + var1.func_149061_d();
         var4.serverPosZ = var4.serverPosZ + var1.func_149064_e();
         double var5 = var4.serverPosX / ModuleManager.v;
         double var7 = var4.serverPosY / ModuleManager.v;
         double var9 = var4.serverPosZ / ModuleManager.v;
         float var11 = var1.func_149060_h() ? var1.func_149066_f() * 360 / 256.0F : var4.rotationYaw;
         float var12 = var1.func_149060_h() ? var1.func_149063_g() * 360 / 256.0F : var4.rotationPitch;
         var4.setPositionAndRotation2(var5, var7, var9, var11, var12, 3, false);
         var4.onGround = var1.getOnGround();
      }
   }

   @Override
   public void handleEntityHeadLook(S19PacketEntityHeadLook var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var4 = var1.getEntity(this.clientWorldController);
      if (var4 != null) {
         float var5 = var1.getYaw() * 360 / 256.0F;
         var4.setRotationYawHead(var5);
      }
   }

   @Override
   public void handleDestroyEntities(S13PacketDestroyEntities var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);

      for (int var2 = 0; var2 < var1.getEntityIDs().length; var2++) {
         this.clientWorldController.removeEntityFromWorld(var1.getEntityIDs()[var2]);
      }
   }

   @Override
   public void handlePlayerPosLook(S08PacketPlayerPosLook var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var2 = this.gameController.thePlayer;
      double var3 = var1.getX();
      double var5 = var1.getY();
      double var7 = var1.getZ();
      float var9 = var1.getYaw();
      float var10 = var1.getPitch();
      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         var3 += var2.posX;
      } else {
         var2.motionX = 0.0;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         var5 += var2.posY;
      } else {
         var2.motionY = 0.0;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         var7 += var2.posZ;
      } else {
         var2.motionZ = 0.0;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         var10 += var2.rotationPitch;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         var9 += var2.rotationYaw;
      }

      var2.setPositionAndRotation(var3, var5, var7, var9, var10);
      this.netManager
         .sendPacket(
            new C03PacketPlayer.C06PacketPlayerPosLook(var2.posX, var2.getEntityBoundingBox().minY, var2.posZ, var2.rotationYaw, var2.rotationPitch, false)
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
   public void handleMultiBlockChange(S22PacketMultiBlockChange var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);

      for (S22PacketMultiBlockChange.BlockUpdateData var5 : var1.getChangedBlocks()) {
         this.clientWorldController.invalidateRegionAndSetBlock(var5.getPos(), var5.getBlockState());
      }
   }

   @Override
   public void handleChunkData(S21PacketChunkData var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.func_149274_i()) {
         if (var1.getExtractedSize() == 0) {
            this.clientWorldController.doPreChunk(var1.getChunkX(), var1.getChunkZ(), false);
            return;
         }

         this.clientWorldController.doPreChunk(var1.getChunkX(), var1.getChunkZ(), true);
      }

      this.clientWorldController
         .invalidateBlockReceiveRegion(var1.getChunkX() << 4, 0, var1.getChunkZ() << 4, (var1.getChunkX() << 4) + 15, 256, (var1.getChunkZ() << 4) + 15);
      Chunk var4 = this.clientWorldController.getChunkFromChunkCoords(var1.getChunkX(), var1.getChunkZ());
      var4.fillChunk(var1.getExtractedDataBytes(), var1.getExtractedSize(), var1.func_149274_i());
      this.clientWorldController
         .markBlockRangeForRenderUpdate(var1.getChunkX() << 4, 0, var1.getChunkZ() << 4, (var1.getChunkX() << 4) + 15, 256, (var1.getChunkZ() << 4) + 15);
      if (!var1.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
         var4.resetRelightChecks();
      }
   }

   @Override
   public void handleBlockChange(S23PacketBlockChange var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.clientWorldController.invalidateRegionAndSetBlock(var1.getBlockPosition(), var1.getBlockState());
   }

   @Override
   public void handleDisconnect(S40PacketDisconnect var1) {
      this.netManager.closeChannel(var1.getReason());
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
      this.gameController.loadWorld((WorldClient)null);
      if (GameUtil.isOnHypixel()) {
         for (IChatComponent var5 : var1.getSiblings()) {
            if (var5.getFormattedText().startsWith("§fCheating through the use of unfair game advantages")
               || var5.getFormattedText().startsWith("§fBoosting your account to improve your stats.")
               || var5.getFormattedText().startsWith("§fDiscussing or acting in a manner which encourages")) {
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
               .displayGuiScreen(new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).func_154321_a(), "disconnect.lost", var1).getProxy());
         } else {
            this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", var1));
         }
      } else {
         this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", var1));
      }
   }

   public void addToSendQueue(Packet var1) {
      this.netManager.sendPacket(var1);
   }

   public void u(Packet var1) {
      this.netManager.B(var1);
   }

   @Override
   public void handleCollectItem(S0DPacketCollectItem var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getCollectedItemEntityID());
      EntityLivingBase var3 = (EntityLivingBase)this.clientWorldController.getEntityByID(var1.getEntityID());
      if (var3 == null) {
         var3 = this.gameController.thePlayer;
      }

      if (var2 != null) {
         if (var2 instanceof EntityXPOrb) {
            this.clientWorldController
               .playSoundAtEntity(var2, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.clientWorldController
               .playSoundAtEntity(var2, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, var2, var3, 0.5F));
         this.clientWorldController.removeEntityFromWorld(var1.getCollectedItemEntityID());
      }
   }

   @Override
   public void handleChat(S02PacketChat var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.getType() == 2) {
         this.gameController.ingameGUI.setRecordPlaying(var1.getChatComponent(), false);
      } else {
         this.gameController.ingameGUI.getChatGUI().printChatMessage(var1.getChatComponent());
      }
   }

   @Override
   public void handleAnimation(S0BPacketAnimation var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityID());
      if (var2 != null) {
         if (var1.getAnimationType() == 0) {
            EntityLivingBase var3 = (EntityLivingBase)var2;
            var3.swingItem();
         } else if (var1.getAnimationType() == 1) {
            var2.performHurtAnimation();
         } else if (var1.getAnimationType() == 2) {
            EntityPlayer var4 = (EntityPlayer)var2;
            var4.wakeUpPlayer(false, false, false);
         } else if (var1.getAnimationType() == 4) {
            this.gameController.effectRenderer.emitParticleAtEntity(var2, EnumParticleTypes.CRIT);
         } else if (var1.getAnimationType() == 5) {
            this.gameController.effectRenderer.emitParticleAtEntity(var2, EnumParticleTypes.CRIT_MAGIC);
         }
      }
   }

   @Override
   public void handleUseBed(S0APacketUseBed var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      var1.getPlayer(this.clientWorldController).trySleep(var1.getBedPosition());
   }

   @Override
   public void handleSpawnMob(S0FPacketSpawnMob var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      double var4 = var1.getX() / ModuleManager.v;
      double var6 = var1.getY() / ModuleManager.v;
      double var8 = var1.getZ() / ModuleManager.v;
      float var10 = var1.getYaw() * 360 / 256.0F;
      float var11 = var1.getPitch() * 360 / 256.0F;
      EntityLivingBase var12 = (EntityLivingBase)EntityList.createEntityByID(var1.getEntityType(), this.gameController.theWorld);
      var12.serverPosX = var1.getX();
      var12.serverPosY = var1.getY();
      var12.serverPosZ = var1.getZ();
      var12.renderYawOffset = var12.rotationYawHead = var1.getHeadPitch() * 360 / 256.0F;
      Entity[] var13 = var12.getParts();
      if (var13 != null) {
         int var14 = var1.getEntityID() - var12.getEntityId();

         for (int var15 = 0; var15 < var13.length; var15++) {
            var13[var15].setEntityId(var13[var15].getEntityId() + var14);
         }
      }

      var12.setEntityId(var1.getEntityID());
      var12.setPositionAndRotation(var4, var6, var8, var10, var11);
      var12.motionX = var1.getVelocityX() / 8000.0F;
      var12.motionY = var1.getVelocityY() / 8000.0F;
      var12.motionZ = var1.getVelocityZ() / 8000.0F;
      this.clientWorldController.addEntityToWorld(var1.getEntityID(), var12);
      List var16 = var1.func_149027_c();
      if (var16 != null) {
         var12.getDataWatcher().updateWatchedObjectsFromList(var16);
      }
   }

   @Override
   public void handleTimeUpdate(S03PacketTimeUpdate var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.theWorld.m(var1.getTotalWorldTime());
      this.gameController.theWorld.setWorldTime(var1.getWorldTime());
   }

   @Override
   public void handleSpawnPosition(S05PacketSpawnPosition var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.thePlayer.setSpawnPoint(var1.getSpawnPos(), true);
      this.gameController.theWorld.getWorldInfo().setSpawn(var1.getSpawnPos());
   }

   @Override
   public void handleEntityAttach(S1BPacketEntityAttach var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityId());
      Entity var3 = this.clientWorldController.getEntityByID(var1.getVehicleEntityId());
      if (var1.getLeash() == 0) {
         boolean var4 = false;
         if (var1.getEntityId() == this.gameController.thePlayer.getEntityId()) {
            var2 = this.gameController.thePlayer;
            if (var3 instanceof EntityBoat) {
               ((EntityBoat)var3).setIsBoatEmpty(false);
            }

            var4 = var2.ridingEntity == null && var3 != null;
         } else if (var3 instanceof EntityBoat) {
            ((EntityBoat)var3).setIsBoatEmpty(true);
         }

         if (var2 == null) {
            return;
         }

         var2.mountEntity(var3);
         if (var4) {
            GameSettings var5 = this.gameController.gameSettings;
            this.gameController
               .ingameGUI
               .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(var5.keyBindSneak.getKeyCode())), false);
         }
      } else if (var1.getLeash() == 1 && var2 instanceof EntityLiving) {
         if (var3 != null) {
            ((EntityLiving)var2).setLeashedToEntity(var3, false);
         } else {
            ((EntityLiving)var2).clearLeashed(false, false);
         }
      }
   }

   @Override
   public void handleEntityStatus(S19PacketEntityStatus var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var4 = var1.getEntity(this.clientWorldController);
      if (var4 != null) {
         if (var1.getOpCode() == 21) {
            this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)var4));
         } else {
            var4.handleStatusUpdate(var1.getOpCode());
         }
      }
   }

   @Override
   public void handleUpdateHealth(S06PacketUpdateHealth var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.thePlayer.setPlayerSPHealth(var1.getHealth());
      this.gameController.thePlayer.getFoodStats().setFoodLevel(var1.getFoodLevel());
      this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(var1.getSaturationLevel());
   }

   @Override
   public void handleSetExperience(S1FPacketSetExperience var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.thePlayer.setXPStats(var1.func_149397_c(), var1.getTotalExperience(), var1.getLevel());
   }

   @Override
   public void handleRespawn(S07PacketRespawn var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.getDimensionID() != this.gameController.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard var2 = this.clientWorldController.getScoreboard();
         this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, var1.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), var1.getWorldType()),
            var1.getDimensionID(),
            var1.getDifficulty(),
            this.gameController.mcProfiler
         );
         this.clientWorldController.setWorldScoreboard(var2);
         this.gameController.loadWorld(this.clientWorldController);
         this.gameController.thePlayer.dimension = var1.getDimensionID();
         this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.gameController.setDimensionAndSpawnPlayer(var1.getDimensionID());
      this.gameController.playerController.setGameType(var1.getGameType());
   }

   @Override
   public void handleExplosion(S27PacketExplosion var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Explosion var2 = new Explosion(
         this.gameController.theWorld, (Entity)null, var1.getX(), var1.getY(), var1.getZ(), var1.getStrength(), var1.getAffectedBlockPositions()
      );
      var2.doExplosionB(true);
      this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + var1.func_149149_c();
      this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + var1.func_149144_d();
      this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + var1.func_149147_e();
   }

   @Override
   public void handleOpenWindow(S2DPacketOpenWindow var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var2 = this.gameController.thePlayer;
      if ("minecraft:container".equals(var1.getGuiId())) {
         var2.displayGUIChest(new InventoryBasic(var1.getWindowTitle(), var1.getSlotCount()));
         var2.openContainer.windowId = var1.getWindowId();
      } else if ("minecraft:villager".equals(var1.getGuiId())) {
         var2.displayVillagerTradeGui(new NpcMerchant(var2, var1.getWindowTitle()));
         var2.openContainer.windowId = var1.getWindowId();
      } else if ("EntityHorse".equals(var1.getGuiId())) {
         Entity var3 = this.clientWorldController.getEntityByID(var1.getEntityId());
         if (var3 instanceof EntityHorse) {
            var2.displayGUIHorse((EntityHorse)var3, new AnimalChest(var1.getWindowTitle(), var1.getSlotCount()));
            var2.openContainer.windowId = var1.getWindowId();
         }
      } else if (!var1.hasSlots()) {
         var2.displayGui(new LocalBlockIntercommunication(var1.getGuiId(), var1.getWindowTitle()));
         var2.openContainer.windowId = var1.getWindowId();
      } else {
         ContainerLocalMenu var4 = new ContainerLocalMenu(var1.getGuiId(), var1.getWindowTitle(), var1.getSlotCount());
         var2.displayGUIChest(var4);
         var2.openContainer.windowId = var1.getWindowId();
      }
   }

   @Override
   public void handleSetSlot(S2FPacketSetSlot var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var4 = this.gameController.thePlayer;
      if (var1.func_149175_c() == -1) {
         var4.inventory.setItemStack(var1.func_149174_e());
      } else {
         boolean var5 = false;
         if (this.gameController.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative var6 = (GuiContainerCreative)this.gameController.currentScreen;
            var5 = var6.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
         }

         if (var1.func_149175_c() == 0 && var1.func_149173_d() >= 36 && var1.func_149173_d() < 45) {
            ItemStack var7 = var4.inventoryContainer.getSlot(var1.func_149173_d()).getStack();
            if (var1.func_149174_e() != null && (var7 == null || var7.stackSize < var1.func_149174_e().stackSize)) {
               var1.func_149174_e().animationsToGo = 5;
            }

            var4.inventoryContainer.putStackInSlot(var1.func_149173_d(), var1.func_149174_e());
         } else if (var1.func_149175_c() == var4.openContainer.windowId && (var1.func_149175_c() != 0 || !var5)) {
            var4.openContainer.putStackInSlot(var1.func_149173_d(), var1.func_149174_e());
         }
      }
   }

   @Override
   public void handleConfirmTransaction(S32PacketConfirmTransaction var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Container var2 = null;
      EntityPlayerSP var3 = this.gameController.thePlayer;
      if (var1.getWindowId() == 0) {
         var2 = var3.inventoryContainer;
      } else if (var1.getWindowId() == var3.openContainer.windowId) {
         var2 = var3.openContainer;
      }

      if (var2 != null && !var1.func_148888_e()) {
         this.addToSendQueue(new C0FPacketConfirmTransaction(var1.getWindowId(), var1.getActionNumber(), true));
      }
   }

   @Override
   public void handleWindowItems(S30PacketWindowItems var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var2 = this.gameController.thePlayer;
      if (var1.func_148911_c() == 0) {
         var2.inventoryContainer.putStacksInSlots(var1.getItemStacks());
      } else if (var1.func_148911_c() == var2.openContainer.windowId) {
         var2.openContainer.putStacksInSlots(var1.getItemStacks());
      }
   }

   @Override
   public void handleSignEditorOpen(S36PacketSignEditorOpen var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      TileEntity var2 = this.clientWorldController.getTileEntity(var1.getSignPosition());
      if (!(var2 instanceof TileEntitySign)) {
         var2 = new TileEntitySign();
         var2.setWorldObj(this.clientWorldController);
         var2.setPos(var1.getSignPosition());
      }

      this.gameController.thePlayer.openEditSign((TileEntitySign)var2);
   }

   @Override
   public void handleUpdateSign(S33PacketUpdateSign var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      boolean var2 = false;
      if (this.gameController.theWorld.isBlockLoaded(var1.getPos())) {
         TileEntity var3 = this.gameController.theWorld.getTileEntity(var1.getPos());
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (var4.getIsEditable()) {
               System.arraycopy(var1.getLines(), 0, var4.signText, 0, 4);
               var4.markDirty();
            }

            var2 = true;
         }
      }

      if (!var2 && this.gameController.thePlayer != null) {
         this.gameController
            .thePlayer
            .addChatMessage(
               new ChatComponentText("Unable to locate sign at " + var1.getPos().getX() + ", " + var1.getPos().getY() + ", " + var1.getPos().getZ())
            );
      }
   }

   @Override
   public void handleUpdateTileEntity(S35PacketUpdateTileEntity var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (this.gameController.theWorld.isBlockLoaded(var1.getPos())) {
         TileEntity var4 = this.gameController.theWorld.getTileEntity(var1.getPos());
         int var5 = var1.getTileEntityType();
         if (var5 == 1 && var4 instanceof TileEntityMobSpawner
            || var5 == 2 && var4 instanceof TileEntityCommandBlock
            || var5 == 3 && var4 instanceof TileEntityBeacon
            || var5 == 4 && var4 instanceof TileEntitySkull
            || var5 == 5 && var4 instanceof TileEntityFlowerPot
            || var5 == 6 && var4 instanceof TileEntityBanner) {
            var4.readFromNBT(var1.getNbtCompound());
         }
      }
   }

   @Override
   public void handleWindowProperty(S31PacketWindowProperty var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var2 = this.gameController.thePlayer;
      if (var2.openContainer != null && var2.openContainer.windowId == var1.getWindowId()) {
         var2.openContainer.updateProgressBar(var1.getVarIndex(), var1.getVarValue());
      }
   }

   @Override
   public void handleEntityEquipment(S04PacketEntityEquipment var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityID());
      if (var2 != null) {
         var2.setCurrentItemOrArmor(var1.getEquipmentSlot(), var1.getItemStack());
      }
   }

   @Override
   public void handleCloseWindow(S2EPacketCloseWindow var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.thePlayer.closeScreenAndDropStack();
   }

   @Override
   public void handleBlockAction(S24PacketBlockAction var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.theWorld.addBlockEvent(var1.getBlockPosition(), var1.getBlockType(), var1.getData1(), var1.getData2());
   }

   @Override
   public void handleBlockBreakAnim(S25PacketBlockBreakAnim var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.theWorld.sendBlockBreakProgress(var1.getBreakerId(), var1.getPosition(), var1.getProgress());
   }

   @Override
   public void handleMapChunkBulk(S26PacketMapChunkBulk var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);

      for (int var4 = 0; var4 < var1.getChunkCount(); var4++) {
         int var5 = var1.getChunkX(var4);
         int var6 = var1.getChunkZ(var4);
         this.clientWorldController.doPreChunk(var5, var6, true);
         this.clientWorldController.invalidateBlockReceiveRegion(var5 << 4, 0, var6 << 4, (var5 << 4) + 15, 256, (var6 << 4) + 15);
         Chunk var7 = this.clientWorldController.getChunkFromChunkCoords(var5, var6);
         var7.fillChunk(var1.getChunkBytes(var4), var1.getChunkSize(var4), true);
         this.clientWorldController.markBlockRangeForRenderUpdate(var5 << 4, 0, var6 << 4, (var5 << 4) + 15, 256, (var6 << 4) + 15);
         if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            var7.resetRelightChecks();
         }
      }
   }

   @Override
   public void handleChangeGameState(S2BPacketChangeGameState var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var4 = this.gameController.thePlayer;
      int var5 = var1.getGameState();
      float var6 = var1.func_149137_d();
      int var7 = MathHelper.floor_float(var6 + 0.5F);
      if (var5 >= 0 && var5 < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[var5] != null) {
         var4.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[var5]));
      }

      if (var5 == 1) {
         this.clientWorldController.getWorldInfo().setRaining(true);
         this.clientWorldController.V(0.0F);
      } else if (var5 == 2) {
         this.clientWorldController.getWorldInfo().setRaining(false);
         this.clientWorldController.V(1.0F);
      } else if (var5 == 3) {
         this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(var7));
      } else if (var5 == 4) {
         this.gameController.displayGuiScreen(new GuiWinGame());
      } else if (var5 == 5) {
         GameSettings var8 = this.gameController.gameSettings;
         if (var6 == 0.0F) {
            this.gameController.displayGuiScreen(new GuiScreenDemo());
         } else if (var6 == 101.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.getKeyDisplayString(var8.keyBindForward.getKeyCode()),
                     GameSettings.getKeyDisplayString(var8.keyBindLeft.getKeyCode()),
                     GameSettings.getKeyDisplayString(var8.keyBindBack.getKeyCode()),
                     GameSettings.getKeyDisplayString(var8.keyBindRight.getKeyCode())
                  )
               );
         } else if (var6 == 102.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(var8.keyBindJump.getKeyCode())));
         } else if (var6 == 103.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(var8.keyBindInventory.getKeyCode())));
         }
      } else if (var5 == 6) {
         this.clientWorldController.playSound(var4.posX, var4.posY + var4.getEyeHeight(), var4.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (var5 == 7) {
         this.clientWorldController.V(var6);
      } else if (var5 == 8) {
         this.clientWorldController.t(var6);
      } else if (var5 == 10) {
         this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, var4.posX, var4.posY, var4.posZ, 0.0, 0.0, 0.0);
         this.clientWorldController.playSound(var4.posX, var4.posY, var4.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleMaps(S34PacketMaps var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      MapData var2 = ItemMap.loadMapData(var1.getMapId(), this.gameController.theWorld);
      var1.setMapdataTo(var2);
      this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(var2);
   }

   @Override
   public void handleEffect(S28PacketEffect var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.isSoundServerwide()) {
         this.gameController.theWorld.playBroadcastSound(var1.getSoundType(), var1.getSoundPos(), var1.getSoundData());
      } else {
         this.gameController.theWorld.playAuxSFX(var1.getSoundType(), var1.getSoundPos(), var1.getSoundData());
      }
   }

   @Override
   public void handleStatistics(S37PacketStatistics var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      boolean var2 = false;

      for (Entry var4 : var1.func_148974_c().entrySet()) {
         StatBase var5 = (StatBase)var4.getKey();
         int var6 = (Integer)var4.getValue();
         if (var5.isAchievement() && var6 > 0) {
            if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().readStat(var5) == 0) {
               Achievement var7 = (Achievement)var5;
               this.gameController.guiAchievement.displayAchievement(var7);
               if (var5 == AchievementList.openInventory) {
                  this.gameController.gameSettings.l8 = false;
                  this.gameController.gameSettings.saveOptions();
               }
            }

            var2 = true;
         }

         this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, var5, var6);
      }

      if (!this.field_147308_k && !var2 && this.gameController.gameSettings.l8) {
         this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
      }

      this.field_147308_k = true;
      if (this.gameController.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.gameController.currentScreen).doneLoading();
      }
   }

   @Override
   public void handleEntityEffect(S1DPacketEntityEffect var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityId());
      if (var2 instanceof EntityLivingBase) {
         PotionEffect var3 = new PotionEffect(var1.getEffectId(), var1.getDuration(), var1.getAmplifier(), false, var1.func_179707_f());
         var3.setPotionDurationMax(var1.func_149429_c());
         ((EntityLivingBase)var2).addPotionEffect(var3);
      }
   }

   @Override
   public void handleCombatEvent(S42PacketCombatEvent var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var4 = this.clientWorldController.getEntityByID(var1.field_179775_c);
      if (!(var4 instanceof EntityLivingBase)) {
         Object var10000 = null;
      }

      if (var1.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         long var6 = 1000 * var1.field_179772_d / 20;
      } else if (var1.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         Entity var8 = this.clientWorldController.getEntityByID(var1.field_179774_b);
         if (var8 instanceof EntityPlayer) {
         }
      }
   }

   @Override
   public void handleServerDifficulty(S41PacketServerDifficulty var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.theWorld.getWorldInfo().setDifficulty(var1.getDifficulty());
      this.gameController.theWorld.getWorldInfo().setDifficultyLocked(var1.isDifficultyLocked());
   }

   @Override
   public void handleCamera(S43PacketCamera var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = var1.getEntity(this.clientWorldController);
      if (var2 != null) {
         this.gameController.setRenderViewEntity(var2);
      }
   }

   @Override
   public void handleWorldBorder(S44PacketWorldBorder var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      var1.func_179788_a(this.clientWorldController.getWorldBorder());
   }

   @Override
   public void handleTitle(S45PacketTitle var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      S45PacketTitle.Type var2 = var1.getType();
      String var3 = null;
      String var4 = null;
      String var5 = var1.getMessage() != null ? var1.getMessage().getFormattedText() : "";
      switch (var2) {
         case TITLE:
            var3 = var5;
            break;
         case SUBTITLE:
            var4 = var5;
            break;
         case RESET:
            this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
            this.gameController.ingameGUI.setDefaultTitlesTimes();
            return;
      }

      this.gameController.ingameGUI.displayTitle(var3, var4, var1.getFadeInTime(), var1.getDisplayTime(), var1.getFadeOutTime());
   }

   @Override
   public void handleSetCompressionLevel(S46PacketSetCompressionLevel var1) {
      if (!this.netManager.isLocalChannel()) {
         this.netManager.setCompressionTreshold(var1.getThreshold());
      }
   }

   @Override
   public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter var1) {
      this.gameController.ingameGUI.getTabList().setHeader(var1.getHeader().getFormattedText().length() == 0 ? null : var1.getHeader());
      this.gameController.ingameGUI.getTabList().setFooter(var1.getFooter().getFormattedText().length() == 0 ? null : var1.getFooter());
   }

   @Override
   public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityId());
      if (var2 instanceof EntityLivingBase) {
         ((EntityLivingBase)var2).removePotionEffectClient(var1.getEffectId());
      }
   }

   @Override
   public void handlePlayerListItem(S38PacketPlayerListItem var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);

      for (S38PacketPlayerListItem.AddPlayerData var3 : var1.getEntries()) {
         if (var1.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(var3.getProfile().getId());
         } else {
            NetworkPlayerInfo var4 = this.playerInfoMap.get(var3.getProfile().getId());
            if (var1.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
               var4 = new NetworkPlayerInfo(var3);
               this.playerInfoMap.put(var4.getGameProfile().getId(), var4);
            }

            if (var4 != null) {
               switch (var1.getAction()) {
                  case ADD_PLAYER:
                     var4.setGameType(var3.getGameMode());
                     var4.setResponseTime(var3.getPing());
                     break;
                  case UPDATE_GAME_MODE:
                     var4.setGameType(var3.getGameMode());
                     break;
                  case UPDATE_LATENCY:
                     var4.setResponseTime(var3.getPing());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     var4.setDisplayName(var3.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleKeepAlive(S00PacketKeepAlive var1) {
      this.addToSendQueue(new C00PacketKeepAlive(var1.func_149134_c()));
   }

   @Override
   public void handlePlayerAbilities(S39PacketPlayerAbilities var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      EntityPlayerSP var2 = this.gameController.thePlayer;
      var2.capabilities.isFlying = var1.isFlying();
      var2.capabilities.isCreativeMode = var1.isCreativeMode();
      var2.capabilities.disableDamage = var1.isInvulnerable();
      var2.capabilities.allowFlying = var1.isAllowFlying();
      var2.capabilities.setFlySpeed(var1.getFlySpeed());
      var2.capabilities.setPlayerWalkSpeed(var1.getWalkSpeed());
   }

   @Override
   public void handleTabComplete(S3APacketTabComplete var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      String[] var2 = var1.func_149630_c();
      if (this.gameController.currentScreen instanceof GuiChat) {
         GuiChat var3 = (GuiChat)this.gameController.currentScreen;
         var3.onAutocompleteResponse(var2);
      }
   }

   @Override
   public void handleSoundEffect(S29PacketSoundEffect var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      this.gameController.theWorld.playSound(var1.getX(), var1.getY(), var1.getZ(), var1.getSoundName(), var1.getVolume(), var1.getPitch(), false);
   }

   @Override
   public void handleResourcePack(S48PacketResourcePackSend var1) {
      if (this.C(var1.R(), var1.r())) {
         final String var2 = var1.R();
         final String var3 = var1.r();
         if (var2.startsWith("level://")) {
            String var4 = var2.substring("level://".length());
            File var5 = new File(this.gameController.mcDataDir, "saves");
            File var6 = new File(var5, var4);
            if (var6.isFile()) {
               this.netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
               Futures.addCallback(
                  this.gameController.getResourcePackRepository().setResourcePackInstance(var6),
                  new FutureCallback<Object>() {
                     @Override
                     public void onSuccess(Object var1) {
                        NetHandlerPlayClient.this.netManager
                           .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                     }

                     @Override
                     public void onFailure(Throwable var1) {
                        NetHandlerPlayClient.this.netManager
                           .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                     }
                  }
               );
            } else {
               this.netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
         } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(
               this.gameController.getResourcePackRepository().downloadResourcePack(var2, var3),
               new FutureCallback<Object>() {
                  @Override
                  public void onSuccess(Object var1) {
                     NetHandlerPlayClient.this.netManager
                        .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                  }

                  @Override
                  public void onFailure(Throwable var1) {
                     NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                  }
               }
            );
         } else if (this.gameController.getCurrentServerData() != null
            && this.gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.DECLINED));
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
                                    public void confirmClicked(boolean var1, int var2x) {
                                       NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                                       if (var1) {
                                          if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                             NetHandlerPlayClient.this.gameController
                                                .getCurrentServerData()
                                                .setResourceMode(ServerData.ServerResourceMode.ENABLED);
                                          }

                                          NetHandlerPlayClient.this.netManager
                                             .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
                                          Futures.addCallback(
                                             NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(var2, var3),
                                             new FutureCallback<Object>() {
                                                @Override
                                                public void onSuccess(Object var1) {
                                                   NetHandlerPlayClient.this.netManager
                                                      .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                                }

                                                @Override
                                                public void onFailure(Throwable var1) {
                                                   NetHandlerPlayClient.this.netManager
                                                      .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
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
                                             .sendPacket(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.DECLINED));
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

   private boolean C(String var1, String var2) {
      try {
         URI var3 = new URI(var1);
         String var4 = var3.getScheme();
         boolean var5 = "level".equals(var4);
         if (!"http".equals(var4) && !"https".equals(var4) && !var5) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(var2, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            throw new URISyntaxException(var1, "Wrong protocol");
         }

         var1 = URLDecoder.decode(var1.substring("level://".length()), StandardCharsets.UTF_8.toString());
         if (var5 && (var1.contains("..") || !var1.endsWith("/resources.zip"))) {
            System.out.println("Malicious server tried to access " + var1);
            EntityPlayerSP var6 = Minecraft.getMinecraft().thePlayer;
            if (var6 != null) {
               var6.addChatMessage(
                  new ChatComponentText(
                     EnumChatFormatting.RED
                        + EnumChatFormatting.BOLD.toString()
                        + "[WARNING] The current server has attempted to be malicious but we have stopped them."
                  )
               );
            }

            throw new URISyntaxException(var1, "Invalid levelstorage resourcepack path");
         } else {
            return true;
         }
      } catch (URISyntaxException var7) {
         return false;
      } catch (UnsupportedEncodingException var8) {
         var8.printStackTrace();
         return false;
      }
   }

   @Override
   public void handleEntityNBT(S49PacketUpdateEntityNBT var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = var1.getEntity(this.clientWorldController);
      if (var2 != null) {
         var2.clientUpdateEntityNBT(var1.getTagCompound());
      }
   }

   @Override
   public void handleCustomPayload(S3FPacketCustomPayload var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if ("MC|TrList".equals(var1.getChannelName())) {
         PacketBuffer var4 = var1.getBufferData();

         try {
            int var5 = var4.readInt();
            GuiScreen var6 = this.gameController.currentScreen;
            if (var6 != null && var6 instanceof GuiMerchant && var5 == this.gameController.thePlayer.openContainer.windowId) {
               IMerchant var7 = ((GuiMerchant)var6).getMerchant();
               MerchantRecipeList var8 = MerchantRecipeList.readFromBuf(var4);
               var7.setRecipes(var8);
            }
         } catch (IOException var12) {
            logger.error("Couldn't load trade info", var12);
         } finally {
            var4.release();
         }
      } else if ("MC|Brand".equals(var1.getChannelName())) {
         if (ViaFabric.INSTANCE.isAfter1_12_2()
            && this.gameController.thePlayer.getClientBrand() != null
            && this.gameController.thePlayer.getClientBrand().startsWith("Hypixel BungeeCord")) {
            return;
         }

         this.gameController.thePlayer.setClientBrand(var1.getBufferData().readStringFromBuffer(32767));
         Astolfo.INSTANCE.getServerManager().x();
      } else if ("MC|BOpen".equals(var1.getChannelName())) {
         ItemStack var14 = this.gameController.thePlayer.getCurrentEquippedItem();
         if (var14 != null && var14.getItem() == Items.written_book) {
            this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, var14, false));
         }
      }
   }

   @Override
   public void handleScoreboardObjective(S3BPacketScoreboardObjective var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Scoreboard var2 = this.clientWorldController.getScoreboard();
      if (var1.func_149338_e() == 0) {
         ScoreObjective var3 = var2.addScoreObjective(var1.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
         var3.setDisplayName(var1.func_149337_d());
         var3.setRenderType(var1.func_179817_d());
      } else {
         ScoreObjective var4 = var2.getObjective(var1.func_149339_c());
         if (var1.func_149338_e() == 1) {
            var2.removeObjective(var4);
         } else if (var1.func_149338_e() == 2) {
            var4.setDisplayName(var1.func_149337_d());
            var4.setRenderType(var1.func_179817_d());
         }
      }
   }

   @Override
   public void handleUpdateScore(S3CPacketUpdateScore var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Scoreboard var2 = this.clientWorldController.getScoreboard();
      ScoreObjective var3 = var2.getObjective(var1.getObjectiveName());
      if (var1.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE) {
         Score var4 = var2.getValueFromObjective(var1.getPlayerName(), var3);
         var4.setScorePoints(var1.getScoreValue());
      } else if (var1.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE) {
         if (StringUtils.isNullOrEmpty(var1.getObjectiveName())) {
            var2.removeObjectiveFromEntity(var1.getPlayerName(), (ScoreObjective)null);
         } else if (var3 != null) {
            var2.removeObjectiveFromEntity(var1.getPlayerName(), var3);
         }
      }
   }

   @Override
   public void handleDisplayScoreboard(S3DPacketDisplayScoreboard var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Scoreboard var2 = this.clientWorldController.getScoreboard();
      if (var1.func_149370_d().length() == 0) {
         var2.setObjectiveInDisplaySlot(var1.func_149371_c(), (ScoreObjective)null);
      } else {
         ScoreObjective var3 = var2.getObjective(var1.func_149370_d());
         var2.setObjectiveInDisplaySlot(var1.func_149371_c(), var3);
      }
   }

   @Override
   public void handleTeams(S3EPacketTeams var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Scoreboard var2 = this.clientWorldController.getScoreboard();
      ScorePlayerTeam var3;
      if (var1.getAction() == 0) {
         var3 = var2.createTeam(var1.getName());
      } else {
         var3 = var2.getTeam(var1.getName());
      }

      if (var1.getAction() == 0 || var1.getAction() == 2) {
         var3.setTeamName(var1.getDisplayName());
         var3.setNamePrefix(var1.getPrefix());
         var3.setNameSuffix(var1.getSuffix());
         var3.setChatFormat(EnumChatFormatting.func_175744_a(var1.getColor()));
         var3.func_98298_a(var1.getFriendlyFlags());
         Team.EnumVisible var4 = Team.EnumVisible.func_178824_a(var1.getNameTagVisibility());
         if (var4 != null) {
            var3.setNameTagVisibility(var4);
         }
      }

      if (var1.getAction() == 0 || var1.getAction() == 3) {
         for (String var5 : var1.getPlayers()) {
            var2.addPlayerToTeam(var5, var1.getName());
         }
      }

      if (var1.getAction() == 4) {
         for (String var8 : var1.getPlayers()) {
            var2.removePlayerFromTeam(var8, var3);
         }
      }

      if (var1.getAction() == 1) {
         var2.removeTeam(var3);
      }
   }

   @Override
   public void handleParticles(S2APacketParticles var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      if (var1.getParticleCount() == 0) {
         double var2 = var1.getParticleSpeed() * var1.getXOffset();
         double var4 = var1.getParticleSpeed() * var1.getYOffset();
         double var6 = var1.getParticleSpeed() * var1.getZOffset();

         try {
            this.clientWorldController
               .spawnParticle(
                  var1.getParticleType(),
                  var1.isLongDistance(),
                  var1.getXCoordinate(),
                  var1.getYCoordinate(),
                  var1.getZCoordinate(),
                  var2,
                  var4,
                  var6,
                  var1.getParticleArgs()
               );
         } catch (Throwable var17) {
            logger.warn("Could not spawn particle effect " + var1.getParticleType());
         }
      } else {
         for (int var18 = 0; var18 < var1.getParticleCount(); var18++) {
            double var3 = this.avRandomizer.nextGaussian() * var1.getXOffset();
            double var5 = this.avRandomizer.nextGaussian() * var1.getYOffset();
            double var7 = this.avRandomizer.nextGaussian() * var1.getZOffset();
            double var9 = this.avRandomizer.nextGaussian() * var1.getParticleSpeed();
            double var11 = this.avRandomizer.nextGaussian() * var1.getParticleSpeed();
            double var13 = this.avRandomizer.nextGaussian() * var1.getParticleSpeed();

            try {
               this.clientWorldController
                  .spawnParticle(
                     var1.getParticleType(),
                     var1.isLongDistance(),
                     var1.getXCoordinate() + var3,
                     var1.getYCoordinate() + var5,
                     var1.getZCoordinate() + var7,
                     var9,
                     var11,
                     var13,
                     var1.getParticleArgs()
                  );
            } catch (Throwable var16) {
               logger.warn("Could not spawn particle effect " + var1.getParticleType());
               return;
            }
         }
      }
   }

   @Override
   public void handleEntityProperties(S20PacketEntityProperties var1) {
      PacketThreadUtil.checkThreadAndEnqueue(var1, this, this.gameController);
      Entity var2 = this.clientWorldController.getEntityByID(var1.getEntityId());
      if (var2 != null) {
         if (!(var2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         }

         BaseAttributeMap var3 = ((EntityLivingBase)var2).getAttributeMap();

         for (S20PacketEntityProperties.Snapshot var5 : var1.func_149441_d()) {
            IAttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_151409_a());
            if (var6 == null) {
               var6 = var3.registerAttribute(new RangedAttribute((IAttribute)null, var5.func_151409_a(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }

            var6.setBaseValue(var5.func_151410_b());
            var6.removeAllModifiers();

            for (AttributeModifier var8 : var5.func_151408_c()) {
               var6.applyModifier(var8);
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

   public NetworkPlayerInfo getPlayerInfo(UUID var1) {
      return this.playerInfoMap.get(var1);
   }

   public NetworkPlayerInfo getPlayerInfo(String var1) {
      for (NetworkPlayerInfo var3 : this.playerInfoMap.values()) {
         if (var3.getGameProfile().getName().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }
}
