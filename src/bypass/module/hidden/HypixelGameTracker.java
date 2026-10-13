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
   private Listener<MotionEvent> onMotion = var0 -> {
      if (var0.getState() == EventState.PRE) {
         if (GameUtil.isInHypixelGame()) {
            String var4 = GameUtil.getScoreboardTitle();
            if (var4.contains("PIT")) {
               game = 5;
            } else if (var4.contains("BED WARS")) {
               game = 2;
            } else if (var4.contains("SKYWARS")) {
               game = 1;
            } else if (var4.contains("UHC CHAMPIONS") || var4.contains("HYPIXEL UHC")) {
               game = 3;
            } else if (var4.contains("MEGA WALLS")) {
               game = 4;
            } else if (!var4.contains("HYPIXEL")) {
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
   private final Listener<WorldLoadEvent> onWorldLoad = var0 -> playerEffects.clear();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1 -> {
      if (var1.getDirection() == PacketDirection.RECEIVED) {
         if (mc.theWorld == null) {
            return;
         }

         if (mc.thePlayer == null) {
            return;
         }

         if (var1.getPacket() instanceof S1CPacketEntityMetadata) {
            S1CPacketEntityMetadata var5 = (S1CPacketEntityMetadata)var1.getPacket();
            if (var5.func_149376_c() == null) {
               return;
            }

            for (DataWatcher.WatchableObject var7 : var5.func_149376_c()) {
               if (var7 != null) {
                  Entity var8 = mc.theWorld.getEntityByID(var5.getEntityId());
                  if (var7.getDataValueId() == 7 && var8 instanceof EntityOtherPlayerMP) {
                     EntityOtherPlayerMP var9 = (EntityOtherPlayerMP)var8;
                     int var10 = Integer.parseInt(var7.getObject().toString());
                     PlayerPotionEffects var11 = playerEffects.computeIfAbsent(var9, var1x -> new PlayerPotionEffects(var9));
                     if (var10 == 0) {
                        var11.clear();
                     } else if (var11.color == 0 && var11.effects.isEmpty()) {
                        var11.color = var10;
                        if (matchesColor(var10, GOLDEN_APPLE_EFFECTS)) {
                           var11.set(GOLDEN_APPLE_EFFECTS);
                        } else if (matchesColor(var10, NOTCH_APPLE_EFFECTS)) {
                           var11.set(NOTCH_APPLE_EFFECTS);
                        } else {
                           int[] var21 = new int[]{0, 1, -1, 5, -5, 16, -16, 256, -256, 65536, -65536};

                           for (int var16 : var21) {
                              if (POTIONS_BY_COLOR.containsKey(var10 + var16)) {
                                 var11.add(POTIONS_BY_COLOR.get(var10 + var16));
                              }
                           }
                        }
                     } else {
                        Optional var14 = this.findRemovedEffect(playerEffects.get(var9).effects.values(), var10);
                        if (var14.isPresent() && !this.findAddedEffects(playerEffects.get(var9).effects.values(), var10, false).isPresent()) {
                           playerEffects.get(var9).remove((PotionEffect)var14.get());
                        } else if (matchesColor(var10, GOLDEN_APPLE_EFFECTS)) {
                           var11.set(GOLDEN_APPLE_EFFECTS);
                        } else if (matchesColor(var10, NOTCH_APPLE_EFFECTS)) {
                           var11.set(NOTCH_APPLE_EFFECTS);
                        } else {
                           Optional var12;
                           if (!var14.isPresent() && (var12 = this.findAddedEffects(playerEffects.get(var9).effects.values(), var10, false)).isPresent()) {
                              playerEffects.get(var9).set((List<PotionEffect>)var12.get());
                           } else {
                              Optional var13;
                              if ((var13 = this.findAddedEffects(playerEffects.get(var9).effects.values(), var10, true)).isPresent()) {
                                 playerEffects.get(var9).set((List<PotionEffect>)var13.get());
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else if (var1.getPacket() instanceof S1DPacketEntityEffect) {
            if (mc.theWorld == null) {
               return;
            }

            S1DPacketEntityEffect var17 = (S1DPacketEntityEffect)var1.getPacket();
            Entity var18 = mc.theWorld.getEntityByID(var17.getEntityId());
            if (var18 instanceof EntityOtherPlayerMP) {
               EntityOtherPlayerMP var19 = (EntityOtherPlayerMP)var18;
               PotionEffect var20 = new PotionEffect(var17.getEffectId(), var17.getDuration(), var17.getAmplifier());
               playerEffects.computeIfAbsent(var19, var1x -> new PlayerPotionEffects(var19)).add(var20);
            }
         }
      }
   };
   private static final long c = 130295296867022L;

   public HypixelGameTracker() {
      client.getEventBus().subscribe(this);
   }

   public static List<PotionEffect> getEffects(EntityPlayer var0) {
      try {
         return playerEffects.containsKey(var0) ? new ArrayList<>(playerEffects.get(var0).effects.values()) : Collections.emptyList();
      } catch (Exception var4) {
         return Collections.emptyList();
      }
   }

   private Optional<List<PotionEffect>> findAddedEffects(Collection<PotionEffect> var1, int var2, boolean var3) {
      ArrayList var7 = new ArrayList(var1);

      for (int var8 = 0; var8 < (var3 ? 4 : 1); var8++) {
         for (PotionEffect var10 : POTIONS_BY_COLOR.values()) {
            PotionEffect var11 = new PotionEffect(var10.getPotionID(), 0, var8);
            if (!var7.contains(var11)) {
               var7.add(var11);
               if (matchesColor(var2, var7) || Math.abs(mixColor(var7) - var2) == 65792 && var2 == 9017966) {
                  return Optional.of(var7);
               }

               var7.remove(var11);
            }
         }
      }

      ArrayList var16 = new ArrayList();
      var16.add(GOLDEN_APPLE_EFFECTS);
      var16.add(NOTCH_APPLE_EFFECTS);
      ArrayList var17 = new ArrayList();

      for (Object var20$raw : var16) {
         List var20 = (List)var20$raw;
         var17.addAll(var7);
         var17.removeAll(GOLDEN_APPLE_EFFECTS);
         var17.removeAll(NOTCH_APPLE_EFFECTS);
         var17.addAll(var20);
         if (matchesColor(var2, var17)) {
            return Optional.of(var17);
         }

         var17.clear();
      }

      if (var3) {
         ArrayList var19 = new ArrayList();

         for (int var21 = 1; var21 < 4; var21++) {
            var19.clear();

            for (Object var13$raw : var7) {
               PotionEffect var13 = (PotionEffect)var13$raw;
               var19.add(new PotionEffect(var13.getPotionID(), 0, var21));
            }

            for (int var23 = 0; var23 < 4; var23++) {
               for (PotionEffect var14 : POTIONS_BY_COLOR.values()) {
                  PotionEffect var15 = new PotionEffect(var14.getPotionID(), 0, var23);
                  if (!var19.contains(var15)) {
                     var19.add(var15);
                     if (matchesColor(var2, var19)) {
                        return Optional.of(var19);
                     }

                     var19.remove(var15);
                  }
               }
            }
         }

         for (int var22 = 1; var22 < 4; var22++) {
            var17.clear();

            for (Object var27$raw : var7) {
               PotionEffect var27 = (PotionEffect)var27$raw;
               var17.add(new PotionEffect(var27.getPotionID(), 0, var22));
            }

            for (Object var28$raw : var16) {
               List var28 = (List)var28$raw;
               var17.addAll(var28);
               if (matchesColor(var2, var17)) {
                  return Optional.of(var17);
               }

               var17.removeAll(var28);
            }
         }
      }

      return Optional.empty();
   }

   private Optional<PotionEffect> findRemovedEffect(Collection<PotionEffect> var1, int var2) {
      ArrayList var6 = new ArrayList(var1);

      for (PotionEffect var8 : var1) {
         var6.remove(var8);
         if (matchesColor(var2, var6)) {
            return Optional.of(var8);
         }

         var6.add(var8);
      }

      return Optional.empty();
   }

   private static boolean matchesColor(int var0, Collection<PotionEffect> var1) {
      int[] var4 = new int[]{0, 1, 5, 16, 256, 65536};
      int var5 = Math.abs(mixColor(var1) - var0);
      return ArrayUtils.contains(var4, var5);
   }

   private static int mixColor(Collection<PotionEffect> var0) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for (PotionEffect var9 : var0) {
         Potion var10 = Potion.potionTypes[var9.getPotionID()];
         int var11 = var10.getLiquidColor();
         int var12 = var9.getAmplifier() + 1;
         var4 += var12 * (var11 >> 16 & 0xFF);
         var5 += var12 * (var11 >> 8 & 0xFF);
         var6 += var12 * (var11 & 0xFF);
         var7 += var12;
      }

      return MathHelper.floor_double((double)var4 / var7) << 16
         | MathHelper.floor_double((double)var5 / var7) << 8
         | MathHelper.floor_double((double)var6 / var7);
   }

   public static int getColor(Collection var0) {
      return mixColor(var0);
   }
}
