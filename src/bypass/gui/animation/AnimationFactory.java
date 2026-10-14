package bypass.gui.animation;

public class AnimationFactory {
   public Animation P(int i) {
      return new Animation().setDuration(i);
   }

   public Animation O(int i) {
      return new TimedAnimation().setDuration(i);
   }
}
