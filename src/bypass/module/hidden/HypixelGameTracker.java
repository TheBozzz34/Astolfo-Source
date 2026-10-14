package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.HiddenModule;
import bypass.util.GameUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public final class HypixelGameTracker extends HiddenModule {
   private boolean unusedFlag;
   private boolean unusedFlag2;
   public static int game;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGHEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         if (GameUtil.isInHypixelGame()) {
            String string = GameUtil.getScoreboardTitle();
            if (string.contains("PIT")) {
               game = 5;
            } else if (string.contains("BED WARS")) {
               game = 2;
            } else if (string.contains("SKYWARS")) {
               game = 1;
            } else if (string.contains("UHC CHAMPIONS") || string.contains("HYPIXEL UHC")) {
               game = 3;
            } else if (string.contains("MEGA WALLS")) {
               game = 4;
            } else if (!string.contains("HYPIXEL")) {
               game = 0;
            }
         } else {
            game = -1;
         }
      }
   };
   private static final ImmutableMap<Integer, PotionEffect> POTIONS_BY_COLOR = new Builder<Integer, PotionEffect>()
      .put(Potion.potionTypes[1].getLiquidColor(), new PotionEffect(Potion.potionTypes[1]))
      .put(Potion.potionTypes[2].getLiquidColor(), new PotionEffect(Potion.potionTypes[2]))
      .put(Potion.potionTypes[3].getLiquidColor(), new PotionEffect(Potion.potionTypes[3]))
      .put(Potion.potionTypes[4].getLiquidColor(), new PotionEffect(Potion.potionTypes[4]))
      .put(Potion.potionTypes[5].getLiquidColor(), new PotionEffect(Potion.potionTypes[5]))
      .put(Potion.potionTypes[8].getLiquidColor(), new PotionEffect(Potion.potionTypes[8]))
      .put(Potion.potionTypes[9].getLiquidColor(), new PotionEffect(Potion.potionTypes[9]))
      .put(Potion.potionTypes[10].getLiquidColor(), new PotionEffect(Potion.potionTypes[10]))
      .put(Potion.potionTypes[11].getLiquidColor(), new PotionEffect(Potion.potionTypes[11]))
      .put(Potion.potionTypes[12].getLiquidColor(), new PotionEffect(Potion.potionTypes[12]))
      .put(Potion.potionTypes[13].getLiquidColor(), new PotionEffect(Potion.potionTypes[13]))
      .put(Potion.potionTypes[14].getLiquidColor(), new PotionEffect(Potion.potionTypes[14]))
      .put(Potion.potionTypes[15].getLiquidColor(), new PotionEffect(Potion.potionTypes[15]))
      .put(Potion.potionTypes[16].getLiquidColor(), new PotionEffect(Potion.potionTypes[16]))
      .put(Potion.potionTypes[17].getLiquidColor(), new PotionEffect(Potion.potionTypes[17]))
      .put(Potion.potionTypes[18].getLiquidColor(), new PotionEffect(Potion.potionTypes[18]))
      .put(Potion.potionTypes[19].getLiquidColor(), new PotionEffect(Potion.potionTypes[19]))
      .put(Potion.potionTypes[20].getLiquidColor(), new PotionEffect(Potion.potionTypes[20]))
      .put(Potion.potionTypes[21].getLiquidColor(), new PotionEffect(Potion.potionTypes[21]))
      .put(Potion.potionTypes[22].getLiquidColor(), new PotionEffect(Potion.potionTypes[22]))
      .build();
   public static Map<EntityPlayer, PlayerPotionEffects> playerEffects = Maps.newHashMap();
   private static final List<PotionEffect> GOLDEN_APPLE_EFFECTS = Arrays.asList(new PotionEffect(10, 0, 1), new PotionEffect(Potion.potionTypes[22]));
   private static final List<PotionEffect> NOTCH_APPLE_EFFECTS = Arrays.asList(
      new PotionEffect(10, 0, 1), new PotionEffect(22, 0, 3), new PotionEffect(Potion.potionTypes[12]), new PotionEffect(Potion.potionTypes[11])
   );
   private static final List<PotionEffect> unusedEffects = Arrays.asList(new PotionEffect(11, 0, 1), new PotionEffect(Potion.potionTypes[12]));
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> playerEffects.clear();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (mc.theWorld == null) {
            return;
         }

         if (mc.thePlayer == null) {
            return;
         }

         if (packetEvent.getPacket() instanceof S1CPacketEntityMetadata) {
            S1CPacketEntityMetadata s1CPacketEntityMetadata = (S1CPacketEntityMetadata)packetEvent.getPacket();
            if (s1CPacketEntityMetadata.func_149376_c() == null) {
               return;
            }

            for (DataWatcher.WatchableObject watchableObject : s1CPacketEntityMetadata.func_149376_c()) {
               if (watchableObject != null) {
                  Entity entity = mc.theWorld.getEntityByID(s1CPacketEntityMetadata.getEntityId());
                  if (watchableObject.getDataValueId() == 7 && entity instanceof EntityOtherPlayerMP) {
                     EntityOtherPlayerMP entityOtherPlayerMP = (EntityOtherPlayerMP)entity;
                     int i = Integer.parseInt(watchableObject.getObject().toString());
                     PlayerPotionEffects playerPotionEffects = playerEffects.computeIfAbsent(entityOtherPlayerMP, entityPlayer -> new PlayerPotionEffects(entityOtherPlayerMP));
                     if (i == 0) {
                        playerPotionEffects.clear();
                     } else if (playerPotionEffects.color == 0 && playerPotionEffects.effects.isEmpty()) {
                        playerPotionEffects.color = i;
                        if (matchesColor(i, GOLDEN_APPLE_EFFECTS)) {
                           playerPotionEffects.set(GOLDEN_APPLE_EFFECTS);
                        } else if (matchesColor(i, NOTCH_APPLE_EFFECTS)) {
                           playerPotionEffects.set(NOTCH_APPLE_EFFECTS);
                        } else {
                           int[] ints = new int[]{0, 1, -1, 5, -5, 16, -16, 256, -256, 65536, -65536};

                           for (int j : ints) {
                              if (POTIONS_BY_COLOR.containsKey(i + j)) {
                                 playerPotionEffects.add(POTIONS_BY_COLOR.get(i + j));
                              }
                           }
                        }
                     } else {
                        Optional optional = this.findRemovedEffect(playerEffects.get(entityOtherPlayerMP).effects.values(), i);
                        if (optional.isPresent() && !this.findAddedEffects(playerEffects.get(entityOtherPlayerMP).effects.values(), i, false).isPresent()) {
                           playerEffects.get(entityOtherPlayerMP).remove((PotionEffect)optional.get());
                        } else if (matchesColor(i, GOLDEN_APPLE_EFFECTS)) {
                           playerPotionEffects.set(GOLDEN_APPLE_EFFECTS);
                        } else if (matchesColor(i, NOTCH_APPLE_EFFECTS)) {
                           playerPotionEffects.set(NOTCH_APPLE_EFFECTS);
                        } else {
                           Optional optional2;
                           if (!optional.isPresent() && (optional2 = this.findAddedEffects(playerEffects.get(entityOtherPlayerMP).effects.values(), i, false)).isPresent()) {
                              playerEffects.get(entityOtherPlayerMP).set((List<PotionEffect>)optional2.get());
                           } else {
                              Optional optional3;
                              if ((optional3 = this.findAddedEffects(playerEffects.get(entityOtherPlayerMP).effects.values(), i, true)).isPresent()) {
                                 playerEffects.get(entityOtherPlayerMP).set((List<PotionEffect>)optional3.get());
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else if (packetEvent.getPacket() instanceof S1DPacketEntityEffect) {
            if (mc.theWorld == null) {
               return;
            }

            S1DPacketEntityEffect s1DPacketEntityEffect = (S1DPacketEntityEffect)packetEvent.getPacket();
            Entity entity2 = mc.theWorld.getEntityByID(s1DPacketEntityEffect.getEntityId());
            if (entity2 instanceof EntityOtherPlayerMP) {
               EntityOtherPlayerMP entityOtherPlayerMP2 = (EntityOtherPlayerMP)entity2;
               PotionEffect potionEffect = new PotionEffect(s1DPacketEntityEffect.getEffectId(), s1DPacketEntityEffect.getDuration(), s1DPacketEntityEffect.getAmplifier());
               playerEffects.computeIfAbsent(entityOtherPlayerMP2, entityPlayer2 -> new PlayerPotionEffects(entityOtherPlayerMP2)).add(potionEffect);
            }
         }
      }
   };

   public HypixelGameTracker() {
      client.getEventBus().subscribe(this);
   }

   public static List<PotionEffect> getEffects(EntityPlayer entityPlayer) {
      try {
         return playerEffects.containsKey(entityPlayer) ? new ArrayList<>(playerEffects.get(entityPlayer).effects.values()) : Collections.emptyList();
      } catch (Exception exception) {
         return Collections.emptyList();
      }
   }

   private Optional<List<PotionEffect>> findAddedEffects(Collection<PotionEffect> collection, int i, boolean bl) {
      ArrayList arrayList = new ArrayList(collection);

      for (int j = 0; j < (bl ? 4 : 1); j++) {
         for (PotionEffect potionEffect : POTIONS_BY_COLOR.values()) {
            PotionEffect potionEffect2 = new PotionEffect(potionEffect.getPotionID(), 0, j);
            if (!arrayList.contains(potionEffect2)) {
               arrayList.add(potionEffect2);
               if (matchesColor(i, arrayList) || Math.abs(mixColor(arrayList) - i) == 65792 && i == 9017966) {
                  return Optional.of(arrayList);
               }

               arrayList.remove(potionEffect2);
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();
      arrayList2.add(GOLDEN_APPLE_EFFECTS);
      arrayList2.add(NOTCH_APPLE_EFFECTS);
      ArrayList arrayList3 = new ArrayList();

      for (Object object : arrayList2) {
         List list = (List)object;
         arrayList3.addAll(arrayList);
         arrayList3.removeAll(GOLDEN_APPLE_EFFECTS);
         arrayList3.removeAll(NOTCH_APPLE_EFFECTS);
         arrayList3.addAll(list);
         if (matchesColor(i, arrayList3)) {
            return Optional.of(arrayList3);
         }

         arrayList3.clear();
      }

      if (bl) {
         ArrayList arrayList4 = new ArrayList();

         for (int k = 1; k < 4; k++) {
            arrayList4.clear();

            for (Object object2 : arrayList) {
               PotionEffect potionEffect3 = (PotionEffect)object2;
               arrayList4.add(new PotionEffect(potionEffect3.getPotionID(), 0, k));
            }

            for (int l = 0; l < 4; l++) {
               for (PotionEffect potionEffect4 : POTIONS_BY_COLOR.values()) {
                  PotionEffect potionEffect5 = new PotionEffect(potionEffect4.getPotionID(), 0, l);
                  if (!arrayList4.contains(potionEffect5)) {
                     arrayList4.add(potionEffect5);
                     if (matchesColor(i, arrayList4)) {
                        return Optional.of(arrayList4);
                     }

                     arrayList4.remove(potionEffect5);
                  }
               }
            }
         }

         for (int m = 1; m < 4; m++) {
            arrayList3.clear();

            for (Object object3 : arrayList) {
               PotionEffect potionEffect6 = (PotionEffect)object3;
               arrayList3.add(new PotionEffect(potionEffect6.getPotionID(), 0, m));
            }

            for (Object object4 : arrayList2) {
               List list2 = (List)object4;
               arrayList3.addAll(list2);
               if (matchesColor(i, arrayList3)) {
                  return Optional.of(arrayList3);
               }

               arrayList3.removeAll(list2);
            }
         }
      }

      return Optional.empty();
   }

   private Optional<PotionEffect> findRemovedEffect(Collection<PotionEffect> collection, int i) {
      ArrayList arrayList = new ArrayList(collection);

      for (PotionEffect potionEffect : collection) {
         arrayList.remove(potionEffect);
         if (matchesColor(i, arrayList)) {
            return Optional.of(potionEffect);
         }

         arrayList.add(potionEffect);
      }

      return Optional.empty();
   }

   private static boolean matchesColor(int i, Collection<PotionEffect> collection) {
      int[] ints = new int[]{0, 1, 5, 16, 256, 65536};
      int j = Math.abs(mixColor(collection) - i);
      return ArrayUtils.contains(ints, j);
   }

   private static int mixColor(Collection<PotionEffect> collection) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;

      for (PotionEffect potionEffect : collection) {
         Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
         int m = potion.getLiquidColor();
         int n = potionEffect.getAmplifier() + 1;
         i += n * (m >> 16 & 0xFF);
         j += n * (m >> 8 & 0xFF);
         k += n * (m & 0xFF);
         l += n;
      }

      return MathHelper.floor_double((double)i / l) << 16
         | MathHelper.floor_double((double)j / l) << 8
         | MathHelper.floor_double((double)k / l);
   }

   public static int getColor(Collection collection) {
      return mixColor(collection);
   }
}
