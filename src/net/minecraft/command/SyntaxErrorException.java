package net.minecraft.command;

public class SyntaxErrorException extends CommandException {
   private static final long a = 62581459230347L;

   public SyntaxErrorException() {
      this("commands.generic.snytax");
   }

   public SyntaxErrorException(String message, Object... replacements) {
      super(message, replacements);
   }
}
