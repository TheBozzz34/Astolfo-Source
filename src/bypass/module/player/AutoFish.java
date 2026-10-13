package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

public final class AutoFish extends Module {
   private final EnumSetting<AutoFishMode> mode = new EnumSetting<>("Mode", "Set your AutoFish mode", AutoFishMode.SOUND);
   private boolean l;
   private boolean A;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         boolean var5 = this.a_boolean();
         if (var5) {
            if (!this.A) {
               if (mc.objectMouseOver.getBlockPos() != null && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockLiquid) {
                  mc.rightClickMouse();
               }
            } else {
               EntityFishHook var6 = this.a();
               if (var6 != null) {
                  switch ((AutoFishMode)this.mode.getValue()) {
                     case MOTION:
                        if (var6.isInWater() && var6.ticksExisted > 30 && var6.motionY <= -0.06) {
                           this.l = true;
                        }
                     case TEXT:
                        if (!StringUtils.isEmpty(mc.ingameGUI.displayedSubTitle) && mc.ingameGUI.displayedSubTitle.toLowerCase().contains("reel")) {
                           this.l = true;
                        }
                  }
               }
            }

            if (this.l) {
               toasts.g("Found fish", "Caught fish!");
               mc.rightClickMouse();
               this.l = this.A = false;
            }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case RECEIVED:
            if (var1x.getPacket() instanceof S29PacketSoundEffect) {
               if (this.mode.getValue() != AutoFishMode.SOUND) {
                  return;
               }

               S29PacketSoundEffect var6 = (S29PacketSoundEffect)var1x.getPacket();
               if ((var6.getSoundName().equals("random.splash") || var6.getSoundName().equals("game.neutral.swim.splash")) && mc.thePlayer.fishEntity != null) {
                  this.l = true;
               }
            }
            break;
         case OUTGOING:
            if (var1x.getPacket() instanceof C08PacketPlayerBlockPlacement) {
               boolean var5 = this.a_boolean();
               if (var5 && ((C08PacketPlayerBlockPlacement)var1x.getPacket()).getPlacedBlockDirection() == 255) {
                  this.A = !this.A;
               }
            }

            if (var1x.getPacket() instanceof C09PacketHeldItemChange) {
               this.A = false;
            }
      }
   };
   private static final long c = 118521303781827L;

   public AutoFish(Category var1) {
      super(var1);
      this.addSettings(this.mode);
   }

   @Override
   public void p() {
      this.setDisplayName("Auto Fish " + EnumChatFormatting.GRAY + AutoFishMode.m(this.mode.getValue()));
      super.p();
   }

   private EntityFishHook a() {
      if (this.A) {
         if (mc.thePlayer.fishEntity != null) {
            return mc.thePlayer.fishEntity;
         }

         for (Entity var5 : mc.theWorld.loadedEntityList) {
            if (var5 instanceof EntityFishHook && ((EntityFishHook)var5).angler == mc.thePlayer) {
               return (EntityFishHook)var5;
            }
         }
      }

      return null;
   }

   private boolean a_boolean() {
      return mc.thePlayer != null && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFishingRod;
   }
}
