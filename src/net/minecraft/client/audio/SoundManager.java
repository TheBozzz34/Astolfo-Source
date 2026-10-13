package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
   private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger logger = LogManager.getLogger();
   private final SoundHandler sndHandler;
   private final GameSettings options;
   private SoundManager.SoundSystemStarterThread sndSystem;
   private boolean loaded;
   private int playTime = 0;
   private final Map<String, ISound> playingSounds = HashBiMap.create();
   private final Map<ISound, String> invPlayingSounds = ((BiMap)this.playingSounds).inverse();
   private Map<ISound, SoundPoolEntry> playingSoundPoolEntries = Maps.newHashMap();
   private final Multimap<SoundCategory, String> categorySounds = HashMultimap.create();
   private final List<ITickableSound> tickableSounds = Lists.newArrayList();
   private final Map<ISound, Integer> delayedSounds = Maps.newHashMap();
   private final Map<String, Integer> playingSoundsStopTime = Maps.newHashMap();
   private static final long b = 124998548071178L;
   private static final long j = 1580840688424058900L;

   public SoundManager(SoundHandler var1, GameSettings var2) {
      this.sndHandler = var1;
      this.options = var2;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException var6) {
         logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", var6);
      }
   }

   public void reloadSoundSystem() {
      this.unloadSoundSystem();
      this.loadSoundSystem();
   }

   private synchronized void loadSoundSystem() {
      if (!this.loaded) {
         try {
            new Thread(new Runnable() {
               private static final long a = 64053558088009L;

               @Override
               public void run() {
                  SoundSystemConfig.setLogger(new SoundSystemLogger() {
                     private static final long a = 48435486109883L;

                     @Override
                     public void message(String var1, int var2) {
                        if (!var1.isEmpty()) {
                           SoundManager.logger.info(var1);
                        }
                     }

                     @Override
                     public void importantMessage(String var1, int var2) {
                        if (!var1.isEmpty()) {
                           SoundManager.logger.warn(var1);
                        }
                     }

                     @Override
                     public void errorMessage(String var1, String var2, int var3) {
                        if (!var2.isEmpty()) {
                           SoundManager.logger.error("Error in class '" + var1 + "'");
                           SoundManager.logger.error(var2);
                        }
                     }
                  });
                  SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                  SoundManager.this.loaded = true;
                  SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                  SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
               }
            }, "Sound Library Loader").start();
         } catch (RuntimeException var5) {
            logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", var5);
            this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
            this.options.saveOptions();
         }
      }
   }

   private float getSoundCategoryVolume(SoundCategory var1) {
      return var1 != null && var1 != SoundCategory.MASTER ? this.options.getSoundLevel(var1) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundCategory var1, float var2) {
      if (this.loaded) {
         if (var1 == SoundCategory.MASTER) {
            this.sndSystem.setMasterVolume(var2);
         } else {
            for (String var7 : this.categorySounds.get(var1)) {
               ISound var8 = this.playingSounds.get(var7);
               float var9 = this.getNormalizedVolume(var8, this.playingSoundPoolEntries.get(var8), var1);
               if (var9 <= 0.0F) {
                  this.stopSound(var8);
               } else {
                  this.sndSystem.setVolume(var7, var9);
               }
            }
         }
      }
   }

   public void unloadSoundSystem() {
      if (this.loaded) {
         this.stopAllSounds();
         this.sndSystem.cleanup();
         this.loaded = false;
      }
   }

   public void stopAllSounds() {
      if (this.loaded) {
         for (String var5 : this.playingSounds.keySet()) {
            this.sndSystem.stop(var5);
         }

         this.playingSounds.clear();
         this.delayedSounds.clear();
         this.tickableSounds.clear();
         this.categorySounds.clear();
         this.playingSoundPoolEntries.clear();
         this.playingSoundsStopTime.clear();
      }
   }

   public void updateAllSounds() {
      this.playTime++;

      for (ITickableSound var5 : this.tickableSounds) {
         var5.update();
         if (var5.isDonePlaying()) {
            this.stopSound(var5);
         } else {
            String var6 = this.invPlayingSounds.get(var5);
            this.sndSystem
               .setVolume(
                  var6,
                  this.getNormalizedVolume(var5, this.playingSoundPoolEntries.get(var5), this.sndHandler.getSound(var5.getSoundLocation()).getSoundCategory())
               );
            this.sndSystem.setPitch(var6, this.getNormalizedPitch(var5, this.playingSoundPoolEntries.get(var5)));
            this.sndSystem.setPosition(var6, var5.getXPosF(), var5.getYPosF(), var5.getZPosF());
         }
      }

      Iterator var12 = this.playingSounds.entrySet().iterator();

      while (var12.hasNext()) {
         Entry var13 = (Entry)var12.next();
         String var15 = (String)var13.getKey();
         ISound var7 = (ISound)var13.getValue();
         if (!this.sndSystem.playing(var15)) {
            int var8 = this.playingSoundsStopTime.get(var15);
            if (var8 <= this.playTime) {
               int var9 = var7.getRepeatDelay();
               if (var7.canRepeat() && var9 > 0) {
                  this.delayedSounds.put(var7, this.playTime + var9);
               }

               var12.remove();
               logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", new Object[]{var15});
               this.sndSystem.removeSource(var15);
               this.playingSoundsStopTime.remove(var15);
               this.playingSoundPoolEntries.remove(var7);

               try {
                  this.categorySounds.remove(this.sndHandler.getSound(var7.getSoundLocation()).getSoundCategory(), var15);
               } catch (RuntimeException var11) {
               }

               if (var7 instanceof ITickableSound) {
                  this.tickableSounds.remove(var7);
               }
            }
         }
      }

      Iterator var14 = this.delayedSounds.entrySet().iterator();

      while (var14.hasNext()) {
         Entry var16 = (Entry)var14.next();
         if (this.playTime >= (Integer)var16.getValue()) {
            ISound var17 = (ISound)var16.getKey();
            if (var17 instanceof ITickableSound) {
               ((ITickableSound)var17).update();
            }

            this.playSound(var17);
            var14.remove();
         }
      }
   }

   public boolean isSoundPlaying(ISound var1) {
      if (!this.loaded) {
         return false;
      }

      String var5 = this.invPlayingSounds.get(var1);
      return var5 == null
         ? false
         : this.sndSystem.playing(var5) || this.playingSoundsStopTime.containsKey(var5) && this.playingSoundsStopTime.get(var5) <= this.playTime;
   }

   public void stopSound(ISound var1) {
      if (this.loaded) {
         String var5 = this.invPlayingSounds.get(var1);
         if (var5 != null) {
            this.sndSystem.stop(var5);
         }
      }
   }

   public void playSound(ISound var1) {
      if (this.loaded) {
         if (this.sndSystem.getMasterVolume() <= 0.0F) {
            logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{var1.getSoundLocation()});
         } else {
            SoundEventAccessorComposite var5 = this.sndHandler.getSound(var1.getSoundLocation());
            if (var5 == null) {
               logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", new Object[]{var1.getSoundLocation()});
            } else {
               SoundPoolEntry var6 = var5.cloneEntry();
               if (var6 == SoundHandler.missing_sound) {
                  logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", new Object[]{var5.getSoundEventLocation()});
               } else {
                  float var7 = var1.getVolume();
                  float var8 = 16.0F;
                  if (var7 > 1.0F) {
                     var8 *= var7;
                  }

                  SoundCategory var9 = var5.getSoundCategory();
                  float var10 = this.getNormalizedVolume(var1, var6, var9);
                  double var11 = this.getNormalizedPitch(var1, var6);
                  ResourceLocation var13 = var6.getSoundPoolEntryLocation();
                  if (var10 == 0.0F) {
                     logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{var13});
                  } else {
                     boolean var14 = var1.canRepeat() && var1.getRepeatDelay() == 0;
                     String var15 = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
                     if (var6.isStreamingSound()) {
                        this.sndSystem
                           .newStreamingSource(
                              false,
                              var15,
                              getURLForSoundResource(var13),
                              var13.toString(),
                              var14,
                              var1.getXPosF(),
                              var1.getYPosF(),
                              var1.getZPosF(),
                              var1.getAttenuationType().getTypeInt(),
                              var8
                           );
                     } else {
                        this.sndSystem
                           .newSource(
                              false,
                              var15,
                              getURLForSoundResource(var13),
                              var13.toString(),
                              var14,
                              var1.getXPosF(),
                              var1.getYPosF(),
                              var1.getZPosF(),
                              var1.getAttenuationType().getTypeInt(),
                              var8
                           );
                     }

                     logger.debug(
                        LOG_MARKER, "Playing sound {} for event {} as channel {}", var6.getSoundPoolEntryLocation(), var5.getSoundEventLocation(), var15
                     );
                     this.sndSystem.setPitch(var15, (float)var11);
                     this.sndSystem.setVolume(var15, var10);
                     this.sndSystem.play(var15);
                     this.playingSoundsStopTime.put(var15, this.playTime + (int)j);
                     this.playingSounds.put(var15, var1);
                     this.playingSoundPoolEntries.put(var1, var6);
                     if (var9 != SoundCategory.MASTER) {
                        this.categorySounds.put(var9, var15);
                     }

                     if (var1 instanceof ITickableSound) {
                        this.tickableSounds.add((ITickableSound)var1);
                     }
                  }
               }
            }
         }
      }
   }

   private float getNormalizedPitch(ISound var1, SoundPoolEntry var2) {
      return (float)MathHelper.clamp_double(var1.getPitch() * var2.getPitch(), 0.5, 2.0);
   }

   private float getNormalizedVolume(ISound var1, SoundPoolEntry var2, SoundCategory var3) {
      return (float)MathHelper.clamp_double(var1.getVolume() * var2.getVolume(), 0.0, 1.0) * this.getSoundCategoryVolume(var3);
   }

   public void pauseAllSounds() {
      for (String var5 : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Pausing channel {}", new Object[]{var5});
         this.sndSystem.pause(var5);
      }
   }

   public void resumeAllSounds() {
      for (String var5 : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Resuming channel {}", new Object[]{var5});
         this.sndSystem.play(var5);
      }
   }

   public void playDelayedSound(ISound var1, int var2) {
      this.delayedSounds.put(var1, this.playTime + var2);
   }

   private static URL getURLForSoundResource(final ResourceLocation var0) {
      String var3 = String.format("%s:%s:%s", "mcsounddomain", var0.getResourceDomain(), var0.getResourcePath());
      URLStreamHandler var4 = new URLStreamHandler() {
         @Override
         public URLConnection openConnection(URL var1) {
            return new URLConnection(var1) {
               @Override
               public void connect() throws IOException {
               }

               @Override
               public InputStream getInputStream() throws IOException {
                  return Minecraft.getMinecraft().getResourceManager().getResource(var0).getInputStream();
               }
            };
         }
      };

      try {
         return new URL((URL)null, var3, var4);
      } catch (MalformedURLException var6) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   public void setListener(EntityPlayer var1, float var2) {
      if (this.loaded && var1 != null) {
         float var6 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
         float var7 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2;
         double var8 = var1.prevPosX + (var1.posX - var1.prevPosX) * var2;
         double var10 = var1.prevPosY + (var1.posY - var1.prevPosY) * var2 + var1.getEyeHeight();
         double var12 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * var2;
         float var14 = MathHelper.cos((var7 + 90.0F) * (float) (Math.PI / 180.0));
         float var15 = MathHelper.sin((var7 + 90.0F) * (float) (Math.PI / 180.0));
         float var16 = MathHelper.cos(-var6 * (float) (Math.PI / 180.0));
         float var17 = MathHelper.sin(-var6 * (float) (Math.PI / 180.0));
         float var18 = MathHelper.cos((-var6 + 90.0F) * (float) (Math.PI / 180.0));
         float var19 = MathHelper.sin((-var6 + 90.0F) * (float) (Math.PI / 180.0));
         float var20 = var14 * var16;
         float var21 = var15 * var16;
         float var22 = var14 * var18;
         float var23 = var15 * var18;
         this.sndSystem.setListenerPosition((float)var8, (float)var10, (float)var12);
         this.sndSystem.setListenerOrientation(var20, var17, var21, var22, var19, var23);
      }
   }

   public class SoundSystemStarterThread extends SoundSystem {
      private static final long a = 7367626684981L;

      public SoundSystemStarterThread() {
      }

      @Override
      public boolean playing(String var1) {
         synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            }

            Source var6 = this.soundLibrary.getSources().get(var1);
            return var6 == null ? false : var6.playing() || var6.paused() || var6.preLoad;
         }
      }
   }
}
