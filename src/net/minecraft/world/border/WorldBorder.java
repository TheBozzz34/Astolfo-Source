package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double centerX = 0.0;
   private double centerZ = 0.0;
   private double startDiameter = 6.0E7;
   private double endDiameter = this.startDiameter;
   private long endTime;
   private long startTime;
   private int worldSize = 29999984;
   private double damageAmount = 0.2;
   private double damageBuffer = 5.0;
   private int warningTime = 15;
   private int warningDistance = 5;
   private static final long a = 78978247543139L;

   public boolean contains(BlockPos var1) {
      return var1.getX() + 1 > this.minX() && var1.getX() < this.maxX() && var1.getZ() + 1 > this.minZ() && var1.getZ() < this.maxZ();
   }

   public boolean contains(ChunkCoordIntPair var1) {
      return var1.getXEnd() > this.minX() && var1.getXStart() < this.maxX() && var1.getZEnd() > this.minZ() && var1.getZStart() < this.maxZ();
   }

   public boolean contains(AxisAlignedBB var1) {
      return var1.maxX > this.minX() && var1.minX < this.maxX() && var1.maxZ > this.minZ() && var1.minZ < this.maxZ();
   }

   public double getClosestDistance(Entity var1) {
      return this.getClosestDistance(var1.posX, var1.posZ);
   }

   public double getClosestDistance(double var1, double var3) {
      double var8 = var3 - this.minZ();
      double var10 = this.maxZ() - var3;
      double var12 = var1 - this.minX();
      double var14 = this.maxX() - var1;
      double var16 = Math.min(var12, var14);
      var16 = Math.min(var16, var8);
      return Math.min(var16, var10);
   }

   public EnumBorderStatus getStatus() {
      return this.endDiameter < this.startDiameter
         ? EnumBorderStatus.SHRINKING
         : (this.endDiameter > this.startDiameter ? EnumBorderStatus.GROWING : EnumBorderStatus.STATIONARY);
   }

   public double minX() {
      double var4 = this.getCenterX() - this.getDiameter() / 2.0;
      if (var4 < -this.worldSize) {
         var4 = -this.worldSize;
      }

      return var4;
   }

   public double minZ() {
      double var4 = this.getCenterZ() - this.getDiameter() / 2.0;
      if (var4 < -this.worldSize) {
         var4 = -this.worldSize;
      }

      return var4;
   }

   public double maxX() {
      double var4 = this.getCenterX() + this.getDiameter() / 2.0;
      if (var4 > this.worldSize) {
         var4 = this.worldSize;
      }

      return var4;
   }

   public double maxZ() {
      double var4 = this.getCenterZ() + this.getDiameter() / 2.0;
      if (var4 > this.worldSize) {
         var4 = this.worldSize;
      }

      return var4;
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double var1, double var3) {
      this.centerX = var1;
      this.centerZ = var3;

      for (IBorderListener var9 : this.getListeners()) {
         var9.onCenterChanged(this, var1, var3);
      }
   }

   public double getDiameter() {
      if (this.getStatus() != EnumBorderStatus.STATIONARY) {
         double var4 = (float)(System.currentTimeMillis() - this.startTime) / (float)(this.endTime - this.startTime);
         if (var4 < 1.0) {
            return this.startDiameter + (this.endDiameter - this.startDiameter) * var4;
         }

         this.setTransition(this.endDiameter);
      }

      return this.startDiameter;
   }

   public long getTimeUntilTarget() {
      return this.getStatus() != EnumBorderStatus.STATIONARY ? this.endTime - System.currentTimeMillis() : 0L;
   }

   public double getTargetSize() {
      return this.endDiameter;
   }

   public void setTransition(double var1) {
      this.startDiameter = var1;
      this.endDiameter = var1;
      this.endTime = System.currentTimeMillis();
      this.startTime = this.endTime;

      for (IBorderListener var7 : this.getListeners()) {
         var7.onSizeChanged(this, var1);
      }
   }

   public void setTransition(double var1, double var3, long var5) {
      this.startDiameter = var1;
      this.endDiameter = var3;
      this.startTime = System.currentTimeMillis();
      this.endTime = this.startTime + var5;

      for (IBorderListener var11 : this.getListeners()) {
         var11.onTransitionStarted(this, var1, var3, var5);
      }
   }

   public List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener var1) {
      this.listeners.add(var1);
   }

   public void setSize(int var1) {
      this.worldSize = var1;
   }

   public int getSize() {
      return this.worldSize;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public void setDamageBuffer(double var1) {
      this.damageBuffer = var1;

      for (IBorderListener var7 : this.getListeners()) {
         var7.onDamageBufferChanged(this, var1);
      }
   }

   public double getDamageAmount() {
      return this.damageAmount;
   }

   public void setDamageAmount(double var1) {
      this.damageAmount = var1;

      for (IBorderListener var7 : this.getListeners()) {
         var7.onDamageAmountChanged(this, var1);
      }
   }

   public double getResizeSpeed() {
      return this.endTime == this.startTime ? 0.0 : Math.abs(this.startDiameter - this.endDiameter) / (this.endTime - this.startTime);
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int var1) {
      this.warningTime = var1;

      for (IBorderListener var6 : this.getListeners()) {
         var6.onWarningTimeChanged(this, var1);
      }
   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public void setWarningDistance(int var1) {
      this.warningDistance = var1;

      for (IBorderListener var6 : this.getListeners()) {
         var6.onWarningDistanceChanged(this, var1);
      }
   }
}
