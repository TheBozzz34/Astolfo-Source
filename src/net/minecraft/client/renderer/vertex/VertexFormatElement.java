package net.minecraft.client.renderer.vertex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.EnumType type;
   private final VertexFormatElement.EnumUsage usage;
   private int index;
   private int elementCount;
   private static final long a = 110602970323374L;

   public VertexFormatElement(int var1, VertexFormatElement.EnumType var2, VertexFormatElement.EnumUsage var3, int var4) {
      if (!this.func_177372_a(var1, var3)) {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.EnumUsage.UV;
      } else {
         this.usage = var3;
      }

      this.type = var2;
      this.index = var1;
      this.elementCount = var4;
   }

   private final boolean func_177372_a(int var1, VertexFormatElement.EnumUsage var2) {
      return var1 == 0 || var2 == VertexFormatElement.EnumUsage.UV;
   }

   public final VertexFormatElement.EnumType getType() {
      return this.type;
   }

   public final VertexFormatElement.EnumUsage getUsage() {
      return this.usage;
   }

   public final int getElementCount() {
      return this.elementCount;
   }

   public final int getIndex() {
      return this.index;
   }

   @Override
   public String toString() {
      return this.elementCount + "," + this.usage.getDisplayName() + "," + this.type.getDisplayName();
   }

   public final int getSize() {
      return this.type.getSize() * this.elementCount;
   }

   public final boolean isPositionElement() {
      return this.usage == VertexFormatElement.EnumUsage.POSITION;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormatElement var5 = (VertexFormatElement)var1;
         return this.elementCount != var5.elementCount
            ? false
            : (this.index != var5.index ? false : (this.type != var5.type ? false : this.usage == var5.usage));
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var5 = this.type.hashCode();
      var5 = 31 * var5 + this.usage.hashCode();
      var5 = 31 * var5 + this.index;
      return 31 * var5 + this.elementCount;
   }

   public enum EnumType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String displayName;
      private final int glConstant;

      EnumType(int var3, String var4, int var5) {
         this.size = var3;
         this.displayName = var4;
         this.glConstant = var5;
      }

      public int getSize() {
         return this.size;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public int getGlConstant() {
         return this.glConstant;
      }
   }

   public enum EnumUsage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String displayName;

      EnumUsage(String var3) {
         this.displayName = var3;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
