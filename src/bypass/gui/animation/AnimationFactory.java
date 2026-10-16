package bypass.gui.animation;

public class AnimationFactory {
   public Animation createAnimation(int i) {
      return new Animation().setDuration(i);
   }

   public Animation createTimedAnimation(int i) {
      return new TimedAnimation().setDuration(i);
   }
}
