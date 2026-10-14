package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
   private static final long a = 32216372424079L;

   public PlayerNotFoundException() {
      this("commands.generic.player.notFound");
   }

   public PlayerNotFoundException(String message, Object... replacements) {
      super(message, replacements);
   }
}
