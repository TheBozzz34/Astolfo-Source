package net.minecraft.util;

import bypass.Astolfo;
import bypass.event.EventState;
import bypass.event.player.MovementInputEvent;
import bypass.module.movement.Sneak;
import bypass.module.player.Scaffold;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
   private final GameSettings gameSettings;
   private static final long a = 1094370570501L;

   public MovementInputFromOptions(GameSettings var1) {
      this.gameSettings = var1;
   }

   @Override
   public void updatePlayerMoveState() {
      long var1 = a ^ 124518114660840L;
      long var3 = var1 ^ 77750445989214L;
      MovementInputEvent var6 = new MovementInputEvent(EventState.PRE);
      Astolfo.INSTANCE.getEventBus().post(var6);
      this.moveStrafe = 0.0F;
      this.moveForward = 0.0F;
      if (!var6.isCancelled()) {
         if (this.gameSettings.keyBindForward.isKeyDown()) {
            this.moveForward++;
         }

         if (this.gameSettings.keyBindBack.isKeyDown()) {
            this.moveForward--;
         }

         if (this.gameSettings.keyBindLeft.isKeyDown()) {
            this.moveStrafe++;
         }

         if (this.gameSettings.keyBindRight.isKeyDown()) {
            this.moveStrafe--;
         }
      }

      this.jump = this.gameSettings.keyBindJump.isKeyDown();
      this.sneak = (!Astolfo.INSTANCE.getModuleManager().get(Scaffold.class).isEnabled() || !Scaffold.down.getValue())
         && this.gameSettings.keyBindSneak.isKeyDown();
      if (this.sneak && Astolfo.INSTANCE.getModuleManager().get(Sneak.class).isEnabled() && Sneak.hold.getValue()) {
         this.sneak = false;
      }

      if (this.sneak) {
         this.moveStrafe = (float)(this.moveStrafe * 0.3);
         this.moveForward = (float)(this.moveForward * 0.3);
      }

      Astolfo.INSTANCE.getEventBus().post(new MovementInputEvent(EventState.POST));
   }
}
