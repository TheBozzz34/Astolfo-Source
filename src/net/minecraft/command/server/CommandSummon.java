package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase {
   private static final long b = 64000130864738L;
   private static final long j = -8520344701486759926L;

   @Override
   public String getCommandName() {
      return "summon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.summon.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.summon.usage");
      }

      String var6 = var2[0];
      BlockPos var7 = var1.getPosition();
      Vec3 var8 = var1.getPositionVector();
      double var9 = var8.xCoord;
      double var11 = var8.yCoord;
      double var13 = var8.zCoord;
      if (var2.length >= 4) {
         var9 = parseDouble(var9, var2[1], true);
         var11 = parseDouble(var11, var2[2], false);
         var13 = parseDouble(var13, var2[3], true);
         var7 = new BlockPos(var9, var11, var13);
      }

      World var15 = var1.getEntityWorld();
      if (!var15.isBlockLoaded(var7)) {
         throw new CommandException("commands.summon.outOfWorld");
      }

      if ("LightningBolt".equals(var6)) {
         var15.addWeatherEffect(new EntityLightningBolt(var15, var9, var11, var13));
         notifyOperators(var1, this, "commands.summon.success");
      } else {
         NBTTagCompound var16 = new NBTTagCompound();
         boolean var17 = false;
         if (var2.length >= 5) {
            IChatComponent var18 = getChatComponentFromNthArg(var1, var2, 4);

            try {
               var16 = JsonToNBT.getTagFromJson(var18.getUnformattedText());
               var17 = true;
            } catch (NBTException var23) {
               throw new CommandException("commands.summon.tagError", var23.getMessage());
            }
         }

         var16.setString("id", var6);

         Entity var24;
         try {
            var24 = EntityList.createEntityFromNBT(var16, var15);
         } catch (RuntimeException var22) {
            throw new CommandException("commands.summon.failed");
         }

         if (var24 == null) {
            throw new CommandException("commands.summon.failed");
         }

         var24.setLocationAndAngles(var9, var11, var13, var24.rotationYaw, var24.rotationPitch);
         if (!var17 && var24 instanceof EntityLiving) {
            ((EntityLiving)var24).onInitialSpawn(var15.getDifficultyForLocation(new BlockPos(var24)), (IEntityLivingData)null);
         }

         var15.spawnEntityInWorld(var24);
         Entity var19 = var24;

         for (NBTTagCompound var20 = var16; var19 != null && var20.hasKey("Riding", (int)j); var20 = var20.getCompoundTag("Riding")) {
            Entity var21 = EntityList.createEntityFromNBT(var20.getCompoundTag("Riding"), var15);
            if (var21 != null) {
               var21.setLocationAndAngles(var9, var11, var13, var21.rotationYaw, var21.rotationPitch);
               var15.spawnEntityInWorld(var21);
               var19.mountEntity(var21);
            }

            var19 = var21;
         }

         notifyOperators(var1, this, "commands.summon.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, EntityList.getEntityNameList())
         : (var2.length > 1 && var2.length <= 4 ? func_175771_a(var2, 1, var3) : null);
   }
}
