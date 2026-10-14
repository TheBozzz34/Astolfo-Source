package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
   public EntityLiving entityObj;
   public List<Entity> seenEntities = Lists.newArrayList();
   public List<Entity> unseenEntities = Lists.newArrayList();

   public EntitySenses(EntityLiving entityObjIn) {
      this.entityObj = entityObjIn;
   }

   public void clearSensingCache() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public boolean canSee(Entity entityIn) {
      if (this.seenEntities.contains(entityIn)) {
         return true;
      }

      if (this.unseenEntities.contains(entityIn)) {
         return false;
      }

      this.entityObj.worldObj.theProfiler.startSection("canSee");
      boolean bl = this.entityObj.C(entityIn);
      this.entityObj.worldObj.theProfiler.endSection();
      if (bl) {
         this.seenEntities.add(entityIn);
      } else {
         this.unseenEntities.add(entityIn);
      }

      return bl;
   }
}
