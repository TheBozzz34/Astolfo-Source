package bypass.gui.animation;

public class AnimationFactory {
   public Animation P(int var1) {
      return new Animation().setDuration(var1);
   }

   public Animation O(int var1) {
      return new TimedAnimation().setDuration(var1);
   }
}
