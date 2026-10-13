package net.minecraft.block.state;

import com.google.common.base.Predicate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockWorldState {
   private final World world;
   private final BlockPos pos;
   private final boolean field_181628_c;
   private IBlockState state;
   private TileEntity tileEntity;
   private boolean tileEntityInitialized;
   private static final long a = 70751544373813L;

   public BlockWorldState(World var1, BlockPos var2, boolean var3) {
      this.world = var1;
      this.pos = var2;
      this.field_181628_c = var3;
   }

   public IBlockState getBlockState() {
      if (this.state == null && (this.field_181628_c || this.world.isBlockLoaded(this.pos))) {
         this.state = this.world.getBlockState(this.pos);
      }

      return this.state;
   }

   public TileEntity getTileEntity() {
      if (this.tileEntity == null && !this.tileEntityInitialized) {
         this.tileEntity = this.world.getTileEntity(this.pos);
         this.tileEntityInitialized = true;
      }

      return this.tileEntity;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate<BlockWorldState> hasState(final Predicate<IBlockState> var0) {
      return new Predicate<BlockWorldState>() {
         private static final long a = 97833484951791L;

         public boolean apply(BlockWorldState var1) {
            return var1 != null && var0.apply(var1.getBlockState());
         }
      };
   }
}
