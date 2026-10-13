package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   private static final Logger logger = LogManager.getLogger();
   private List<EntityAITasks.EntityAITaskEntry> taskEntries = Lists.newArrayList();
   private List<EntityAITasks.EntityAITaskEntry> executingTaskEntries = Lists.newArrayList();
   private final Profiler theProfiler;
   private int tickCount;
   private int tickRate;
   private static final long a = 18105175025529L;
   private static final long e = -4211324311148953597L;

   public EntityAITasks(Profiler var1) {
      this.tickRate = (int)e;
      this.theProfiler = var1;
   }

   public void addTask(int var1, EntityAIBase var2) {
      this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(var1, var2));
   }

   public void removeTask(EntityAIBase var1) {
      Iterator var5 = this.taskEntries.iterator();

      while (var5.hasNext()) {
         EntityAITasks.EntityAITaskEntry var6 = (EntityAITasks.EntityAITaskEntry)var5.next();
         EntityAIBase var7 = var6.action;
         if (var7 == var1) {
            if (this.executingTaskEntries.contains(var6)) {
               var7.resetTask();
               this.executingTaskEntries.remove(var6);
            }

            var5.remove();
         }
      }
   }

   public void onUpdateTasks() {
      this.theProfiler.startSection("goalSetup");
      if (this.tickCount++ % this.tickRate == 0) {
         for (EntityAITasks.EntityAITaskEntry var5 : this.taskEntries) {
            boolean var6 = this.executingTaskEntries.contains(var5);
            if (var6) {
               if (this.canUse(var5) && this.canContinue(var5)) {
                  continue;
               }

               var5.action.resetTask();
               this.executingTaskEntries.remove(var5);
            }

            if (this.canUse(var5) && var5.action.shouldExecute()) {
               var5.action.startExecuting();
               this.executingTaskEntries.add(var5);
            }
         }
      } else {
         Iterator var7 = this.executingTaskEntries.iterator();

         while (var7.hasNext()) {
            EntityAITasks.EntityAITaskEntry var9 = (EntityAITasks.EntityAITaskEntry)var7.next();
            if (!this.canContinue(var9)) {
               var9.action.resetTask();
               var7.remove();
            }
         }
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("goalTick");

      for (EntityAITasks.EntityAITaskEntry var10 : this.executingTaskEntries) {
         var10.action.updateTask();
      }

      this.theProfiler.endSection();
   }

   private boolean canContinue(EntityAITasks.EntityAITaskEntry var1) {
      return var1.action.continueExecuting();
   }

   private boolean canUse(EntityAITasks.EntityAITaskEntry var1) {
      for (EntityAITasks.EntityAITaskEntry var6 : this.taskEntries) {
         if (var6 != var1) {
            if (var1.priority >= var6.priority) {
               if (!this.areTasksCompatible(var1, var6) && this.executingTaskEntries.contains(var6)) {
                  return false;
               }
            } else if (!var6.action.isInterruptible() && this.executingTaskEntries.contains(var6)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry var1, EntityAITasks.EntityAITaskEntry var2) {
      return (var1.action.getMutexBits() & var2.action.getMutexBits()) == 0;
   }

   public class EntityAITaskEntry {
      public EntityAIBase action;
      public int priority;

      public EntityAITaskEntry(int var2, EntityAIBase var3) {
         this.priority = var2;
         this.action = var3;
      }
   }
}
