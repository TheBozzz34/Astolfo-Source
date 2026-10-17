package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiStats extends GuiScreen implements IProgressMeter {
   public GuiScreen parentScreen;
   public String screenTitle = "Select world";
   private GuiStats.StatsGeneral generalStats;
   private GuiStats.StatsItem itemStats;
   private GuiStats.StatsBlock blockStats;
   private GuiStats.StatsMobsList mobStats;
   private StatFileWriter field_146546_t;
   private GuiSlot displaySlot;
   private boolean doesGuiPauseGame = true;

   public GuiStats(GuiScreen guiScreen, StatFileWriter statFileWriter) {
      this.parentScreen = guiScreen;
      this.field_146546_t = statFileWriter;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("gui.stats");
      this.doesGuiPauseGame = true;
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (this.displaySlot != null) {
         this.displaySlot.handleMouseInput();
      }
   }

   public void func_175366_f() {
      this.generalStats = new GuiStats.StatsGeneral(this.mc);
      this.generalStats.registerScrollButtons(1, 1);
      this.itemStats = new GuiStats.StatsItem(this.mc);
      this.itemStats.registerScrollButtons(1, 1);
      this.blockStats = new GuiStats.StatsBlock(this.mc);
      this.blockStats.registerScrollButtons(1, 1);
      this.mobStats = new GuiStats.StatsMobsList(this.mc);
      this.mobStats.registerScrollButtons(1, 1);
   }

   public void createButtons() {
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton")));
      GuiButton guiButton;
      this.buttonList.add(guiButton = new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton")));
      GuiButton guiButton2;
      this.buttonList.add(guiButton2 = new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton")));
      GuiButton guiButton3;
      this.buttonList.add(guiButton3 = new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton")));
      if (this.blockStats.getSize() == 0) {
         guiButton.enabled = false;
      }

      if (this.itemStats.getSize() == 0) {
         guiButton2.enabled = false;
      }

      if (this.mobStats.getSize() == 0) {
         guiButton3.enabled = false;
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 1) {
            this.displaySlot = this.generalStats;
         } else if (button.id == 3) {
            this.displaySlot = this.itemStats;
         } else if (button.id == 2) {
            this.displaySlot = this.blockStats;
         } else if (button.id == 4) {
            this.displaySlot = this.mobStats;
         } else {
            this.displaySlot.actionPerformed(button);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.doesGuiPauseGame) {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
         this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
         super.drawScreen(mouseX, mouseY, partialTicks);
      }
   }

   @Override
   public void doneLoading() {
      if (this.doesGuiPauseGame) {
         this.func_175366_f();
         this.createButtons();
         this.displaySlot = this.generalStats;
         this.doesGuiPauseGame = false;
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.doesGuiPauseGame;
   }

   private void drawStatsScreen(int i, int j, Item item) {
      this.drawButtonBackground(i + 1, j + 1);
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.renderItemIntoGUI(new ItemStack(item, 1, 0), i + 2, j + 2);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
   }

   private void drawButtonBackground(int i, int j) {
      this.drawSprite(i, j, 0, 0);
   }

   private void drawSprite(int i, int j, int k, int l) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(statIcons);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(i + 0, j + 18, this.zLevel).tex((k + 0) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(i + 18, j + 18, this.zLevel).tex((k + 18) * 0.0078125F, (l + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(i + 18, j + 0, this.zLevel).tex((k + 18) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
      worldRenderer.pos(i + 0, j + 0, this.zLevel).tex((k + 0) * 0.0078125F, (l + 0) * 0.0078125F).endVertex();
      tessellator.draw();
   }

   public abstract class Stats extends GuiSlot {
      public int field_148218_l = -1;
      public List<StatCrafting> statsHolder;
      public Comparator<StatCrafting> statSorter;
      public int field_148217_o = -1;
      public int field_148215_p;

      public Stats(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
         this.setShowSelectionBox(false);
         this.setHasListHeader(true, 20);
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      @Override
      public void drawListHeader(int i, int j, Tessellator tessellator) {
         if (!Mouse.isButtonDown(0)) {
            this.field_148218_l = -1;
         }

         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(i + 115 - 18, j + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(i + 115 - 18, j + 1, 0, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(i + 165 - 18, j + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(i + 165 - 18, j + 1, 0, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(i + 215 - 18, j + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(i + 215 - 18, j + 1, 0, 18);
         }

         if (this.field_148217_o != -1) {
            short s = 79;
            byte b = 18;
            if (this.field_148217_o == 1) {
               s = 129;
            } else if (this.field_148217_o == 2) {
               s = 179;
            }

            if (this.field_148215_p == 1) {
               b = 36;
            }

            GuiStats.this.drawSprite(i + s, j + 1, b, 0);
         }
      }

      @Override
      public void func_148132_a(int i, int j) {
         this.field_148218_l = -1;
         if (i >= 79 && i < 115) {
            this.field_148218_l = 0;
         } else if (i >= 129 && i < 165) {
            this.field_148218_l = 1;
         } else if (i >= 179 && i < 215) {
            this.field_148218_l = 2;
         }

         if (this.field_148218_l >= 0) {
            this.func_148212_h(this.field_148218_l);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }
      }

      @Override
      public final int getSize() {
         return this.statsHolder.size();
      }

      public final StatCrafting func_148211_c(int i) {
         return this.statsHolder.get(i);
      }

      public abstract String func_148210_b(int i);

      public void func_148209_a(StatBase statBase, int i, int j, boolean bl) {
         if (statBase != null) {
            String string = statBase.format(GuiStats.this.field_146546_t.readStat(statBase));
            GuiStats.this.drawString(
               GuiStats.this.fontRendererObj, string, i - GuiStats.this.fontRendererObj.getStringWidth(string), j + 5, bl ? 16777215 : 9474192
            );
         } else {
            String string2 = "-";
            GuiStats.this.drawString(
               GuiStats.this.fontRendererObj, string2, i - GuiStats.this.fontRendererObj.getStringWidth(string2), j + 5, bl ? 16777215 : 9474192
            );
         }
      }

      @Override
      public void func_148142_b(int i, int j) {
         if (j >= this.top && j <= this.bottom) {
            int k = this.getSlotIndexFromScreenCoords(i, j);
            int l = this.width / 2 - 92 - 16;
            if (k >= 0) {
               if (i < l + 40 || i > l + 40 + 20) {
                  return;
               }

               StatCrafting statCrafting = this.func_148211_c(k);
               this.func_148213_a(statCrafting, i, j);
            } else {
               String string = "";
               if (i >= l + 115 - 18 && i <= l + 115) {
                  string = this.func_148210_b(0);
               } else if (i >= l + 165 - 18 && i <= l + 165) {
                  string = this.func_148210_b(1);
               } else {
                  if (i < l + 215 - 18 || i > l + 215) {
                     return;
                  }

                  string = this.func_148210_b(2);
               }

               string = ("" + I18n.format(string)).trim();
               if (string.length() > 0) {
                  int m = i + 12;
                  int n = j - 12;
                  int i2 = GuiStats.this.fontRendererObj.getStringWidth(string);
                  GuiStats.this.drawGradientRect(m - 3, n - 3, m + i2 + 3, n + 8 + 3, -1073741824, -1073741824);
                  GuiStats.this.fontRendererObj.drawStringWithShadow(string, m, n, -1);
               }
            }
         }
      }

      public void func_148213_a(StatCrafting statCrafting, int i, int j) {
         if (statCrafting != null) {
            Item item = statCrafting.func_150959_a();
            ItemStack itemStack = new ItemStack(item);
            String string = itemStack.getUnlocalizedName();
            String string2 = ("" + I18n.format(string + ".name")).trim();
            if (string2.length() > 0) {
               int k = i + 12;
               int l = j - 12;
               int m = GuiStats.this.fontRendererObj.getStringWidth(string2);
               GuiStats.this.drawGradientRect(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
               GuiStats.this.fontRendererObj.drawStringWithShadow(string2, k, l, -1);
            }
         }
      }

      public void func_148212_h(int i) {
         if (i != this.field_148217_o) {
            this.field_148217_o = i;
            this.field_148215_p = -1;
         } else if (this.field_148215_p == -1) {
            this.field_148215_p = 1;
         } else {
            this.field_148217_o = -1;
            this.field_148215_p = 0;
         }

         Collections.sort(this.statsHolder, this.statSorter);
      }
   }

   public class StatsBlock extends GuiStats.Stats {
      public StatsBlock(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting3 : StatList.objectMineStats) {
            boolean bl = false;
            int i = Item.getIdFromItem(statCrafting3.func_150959_a());
            if (GuiStats.this.field_146546_t.readStat(statCrafting3) > 0) {
               bl = true;
            } else if (StatList.objectUseStats[i] != null && GuiStats.this.field_146546_t.readStat(StatList.objectUseStats[i]) > 0) {
               bl = true;
            } else if (StatList.objectCraftStats[i] != null && GuiStats.this.field_146546_t.readStat(StatList.objectCraftStats[i]) > 0) {
               bl = true;
            }

            if (bl) {
               this.statsHolder.add(statCrafting3);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
               int j = Item.getIdFromItem(statCrafting.func_150959_a());
               int k = Item.getIdFromItem(statCrafting2.func_150959_a());
               StatBase statBase = null;
               StatBase statBase2 = null;
               if (StatsBlock.this.field_148217_o == 2) {
                  statBase = StatList.mineBlockStatArray[j];
                  statBase2 = StatList.mineBlockStatArray[k];
               } else if (StatsBlock.this.field_148217_o == 0) {
                  statBase = StatList.objectCraftStats[j];
                  statBase2 = StatList.objectCraftStats[k];
               } else if (StatsBlock.this.field_148217_o == 1) {
                  statBase = StatList.objectUseStats[j];
                  statBase2 = StatList.objectUseStats[k];
               }

               if (statBase != null || statBase2 != null) {
                  if (statBase == null) {
                     return 1;
                  }

                  if (statBase2 == null) {
                     return -1;
                  }

                  int l = GuiStats.this.field_146546_t.readStat(statBase);
                  int m = GuiStats.this.field_146546_t.readStat(statBase2);
                  if (l != m) {
                     return (l - m) * StatsBlock.this.field_148215_p;
                  }
               }

               return j - k;
            }
         };
      }

      @Override
      public void drawListHeader(int i, int j, Tessellator tessellator) {
         super.drawListHeader(i, j, tessellator);
         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(i + 115 - 18 + 1, j + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(i + 115 - 18, j + 1, 18, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(i + 165 - 18 + 1, j + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(i + 165 - 18, j + 1, 36, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(i + 215 - 18 + 1, j + 1 + 1, 54, 18);
         } else {
            GuiStats.this.drawSprite(i + 215 - 18, j + 1, 54, 18);
         }
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         StatCrafting statCrafting = this.func_148211_c(entryID);
         Item item = statCrafting.func_150959_a();
         GuiStats.this.drawStatsScreen(i + 40, j, item);
         int l = Item.getIdFromItem(item);
         this.func_148209_a(StatList.objectCraftStats[l], i + 115, j, entryID % 2 == 0);
         this.func_148209_a(StatList.objectUseStats[l], i + 165, j, entryID % 2 == 0);
         this.func_148209_a(statCrafting, i + 215, j, entryID % 2 == 0);
      }

      @Override
      public String func_148210_b(int i) {
         return i == 0 ? "stat.crafted" : (i == 1 ? "stat.used" : "stat.mined");
      }
   }

   public class StatsGeneral extends GuiSlot {
      public StatsGeneral(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
         this.setShowSelectionBox(false);
      }

      @Override
      public int getSize() {
         return StatList.generalStats.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public int getContentHeight() {
         return this.getSize() * 10;
      }

      @Override
      public void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         StatBase statBase = StatList.generalStats.get(entryID);
         GuiStats.this.drawString(GuiStats.this.fontRendererObj, statBase.getStatName().getUnformattedText(), i + 2, j + 1, entryID % 2 == 0 ? 16777215 : 9474192);
         String string = statBase.format(GuiStats.this.field_146546_t.readStat(statBase));
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj,
            string,
            i + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(string),
            j + 1,
            entryID % 2 == 0 ? 16777215 : 9474192
         );
      }
   }

   public class StatsItem extends GuiStats.Stats {
      public StatsItem(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting3 : StatList.itemStats) {
            boolean bl = false;
            int i = Item.getIdFromItem(statCrafting3.func_150959_a());
            if (GuiStats.this.field_146546_t.readStat(statCrafting3) > 0) {
               bl = true;
            } else if (StatList.objectBreakStats[i] != null && GuiStats.this.field_146546_t.readStat(StatList.objectBreakStats[i]) > 0) {
               bl = true;
            } else if (StatList.objectCraftStats[i] != null && GuiStats.this.field_146546_t.readStat(StatList.objectCraftStats[i]) > 0) {
               bl = true;
            }

            if (bl) {
               this.statsHolder.add(statCrafting3);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
               int j = Item.getIdFromItem(statCrafting.func_150959_a());
               int k = Item.getIdFromItem(statCrafting2.func_150959_a());
               StatBase statBase = null;
               StatBase statBase2 = null;
               if (StatsItem.this.field_148217_o == 0) {
                  statBase = StatList.objectBreakStats[j];
                  statBase2 = StatList.objectBreakStats[k];
               } else if (StatsItem.this.field_148217_o == 1) {
                  statBase = StatList.objectCraftStats[j];
                  statBase2 = StatList.objectCraftStats[k];
               } else if (StatsItem.this.field_148217_o == 2) {
                  statBase = StatList.objectUseStats[j];
                  statBase2 = StatList.objectUseStats[k];
               }

               if (statBase != null || statBase2 != null) {
                  if (statBase == null) {
                     return 1;
                  }

                  if (statBase2 == null) {
                     return -1;
                  }

                  int l = GuiStats.this.field_146546_t.readStat(statBase);
                  int m = GuiStats.this.field_146546_t.readStat(statBase2);
                  if (l != m) {
                     return (l - m) * StatsItem.this.field_148215_p;
                  }
               }

               return j - k;
            }
         };
      }

      @Override
      public void drawListHeader(int i, int j, Tessellator tessellator) {
         super.drawListHeader(i, j, tessellator);
         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(i + 115 - 18 + 1, j + 1 + 1, 72, 18);
         } else {
            GuiStats.this.drawSprite(i + 115 - 18, j + 1, 72, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(i + 165 - 18 + 1, j + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(i + 165 - 18, j + 1, 18, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(i + 215 - 18 + 1, j + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(i + 215 - 18, j + 1, 36, 18);
         }
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         StatCrafting statCrafting = this.func_148211_c(entryID);
         Item item = statCrafting.func_150959_a();
         GuiStats.this.drawStatsScreen(i + 40, j, item);
         int l = Item.getIdFromItem(item);
         this.func_148209_a(StatList.objectBreakStats[l], i + 115, j, entryID % 2 == 0);
         this.func_148209_a(StatList.objectCraftStats[l], i + 165, j, entryID % 2 == 0);
         this.func_148209_a(statCrafting, i + 215, j, entryID % 2 == 0);
      }

      @Override
      public String func_148210_b(int i) {
         return i == 1 ? "stat.crafted" : (i == 2 ? "stat.used" : "stat.depleted");
      }
   }

   public class StatsMobsList extends GuiSlot {
      private final List<EntityList.EntityEggInfo> field_148222_l = Lists.newArrayList();

      public StatsMobsList(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
         this.setShowSelectionBox(false);

         for (EntityList.EntityEggInfo entityEggInfo : EntityList.entityEggs.values()) {
            if (GuiStats.this.field_146546_t.readStat(entityEggInfo.field_151512_d) > 0 || GuiStats.this.field_146546_t.readStat(entityEggInfo.field_151513_e) > 0) {
               this.field_148222_l.add(entityEggInfo);
            }
         }
      }

      @Override
      public int getSize() {
         return this.field_148222_l.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public int getContentHeight() {
         return this.getSize() * GuiStats.this.fontRendererObj.FONT_HEIGHT * 4;
      }

      @Override
      public void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         EntityList.EntityEggInfo entityEggInfo = this.field_148222_l.get(entryID);
         String string = I18n.format("entity." + EntityList.getStringFromID(entityEggInfo.spawnedID) + ".name");
         int l = GuiStats.this.field_146546_t.readStat(entityEggInfo.field_151512_d);
         int m = GuiStats.this.field_146546_t.readStat(entityEggInfo.field_151513_e);
         String string2 = I18n.format("stat.entityKills", l, string);
         String string3 = I18n.format("stat.entityKilledBy", string, m);
         if (l == 0) {
            string2 = I18n.format("stat.entityKills.none", string);
         }

         if (m == 0) {
            string3 = I18n.format("stat.entityKilledBy.none", string);
         }

         GuiStats.this.drawString(GuiStats.this.fontRendererObj, string, i + 2 - 10, j + 1, 16777215);
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, string2, i + 2, j + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, l == 0 ? 6316128 : 9474192
         );
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, string3, i + 2, j + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, m == 0 ? 6316128 : 9474192
         );
      }
   }
}
