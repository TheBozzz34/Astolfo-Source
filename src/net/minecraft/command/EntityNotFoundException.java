package net.minecraft.command;

public class EntityNotFoundException extends CommandException {
   private static final long a = 6855010593566L;

   public EntityNotFoundException() {
      this("commands.generic.entity.notFound");
   }

   public EntityNotFoundException(String string, Object... objects) {
      super(string, objects);
   }
}
