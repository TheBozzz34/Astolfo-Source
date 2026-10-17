package net.minecraft.client.entity;

import bypass.Astolfo;
import bypass.module.movement.Speed;
import bypass.module.movement.SpeedMode;
import bypass.user.AstolfoUser;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.src.CapeUtils;
import net.minecraft.src.Config;
import net.minecraft.src.PlayerConfigurations;
import net.minecraft.src.Reflector;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class AbstractClientPlayer extends EntityPlayer {
   private NetworkPlayerInfo playerInfo;
   private ResourceLocation locationOfCape = null;
   private String nameClear = null;
   public static final ResourceLocation locationNormalCape = new ResourceLocation("client/normalCape.png");

   public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
      super(worldIn, playerProfile);
      this.nameClear = playerProfile.getName();
      if (this.nameClear != null && !this.nameClear.isEmpty()) {
         this.nameClear = StringUtils.stripControlCodes(this.nameClear);
      }

      CapeUtils.downloadCape(this);
      PlayerConfigurations.getPlayerConfiguration(this);
   }

   @Override
   public boolean isSpectator() {
      NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
      return networkPlayerInfo != null && networkPlayerInfo.getGameType() == WorldSettings.GameType.SPECTATOR;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   public NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo != null && networkPlayerInfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkPlayerInfo.getDisplayedSkin();
   }

   public ResourceLocation getLocationCape() {
      AstolfoUser astolfoUser = Astolfo.INSTANCE.getUserManager().getUser(this.getName());
      if (astolfoUser != null) {
         return LayerCape.downloadCapeTexture(this, astolfoUser.clientName);
      }

      if (!Config.isShowCapes()) {
         return null;
      }

      if (this.locationOfCape != null) {
         return this.locationOfCape;
      }

      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo == null ? null : networkPlayerInfo.getLocationCape();
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject iTextureObject = textureManager.getTexture(resourceLocationIn);
      if (iTextureObject == null) {
         iTextureObject = new ThreadDownloadImageData(
            (File)null,
            String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)),
            DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)),
            new ImageBufferDownload()
         );
         textureManager.loadTexture(resourceLocationIn, iTextureObject);
      }

      return (ThreadDownloadImageData)iTextureObject;
   }

   public static ResourceLocation getLocationSkin(String username) {
      return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
   }

   public String getSkinType() {
      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkPlayerInfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.capabilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance iAttributeInstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      double d = iAttributeInstance.getAttributeValue();
      if (Astolfo.INSTANCE.getModuleManager().getModule("Speed").isEnabled() && Speed.mode.getValue() == SpeedMode.WATCHDOG) {
         d = 0.15600000414252285;
      }

      f = (float)(f * ((d / this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
         int i = this.getItemInUseDuration();
         float g = i / 20.0F;
         if (g > 1.0F) {
            g = 1.0F;
         } else {
            g *= g;
         }

         f *= 1.0F - g * 0.15F;
      }

      return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, f) : f;
   }

   public String getNameClear() {
      return this.nameClear;
   }

   public ResourceLocation v_ResourceLocation() {
      return this.locationOfCape;
   }

   public void setLocationOfCape(ResourceLocation resourceLocation) {
      this.locationOfCape = resourceLocation;
   }
}
