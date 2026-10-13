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

   public Container(String var1, float var2, float var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.U = new TimedAnimation().setDuration((int)eb).setEasing(Easing.LINEAR);
      this.c = new ArrayList<>();
   }

   public void a(Element... var1) {
      this.c.addAll(Arrays.asList(var1));
   }

   public ArrayList<Element> M_ArrayList() {
      return this.c;
   }

   public void u(boolean var1) {
      this.V = var1;
   }

   public boolean s_boolean() {
      return this.V;
   }

   public void f(Component var1) {
      this.x = var1;
   }

   public Component O() {
      return this.x;
   }
}
