package bypass.module.other;

import bypass.anticheat.AutoArmorCheck;
import bypass.anticheat.BlinkCheck;
import bypass.anticheat.Check;
import bypass.anticheat.CheckType;
import bypass.anticheat.IllegalAnglesCheck;
import bypass.anticheat.MovementCheck;
import bypass.anticheat.NoSlowdownCheck;
import bypass.anticheat.PlayerChecks;
import bypass.anticheat.SpoofGroundACheck;
import bypass.anticheat.SpoofGroundBCheck;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.hidden.BotDetector;
import bypass.relation.Relation;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.util.GameUtil;
import bypass.util.collection.ShapedMap;
import java.util.ArrayList;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;

public final class HackerDetector extends Module {
   private final ShapedMap<Integer, PlayerChecks> N = new ShapedMap<>();
   private final MapSetting checks = new MapSettingBuilder("Checks")
      .ordered()
      .description("killaura entiti")
      .add("Spoof-Ground", new BooleanSetting("Spoof-Ground", "Checks for nofall/ground spoof", true))
      .add("Auto-Armor", new BooleanSetting("Auto-Armor", "Checks for Auto-Armor", true))
      .add("Blink", new BooleanSetting("Blink", "Checks for Blink", true))
      .add("No-Slow", new BooleanSetting("No-Slow", "Checks for no slowdown", true))
      .add("Illegal-Angles", new BooleanSetting("Illegal-Angles", "Checks for illegal angles", true))
      .build();
   public static final BooleanSetting debug = new BooleanSetting("Debug", "prints flags to chat", false);
   private final BooleanSetting autoEnemy = new BooleanSetting("Auto-Enemy", "Sets people to enemy", true);
   private final BooleanSetting autoReport = new BooleanSetting("Auto-Report", "Auto reports cheaters", false);
   private final ArrayList<String> l = new ArrayList<>();
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 4603551041761L;
      long var10001 = var2 ^ 100016226425167L;
      int var4 = (int)((var2 ^ 100016226425167L) >>> 56);
      int var5 = (int)((var2 ^ 100016226425167L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      long var7 = var2 ^ 85645520157928L;
      if (var1x.getDirection() == PacketDirection.RECEIVED) {
         if (var1x.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity var10 = (S14PacketEntity)var1x.getPacket();
            if (var10.func_149062_c() == 0 && var10.func_149061_d() == 0 && var10.func_149064_e() == 0 || mc.theWorld == null) {
               return;
            }

            Entity var11 = var10.getEntity(mc.theWorld);
            if (var11 instanceof EntityPlayer) {
               EntityPlayer var12 = (EntityPlayer)var11;
               if (var12.ticksExisted < 15
                  || GameUtil.isDead(var12)
                  || client.getUserManager().g(var11.getName()) != null
                  || var11.E()
                  || var12.capabilities != null && var12.capabilities.allowFlying
                  || BotDetector.w.contains(var12)
                  || client.getRelationManager().z(var11)
                  || client.getRelationManager().D(var11)
                  || this.l.contains(var12.getName())) {
                  this.N.remove(var11.getEntityId());
                  return;
               }

               PlayerChecks var13 = this.N.get(var11.getEntityId());
               this.N.computeIfAbsent(var11.getEntityId(), var1xx -> {
                  PlayerChecks var5x = new PlayerChecks();
                  if (this.checks.getSetting("No-Slow").asBoolean()) {
                     PlayerChecks.p(var5x, new NoSlowdownCheck());
                  }

                  if (this.checks.getSetting("Spoof-Ground").asBoolean()) {
                     PlayerChecks.p(var5x, new SpoofGroundACheck());
                     PlayerChecks.p(var5x, new SpoofGroundBCheck());
                  }

                  if (this.checks.getSetting("Auto-Armor").asBoolean()) {
                     PlayerChecks.p(var5x, new AutoArmorCheck());
                  }

                  if (this.checks.getSetting("Illegal-Angles").asBoolean()) {
                     PlayerChecks.p(var5x, new IllegalAnglesCheck());
                  }

                  if (this.checks.getSetting("Blink").asBoolean()) {
                     PlayerChecks.p(var5x, new BlinkCheck());
                  }

                  return var5x;
               });
               if (var13 != null) {
                  for (Object var15$raw : PlayerChecks.P(var13)) {
                     Check var15 = (Check)var15$raw;
                     if (var15.D() == CheckType.MOVEMENT) {
                        MovementCheck var16 = (MovementCheck)var15;
                        double var17 = var11.serverPosX + var10.func_149062_c();
                        double var19 = var11.serverPosY + var10.func_149061_d();
                        double var21 = var11.serverPosZ + var10.func_149064_e();
                        double var23 = var17 / 32.0;
                        double var25 = var19 / 32.0;
                        double var27 = var21 / 32.0;
                        float var29 = var10.func_149066_f() * 360 / 256.0F;
                        float var30 = var10.func_149063_g() * 360 / 256.0F;
                        if (var16.D(var23, var25, var27, var29, var30, var10.getOnGround(), var12) >= var16.p()) {
                           toasts.X("Hacker Detector", var11.getName() + " has been detected for " + var16.A());
                           if (this.autoEnemy.getValue()) {
                              client.getRelationManager().add(new Relation(var12.getName(), var12.getName(), false));
                           }

                           if (this.autoReport.getValue()) {
                              mc.thePlayer.sendQueue.u(new C01PacketChatMessage("/wdr " + var11.getName() + " ka fly nokb ac speed"));
                           }

                           this.l.add(var12.getName());
                           var16.D_void();
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (var1x.getPacket() instanceof S1CPacketEntityMetadata) {
            S1CPacketEntityMetadata var31 = (S1CPacketEntityMetadata)var1x.getPacket();
            if (var31.func_149376_c() != null) {
               for (DataWatcher.WatchableObject var33 : var31.func_149376_c()) {
                  if (var33.getDataValueId() == 6 && var33.getObject() instanceof Number) {
                     Number var34 = (Number)var33.getObject();
                     if (var34.floatValue() <= 0.0F) {
                        this.N.remove(var31.getEntityId());
                     }
                  }
               }
            }
         }
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var1x -> this.N.clear();
   private static final long c = 52323026580723L;

   public HackerDetector(Category var1) {
      super(var1);
      this.addSettings(this.checks, this.autoEnemy, this.autoReport);
   }
}
