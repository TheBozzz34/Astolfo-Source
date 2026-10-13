package bypass.gui;

import bypass.Astolfo;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.game.KeyPressEvent;
import bypass.gui.tabgui.CategoryItem;
import bypass.gui.tabgui.ListAccessor;
import bypass.gui.tabgui.PanelState;
import bypass.gui.tabgui.Sized;
import bypass.gui.tabgui.TabGuiItem;
import bypass.gui.tabgui.TabGuiPanel;
import bypass.gui.tabgui.render.DefaultRendererSet;
import bypass.gui.tabgui.render.RendererSet;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.util.Stopwatch;
import com.google.common.collect.Lists;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class TabGui implements ListAccessor<TabGui.Panel>, Sized {
   private float z;
   private float r;
   private final Stopwatch Y = new Stopwatch();
   private final List<TabGui.Panel> C = new ArrayList<>();
   private RendererSet N;
   private final Dimension e = new Dimension(0, 0);
   @EventHandler(KeyPressEvent.class)
   private Listener<KeyPressEvent> onKeyPress = var1 -> {
      if (HUD.tabGui.getValue()) {
         if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            int var5x = this.L().size();

            for (int var6 = 0; var6 < var5x; var6++) {
               TabGui.Panel var7 = this.L().get(var6);
               var7.onKeyPressed(var1.getKey());
            }
         }
      }
   };
   private static final long b = 96522318301394L;

   public TabGui() {
      this.N = new DefaultRendererSet();
      TabGui.Panel var5 = new TabGui.Panel(this);

      for (Category var9 : Category.values()) {
         if (!var9.equals(Category.HIDDEN) && !var9.equals(Category.CONFIG) && !var9.equals(Category.SCRIPTS)) {
            var5.addItem(new CategoryItem(var5, var9));
         }
      }

      this.z = 0.0F;
      this.r = 2.0F;
      this.s(var5);
      Astolfo.INSTANCE.getEventBus().subscribe(this);
   }

   public void render(ScaledResolution var1, Minecraft var2) {
      boolean var6 = this.Y.hasElapsed(16.0F);
      if (var6) {
         this.Y.reset();
      }

      TabGui.Panel var7 = null;
      int var8 = this.C.size();

      for (int var9 = 0; var9 < var8; var9++) {
         try {
            var7 = this.C.get(var9);
         } catch (IndexOutOfBoundsException var11) {
         }

         GL11.glEnable(3089);
         if (var7.getState() == PanelState.CLOSING) {
            A(this.J() + var7.F(), this.Q(), (int)this.J() + var7.F() + (int)var7.j(), this.Q() + (float)var7.x().getHeight());
         } else if (var7.getState() == PanelState.OPENING) {
            A(this.J() + var7.F(), this.Q(), this.J() + var7.F() + var7.j(), this.Q() + (float)var7.x().getHeight());
         } else if (var7.getState() == PanelState.NORMAL) {
            A(this.J() + var7.F(), this.Q(), this.J() + var7.F() + (float)var7.x().getWidth(), this.Q() + (float)var7.x().getHeight());
         }

         this.N.get((Class<? extends Sized>)var7.getClass()).P(var7, var1, var2);
         if (var6) {
            this.N.get((Class<? extends Sized>)var7.getClass()).v(var7, var1, var2);
         }

         var7.getItems().forEach(var4 -> {
            this.N.get((Class<? extends Sized>)var4.getClass()).P(var4, var1, var2);
            if (var6) {
               this.N.get((Class<? extends Sized>)var4.getClass()).v(var4, var1, var2);
            }
         });
         GL11.glDisable(3089);
      }
   }

   public static void A(float var0, float var1, float var2, float var3) {
      float var7 = HUD.hudScale.getValue();
      GL11.glScissor((int)(var0 * var7), (int)((Display.getHeight() / var7 - var3) * var7), (int)((var2 - var0) * var7), (int)((var3 - var1) * var7));
   }

   public List<TabGui.Panel> L() {
      return this.C;
   }

   public void Z(RendererSet var1) {
      this.N = var1;
   }

   public RendererSet p() {
      return this.N;
   }

   public TabGui.Panel Q_r6q() {
      return this.w();
   }

   public void s(TabGui.Panel var1) {
      this.L().add(var1);
      var1.refreshSize();
   }

   @Override
   public Dimension x() {
      return this.e;
   }

   public float J() {
      return this.z;
   }

   public float Q() {
      return this.r;
   }

   public void C(float var1) {
      this.z = var1;
   }

   public void P(float var1) {
      this.r = var1;
   }

   public static class Panel implements Sized, TabGuiPanel {
      private final TabGui J;
      private final List<TabGuiItem> A;
      private int b;
      private float D;
      private float E;
      public float h;
      private float x = 0.0F;
      private PanelState n;
      private final Dimension R = new Dimension(0, 0);
      private static final long d = 67642182782063L;

      @Override
      public PanelState getState() {
         return this.n;
      }

      @Override
      public void setState(PanelState var1) {
         this.n = var1;
      }

      public Panel(TabGui var1, TabGuiItem... var2) {
         this.J = var1;
         this.A = Lists.newArrayList(var2);
         this.D = 0.0F;
         int var7 = var1.L().size();

         for (int var8 = 0; var8 < var7; var8++) {
            this.D = (float)(this.D + var1.L().get(var8).x().getWidth());
            this.D += 3.0F;
         }

         this.E = 30.0F;
         this.n = PanelState.OPENING;
      }

      public Panel(TabGui var1, Collection<TabGuiItem> var2) {
         this.J = var1;
         this.A = new ArrayList<>(var2);
      }

      @Override
      public List<TabGuiItem> getItems() {
         return this.A;
      }

      @Override
      public int getSelectedIndex() {
         return this.b;
      }

      @Override
      public void setSelectedIndex(int var1) {
         this.b = var1;
      }

      @Override
      public Dimension x() {
         return this.R;
      }

      @Override
      public TabGui getTabGui() {
         return this.J;
      }

      @Override
      public float F() {
         return this.D;
      }

      public float A_float() {
         return this.E;
      }

      @Override
      public float j() {
         return this.x;
      }

      @Override
      public void q(float var1) {
         this.x = var1;
      }
   }
}
