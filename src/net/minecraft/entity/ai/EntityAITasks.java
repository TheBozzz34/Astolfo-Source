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
   private int tickRate = 3;

   public EntityAITasks(Profiler profilerIn) {
      this.theProfiler = profilerIn;
   }

   public void addTask(int priority, EntityAIBase task) {
      this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(priority, task));
   }

   public void removeTask(EntityAIBase task) {
      Iterator iterator2 = this.taskEntries.iterator();

      while (iterator2.hasNext()) {
         EntityAITasks.EntityAITaskEntry entityAITaskEntry = (EntityAITasks.EntityAITaskEntry)iterator2.next();
         EntityAIBase entityAIBase = entityAITaskEntry.action;
         if (entityAIBase == task) {
            if (this.executingTaskEntries.contains(entityAITaskEntry)) {
               entityAIBase.resetTask();
               this.executingTaskEntries.remove(entityAITaskEntry);
            }

            iterator2.remove();
         }
      }
   }

   public void onUpdateTasks() {
      this.theProfiler.startSection("goalSetup");
      if (this.tickCount++ % this.tickRate == 0) {
         for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : this.taskEntries) {
            boolean bl = this.executingTaskEntries.contains(entityAITaskEntry);
            if (bl) {
               if (this.canUse(entityAITaskEntry) && this.canContinue(entityAITaskEntry)) {
                  continue;
               }

               entityAITaskEntry.action.resetTask();
               this.executingTaskEntries.remove(entityAITaskEntry);
            }

            if (this.canUse(entityAITaskEntry) && entityAITaskEntry.action.shouldExecute()) {
               entityAITaskEntry.action.startExecuting();
               this.executingTaskEntries.add(entityAITaskEntry);
            }
         }
      } else {
         Iterator iterator2 = this.executingTaskEntries.iterator();

         while (iterator2.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityAITaskEntry2 = (EntityAITasks.EntityAITaskEntry)iterator2.next();
            if (!this.canContinue(entityAITaskEntry2)) {
               entityAITaskEntry2.action.resetTask();
               iterator2.remove();
            }
         }
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("goalTick");

      for (EntityAITasks.EntityAITaskEntry entityAITaskEntry3 : this.executingTaskEntries) {
         entityAITaskEntry3.action.updateTask();
      }

      this.theProfiler.endSection();
   }

   private boolean canContinue(EntityAITasks.EntityAITaskEntry taskEntry) {
      return taskEntry.action.continueExecuting();
   }

   private boolean canUse(EntityAITasks.EntityAITaskEntry taskEntry) {
      for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : this.taskEntries) {
         if (entityAITaskEntry != taskEntry) {
            if (taskEntry.priority >= entityAITaskEntry.priority) {
               if (!this.areTasksCompatible(taskEntry, entityAITaskEntry) && this.executingTaskEntries.contains(entityAITaskEntry)) {
                  return false;
               }
            } else if (!entityAITaskEntry.action.isInterruptible() && this.executingTaskEntries.contains(entityAITaskEntry)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry taskEntry1, EntityAITasks.EntityAITaskEntry taskEntry2) {
      return (taskEntry1.action.getMutexBits() & taskEntry2.action.getMutexBits()) == 0;
   }

   public class EntityAITaskEntry {
      public EntityAIBase action;
      public int priority;

      public EntityAITaskEntry(int priorityIn, EntityAIBase task) {
         this.priority = priorityIn;
         this.action = task;
      }
   }
}
