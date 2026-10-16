package net.minecraft.client.network;

import bypass.module.ModuleManager;
import bypass.module.visuals.StreamerMode;
import bypass.util.Stopwatch;
import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;

public class NetworkPlayerInfo {
   private final GameProfile gameProfile;
   private WorldSettings.GameType gameType;
   private int D;
   private boolean playerTexturesLoaded = false;
   private ResourceLocation locationSkin;
   private ResourceLocation locationCape;
   private String skinType;
   private IChatComponent displayName;
   private int A = 0;
   private int K = 0;
   private long field_178871_k = 0L;
   private long field_178868_l = 0L;
   private long field_178869_m = 0L;
   public Stopwatch lookupTimer = new Stopwatch();
   public int nickedTicks;

   public NetworkPlayerInfo(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }

   public NetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData addPlayerData) {
      this.gameProfile = addPlayerData.getProfile();
      this.gameType = addPlayerData.getGameMode();
      this.D = addPlayerData.getPing();
      this.displayName = addPlayerData.getDisplayName();
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public int getResponseTime() {
      return this.D;
   }

   public void setGameType(WorldSettings.GameType gameType) {
      this.gameType = gameType;
   }

   public void setResponseTime(int i) {
      this.D = i;
   }

   public boolean hasLocationSkin() {
      return this.locationSkin != null;
   }

   public String getSkinType() {
      return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
   }

   public ResourceLocation c() {
      boolean bl = ModuleManager.streamerMode.isEnabled();
      boolean bl2 = bl && StreamerMode.hideSkins.getValue();
      if (this.locationSkin == null) {
         this.loadPlayerTextures();
      }

      return Objects.firstNonNull(bl2 ? null : (!bl ? this.locationSkin : this.locationSkin), DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
   }

   public ResourceLocation getLocationSkin() {
      if (this.locationSkin == null) {
         this.loadPlayerTextures();
      }

      return Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
   }

   public ResourceLocation B() {
      if (this.locationCape == null) {
         this.loadPlayerTextures();
      }

      return this.locationCape;
   }

   public ScorePlayerTeam getPlayerTeam() {
      return Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
   }

   public void loadPlayerTextures() {
      synchronized (this) {
         if (!this.playerTexturesLoaded) {
            this.playerTexturesLoaded = true;
            Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (type, resourceLocation, minecraftProfileTexture) -> {
               switch (type) {
                  case SKIN:
                     this.locationSkin = resourceLocation;
                     this.skinType = minecraftProfileTexture.getMetadata("model");
                     if (this.skinType == null) {
                        this.skinType = "default";
                     }
                     break;
                  case CAPE:
                     this.locationCape = resourceLocation;
               }
            }, true);
         }
      }
   }

   public void R(ResourceLocation resourceLocation) {
      this.locationSkin = resourceLocation;
   }

   public void setDisplayName(IChatComponent displayNameIn) {
      this.displayName = displayNameIn;
   }

   public IChatComponent getDisplayName() {
      return this.displayName;
   }

   public int func_178835_l() {
      return this.A;
   }

   public void func_178836_b(int i) {
      this.A = i;
   }

   public int func_178860_m() {
      return this.K;
   }

   public void func_178857_c(int i) {
      this.K = i;
   }

   public long func_178847_n() {
      return this.field_178871_k;
   }

   public void func_178846_a(long l) {
      this.field_178871_k = l;
   }

   public long func_178858_o() {
      return this.field_178868_l;
   }

   public void func_178844_b(long l) {
      this.field_178868_l = l;
   }

   public long func_178855_p() {
      return this.field_178869_m;
   }

   public void func_178843_c(long l) {
      this.field_178869_m = l;
   }
}
