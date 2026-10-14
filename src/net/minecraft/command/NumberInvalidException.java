package net.minecraft.command;

public class NumberInvalidException extends CommandException {
   private static final long a = 139072754084749L;

   public NumberInvalidException() {
      this("commands.generic.num.invalid");
   }

   public NumberInvalidException(String message, Object... replacements) {
      super(message, replacements);
   }
}
