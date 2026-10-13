package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator {
   public WorldGenerator spikeGen = new WorldGenSpikes(Blocks.end_stone);
   private static final long f = 101829591125406L;

   @Override
   public void genDecorations(BiomeGenBase var1) {
      this.generateOres();
      if (this.randomGenerator.nextInt(5) == 0) {
         int var5 = this.randomGenerator.nextInt(16) + 8;
         int var6 = this.randomGenerator.nextInt(16) + 8;
         this.spikeGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(var5, 0, var6)));
      }

      if (this.field_180294_c.getX() == 0 && this.field_180294_c.getZ() == 0) {
         EntityDragon var7 = new EntityDragon(this.currentWorld);
         var7.setLocationAndAngles(0.0, 128.0, 0.0, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
         this.currentWorld.spawnEntityInWorld(var7);
      }
   }
}
