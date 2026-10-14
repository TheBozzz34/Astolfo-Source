package bypass.event.render;

import bypass.event.CancellableEvent;
import bypass.event.EventState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

public class RenderTileEntityEvent extends CancellableEvent {
   private EventState state;
   private TileEntity tileEntity;
   private TileEntityRendererDispatcher dispatcher;
   private float partialTicks;
   private int destroyStage;

   public RenderTileEntityEvent(TileEntityRendererDispatcher tileEntityRendererDispatcher, TileEntity tileEntity, float f, int i, EventState eventState) {
      this.dispatcher = tileEntityRendererDispatcher;
      this.tileEntity = tileEntity;
      this.partialTicks = f;
      this.destroyStage = i;
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }

   public TileEntity getTileEntity() {
      return this.tileEntity;
   }

   public TileEntityRendererDispatcher getDispatcher() {
      return this.dispatcher;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public int getDestroyStage() {
      return this.destroyStage;
   }
}
