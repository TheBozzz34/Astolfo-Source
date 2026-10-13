package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
   private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   public final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;
   private static final long a = 7260984002604L;

   public SkinManager(TextureManager var1, File var2, MinecraftSessionService var3) {
      this.textureManager = var1;
      this.skinCacheDir = var2;
      this.sessionService = var3;
      this.skinCacheLoader = CacheBuilder.newBuilder()
         .expireAfterAccess(15L, TimeUnit.SECONDS)
         .build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
            public Map<Type, MinecraftProfileTexture> load(GameProfile var1) throws Exception {
               return Minecraft.getMinecraft().getSessionService().getTextures(var1, false);
            }
         });
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture var1, Type var2) {
      return this.loadSkin(var1, var2, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation loadSkin(final MinecraftProfileTexture var1, final Type var2, final SkinManager.SkinAvailableCallback var3) {
      final ResourceLocation var7 = new ResourceLocation("skins/" + var1.getHash());
      ITextureObject var8 = this.textureManager.getTexture(var7);
      if (var8 != null) {
         if (var3 != null) {
            var3.skinAvailable(var2, var7, var1);
         }
      } else {
         File var9 = new File(this.skinCacheDir, var1.getHash().length() > 2 ? var1.getHash().substring(0, 2) : "xx");
         File var10 = new File(var9, var1.getHash());
         final ImageBufferDownload var11 = var2 == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData var12 = new ThreadDownloadImageData(var10, var1.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
            private static final long a = 4382979425302L;

            @Override
            public BufferedImage parseUserSkin(BufferedImage var1x) {
               if (var11 != null) {
                  var1x = var11.parseUserSkin(var1x);
               }

               return var1x;
            }

            @Override
            public void skinAvailable() {
               if (var11 != null) {
                  var11.skinAvailable();
               }

               if (var3 != null) {
                  var3.skinAvailable(var2, var7, var1);
               }
            }
         });
         this.textureManager.loadTexture(var7, var12);
      }

      return var7;
   }

   public void loadProfileTextures(final GameProfile var1, final SkinManager.SkinAvailableCallback var2, final boolean var3) {
      THREAD_POOL.submit(new Runnable() {
         private static final long a = 130611592261724L;

         @Override
         public void run() {
            final HashMap var4 = Maps.newHashMap();

            try {
               var4.putAll(SkinManager.this.sessionService.getTextures(var1, var3));
            } catch (InsecureTextureException var6) {
            }

            if (var4.isEmpty() && var1.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
               var1.getProperties().clear();
               var1.getProperties().putAll(Minecraft.getMinecraft().getProfileProperties());
               var4.putAll(SkinManager.this.sessionService.getTextures(var1, false));
            }

            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
               private static final long a = 6518631476421L;

               @Override
               public void run() {
                  if (var4.containsKey(Type.SKIN)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN, var2);
                  }

                  if (var4.containsKey(Type.CAPE)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)var4.get(Type.CAPE), Type.CAPE, var2);
                  }
               }
            });
         }
      });
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile var1) {
      return this.skinCacheLoader.getUnchecked(var1);
   }

   public interface SkinAvailableCallback {
      void skinAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
