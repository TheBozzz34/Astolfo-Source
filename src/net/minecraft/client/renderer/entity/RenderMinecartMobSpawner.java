package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {
   private static final long c = 96485787986436L;

   public RenderMinecartMobSpawner(RenderManager var1) {
      super(var1);
   }

   public void func_180560_a(EntityMinecartMobSpawner var1, float var2, IBlockState var3) {
      super.func_180560_a(var1, var2, var3);
      if (var3.getBlock() == Blocks.mob_spawner) {
         TileEntityMobSpawnerRenderer.renderMob(var1.func_98039_d(), var1.posX, var1.posY, var1.posZ, var2);
      }
   }
}
