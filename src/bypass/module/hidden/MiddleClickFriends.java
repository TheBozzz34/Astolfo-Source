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
   private Listener<UpdateEvent> onUpdate = var1 -> {
      long var2 = c ^ 55071442415884L;
      long var10001 = var2 ^ 116541413887354L;
      int var4 = (int)((var2 ^ 116541413887354L) >>> 56);
      int var5 = (int)((var2 ^ 116541413887354L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (var1.getState() == EventState.PRE
         && Mouse.isButtonDown(2)
         && mc.objectMouseOver.entityHit != null
         && mc.gameSettings.thirdPersonView == 0
         && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
         && this.G.hasElapsed(500.0F)) {
         Entity var8 = mc.objectMouseOver.entityHit;
         if (!(var8 instanceof EntityPlayer)) {
            return;
         }

         EntityPlayer var9 = (EntityPlayer)var8;
         if (!client.getRelationManager().z(var9) && !client.getRelationManager().D(var9)) {
            client.getRelationManager()
               .n()
               .put(
                  var9.getCommandSenderEntity().getName(), new Relation(var9.getCommandSenderEntity().getName(), var9.getCommandSenderEntity().getName(), true)
               );
         } else {
            client.getRelationManager().n().remove(var9.getCommandSenderEntity().getName());
         }

         this.G.reset();
      }
   };
   private static final long c = 46898116991275L;

   public MiddleClickFriends() {
      client.getEventBus().subscribe(this);
   }
}
