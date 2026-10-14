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
   @Override
   public String getCommandName() {
      return "summon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.summon.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.summon.usage");
      }

      String string = args[0];
      BlockPos blockPos = sender.getPosition();
      Vec3 vec3 = sender.getPositionVector();
      double d = vec3.xCoord;
      double e = vec3.yCoord;
      double d2 = vec3.zCoord;
      if (args.length >= 4) {
         d = parseDouble(d, args[1], true);
         e = parseDouble(e, args[2], false);
         d2 = parseDouble(d2, args[3], true);
         blockPos = new BlockPos(d, e, d2);
      }

      World world = sender.getEntityWorld();
      if (!world.isBlockLoaded(blockPos)) {
         throw new CommandException("commands.summon.outOfWorld");
      }

      if ("LightningBolt".equals(string)) {
         world.addWeatherEffect(new EntityLightningBolt(world, d, e, d2));
         notifyOperators(sender, this, "commands.summon.success");
      } else {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         boolean bl = false;
         if (args.length >= 5) {
            IChatComponent iChatComponent = getChatComponentFromNthArg(sender, args, 4);

            try {
               nbtTagCompound = JsonToNBT.getTagFromJson(iChatComponent.getUnformattedText());
               bl = true;
            } catch (NBTException nbtException) {
               throw new CommandException("commands.summon.tagError", nbtException.getMessage());
            }
         }

         nbtTagCompound.setString("id", string);

         Entity entity;
         try {
            entity = EntityList.createEntityFromNBT(nbtTagCompound, world);
         } catch (RuntimeException runtimeException) {
            throw new CommandException("commands.summon.failed");
         }

         if (entity == null) {
            throw new CommandException("commands.summon.failed");
         }

         entity.setLocationAndAngles(d, e, d2, entity.rotationYaw, entity.rotationPitch);
         if (!bl && entity instanceof EntityLiving) {
            ((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
         }

         world.spawnEntityInWorld(entity);
         Entity entity2 = entity;

         for (NBTTagCompound nbtTagCompound2 = nbtTagCompound; entity2 != null && nbtTagCompound2.hasKey("Riding", 10); nbtTagCompound2 = nbtTagCompound2.getCompoundTag("Riding")) {
            Entity entity3 = EntityList.createEntityFromNBT(nbtTagCompound2.getCompoundTag("Riding"), world);
            if (entity3 != null) {
               entity3.setLocationAndAngles(d, e, d2, entity3.rotationYaw, entity3.rotationPitch);
               world.spawnEntityInWorld(entity3);
               entity2.mountEntity(entity3);
            }

            entity2 = entity3;
         }

         notifyOperators(sender, this, "commands.summon.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList())
         : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : null);
   }
}
