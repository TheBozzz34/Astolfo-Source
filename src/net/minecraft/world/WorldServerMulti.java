package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   private WorldServer delegate;
   private static final long cb = 38136372876532L;

   public WorldServerMulti(MinecraftServer var1, ISaveHandler var2, int var3, WorldServer var4, Profiler var5) {
      super(var1, var2, new DerivedWorldInfo(var4.getWorldInfo()), var3, var5);
      this.delegate = var4;
      var4.getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder var1, double var2x) {
            WorldServerMulti.this.getWorldBorder().setTransition(var2x);
         }

         @Override
         public void onTransitionStarted(WorldBorder var1, double var2x, double var4x, long var6) {
            WorldServerMulti.this.getWorldBorder().setTransition(var2x, var4x, var6);
         }

         @Override
         public void onCenterChanged(WorldBorder var1, double var2x, double var4x) {
            WorldServerMulti.this.getWorldBorder().setCenter(var2x, var4x);
         }

         @Override
         public void onWarningTimeChanged(WorldBorder var1, int var2x) {
            WorldServerMulti.this.getWorldBorder().setWarningTime(var2x);
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder var1, int var2x) {
            WorldServerMulti.this.getWorldBorder().setWarningDistance(var2x);
         }

         @Override
         public void onDamageAmountChanged(WorldBorder var1, double var2x) {
            WorldServerMulti.this.getWorldBorder().setDamageAmount(var2x);
         }

         @Override
         public void onDamageBufferChanged(WorldBorder var1, double var2x) {
            WorldServerMulti.this.getWorldBorder().setDamageBuffer(var2x);
         }
      });
   }

   @Override
   public void saveLevel() throws MinecraftException {
   }

   @Override
   public World init() {
      this.mapStorage = this.delegate.getMapStorage();
      this.worldScoreboard = this.delegate.getScoreboard();
      String var4 = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection var5 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, var4);
      if (var5 == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData(var4, this.villageCollectionObj);
      } else {
         this.villageCollectionObj = var5;
         this.villageCollectionObj.setWorldsForAll(this);
      }

      return this;
   }
}
