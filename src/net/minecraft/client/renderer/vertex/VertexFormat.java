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

   public VertexFormat(VertexFormat vertexFormatIn) {
      this();

      for (int i = 0; i < vertexFormatIn.getElementCount(); i++) {
         this.addElement(vertexFormatIn.getElement(i));
      }

      this.nextOffset = vertexFormatIn.getNextOffset();
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

   public VertexFormat addElement(VertexFormatElement element) {
      if (element.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      }

      this.elements.add(element);
      this.offsets.add(this.nextOffset);
      switch (element.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.nextOffset;
            break;
         case COLOR:
            this.colorElementOffset = this.nextOffset;
            break;
         case UV:
            this.uvOffsetsById.add(element.getIndex(), this.nextOffset);
      }

      this.nextOffset = this.nextOffset + element.getSize();
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

   public boolean hasUvOffset(int id) {
      return this.uvOffsetsById.size() - 1 >= id;
   }

   public int getUvOffsetById(int id) {
      return this.uvOffsetsById.get(id);
   }

   @Override
   public String toString() {
      String string = "format: " + this.elements.size() + " elements: ";

      for (int i = 0; i < this.elements.size(); i++) {
         string = string + this.elements.get(i).toString();
         if (i != this.elements.size() - 1) {
            string = string + " ";
         }
      }

      return string;
   }

   private boolean hasPosition() {
      int i = 0;

      for (int j = this.elements.size(); i < j; i++) {
         VertexFormatElement vertexFormatElement = this.elements.get(i);
         if (vertexFormatElement.isPositionElement()) {
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

   public VertexFormatElement getElement(int index) {
      return this.elements.get(index);
   }

   public int getOffset(int i) {
      return this.offsets.get(i);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         VertexFormat vertexFormat = (VertexFormat)object;
         return this.nextOffset != vertexFormat.nextOffset ? false : (!this.elements.equals(vertexFormat.elements) ? false : this.offsets.equals(vertexFormat.offsets));
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      return 31 * i + this.nextOffset;
   }
}
