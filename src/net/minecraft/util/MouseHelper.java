package net.minecraft.util;

import bypass.auth.AuthSession;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper {
   public int deltaX;
   public int deltaY;
   private AuthSession b = new AuthSession();

   public void grabMouseCursor() {
      Mouse.setGrabbed(true);
      this.deltaX = 0;
      this.deltaY = 0;
   }

   public void ungrabMouseCursor() {
      Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
      Mouse.setGrabbed(false);
   }

   public void mouseXYChange() {
      this.deltaX = Mouse.getDX();
      this.deltaY = Mouse.getDY();
   }

   public int a() {
      return this.deltaX;
   }

   public int z() {
      return this.deltaY;
   }

   public AuthSession Y() {
      return this.b;
   }
}
