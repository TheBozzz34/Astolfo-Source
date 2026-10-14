package bypass.gui.component;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Container extends Component {
   private Component x;
   public Animation U;
   private ArrayList<Element> c;
   private boolean V;
   private static final long bb = 92307170903497L;
   private static final long eb = -845582793185427206L;

   public Container(String string, float f, float g, int i, int j) {
      super(string, f, g, i, j);
      this.U = new TimedAnimation().setDuration((int)eb).setEasing(Easing.LINEAR);
      this.c = new ArrayList<>();
   }

   public void a(Element... elements) {
      this.c.addAll(Arrays.asList(elements));
   }

   public ArrayList<Element> M_ArrayList() {
      return this.c;
   }

   public void u(boolean bl) {
      this.V = bl;
   }

   public boolean s_boolean() {
      return this.V;
   }

   public void f(Component component) {
      this.x = component;
   }

   public Component O() {
      return this.x;
   }
}
