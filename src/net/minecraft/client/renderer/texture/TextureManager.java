package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.src.RandomMobs;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class TextureManager implements ITickable, IResourceManagerReloadListener {
   private static final Logger logger = LogManager.getLogger();
   private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
   private final List<ITickable> listTickables = Lists.newArrayList();
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private IResourceManager theResourceManager;
   private static final long a = 103274445130813L;

   public TextureManager(IResourceManager var1) {
      this.theResourceManager = var1;
   }

   public void bindTexture(ResourceLocation var1) {
      if (Config.isRandomMobs()) {
         var1 = RandomMobs.getTextureLocation(var1);
      }

      ITextureObject var5 = this.mapTextureObjects.get(var1);
      if (var5 == null) {
         var5 = new SimpleTexture(var1);
         this.loadTexture(var1, var5);
      }

      if (Config.isShaders()) {
         ShadersTex.bindTexture(var5);
      } else {
         TextureUtil.bindTexture(var5.getGlTextureId());
      }
   }

   public boolean loadTickableTexture(ResourceLocation var1, ITickableTextureObject var2) {
      if (this.loadTexture(var1, var2)) {
         this.listTickables.add(var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean loadTexture(ResourceLocation var1, final ITextureObject var2) {
      boolean var5 = true;
      ITextureObject var6 = var2;

      try {
         var2.loadTexture(this.theResourceManager);
      } catch (IOException var10) {
         logger.warn("Failed to load texture: " + var1, var10);
         var6 = TextureUtil.missingTexture;
         this.mapTextureObjects.put(var1, var6);
         var5 = false;
      } catch (Throwable var11) {
         CrashReport var8 = CrashReport.makeCrashReport(var11, "Registering texture");
         CrashReportCategory var9 = var8.makeCategory("Resource location being registered");
         var9.addCrashSection("Resource location", var1);
         var9.addCrashSectionCallable("Texture object class", new Callable<String>() {
            public String call() throws Exception {
               return var2.getClass().getName();
            }
         });
         throw new ReportedException(var8);
      }

      this.mapTextureObjects.put(var1, var6);
      return var5;
   }

   public ITextureObject getTexture(ResourceLocation var1) {
      return this.mapTextureObjects.get(var1);
   }

   public ResourceLocation getDynamicTextureLocation(String var1, DynamicTexture var2) {
      if (var1.equals("logo")) {
         var2 = Config.getMojangLogoTexture(var2);
      }

      Integer var5 = this.mapTextureCounters.get(var1);
      if (var5 == null) {
         var5 = 1;
      } else {
         var5 = var5 + 1;
      }

      this.mapTextureCounters.put(var1, var5);
      ResourceLocation var6 = new ResourceLocation(String.format("dynamic/%s_%d", var1, var5));
      this.loadTexture(var6, var2);
      return var6;
   }

   @Override
   public void tick() {
      for (Object var5 : this.listTickables) {
         ((ITickable)var5).tick();
      }
   }

   public void deleteTexture(ResourceLocation var1) {
      ITextureObject var5 = this.getTexture(var1);
      if (var5 != null) {
         this.mapTextureObjects.remove(var1);
         TextureUtil.deleteTexture(var5.getGlTextureId());
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      Config.dbg("*** Reloading textures ***");
      Config.log("Resource packs: " + Config.getResourcePackNames());
      Iterator var5 = this.mapTextureObjects.keySet().iterator();

      while (var5.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var5.next();
         String var7 = var6.getResourcePath();
         if (var7.startsWith("mcpatcher/") || var7.startsWith("optifine/")) {
            ITextureObject var8 = this.mapTextureObjects.get(var6);
            if (var8 instanceof AbstractTexture) {
               AbstractTexture var9 = (AbstractTexture)var8;
               var9.deleteGlTexture();
            }

            var5.remove();
         }
      }

      for (Object var11 : this.mapTextureObjects.entrySet()) {
         this.loadTexture((ResourceLocation)((Entry)var11).getKey(), (ITextureObject)((Entry)var11).getValue());
      }
   }

   public void reloadBannerTextures() {
      for (Object var5 : this.mapTextureObjects.entrySet()) {
         ResourceLocation var6 = (ResourceLocation)((Entry)var5).getKey();
         ITextureObject var7 = (ITextureObject)((Entry)var5).getValue();
         if (var7 instanceof LayeredColorMaskTexture) {
            this.loadTexture(var6, var7);
         }
      }
   }
}
