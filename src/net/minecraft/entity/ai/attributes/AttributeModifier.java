package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   private final double amount;
   private final int operation;
   private final String name;
   private final UUID id;
   private boolean isSaved = true;
   private static final long a = 121739643896534L;

   public AttributeModifier(String nameIn, double amountIn, int operationIn) {
      this(MathHelper.getRandomUuid(ThreadLocalRandom.current()), nameIn, amountIn, operationIn);
   }

   public AttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
      this.id = idIn;
      this.name = nameIn;
      this.amount = amountIn;
      this.operation = operationIn;
      Validate.notEmpty(nameIn, "Modifier name cannot be empty");
      Validate.inclusiveBetween(0L, 2L, operationIn, "Invalid operation");
   }

   public UUID getID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public int getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean isSaved() {
      return this.isSaved;
   }

   public AttributeModifier setSaved(boolean saved) {
      this.isSaved = saved;
      return this;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object != null && this.getClass() == object.getClass()) {
         AttributeModifier attributeModifier = (AttributeModifier)object;
         if (this.id != null) {
            if (!this.id.equals(attributeModifier.id)) {
               return false;
            }
         } else if (attributeModifier.id != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "AttributeModifier{amount="
         + this.amount
         + ", operation="
         + this.operation
         + ", name='"
         + this.name
         + '\''
         + ", id="
         + this.id
         + ", serialize="
         + this.isSaved
         + '}';
   }
}
