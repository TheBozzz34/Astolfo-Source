package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<VertexFormatElement> elements = Lists.newArrayList();
   private final List<Integer> offsets = Lists.newArrayList();
   private int nextOffset = 0;
   private int colorElementOffset = -1;
   private List<Integer> uvOffsetsById = Lists.newArrayList();
   private int normalElementOffset = -1;
   private static final long a = 78468434692309L;

   public VertexFormat(VertexFormat var1) {
      this();

      for (int var5 = 0; var5 < var1.getElementCount(); var5++) {
         this.addElement(var1.getElement(var5));
      }

      this.nextOffset = var1.getNextOffset();
   }

   public VertexFormat() {
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.normalElementOffset = -1;
      this.nextOffset = 0;
   }

   public VertexFormat addElement(VertexFormatElement var1) {
      if (var1.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      }

      this.elements.add(var1);
      this.offsets.add(this.nextOffset);
      switch (var1.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.nextOffset;
            break;
         case COLOR:
            this.colorElementOffset = this.nextOffset;
            break;
         case UV:
            this.uvOffsetsById.add(var1.getIndex(), this.nextOffset);
      }

      this.nextOffset = this.nextOffset + var1.getSize();
      return this;
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public boolean H() {
      return this.colorElementOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasUvOffset(int var1) {
      return this.uvOffsetsById.size() - 1 >= var1;
   }

   public int getUvOffsetById(int var1) {
      return this.uvOffsetsById.get(var1);
   }

   @Override
   public String toString() {
      String var4 = "format: " + this.elements.size() + " elements: ";

      for (int var5 = 0; var5 < this.elements.size(); var5++) {
         var4 = var4 + this.elements.get(var5).toString();
         if (var5 != this.elements.size() - 1) {
            var4 = var4 + " ";
         }
      }

      return var4;
   }

   private boolean hasPosition() {
      int var3 = 0;

      for (int var4 = this.elements.size(); var3 < var4; var3++) {
         VertexFormatElement var5 = this.elements.get(var3);
         if (var5.isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getNextOffset() / 4;
   }

   public int getNextOffset() {
      return this.nextOffset;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int var1) {
      return this.elements.get(var1);
   }

   public int getOffset(int var1) {
      return this.offsets.get(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormat var5 = (VertexFormat)var1;
         return this.nextOffset != var5.nextOffset ? false : (!this.elements.equals(var5.elements) ? false : this.offsets.equals(var5.offsets));
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var3 = this.elements.hashCode();
      var3 = 31 * var3 + this.offsets.hashCode();
      return 31 * var3 + this.nextOffset;
   }
}
