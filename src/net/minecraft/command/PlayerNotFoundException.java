package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
   private static final long a = 32216372424079L;

   public PlayerNotFoundException() {
      this("commands.generic.player.notFound");
   }

   public PlayerNotFoundException(String var1, Object... var2) {
      super(var1, var2);
   }
}
