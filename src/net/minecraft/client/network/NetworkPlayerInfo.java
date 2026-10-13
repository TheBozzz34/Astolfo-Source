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

   public NetworkPlayerInfo(GameProfile var1) {
      this.gameProfile = var1;
   }

   public NetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData var1) {
      this.gameProfile = var1.getProfile();
      this.gameType = var1.getGameMode();
      this.D = var1.getPing();
      this.displayName = var1.getDisplayName();
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

   public void setGameType(WorldSettings.GameType var1) {
      this.gameType = var1;
   }

   public void setResponseTime(int var1) {
      this.D = var1;
   }

   public boolean hasLocationSkin() {
      return this.locationSkin != null;
   }

   public String getSkinType() {
      return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
   }

   public ResourceLocation c() {
      boolean var1 = ModuleManager.G.isEnabled();
      boolean var2 = var1 && StreamerMode.hideSkins.getValue();
      if (this.locationSkin == null) {
         this.loadPlayerTextures();
      }

      return Objects.firstNonNull(var2 ? null : (!var1 ? this.locationSkin : this.locationSkin), DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
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
            Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (var1, var2, var3) -> {
               switch (var1) {
                  case SKIN:
                     this.locationSkin = var2;
                     this.skinType = var3.getMetadata("model");
                     if (this.skinType == null) {
                        this.skinType = "default";
                     }
                     break;
                  case CAPE:
                     this.locationCape = var2;
               }
            }, true);
         }
      }
   }

   public void R(ResourceLocation var1) {
      this.locationSkin = var1;
   }

   public void setDisplayName(IChatComponent var1) {
      this.displayName = var1;
   }

   public IChatComponent getDisplayName() {
      return this.displayName;
   }

   public int func_178835_l() {
      return this.A;
   }

   public void func_178836_b(int var1) {
      this.A = var1;
   }

   public int func_178860_m() {
      return this.K;
   }

   public void func_178857_c(int var1) {
      this.K = var1;
   }

   public long func_178847_n() {
      return this.field_178871_k;
   }

   public void func_178846_a(long var1) {
      this.field_178871_k = var1;
   }

   public long func_178858_o() {
      return this.field_178868_l;
   }

   public void func_178844_b(long var1) {
      this.field_178868_l = var1;
   }

   public long func_178855_p() {
      return this.field_178869_m;
   }

   public void func_178843_c(long var1) {
      this.field_178869_m = var1;
   }
}
