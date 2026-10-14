package net.minecraft.command;

public class CommandNotFoundException extends CommandException {
   private static final long a = 54537606045753L;

   public CommandNotFoundException() {
      this("commands.generic.notFound");
   }

   public CommandNotFoundException(String string, Object... objects) {
      super(string, objects);
   }
}
