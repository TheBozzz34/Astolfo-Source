package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledChunk {
   public static final CompiledChunk DUMMY = new CompiledChunk() {
      @Override
      public void q(EnumWorldBlockLayer var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void G(EnumWorldBlockLayer var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isVisible(EnumFacing var1, EnumFacing var2) {
         return false;
      }
   };
   private final boolean[] layersUsed = new boolean[EnumWorldBlockLayer.values().length];
   private final boolean[] layersStarted = new boolean[EnumWorldBlockLayer.values().length];
   private boolean empty = true;
   private final List<TileEntity> tileEntities = Lists.newArrayList();
   private SetVisibility setVisibility = new SetVisibility();
   private WorldRenderer.State state;
   private static final long a = 23825275267390L;

   public boolean isEmpty() {
      return this.empty;
   }

   public void q(EnumWorldBlockLayer var1) {
      this.empty = false;
      this.layersUsed[var1.ordinal()] = true;
   }

   public boolean isLayerEmpty(EnumWorldBlockLayer var1) {
      return !this.layersUsed[var1.ordinal()];
   }

   public void G(EnumWorldBlockLayer var1) {
      this.layersStarted[var1.ordinal()] = true;
   }

   public boolean isLayerStarted(EnumWorldBlockLayer var1) {
      return this.layersStarted[var1.ordinal()];
   }

   public List<TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addTileEntity(TileEntity var1) {
      this.tileEntities.add(var1);
   }

   public boolean isVisible(EnumFacing var1, EnumFacing var2) {
      return this.setVisibility.isVisible(var1, var2);
   }

   public void setVisibility(SetVisibility var1) {
      this.setVisibility = var1;
   }

   public WorldRenderer.State getState() {
      return this.state;
   }

   public void setState(WorldRenderer.State var1) {
      this.state = var1;
   }
}
