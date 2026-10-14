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

   public SoundManager(SoundHandler soundHandler, GameSettings gameSettings) {
      this.sndHandler = soundHandler;
      this.options = gameSettings;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException soundSystemException) {
         logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", soundSystemException);
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
               @Override
               public void run() {
                  SoundSystemConfig.setLogger(new SoundSystemLogger() {
                     @Override
                     public void message(String string, int i) {
                        if (!string.isEmpty()) {
                           SoundManager.logger.info(string);
                        }
                     }

                     @Override
                     public void importantMessage(String string, int i) {
                        if (!string.isEmpty()) {
                           SoundManager.logger.warn(string);
                        }
                     }

                     @Override
                     public void errorMessage(String string, String string2, int i) {
                        if (!string2.isEmpty()) {
                           SoundManager.logger.error("Error in class '" + string + "'");
                           SoundManager.logger.error(string2);
                        }
                     }
                  });
                  SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                  SoundManager.this.loaded = true;
                  SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                  SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
               }
            }, "Sound Library Loader").start();
         } catch (RuntimeException runtimeException) {
            logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", runtimeException);
            this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
            this.options.saveOptions();
         }
      }
   }

   private float getSoundCategoryVolume(SoundCategory category) {
      return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundCategory category, float volume) {
      if (this.loaded) {
         if (category == SoundCategory.MASTER) {
            this.sndSystem.setMasterVolume(volume);
         } else {
            for (String string : this.categorySounds.get(category)) {
               ISound iSound = this.playingSounds.get(string);
               float f = this.getNormalizedVolume(iSound, this.playingSoundPoolEntries.get(iSound), category);
               if (f <= 0.0F) {
                  this.stopSound(iSound);
               } else {
                  this.sndSystem.setVolume(string, f);
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
         for (String string : this.playingSounds.keySet()) {
            this.sndSystem.stop(string);
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

      for (ITickableSound iTickableSound : this.tickableSounds) {
         iTickableSound.update();
         if (iTickableSound.isDonePlaying()) {
            this.stopSound(iTickableSound);
         } else {
            String string = this.invPlayingSounds.get(iTickableSound);
            this.sndSystem
               .setVolume(
                  string,
                  this.getNormalizedVolume(iTickableSound, this.playingSoundPoolEntries.get(iTickableSound), this.sndHandler.getSound(iTickableSound.getSoundLocation()).getSoundCategory())
               );
            this.sndSystem.setPitch(string, this.getNormalizedPitch(iTickableSound, this.playingSoundPoolEntries.get(iTickableSound)));
            this.sndSystem.setPosition(string, iTickableSound.getXPosF(), iTickableSound.getYPosF(), iTickableSound.getZPosF());
         }
      }

      Iterator iterator2 = this.playingSounds.entrySet().iterator();

      while (iterator2.hasNext()) {
         Entry entry = (Entry)iterator2.next();
         String string2 = (String)entry.getKey();
         ISound iSound = (ISound)entry.getValue();
         if (!this.sndSystem.playing(string2)) {
            int i = this.playingSoundsStopTime.get(string2);
            if (i <= this.playTime) {
               int j = iSound.getRepeatDelay();
               if (iSound.canRepeat() && j > 0) {
                  this.delayedSounds.put(iSound, this.playTime + j);
               }

               iterator2.remove();
               logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", new Object[]{string2});
               this.sndSystem.removeSource(string2);
               this.playingSoundsStopTime.remove(string2);
               this.playingSoundPoolEntries.remove(iSound);

               try {
                  this.categorySounds.remove(this.sndHandler.getSound(iSound.getSoundLocation()).getSoundCategory(), string2);
               } catch (RuntimeException runtimeException) {
               }

               if (iSound instanceof ITickableSound) {
                  this.tickableSounds.remove(iSound);
               }
            }
         }
      }

      Iterator iterator3 = this.delayedSounds.entrySet().iterator();

      while (iterator3.hasNext()) {
         Entry entry2 = (Entry)iterator3.next();
         if (this.playTime >= (Integer)entry2.getValue()) {
            ISound iSound2 = (ISound)entry2.getKey();
            if (iSound2 instanceof ITickableSound) {
               ((ITickableSound)iSound2).update();
            }

            this.playSound(iSound2);
            iterator3.remove();
         }
      }
   }

   public boolean isSoundPlaying(ISound sound) {
      if (!this.loaded) {
         return false;
      }

      String string = this.invPlayingSounds.get(sound);
      return string == null
         ? false
         : this.sndSystem.playing(string) || this.playingSoundsStopTime.containsKey(string) && this.playingSoundsStopTime.get(string) <= this.playTime;
   }

   public void stopSound(ISound sound) {
      if (this.loaded) {
         String string = this.invPlayingSounds.get(sound);
         if (string != null) {
            this.sndSystem.stop(string);
         }
      }
   }

   public void playSound(ISound p_sound) {
      if (this.loaded) {
         if (this.sndSystem.getMasterVolume() <= 0.0F) {
            logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{p_sound.getSoundLocation()});
         } else {
            SoundEventAccessorComposite soundEventAccessorComposite = this.sndHandler.getSound(p_sound.getSoundLocation());
            if (soundEventAccessorComposite == null) {
               logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", new Object[]{p_sound.getSoundLocation()});
            } else {
               SoundPoolEntry soundPoolEntry = soundEventAccessorComposite.cloneEntry();
               if (soundPoolEntry == SoundHandler.missing_sound) {
                  logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", new Object[]{soundEventAccessorComposite.getSoundEventLocation()});
               } else {
                  float f = p_sound.getVolume();
                  float g = 16.0F;
                  if (f > 1.0F) {
                     g *= f;
                  }

                  SoundCategory soundCategory = soundEventAccessorComposite.getSoundCategory();
                  float h = this.getNormalizedVolume(p_sound, soundPoolEntry, soundCategory);
                  double d = this.getNormalizedPitch(p_sound, soundPoolEntry);
                  ResourceLocation resourceLocation = soundPoolEntry.getSoundPoolEntryLocation();
                  if (h == 0.0F) {
                     logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{resourceLocation});
                  } else {
                     boolean bl = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;
                     String string = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
                     if (soundPoolEntry.isStreamingSound()) {
                        this.sndSystem
                           .newStreamingSource(
                              false,
                              string,
                              getURLForSoundResource(resourceLocation),
                              resourceLocation.toString(),
                              bl,
                              p_sound.getXPosF(),
                              p_sound.getYPosF(),
                              p_sound.getZPosF(),
                              p_sound.getAttenuationType().getTypeInt(),
                              g
                           );
                     } else {
                        this.sndSystem
                           .newSource(
                              false,
                              string,
                              getURLForSoundResource(resourceLocation),
                              resourceLocation.toString(),
                              bl,
                              p_sound.getXPosF(),
                              p_sound.getYPosF(),
                              p_sound.getZPosF(),
                              p_sound.getAttenuationType().getTypeInt(),
                              g
                           );
                     }

                     logger.debug(
                        LOG_MARKER, "Playing sound {} for event {} as channel {}", soundPoolEntry.getSoundPoolEntryLocation(), soundEventAccessorComposite.getSoundEventLocation(), string
                     );
                     this.sndSystem.setPitch(string, (float)d);
                     this.sndSystem.setVolume(string, h);
                     this.sndSystem.play(string);
                     this.playingSoundsStopTime.put(string, this.playTime + 20);
                     this.playingSounds.put(string, p_sound);
                     this.playingSoundPoolEntries.put(p_sound, soundPoolEntry);
                     if (soundCategory != SoundCategory.MASTER) {
                        this.categorySounds.put(soundCategory, string);
                     }

                     if (p_sound instanceof ITickableSound) {
                        this.tickableSounds.add((ITickableSound)p_sound);
                     }
                  }
               }
            }
         }
      }
   }

   private float getNormalizedPitch(ISound sound, SoundPoolEntry entry) {
      return (float)MathHelper.clamp_double(sound.getPitch() * entry.getPitch(), 0.5, 2.0);
   }

   private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category) {
      return (float)MathHelper.clamp_double(sound.getVolume() * entry.getVolume(), 0.0, 1.0) * this.getSoundCategoryVolume(category);
   }

   public void pauseAllSounds() {
      for (String string : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Pausing channel {}", new Object[]{string});
         this.sndSystem.pause(string);
      }
   }

   public void resumeAllSounds() {
      for (String string : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Resuming channel {}", new Object[]{string});
         this.sndSystem.play(string);
      }
   }

   public void playDelayedSound(ISound sound, int delay) {
      this.delayedSounds.put(sound, this.playTime + delay);
   }

   private static URL getURLForSoundResource(final ResourceLocation resourceLocation) {
      String string = String.format("%s:%s:%s", "mcsounddomain", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
      URLStreamHandler urlStreamHandler = new URLStreamHandler() {
         @Override
         public URLConnection openConnection(URL url) {
            return new URLConnection(url) {
               @Override
               public void connect() throws IOException {
               }

               @Override
               public InputStream getInputStream() throws IOException {
                  return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
               }
            };
         }
      };

      try {
         return new URL((URL)null, string, urlStreamHandler);
      } catch (MalformedURLException malformedURLException) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   public void setListener(EntityPlayer player, float f) {
      if (this.loaded && player != null) {
         float g = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
         float h = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
         double d = player.prevPosX + (player.posX - player.prevPosX) * f;
         double e = player.prevPosY + (player.posY - player.prevPosY) * f + player.getEyeHeight();
         double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
         float f2 = MathHelper.cos((h + 90.0F) * (float) (Math.PI / 180.0));
         float f3 = MathHelper.sin((h + 90.0F) * (float) (Math.PI / 180.0));
         float f4 = MathHelper.cos(-g * (float) (Math.PI / 180.0));
         float f5 = MathHelper.sin(-g * (float) (Math.PI / 180.0));
         float f6 = MathHelper.cos((-g + 90.0F) * (float) (Math.PI / 180.0));
         float f7 = MathHelper.sin((-g + 90.0F) * (float) (Math.PI / 180.0));
         float f8 = f2 * f4;
         float f9 = f3 * f4;
         float f10 = f2 * f6;
         float f11 = f3 * f6;
         this.sndSystem.setListenerPosition((float)d, (float)e, (float)d2);
         this.sndSystem.setListenerOrientation(f8, f5, f9, f10, f7, f11);
      }
   }

   public class SoundSystemStarterThread extends SoundSystem {
      public SoundSystemStarterThread() {
      }

      @Override
      public boolean playing(String string) {
         synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            }

            Source source = this.soundLibrary.getSources().get(string);
            return source == null ? false : source.playing() || source.paused() || source.preLoad;
         }
      }
   }
}
