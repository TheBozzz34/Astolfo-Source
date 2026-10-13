package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, ITickable {
   private static final Logger logger = LogManager.getLogger();
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      @Override
      public Type getRawType() {
         return Map.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
   private final SoundRegistry sndRegistry = new SoundRegistry();
   private final SoundManager sndManager;
   private final IResourceManager mcResourceManager;
   private static final long a = 104700702354303L;

   public SoundHandler(IResourceManager var1, GameSettings var2) {
      this.mcResourceManager = var1;
      this.sndManager = new SoundManager(this, var2);
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      this.sndManager.reloadSoundSystem();
      this.sndRegistry.clearMap();

      for (String var6 : var1.getResourceDomains()) {
         try {
            for (IResource var8 : var1.getAllResources(new ResourceLocation(var6, "sounds.json"))) {
               try {
                  Map var9 = this.getSoundMap(var8.getInputStream());

                  for (Object var11$raw : var9.entrySet()) {
                     Entry var11 = (Entry)var11$raw;
                     this.loadSoundResource(new ResourceLocation(var6, (String)var11.getKey()), (SoundList)var11.getValue());
                  }
               } catch (RuntimeException var12) {
                  logger.warn("Invalid sounds.json", var12);
               }
            }
         } catch (IOException var13) {
         }
      }
   }

   public Map<String, SoundList> getSoundMap(InputStream var1) {
      Map var2;
      try {
         var2 = GSON.fromJson(new InputStreamReader(var1), TYPE);
      } finally {
         IOUtils.closeQuietly(var1);
      }

      return var2;
   }

   private void loadSoundResource(ResourceLocation var1, SoundList var2) {
      boolean var6 = !this.sndRegistry.containsKey(var1);
      SoundEventAccessorComposite var7;
      if (!var6 && !var2.canReplaceExisting()) {
         var7 = this.sndRegistry.getObject(var1);
      } else {
         if (!var6) {
            logger.debug("Replaced sound event location {}", new Object[]{var1});
         }

         var7 = new SoundEventAccessorComposite(var1, 1.0, 1.0, var2.getSoundCategory());
         this.sndRegistry.registerSound(var7);
      }

      for (final SoundList.SoundEntry var9 : var2.getSoundList()) {
         String var10 = var9.getSoundEntryName();
         ResourceLocation var11 = new ResourceLocation(var10);
         final String var12 = var10.contains(":") ? var11.getResourceDomain() : var1.getResourceDomain();
         ISoundEventAccessor var13;
         switch (var9.getSoundEntryType()) {
            case FILE:
               ResourceLocation var14 = new ResourceLocation(var12, "sounds/" + var11.getResourcePath() + ".ogg");
               InputStream var15 = null;

               try {
                  var15 = this.mcResourceManager.getResource(var14).getInputStream();
               } catch (FileNotFoundException var21) {
                  logger.warn("File {} does not exist, cannot add it to event {}", var14, var1);
                  continue;
               } catch (IOException var22) {
                  logger.warn("Could not load sound file " + var14 + ", cannot add it to event " + var1, var22);
                  continue;
               } finally {
                  IOUtils.closeQuietly(var15);
               }

               var13 = new SoundEventAccessor(new SoundPoolEntry(var14, var9.y(), var9.q(), var9.isStreaming()), var9.getSoundEntryWeight());
               break;
            case SOUND_EVENT:
               var13 = new ISoundEventAccessor<SoundPoolEntry>() {
                  public final ResourceLocation field_148726_a = new ResourceLocation(var12, var9.getSoundEntryName());
                  private static final long a = 122117350247287L;

                  @Override
                  public int getWeight() {
                     SoundEventAccessorComposite var4 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                     return var4 == null ? 0 : var4.getWeight();
                  }

                  public SoundPoolEntry cloneEntry() {
                     SoundEventAccessorComposite var4 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                     return var4 == null ? SoundHandler.missing_sound : var4.cloneEntry();
                  }
               };
               break;
            default:
               throw new IllegalStateException("IN YOU FACE");
         }

         var7.addSoundToEventPool(var13);
      }
   }

   public SoundEventAccessorComposite getSound(ResourceLocation var1) {
      return this.sndRegistry.getObject(var1);
   }

   public void playSound(ISound var1) {
      this.sndManager.playSound(var1);
   }

   public void playDelayedSound(ISound var1, int var2) {
      this.sndManager.playDelayedSound(var1, var2);
   }

   public void setListener(EntityPlayer var1, float var2) {
      this.sndManager.setListener(var1, var2);
   }

   public void L() {
      this.sndManager.pauseAllSounds();
   }

   public void stopSounds() {
      this.sndManager.stopAllSounds();
   }

   public void y() {
      this.sndManager.unloadSoundSystem();
   }

   @Override
   public void update() {
      this.sndManager.updateAllSounds();
   }

   public void resumeSounds() {
      this.sndManager.resumeAllSounds();
   }

   public void setSoundLevel(SoundCategory var1, float var2) {
      if (var1 == SoundCategory.MASTER && var2 <= 0.0F) {
         this.stopSounds();
      }

      this.sndManager.setSoundCategoryVolume(var1, var2);
   }

   public void stopSound(ISound var1) {
      this.sndManager.stopSound(var1);
   }

   public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... var1) {
      ArrayList var5 = Lists.newArrayList();

      for (ResourceLocation var7 : this.sndRegistry.getKeys()) {
         SoundEventAccessorComposite var8 = this.sndRegistry.getObject(var7);
         if (ArrayUtils.contains(var1, var8.getSoundCategory())) {
            var5.add(var8);
         }
      }

      return var5.isEmpty() ? null : (SoundEventAccessorComposite)var5.get(new Random().nextInt(var5.size()));
   }

   public boolean isSoundPlaying(ISound var1) {
      return this.sndManager.isSoundPlaying(var1);
   }
}
