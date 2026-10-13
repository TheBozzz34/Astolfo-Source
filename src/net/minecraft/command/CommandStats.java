package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats extends CommandBase {
   private static final long b = 44937080297024L;

   @Override
   public String getCommandName() {
      return "stats";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.stats.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.stats.usage");
      }

      boolean var6;
      if (var2[0].equals("entity")) {
         var6 = false;
      } else {
         if (!var2[0].equals("block")) {
            throw new WrongUsageException("commands.stats.usage");
         }

         var6 = true;
      }

      int var7;
      if (var6) {
         if (var2.length < 5) {
            throw new WrongUsageException("commands.stats.block.usage");
         }

         var7 = 4;
      } else {
         if (var2.length < 3) {
            throw new WrongUsageException("commands.stats.entity.usage");
         }

         var7 = 2;
      }

      String var8 = var2[var7++];
      if ("set".equals(var8)) {
         if (var2.length < var7 + 3) {
            if (var7 == 5) {
               throw new WrongUsageException("commands.stats.block.set.usage");
            }

            throw new WrongUsageException("commands.stats.entity.set.usage");
         }
      } else {
         if (!"clear".equals(var8)) {
            throw new WrongUsageException("commands.stats.usage");
         }

         if (var2.length < var7 + 1) {
            if (var7 == 5) {
               throw new WrongUsageException("commands.stats.block.clear.usage");
            }

            throw new WrongUsageException("commands.stats.entity.clear.usage");
         }
      }

      CommandResultStats.Type var9 = CommandResultStats.Type.getTypeByName(var2[var7++]);
      if (var9 == null) {
         throw new CommandException("commands.stats.failed");
      }

      World var10 = var1.getEntityWorld();
      CommandResultStats var11;
      if (var6) {
         BlockPos var12 = parseBlockPos(var1, var2, 1, false);
         TileEntity var13 = var10.getTileEntity(var12);
         if (var13 == null) {
            throw new CommandException("commands.stats.noCompatibleBlock", var12.getX(), var12.getY(), var12.getZ());
         }

         if (var13 instanceof TileEntityCommandBlock) {
            var11 = ((TileEntityCommandBlock)var13).getCommandResultStats();
         } else {
            if (!(var13 instanceof TileEntitySign)) {
               throw new CommandException("commands.stats.noCompatibleBlock", var12.getX(), var12.getY(), var12.getZ());
            }

            var11 = ((TileEntitySign)var13).getStats();
         }
      } else {
         Entity var17 = getEntity(var1, var2[1]);
         var11 = var17.getCommandStats();
      }

      if ("set".equals(var8)) {
         String var18 = var2[var7++];
         String var20 = var2[var7];
         if (var18.length() == 0 || var20.length() == 0) {
            throw new CommandException("commands.stats.failed");
         }

         CommandResultStats.setScoreBoardStat(var11, var9, var18, var20);
         notifyOperators(var1, this, "commands.stats.success", var9.getTypeName(), var20, var18);
      } else if ("clear".equals(var8)) {
         CommandResultStats.setScoreBoardStat(var11, var9, (String)null, (String)null);
         notifyOperators(var1, this, "commands.stats.cleared", var9.getTypeName());
      }

      if (var6) {
         BlockPos var19 = parseBlockPos(var1, var2, 1, false);
         TileEntity var21 = var10.getTileEntity(var19);
         var21.markDirty();
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, "entity", "block")
         : (
            var2.length == 2 && var2[0].equals("entity")
               ? getListOfStringsMatchingLastWord(var2, this.func_175776_d())
               : (
                  var2.length >= 2 && var2.length <= 4 && var2[0].equals("block")
                     ? func_175771_a(var2, 1, var3)
                     : (
                        (var2.length != 3 || !var2[0].equals("entity")) && (var2.length != 5 || !var2[0].equals("block"))
                           ? (
                              (var2.length != 4 || !var2[0].equals("entity")) && (var2.length != 6 || !var2[0].equals("block"))
                                 ? (
                                    var2.length == 6 && var2[0].equals("entity") || var2.length == 8 && var2[0].equals("block")
                                       ? getListOfStringsMatchingLastWord(var2, this.func_175777_e())
                                       : null
                                 )
                                 : getListOfStringsMatchingLastWord(var2, CommandResultStats.Type.getTypeNames())
                           )
                           : getListOfStringsMatchingLastWord(var2, "set", "clear")
                     )
               )
         );
   }

   public String[] func_175776_d() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   public List<String> func_175777_e() {
      Collection var4 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
      ArrayList var5 = Lists.newArrayList();

      for (Object var7$raw : var4) {
         ScoreObjective var7 = (ScoreObjective)var7$raw;
         if (!var7.getCriteria().isReadOnly()) {
            var5.add(var7.getName());
         }
      }

      return var5;
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var1.length > 0 && var1[0].equals("entity") && var2 == 1;
   }
}
