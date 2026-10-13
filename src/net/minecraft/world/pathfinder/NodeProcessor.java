package net.minecraft.world.pathfinder;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
   public IBlockAccess blockaccess;
   public IntHashMap<PathPoint> pointMap = new IntHashMap<>();
   public int entitySizeX;
   public int entitySizeY;
   public int entitySizeZ;
   private static final long a = 98331749206439L;

   public void initProcessor(IBlockAccess var1, Entity var2) {
      this.blockaccess = var1;
      this.pointMap.clearMap();
      this.entitySizeX = MathHelper.floor_float(var2.width + 1.0F);
      this.entitySizeY = MathHelper.floor_float(var2.height + 1.0F);
      this.entitySizeZ = MathHelper.floor_float(var2.width + 1.0F);
   }

   public void postProcess() {
   }

   public PathPoint openPoint(int var1, int var2, int var3) {
      int var7 = PathPoint.makeHash(var1, var2, var3);
      PathPoint var8 = this.pointMap.lookup(var7);
      if (var8 == null) {
         var8 = new PathPoint(var1, var2, var3);
         this.pointMap.addKey(var7, var8);
      }

      return var8;
   }

   public abstract PathPoint getPathPointTo(Entity var1);

   public abstract PathPoint getPathPointToCoords(Entity var1, double var2, double var4, double var6);

   public abstract int findPathOptions(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5);
}
