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
   public static final ResourceLocation SF = new ResourceLocation("client/normalCape.png");
   private static final long ab = 52656721845962L;

   public AbstractClientPlayer(World var1, GameProfile var2) {
      super(var1, var2);
      this.nameClear = var2.getName();
      if (this.nameClear != null && !this.nameClear.isEmpty()) {
         this.nameClear = StringUtils.stripControlCodes(this.nameClear);
      }

      CapeUtils.downloadCape(this);
      PlayerConfigurations.getPlayerConfiguration(this);
   }

   @Override
   public boolean isSpectator() {
      NetworkPlayerInfo var4 = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
      return var4 != null && var4.getGameType() == WorldSettings.GameType.SPECTATOR;
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
      NetworkPlayerInfo var4 = this.getPlayerInfo();
      return var4 != null && var4.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo var4 = this.getPlayerInfo();
      return var4 == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : var4.c();
   }

   public ResourceLocation u() {
      long var1 = ab ^ 8444913071634L;
      long var3 = var1 ^ 85862798436386L;
      AstolfoUser var6 = Astolfo.INSTANCE.getUserManager().g(this.getName());
      if (var6 != null) {
         return LayerCape.h(this, var6.s);
      }

      if (!Config.b()) {
         return null;
      }

      if (this.locationOfCape != null) {
         return this.locationOfCape;
      }

      NetworkPlayerInfo var7 = this.getPlayerInfo();
      return var7 == null ? null : var7.B();
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation var0, String var1) {
      TextureManager var5 = Minecraft.getMinecraft().getTextureManager();
      ITextureObject var6 = var5.getTexture(var0);
      if (var6 == null) {
         var6 = new ThreadDownloadImageData(
            (File)null,
            String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(var1)),
            DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(var1)),
            new ImageBufferDownload()
         );
         var5.loadTexture(var0, var6);
      }

      return (ThreadDownloadImageData)var6;
   }

   public static ResourceLocation getLocationSkin(String var0) {
      return new ResourceLocation("skins/" + StringUtils.stripControlCodes(var0));
   }

   public String getSkinType() {
      NetworkPlayerInfo var4 = this.getPlayerInfo();
      return var4 == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : var4.getSkinType();
   }

   public float getFovModifier() {
      long var1 = ab ^ 50534433803192L;
      long var3 = var1 ^ 58712942438593L;
      float var6 = 1.0F;
      if (this.capabilities.isFlying) {
         var6 *= 1.1F;
      }

      IAttributeInstance var7 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      double var8 = var7.getAttributeValue();
      if (Astolfo.INSTANCE.getModuleManager().getModule("Speed").isEnabled() && Speed.mode.getValue() == SpeedMode.WATCHDOG) {
         var8 = 0.15600000414252285;
      }

      var6 = (float)(var6 * ((var8 / this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(var6) || Float.isInfinite(var6)) {
         var6 = 1.0F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
         int var10 = this.n();
         float var11 = var10 / 20.0F;
         if (var11 > 1.0F) {
            var11 = 1.0F;
         } else {
            var11 *= var11;
         }

         var6 *= 1.0F - var11 * 0.15F;
      }

      return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, var6) : var6;
   }

   public String getNameClear() {
      return this.nameClear;
   }

   public ResourceLocation v_ResourceLocation() {
      return this.locationOfCape;
   }

   public void setLocationOfCape(ResourceLocation var1) {
      this.locationOfCape = var1;
   }
}
