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

   public RenderTileEntityEvent(TileEntityRendererDispatcher var1, TileEntity var2, float var3, int var4, EventState var5) {
      this.dispatcher = var1;
      this.tileEntity = var2;
      this.partialTicks = var3;
      this.destroyStage = var4;
      this.state = var5;
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
