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
   private Listener<KeyPressEvent> onKeyPress = keyPressEvent -> {
      if (HUD.tabGui.getValue()) {
         if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            int i = this.L().size();

            for (int j = 0; j < i; j++) {
               TabGui.Panel panel = this.L().get(j);
               panel.onKeyPressed(keyPressEvent.getKey());
            }
         }
      }
   };
   private static final long b = 96522318301394L;

   public TabGui() {
      this.N = new DefaultRendererSet();
      TabGui.Panel panel = new TabGui.Panel(this);

      for (Category category : Category.values()) {
         if (!category.equals(Category.HIDDEN) && !category.equals(Category.CONFIG) && !category.equals(Category.SCRIPTS)) {
            panel.addItem(new CategoryItem(panel, category));
         }
      }

      this.z = 0.0F;
      this.r = 2.0F;
      this.s(panel);
      Astolfo.INSTANCE.getEventBus().subscribe(this);
   }

   public void render(ScaledResolution scaledResolution, Minecraft minecraft) {
      boolean bl = this.Y.hasElapsed(16.0F);
      if (bl) {
         this.Y.reset();
      }

      TabGui.Panel panel = null;
      int i = this.C.size();

      for (int k = 0; k < i; k++) {
         try {
            panel = this.C.get(k);
         } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
         }

         GL11.glEnable(3089);
         if (panel.getState() == PanelState.CLOSING) {
            A(this.J() + panel.F(), this.Q(), (int)this.J() + panel.F() + (int)panel.j(), this.Q() + (float)panel.x().getHeight());
         } else if (panel.getState() == PanelState.OPENING) {
            A(this.J() + panel.F(), this.Q(), this.J() + panel.F() + panel.j(), this.Q() + (float)panel.x().getHeight());
         } else if (panel.getState() == PanelState.NORMAL) {
            A(this.J() + panel.F(), this.Q(), this.J() + panel.F() + (float)panel.x().getWidth(), this.Q() + (float)panel.x().getHeight());
         }

         this.N.get((Class<? extends Sized>)panel.getClass()).P(panel, scaledResolution, minecraft);
         if (bl) {
            this.N.get((Class<? extends Sized>)panel.getClass()).v(panel, scaledResolution, minecraft);
         }

         panel.getItems().forEach(tabGuiItem -> {
            this.N.get((Class<? extends Sized>)tabGuiItem.getClass()).P(tabGuiItem, scaledResolution, minecraft);
            if (bl) {
               this.N.get((Class<? extends Sized>)tabGuiItem.getClass()).v(tabGuiItem, scaledResolution, minecraft);
            }
         });
         GL11.glDisable(3089);
      }
   }

   public static void A(float f, float g, float h, float f2) {
      float f3 = HUD.hudScale.getValue();
      GL11.glScissor((int)(f * f3), (int)((Display.getHeight() / f3 - f2) * f3), (int)((h - f) * f3), (int)((f2 - g) * f3));
   }

   public List<TabGui.Panel> L() {
      return this.C;
   }

   public void Z(RendererSet rendererSet) {
      this.N = rendererSet;
   }

   public RendererSet p() {
      return this.N;
   }

   public TabGui.Panel Q_r6q() {
      return this.w();
   }

   public void s(TabGui.Panel panel) {
      this.L().add(panel);
      panel.refreshSize();
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

   public void C(float f) {
      this.z = f;
   }

   public void P(float f) {
      this.r = f;
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
      public void setState(PanelState panelState) {
         this.n = panelState;
      }

      public Panel(TabGui tabGui, TabGuiItem... tabGuiItems) {
         this.J = tabGui;
         this.A = Lists.newArrayList(tabGuiItems);
         this.D = 0.0F;
         int i = tabGui.L().size();

         for (int j = 0; j < i; j++) {
            this.D = (float)(this.D + tabGui.L().get(j).x().getWidth());
            this.D += 3.0F;
         }

         this.E = 30.0F;
         this.n = PanelState.OPENING;
      }

      public Panel(TabGui tabGui, Collection<TabGuiItem> collection) {
         this.J = tabGui;
         this.A = new ArrayList<>(collection);
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
      public void setSelectedIndex(int i) {
         this.b = i;
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
      public void q(float f) {
         this.x = f;
      }
   }
}
