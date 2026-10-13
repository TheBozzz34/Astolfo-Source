package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
   private static final long b = 86061445913258L;

   @Override
   public String getCommandName() {
      return "difficulty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.difficulty.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.difficulty.usage");
      }

      EnumDifficulty var5 = this.getDifficultyFromCommand(var2[0]);
      MinecraftServer.getServer().setDifficultyForAllWorlds(var5);
      notifyOperators(var1, this, "commands.difficulty.success", new ChatComponentTranslation(var5.getDifficultyResourceKey()));
   }

   public EnumDifficulty getDifficultyFromCommand(String var1) throws CommandException, NumberInvalidException {
      return var1.equalsIgnoreCase("peaceful") || var1.equalsIgnoreCase("p")
         ? EnumDifficulty.PEACEFUL
         : (
            var1.equalsIgnoreCase("easy") || var1.equalsIgnoreCase("e")
               ? EnumDifficulty.EASY
               : (
                  !var1.equalsIgnoreCase("normal") && !var1.equalsIgnoreCase("n")
                     ? (
                        !var1.equalsIgnoreCase("hard") && !var1.equalsIgnoreCase("h")
                           ? EnumDifficulty.getDifficultyEnum(parseInt(var1, 0, 3))
                           : EnumDifficulty.HARD
                     )
                     : EnumDifficulty.NORMAL
               )
         );
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, "peaceful", "easy", "normal", "hard") : null;
   }
}
