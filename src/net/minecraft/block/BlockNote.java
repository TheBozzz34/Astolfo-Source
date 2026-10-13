package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
   private static final List<String> INSTRUMENTS = Lists.newArrayList("harp", "bd", "snare", "hat", "bassattack");
   private static final long e = 63487308933721L;
   private static final long K = -1136757330763841524L;

   public BlockNote() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var8 = var1.isBlockPowered(var2);
      TileEntity var9 = var1.getTileEntity(var2);
      if (var9 instanceof TileEntityNote) {
         TileEntityNote var10 = (TileEntityNote)var9;
         if (var10.previousRedstoneState != var8) {
            if (var8) {
               var10.triggerNote(var1, var2);
            }

            var10.previousRedstoneState = var8;
         }
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityNote) {
         TileEntityNote var13 = (TileEntityNote)var12;
         var13.changePitch();
         var13.triggerNote(var1, var2);
         var4.triggerAchievement(StatList.field_181735_S);
      }

      return true;
   }

   @Override
   public void onBlockClicked(World var1, BlockPos var2, EntityPlayer var3) {
      if (!var1.isRemote) {
         TileEntity var7 = var1.getTileEntity(var2);
         if (var7 instanceof TileEntityNote) {
            ((TileEntityNote)var7).triggerNote(var1, var2);
            var3.triggerAchievement(StatList.field_181734_R);
         }
      }
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityNote();
   }

   private String getInstrument(int var1) {
      if (var1 < 0 || var1 >= INSTRUMENTS.size()) {
         var1 = 0;
      }

      return INSTRUMENTS.get(var1);
   }

   @Override
   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      float var8 = (float)Math.pow(2.0, (var5 - (int)K) / 12.0);
      var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, "note." + this.getInstrument(var4), 3.0F, var8);
      var1.spawnParticle(EnumParticleTypes.NOTE, var2.getX() + 0.5, var2.getY() + 1.2, var2.getZ() + 0.5, var5 / 24.0, 0.0, 0.0);
      return true;
   }

   @Override
   public int getRenderType() {
      return 3;
   }
}
