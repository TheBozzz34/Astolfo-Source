package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.setting.NumberSetting;

public abstract class HudElement extends Component {
   private boolean j;
   private NumberSetting<Float> posX = new NumberSetting<>("Pos-X", "Pos-X", 0.0F, 0.0F, 10000.0F);
   private NumberSetting<Float> posY = new NumberSetting<>("Pos-Y", "Pos-Y", 0.0F, 0.0F, 10000.0F);
   private final NumberSetting<Float> scale = new NumberSetting<>("Scale", "Scale", 2.0F, 1.0F, 4.0F);
   private static final long c = 5493745151876L;

   public HudElement(String string, float f, float g, int i, int j) {
      super(string, f, g, i, j);
   }

   public boolean N() {
      return this.j;
   }

   public void f(boolean bl) {
      this.j = bl;
   }

   public NumberSetting<Float> getPosX() {
      return this.posX;
   }

   public NumberSetting<Float> getPosY() {
      return this.posY;
   }

   public NumberSetting<Float> getScale() {
      return this.scale;
   }
}
