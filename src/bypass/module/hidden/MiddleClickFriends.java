package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.HiddenModule;
import bypass.relation.Relation;
import bypass.util.Stopwatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public final class MiddleClickFriends extends HiddenModule {
   private Stopwatch G = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE
         && Mouse.isButtonDown(2)
         && mc.objectMouseOver.entityHit != null
         && mc.gameSettings.thirdPersonView == 0
         && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
         && this.G.hasElapsed(500.0F)) {
         Entity entity = mc.objectMouseOver.entityHit;
         if (!(entity instanceof EntityPlayer)) {
            return;
         }

         EntityPlayer entityPlayer = (EntityPlayer)entity;
         if (!client.getRelationManager().z(entityPlayer) && !client.getRelationManager().D(entityPlayer)) {
            client.getRelationManager()
               .n()
               .put(
                  entityPlayer.getCommandSenderEntity().getName(), new Relation(entityPlayer.getCommandSenderEntity().getName(), entityPlayer.getCommandSenderEntity().getName(), true)
               );
         } else {
            client.getRelationManager().n().remove(entityPlayer.getCommandSenderEntity().getName());
         }

         this.G.reset();
      }
   };

   public MiddleClickFriends() {
      client.getEventBus().subscribe(this);
   }
}
