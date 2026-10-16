package bypass.gui.component;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;

public abstract class Component extends Element {
   private int width;
   private int height;
   private boolean draggable;
   private boolean dragging;
   private float dragOffsetX;
   private float dragOffsetY;
   public Animation stateAnimation = new TimedAnimation().setDuration(100).setEasing(Easing.INERTIA);

   public Component(String string, float f, float g, int i, int j) {
      super(string, f, g);
      this.width = i;
      this.height = j;
   }

   public void updateDragPosition(int i, int j) {
      this.setPosition(i - this.dragOffsetX, j - this.dragOffsetY);
   }

   public void onMouseClicked(int i, int j, int k) {
      this.onClick(i);
   }

   public abstract void onClick(int i);

   public void onKeyTyped(char c, int i) {
   }

   public void onMouseScroll(int i, int j, int k) {
   }

   public boolean isHovered(int i, int j) {
      return i >= this.getX() && i <= this.getX() + this.getWidth() && j >= this.getYPos() && j <= this.getYPos() + this.getHeight();
   }

   public boolean isMouseInRect(int i, int j, int k, int l, int m, int n) {
      return m >= i && m <= i + k && n >= j && n <= j + l;
   }

   public void setIntPosition(int i, int j) {
      this.setX(i);
      this.setY(j);
   }

   public void setPosition(float f, float g) {
      this.setX(f);
      this.setY(g);
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int i) {
      this.width = i;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int i) {
      this.height = i;
   }

   public void setDragging(boolean bl, int i, int j) {
      if (bl) {
         this.dragOffsetX = i - this.getX();
         this.dragOffsetY = j - this.getYPos();
      }

      this.dragging = bl;
   }

   public void setDraggingNoOffset(boolean bl) {
      this.dragOffsetX = 0.0F;
      this.dragOffsetY = 0.0F;
      this.dragging = bl;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public boolean isDraggable() {
      return this.draggable;
   }

   public void setDraggable(boolean bl) {
      this.draggable = bl;
   }
}
