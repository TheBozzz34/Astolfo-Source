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
   private float posX;
   private float posY;
   private final Stopwatch animationTimer = new Stopwatch();
   private final List<TabGui.Panel> panels = new ArrayList<>();
   private RendererSet rendererSet;
   private final Dimension size = new Dimension(0, 0);
   @EventHandler(KeyPressEvent.class)
   private Listener<KeyPressEvent> onKeyPress = keyPressEvent -> {
      if (HUD.tabGui.getValue()) {
         if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            int i = this.getValue().size();

            for (int j = 0; j < i; j++) {
               TabGui.Panel panel = this.getValue().get(j);
               panel.onKeyPressed(keyPressEvent.getKey());
            }
         }
      }
   };

   public TabGui() {
      this.rendererSet = new DefaultRendererSet();
      TabGui.Panel panel = new TabGui.Panel(this);

      for (Category category : Category.values()) {
         if (!category.equals(Category.HIDDEN) && !category.equals(Category.CONFIG) && !category.equals(Category.SCRIPTS)) {
            panel.addItem(new CategoryItem(panel, category));
         }
      }

      this.posX = 0.0F;
      this.posY = 2.0F;
      this.addElement(panel);
      Astolfo.INSTANCE.getEventBus().subscribe(this);
   }

   public void render(ScaledResolution scaledResolution, Minecraft minecraft) {
      boolean bl = this.animationTimer.hasElapsed(16.0F);
      if (bl) {
         this.animationTimer.reset();
      }

      TabGui.Panel panel = null;
      int i = this.panels.size();

      for (int j = 0; j < i; j++) {
         try {
            panel = this.panels.get(j);
         } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
         }

         GL11.glEnable(3089);
         if (panel.getState() == PanelState.CLOSING) {
            applyScissor(
               this.getX() + panel.getOffsetX(),
               this.getY(),
               (int)this.getX() + panel.getOffsetX() + (int)panel.getRevealWidth(),
               this.getY() + (float)panel.getSize().getHeight()
            );
         } else if (panel.getState() == PanelState.OPENING) {
            applyScissor(
               this.getX() + panel.getOffsetX(),
               this.getY(),
               this.getX() + panel.getOffsetX() + panel.getRevealWidth(),
               this.getY() + (float)panel.getSize().getHeight()
            );
         } else if (panel.getState() == PanelState.NORMAL) {
            applyScissor(
               this.getX() + panel.getOffsetX(),
               this.getY(),
               this.getX() + panel.getOffsetX() + (float)panel.getSize().getWidth(),
               this.getY() + (float)panel.getSize().getHeight()
            );
         }

         this.rendererSet.get((Class<? extends Sized>)panel.getClass()).renderUnchecked(panel, scaledResolution, minecraft);
         if (bl) {
            this.rendererSet.get((Class<? extends Sized>)panel.getClass()).updateUnchecked(panel, scaledResolution, minecraft);
         }

         panel.getItems().forEach(tabGuiItem -> {
            this.rendererSet.get((Class<? extends Sized>)tabGuiItem.getClass()).renderUnchecked(tabGuiItem, scaledResolution, minecraft);
            if (bl) {
               this.rendererSet.get((Class<? extends Sized>)tabGuiItem.getClass()).updateUnchecked(tabGuiItem, scaledResolution, minecraft);
            }
         });
         GL11.glDisable(3089);
      }
   }

   public static void applyScissor(float f, float g, float h, float f2) {
      float f3 = HUD.hudScale.getValue();
      GL11.glScissor((int)(f * f3), (int)((Display.getHeight() / f3 - f2) * f3), (int)((h - f) * f3), (int)((f2 - g) * f3));
   }

   public List<TabGui.Panel> getValue() {
      return this.panels;
   }

   public void setRendererSet(RendererSet rendererSet) {
      this.rendererSet = rendererSet;
   }

   public RendererSet getRendererSet() {
      return this.rendererSet;
   }

   public TabGui.Panel Q_r6q() {
      return this.getLast();
   }

   public void addElement(TabGui.Panel panel) {
      this.getValue().add(panel);
      panel.refreshSize();
   }

   @Override
   public Dimension getSize() {
      return this.size;
   }

   public float getX() {
      return this.posX;
   }

   public float getY() {
      return this.posY;
   }

   public void setX(float f) {
      this.posX = f;
   }

   public void setY(float f) {
      this.posY = f;
   }

   public static class Panel implements Sized, TabGuiPanel {
      private final TabGui tabGui;
      private final List<TabGuiItem> items;
      private int selectedIndex;
      private float offsetX;
      private float offsetY;
      public float selectorY;
      private float revealWidth = 0.0F;
      private PanelState state;
      private final Dimension panelSize = new Dimension(0, 0);

      @Override
      public PanelState getState() {
         return this.state;
      }

      @Override
      public void setState(PanelState panelState) {
         this.state = panelState;
      }

      public Panel(TabGui tabGui, TabGuiItem... tabGuiItems) {
         this.tabGui = tabGui;
         this.items = Lists.newArrayList(tabGuiItems);
         this.offsetX = 0.0F;
         int i = tabGui.getValue().size();

         for (int j = 0; j < i; j++) {
            this.offsetX = (float)(this.offsetX + tabGui.getValue().get(j).getSize().getWidth());
            this.offsetX += 3.0F;
         }

         this.offsetY = 30.0F;
         this.state = PanelState.OPENING;
      }

      public Panel(TabGui tabGui, Collection<TabGuiItem> collection) {
         this.tabGui = tabGui;
         this.items = new ArrayList<>(collection);
      }

      @Override
      public List<TabGuiItem> getItems() {
         return this.items;
      }

      @Override
      public int getSelectedIndex() {
         return this.selectedIndex;
      }

      @Override
      public void setSelectedIndex(int i) {
         this.selectedIndex = i;
      }

      @Override
      public Dimension getSize() {
         return this.panelSize;
      }

      @Override
      public TabGui getTabGui() {
         return this.tabGui;
      }

      @Override
      public float getOffsetX() {
         return this.offsetX;
      }

      public float getOffsetY() {
         return this.offsetY;
      }

      @Override
      public float getRevealWidth() {
         return this.revealWidth;
      }

      @Override
      public void setRevealWidth(float f) {
         this.revealWidth = f;
      }
   }
}
