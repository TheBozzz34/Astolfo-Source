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

   public TextureManager(IResourceManager resourceManager) {
      this.theResourceManager = resourceManager;
   }

   public void bindTexture(ResourceLocation resource) {
      if (Config.isRandomMobs()) {
         resource = RandomMobs.getTextureLocation(resource);
      }

      ITextureObject iTextureObject = this.mapTextureObjects.get(resource);
      if (iTextureObject == null) {
         iTextureObject = new SimpleTexture(resource);
         this.loadTexture(resource, iTextureObject);
      }

      if (Config.isShaders()) {
         ShadersTex.bindTexture(iTextureObject);
      } else {
         TextureUtil.bindTexture(iTextureObject.getGlTextureId());
      }
   }

   public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj) {
      if (this.loadTexture(textureLocation, textureObj)) {
         this.listTickables.add(textureObj);
         return true;
      } else {
         return false;
      }
   }

   public boolean loadTexture(ResourceLocation textureLocation, final ITextureObject textureObj) {
      boolean bl = true;
      ITextureObject iTextureObject = textureObj;

      try {
         textureObj.loadTexture(this.theResourceManager);
      } catch (IOException ioException) {
         logger.warn("Failed to load texture: " + textureLocation, ioException);
         iTextureObject = TextureUtil.missingTexture;
         this.mapTextureObjects.put(textureLocation, iTextureObject);
         bl = false;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Registering texture");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Resource location being registered");
         crashReportCategory.addCrashSection("Resource location", textureLocation);
         crashReportCategory.addCrashSectionCallable("Texture object class", new Callable<String>() {
            public String call() throws Exception {
               return textureObj.getClass().getName();
            }
         });
         throw new ReportedException(crashReport);
      }

      this.mapTextureObjects.put(textureLocation, iTextureObject);
      return bl;
   }

   public ITextureObject getTexture(ResourceLocation textureLocation) {
      return this.mapTextureObjects.get(textureLocation);
   }

   public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
      if (name.equals("logo")) {
         texture = Config.getMojangLogoTexture(texture);
      }

      Integer integer = this.mapTextureCounters.get(name);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(name, integer);
      ResourceLocation resourceLocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
      this.loadTexture(resourceLocation, texture);
      return resourceLocation;
   }

   @Override
   public void tick() {
      for (Object object : this.listTickables) {
         ((ITickable)object).tick();
      }
   }

   public void deleteTexture(ResourceLocation textureLocation) {
      ITextureObject iTextureObject = this.getTexture(textureLocation);
      if (iTextureObject != null) {
         this.mapTextureObjects.remove(textureLocation);
         TextureUtil.deleteTexture(iTextureObject.getGlTextureId());
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      Config.dbg("*** Reloading textures ***");
      Config.log("Resource packs: " + Config.getResourcePackNames());
      Iterator iterator2 = this.mapTextureObjects.keySet().iterator();

      while (iterator2.hasNext()) {
         ResourceLocation resourceLocation = (ResourceLocation)iterator2.next();
         String string = resourceLocation.getResourcePath();
         if (string.startsWith("mcpatcher/") || string.startsWith("optifine/")) {
            ITextureObject iTextureObject = this.mapTextureObjects.get(resourceLocation);
            if (iTextureObject instanceof AbstractTexture) {
               AbstractTexture abstractTexture = (AbstractTexture)iTextureObject;
               abstractTexture.deleteGlTexture();
            }

            iterator2.remove();
         }
      }

      for (Object object : this.mapTextureObjects.entrySet()) {
         this.loadTexture((ResourceLocation)((Entry)object).getKey(), (ITextureObject)((Entry)object).getValue());
      }
   }

   public void reloadBannerTextures() {
      for (Object object : this.mapTextureObjects.entrySet()) {
         ResourceLocation resourceLocation = (ResourceLocation)((Entry)object).getKey();
         ITextureObject iTextureObject = (ITextureObject)((Entry)object).getValue();
         if (iTextureObject instanceof LayeredColorMaskTexture) {
            this.loadTexture(resourceLocation, iTextureObject);
         }
      }
   }
}
