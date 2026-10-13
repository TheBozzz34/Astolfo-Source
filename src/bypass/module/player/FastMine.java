package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.DamageBlockEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class FastMine extends Module {
   private final NumberSetting<Double> multiplier = new NumberSetting<>("Multiplier", "Multiplier", 1.25, 0.75, 5.0);
   private final NumberSetting<Integer> hitDelay = new NumberSetting<>("Hit-Delay", "Speed of haste", 0, 0, 4);
   private final BooleanSetting fast = new BooleanSetting("Fast", "Powered by china", false);
   private final BooleanSetting removeFatigue = new BooleanSetting("Remove-Fatigue", "Remove-Fatigue", false);
   private final MapSetting instant = new MapSettingBuilder("Instant")
      .ordered()
      .description("Delay stuff")
      .add("Delay", new NumberSetting<>("Delay", "Delay", 2, 0, 5, " ticks"))
      .add("Enable", new BooleanSetting("Enable", "Enable instant mining", false))
      .build();
   private boolean O = false;
   private float A = 0.0F;
   public BlockPos x;
   public EnumFacing v;
   private final ArrayList<PendingBreak> d = new ArrayList<>();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var1x -> {
      if (!this.d.isEmpty()) {
         Iterator var5 = this.d.iterator();

         while (var5.hasNext()) {
            PendingBreak var6 = (PendingBreak)var5.next();
            if (var6.B <= 0) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, var6.f, var6.e));
               var5.remove();
            } else {
               var6.B--;
            }
         }
      }

      if (this.removeFatigue.getValue() && mc.thePlayer.getActivePotionEffect(Potion.digSlowdown) != null) {
         mc.thePlayer.removePotionEffect(Potion.digSlowdown.id);
      }

      if (this.fast.getValue() && this.O) {
         Block var7 = mc.theWorld.getBlockState(this.x).getBlock();
         this.A = this.A + (float)(var7.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.x) * this.multiplier.asDouble());
         if (this.A >= 1.0F) {
            mc.theWorld.setBlockState(this.x, Blocks.air.getDefaultState(), 11);
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.x, this.v));
            this.A = 0.0F;
            this.O = false;
         }
      }
   };
   @EventHandler(DamageBlockEvent.class)
   private final Listener<DamageBlockEvent> onDamageBlock = var1x -> {
      if (!this.fast.getValue()) {
         var1x.setDamageMultiplier(this.multiplier.getValue());
      }

      var1x.setHitDelay(this.hitDelay.getValue());
   };
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (this.fast.getValue()) {
            if (var1x.getPacket() instanceof C07PacketPlayerDigging && !mc.playerController.L() && mc.playerController != null) {
               C07PacketPlayerDigging var8 = (C07PacketPlayerDigging)var1x.getPacket();
               if (var8.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  this.O = true;
                  this.x = var8.getPosition();
                  this.v = var8.getFacing();
                  this.A = 0.0F;
               } else if (var8.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                  || var8.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.O = false;
                  this.x = null;
                  this.v = null;
               }
            }

            return;
         }

         if (this.instant.getSetting("Enable").asBoolean() && var1x.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging var5 = (C07PacketPlayerDigging)var1x.getPacket();
            if (var5.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var5.getPosition(), var5.getFacing()));
               this.d.add(new PendingBreak(this.instant.getSetting("Delay").asInt() + 5, var5.getPosition(), var5.getFacing()));
               var1x.cancel();
            }

            if (var5.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
               for (PendingBreak var7 : this.d) {
                  if (var7.f.equals(var5.getPosition())) {
                     var1x.cancel();
                     break;
                  }
               }
            }
         }
      }
   };
   private static final long c = 65962023398508L;

   public FastMine(Category var1) {
      super(var1);
      this.addSettings(this.instant, this.multiplier, this.fast, this.hitDelay, this.removeFatigue);
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         mc.playerController.blockHitDelay = 5;
      }

      super.onDisable();
   }
}
