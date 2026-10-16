package bypass.gui.component;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Container extends Component {
   private Component parent;
   public Animation expandAnimation = new TimedAnimation().setDuration(250).setEasing(Easing.LINEAR);
   private ArrayList<Element> children = new ArrayList<>();
   private boolean expanded;

   public Container(String string, float f, float g, int i, int j) {
      super(string, f, g, i, j);
   }

   public void addChildren(Element... elements) {
      this.children.addAll(Arrays.asList(elements));
   }

   public ArrayList<Element> getChildren() {
      return this.children;
   }

   public void setExpanded(boolean bl) {
      this.expanded = bl;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setParent(Component component) {
      this.parent = component;
   }

   public Component getParent() {
      return this.parent;
   }
}
