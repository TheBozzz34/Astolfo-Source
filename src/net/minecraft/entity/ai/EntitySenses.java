package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
   public EntityLiving entityObj;
   public List<Entity> seenEntities = Lists.newArrayList();
   public List<Entity> unseenEntities = Lists.newArrayList();
   private static final long a = 131288285204801L;

   public EntitySenses(EntityLiving var1) {
      this.entityObj = var1;
   }

   public void clearSensingCache() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public boolean canSee(Entity var1) {
      if (this.seenEntities.contains(var1)) {
         return true;
      }

      if (this.unseenEntities.contains(var1)) {
         return false;
      }

      this.entityObj.worldObj.theProfiler.startSection("canSee");
      boolean var5 = this.entityObj.C(var1);
      this.entityObj.worldObj.theProfiler.endSection();
      if (var5) {
         this.seenEntities.add(var1);
      } else {
         this.unseenEntities.add(var1);
      }

      return var5;
   }
}
