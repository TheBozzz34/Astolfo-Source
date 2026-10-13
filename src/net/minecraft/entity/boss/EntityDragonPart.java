package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {
   public final IEntityMultiPart entityDragonObj;
   public final String partName;
   private static final long ab = 46301812791167L;

   public EntityDragonPart(IEntityMultiPart var1, String var2, float var3, float var4) {
      super(var1.getWorld());
      this.setSize(var3, var4);
      this.entityDragonObj = var1;
      this.partName = var2;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      return this.isEntityInvulnerable(var1) ? false : this.entityDragonObj.attackEntityFromPart(this, var1, var2);
   }

   @Override
   public boolean isEntityEqual(Entity var1) {
      return this == var1 || this.entityDragonObj == var1;
   }
}
