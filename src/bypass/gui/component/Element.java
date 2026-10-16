package bypass.gui.component;

import bypass.ClientAccess;

public abstract class Element implements ClientAccess {
   private String name;
   private float xPos;
   private float yPos;

   public Element(String string, float f, float g) {
      this.name = string;
      this.xPos = f;
      this.yPos = g;
   }

   public abstract void render(int i, int j);

   public String getName() {
      return this.name;
   }

   public float getX() {
      return this.xPos;
   }

   public void setX(float f) {
      this.xPos = f;
   }

   public float getYPos() {
      return this.yPos;
   }

   public void setY(float f) {
      this.yPos = f;
   }
}
