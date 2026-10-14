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
   private Listener<UpdateEvent> onUpdate = updateEvent -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         boolean bl = this.a_boolean();
         if (bl) {
            if (!this.A) {
               if (mc.objectMouseOver.getBlockPos() != null && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockLiquid) {
                  mc.rightClickMouse();
               }
            } else {
               EntityFishHook entityFishHook = this.a();
               if (entityFishHook != null) {
                  switch ((AutoFishMode)this.mode.getValue()) {
                     case MOTION:
                        if (entityFishHook.isInWater() && entityFishHook.ticksExisted > 30 && entityFishHook.motionY <= -0.06) {
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
   private Listener<PacketEvent> onPacket = packetEvent -> {
      switch (packetEvent.getDirection()) {
         case RECEIVED:
            if (packetEvent.getPacket() instanceof S29PacketSoundEffect) {
               if (this.mode.getValue() != AutoFishMode.SOUND) {
                  return;
               }

               S29PacketSoundEffect s29PacketSoundEffect = (S29PacketSoundEffect)packetEvent.getPacket();
               if ((s29PacketSoundEffect.getSoundName().equals("random.splash") || s29PacketSoundEffect.getSoundName().equals("game.neutral.swim.splash")) && mc.thePlayer.fishEntity != null) {
                  this.l = true;
               }
            }
            break;
         case OUTGOING:
            if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
               boolean bl = this.a_boolean();
               if (bl && ((C08PacketPlayerBlockPlacement)packetEvent.getPacket()).getPlacedBlockDirection() == 255) {
                  this.A = !this.A;
               }
            }

            if (packetEvent.getPacket() instanceof C09PacketHeldItemChange) {
               this.A = false;
            }
      }
   };
   private static final long c = 118521303781827L;

   public AutoFish(Category category) {
      super(category);
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

         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFishHook && ((EntityFishHook)entity).angler == mc.thePlayer) {
               return (EntityFishHook)entity;
            }
         }
      }

      return null;
   }

   private boolean a_boolean() {
      return mc.thePlayer != null && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFishingRod;
   }
}
