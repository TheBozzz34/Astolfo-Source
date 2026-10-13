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
   private static final long a = 77575536971175L;

   public GuiStats(GuiScreen var1, StatFileWriter var2) {
      this.parentScreen = var1;
      this.field_146546_t = var2;
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

   public void J() {
      this.generalStats = new GuiStats.StatsGeneral(this.mc);
      this.generalStats.U(1, 1);
      this.itemStats = new GuiStats.StatsItem(this.mc);
      this.itemStats.U(1, 1);
      this.blockStats = new GuiStats.StatsBlock(this.mc);
      this.blockStats.U(1, 1);
      this.mobStats = new GuiStats.StatsMobsList(this.mc);
      this.mobStats.U(1, 1);
   }

   public void createButtons() {
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton")));
      GuiButton var4;
      this.buttonList.add(var4 = new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton")));
      GuiButton var5;
      this.buttonList.add(var5 = new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton")));
      GuiButton var6;
      this.buttonList.add(var6 = new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton")));
      if (this.blockStats.getSize() == 0) {
         var4.enabled = false;
      }

      if (this.itemStats.getSize() == 0) {
         var5.enabled = false;
      }

      if (this.mobStats.getSize() == 0) {
         var6.enabled = false;
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (var1.id == 1) {
            this.displaySlot = this.generalStats;
         } else if (var1.id == 3) {
            this.displaySlot = this.itemStats;
         } else if (var1.id == 2) {
            this.displaySlot = this.blockStats;
         } else if (var1.id == 4) {
            this.displaySlot = this.mobStats;
         } else {
            this.displaySlot.actionPerformed(var1);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      if (this.doesGuiPauseGame) {
         this.drawDefaultBackground();
         this.d(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.d(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         this.displaySlot.drawScreen(var1, var2, var3);
         this.d(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
         super.drawScreen(var1, var2, var3);
      }
   }

   @Override
   public void doneLoading() {
      if (this.doesGuiPauseGame) {
         this.J();
         this.createButtons();
         this.displaySlot = this.generalStats;
         this.doesGuiPauseGame = false;
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.doesGuiPauseGame;
   }

   private void drawStatsScreen(int var1, int var2, Item var3) {
      this.drawButtonBackground(var1 + 1, var2 + 1);
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.renderItemIntoGUI(new ItemStack(var3, 1, 0), var1 + 2, var2 + 2);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
   }

   private void drawButtonBackground(int var1, int var2) {
      this.drawSprite(var1, var2, 0, 0);
   }

   private void drawSprite(int var1, int var2, int var3, int var4) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(statIcons);
      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var12 = var11.getWorldRenderer();
      var12.begin(7, DefaultVertexFormats.POSITION_TEX);
      var12.pos(var1 + 0, var2 + 18, this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
      var12.pos(var1 + 18, var2 + 18, this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 18) * 0.0078125F).endVertex();
      var12.pos(var1 + 18, var2 + 0, this.zLevel).tex((var3 + 18) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
      var12.pos(var1 + 0, var2 + 0, this.zLevel).tex((var3 + 0) * 0.0078125F, (var4 + 0) * 0.0078125F).endVertex();
      var11.draw();
   }

   public abstract class Stats extends GuiSlot {
      public int field_148218_l = -1;
      public List<StatCrafting> statsHolder;
      public Comparator<StatCrafting> statSorter;
      public int field_148217_o = -1;
      public int field_148215_p;
      private static final long c = 125220978839315L;

      public Stats(Minecraft var2) {
         super(var2, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
         this.C(false);
         this.setHasListHeader(true, 20);
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
      }

      @Override
      public boolean isSelected(int var1) {
         return false;
      }

      @Override
      public void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      @Override
      public void drawListHeader(int var1, int var2, Tessellator var3) {
         if (!Mouse.isButtonDown(0)) {
            this.field_148218_l = -1;
         }

         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(var1 + 115 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(var1 + 115 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(var1 + 165 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(var1 + 165 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(var1 + 215 - 18, var2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(var1 + 215 - 18, var2 + 1, 0, 18);
         }

         if (this.field_148217_o != -1) {
            short var7 = 79;
            byte var8 = 18;
            if (this.field_148217_o == 1) {
               var7 = 129;
            } else if (this.field_148217_o == 2) {
               var7 = 179;
            }

            if (this.field_148215_p == 1) {
               var8 = 36;
            }

            GuiStats.this.drawSprite(var1 + var7, var2 + 1, var8, 0);
         }
      }

      @Override
      public void func_148132_a(int var1, int var2) {
         this.field_148218_l = -1;
         if (var1 >= 79 && var1 < 115) {
            this.field_148218_l = 0;
         } else if (var1 >= 129 && var1 < 165) {
            this.field_148218_l = 1;
         } else if (var1 >= 179 && var1 < 215) {
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

      public final StatCrafting func_148211_c(int var1) {
         return this.statsHolder.get(var1);
      }

      public abstract String func_148210_b(int var1);

      public void func_148209_a(StatBase var1, int var2, int var3, boolean var4) {
         if (var1 != null) {
            String var8 = var1.format(GuiStats.this.field_146546_t.readStat(var1));
            GuiStats.this.I(GuiStats.this.fontRendererObj, var8, var2 - GuiStats.this.fontRendererObj.getStringWidth(var8), var3 + 5, var4 ? 16777215 : 9474192);
         } else {
            String var9 = "-";
            GuiStats.this.I(GuiStats.this.fontRendererObj, var9, var2 - GuiStats.this.fontRendererObj.getStringWidth(var9), var3 + 5, var4 ? 16777215 : 9474192);
         }
      }

      @Override
      public void func_148142_b(int var1, int var2) {
         if (var2 >= this.top && var2 <= this.bottom) {
            int var6 = this.getSlotIndexFromScreenCoords(var1, var2);
            int var7 = this.width / 2 - 92 - 16;
            if (var6 >= 0) {
               if (var1 < var7 + 40 || var1 > var7 + 40 + 20) {
                  return;
               }

               StatCrafting var14 = this.func_148211_c(var6);
               this.func_148213_a(var14, var1, var2);
            } else {
               String var8 = "";
               if (var1 >= var7 + 115 - 18 && var1 <= var7 + 115) {
                  var8 = this.func_148210_b(0);
               } else if (var1 >= var7 + 165 - 18 && var1 <= var7 + 165) {
                  var8 = this.func_148210_b(1);
               } else {
                  if (var1 < var7 + 215 - 18 || var1 > var7 + 215) {
                     return;
                  }

                  var8 = this.func_148210_b(2);
               }

               var8 = ("" + I18n.format(var8)).trim();
               if (var8.length() > 0) {
                  int var9 = var1 + 12;
                  int var10 = var2 - 12;
                  int var11 = GuiStats.this.fontRendererObj.getStringWidth(var8);
                  GuiStats.this.drawGradientRect(var9 - 3, var10 - 3, var9 + var11 + 3, var10 + 8 + 3, -1073741824, -1073741824);
                  GuiStats.this.fontRendererObj.drawStringWithShadow(var8, var9, var10, -1);
               }
            }
         }
      }

      public void func_148213_a(StatCrafting var1, int var2, int var3) {
         if (var1 != null) {
            Item var7 = var1.func_150959_a();
            ItemStack var8 = new ItemStack(var7);
            String var9 = var8.getUnlocalizedName();
            String var10 = ("" + I18n.format(var9 + ".name")).trim();
            if (var10.length() > 0) {
               int var11 = var2 + 12;
               int var12 = var3 - 12;
               int var13 = GuiStats.this.fontRendererObj.getStringWidth(var10);
               GuiStats.this.drawGradientRect(var11 - 3, var12 - 3, var11 + var13 + 3, var12 + 8 + 3, -1073741824, -1073741824);
               GuiStats.this.fontRendererObj.drawStringWithShadow(var10, var11, var12, -1);
            }
         }
      }

      public void func_148212_h(int var1) {
         if (var1 != this.field_148217_o) {
            this.field_148217_o = var1;
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
      private static final long e = 49909507800462L;

      public StatsBlock(Minecraft var2) {
         super(var2);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting var7 : StatList.objectMineStats) {
            boolean var8 = false;
            int var9 = Item.getIdFromItem(var7.func_150959_a());
            if (GuiStats.this.field_146546_t.readStat(var7) > 0) {
               var8 = true;
            } else if (StatList.objectUseStats[var9] != null && GuiStats.this.field_146546_t.readStat(StatList.objectUseStats[var9]) > 0) {
               var8 = true;
            } else if (StatList.objectCraftStats[var9] != null && GuiStats.this.field_146546_t.readStat(StatList.objectCraftStats[var9]) > 0) {
               var8 = true;
            }

            if (var8) {
               this.statsHolder.add(var7);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            private static final long a = 96009582032490L;

            public int compare(StatCrafting var1, StatCrafting var2x) {
               int var6 = Item.getIdFromItem(var1.func_150959_a());
               int var7 = Item.getIdFromItem(var2x.func_150959_a());
               StatBase var8 = null;
               StatBase var9 = null;
               if (StatsBlock.this.field_148217_o == 2) {
                  var8 = StatList.mineBlockStatArray[var6];
                  var9 = StatList.mineBlockStatArray[var7];
               } else if (StatsBlock.this.field_148217_o == 0) {
                  var8 = StatList.objectCraftStats[var6];
                  var9 = StatList.objectCraftStats[var7];
               } else if (StatsBlock.this.field_148217_o == 1) {
                  var8 = StatList.objectUseStats[var6];
                  var9 = StatList.objectUseStats[var7];
               }

               if (var8 != null || var9 != null) {
                  if (var8 == null) {
                     return 1;
                  }

                  if (var9 == null) {
                     return -1;
                  }

                  int var10 = GuiStats.this.field_146546_t.readStat(var8);
                  int var11 = GuiStats.this.field_146546_t.readStat(var9);
                  if (var10 != var11) {
                     return (var10 - var11) * StatsBlock.this.field_148215_p;
                  }
               }

               return var6 - var7;
            }
         };
      }

      @Override
      public void drawListHeader(int var1, int var2, Tessellator var3) {
         super.drawListHeader(var1, var2, var3);
         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(var1 + 115 - 18 + 1, var2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 115 - 18, var2 + 1, 18, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(var1 + 165 - 18 + 1, var2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 165 - 18, var2 + 1, 36, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(var1 + 215 - 18 + 1, var2 + 1 + 1, 54, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 215 - 18, var2 + 1, 54, 18);
         }
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatCrafting var10 = this.func_148211_c(var1);
         Item var11 = var10.func_150959_a();
         GuiStats.this.drawStatsScreen(var2 + 40, var3, var11);
         int var12 = Item.getIdFromItem(var11);
         this.func_148209_a(StatList.objectCraftStats[var12], var2 + 115, var3, var1 % 2 == 0);
         this.func_148209_a(StatList.objectUseStats[var12], var2 + 165, var3, var1 % 2 == 0);
         this.func_148209_a(var10, var2 + 215, var3, var1 % 2 == 0);
      }

      @Override
      public String func_148210_b(int var1) {
         return var1 == 0 ? "stat.crafted" : (var1 == 1 ? "stat.used" : "stat.mined");
      }
   }

   public class StatsGeneral extends GuiSlot {
      private static final long c = 94725474080393L;

      public StatsGeneral(Minecraft var2) {
         super(var2, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
         this.C(false);
      }

      @Override
      public int getSize() {
         return StatList.generalStats.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
      }

      @Override
      public boolean isSelected(int var1) {
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
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatBase var10 = StatList.generalStats.get(var1);
         GuiStats.this.I(GuiStats.this.fontRendererObj, var10.getStatName().getUnformattedText(), var2 + 2, var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
         String var11 = var10.format(GuiStats.this.field_146546_t.readStat(var10));
         GuiStats.this.I(
            GuiStats.this.fontRendererObj,
            var11,
            var2 + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(var11),
            var3 + 1,
            var1 % 2 == 0 ? 16777215 : 9474192
         );
      }
   }

   public class StatsItem extends GuiStats.Stats {
      private static final long d = 111793860690381L;

      public StatsItem(Minecraft var2) {
         super(var2);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting var7 : StatList.itemStats) {
            boolean var8 = false;
            int var9 = Item.getIdFromItem(var7.func_150959_a());
            if (GuiStats.this.field_146546_t.readStat(var7) > 0) {
               var8 = true;
            } else if (StatList.objectBreakStats[var9] != null && GuiStats.this.field_146546_t.readStat(StatList.objectBreakStats[var9]) > 0) {
               var8 = true;
            } else if (StatList.objectCraftStats[var9] != null && GuiStats.this.field_146546_t.readStat(StatList.objectCraftStats[var9]) > 0) {
               var8 = true;
            }

            if (var8) {
               this.statsHolder.add(var7);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            private static final long a = 74513690210702L;

            public int compare(StatCrafting var1, StatCrafting var2x) {
               int var6 = Item.getIdFromItem(var1.func_150959_a());
               int var7 = Item.getIdFromItem(var2x.func_150959_a());
               StatBase var8 = null;
               StatBase var9 = null;
               if (StatsItem.this.field_148217_o == 0) {
                  var8 = StatList.objectBreakStats[var6];
                  var9 = StatList.objectBreakStats[var7];
               } else if (StatsItem.this.field_148217_o == 1) {
                  var8 = StatList.objectCraftStats[var6];
                  var9 = StatList.objectCraftStats[var7];
               } else if (StatsItem.this.field_148217_o == 2) {
                  var8 = StatList.objectUseStats[var6];
                  var9 = StatList.objectUseStats[var7];
               }

               if (var8 != null || var9 != null) {
                  if (var8 == null) {
                     return 1;
                  }

                  if (var9 == null) {
                     return -1;
                  }

                  int var10 = GuiStats.this.field_146546_t.readStat(var8);
                  int var11 = GuiStats.this.field_146546_t.readStat(var9);
                  if (var10 != var11) {
                     return (var10 - var11) * StatsItem.this.field_148215_p;
                  }
               }

               return var6 - var7;
            }
         };
      }

      @Override
      public void drawListHeader(int var1, int var2, Tessellator var3) {
         super.drawListHeader(var1, var2, var3);
         if (this.field_148218_l == 0) {
            GuiStats.this.drawSprite(var1 + 115 - 18 + 1, var2 + 1 + 1, 72, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 115 - 18, var2 + 1, 72, 18);
         }

         if (this.field_148218_l == 1) {
            GuiStats.this.drawSprite(var1 + 165 - 18 + 1, var2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 165 - 18, var2 + 1, 18, 18);
         }

         if (this.field_148218_l == 2) {
            GuiStats.this.drawSprite(var1 + 215 - 18 + 1, var2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(var1 + 215 - 18, var2 + 1, 36, 18);
         }
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         StatCrafting var10 = this.func_148211_c(var1);
         Item var11 = var10.func_150959_a();
         GuiStats.this.drawStatsScreen(var2 + 40, var3, var11);
         int var12 = Item.getIdFromItem(var11);
         this.func_148209_a(StatList.objectBreakStats[var12], var2 + 115, var3, var1 % 2 == 0);
         this.func_148209_a(StatList.objectCraftStats[var12], var2 + 165, var3, var1 % 2 == 0);
         this.func_148209_a(var10, var2 + 215, var3, var1 % 2 == 0);
      }

      @Override
      public String func_148210_b(int var1) {
         return var1 == 1 ? "stat.crafted" : (var1 == 2 ? "stat.used" : "stat.depleted");
      }
   }

   public class StatsMobsList extends GuiSlot {
      private final List<EntityList.EntityEggInfo> field_148222_l = Lists.newArrayList();
      private static final long c = 116066803607960L;

      public StatsMobsList(Minecraft var2) {
         super(var2, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
         this.C(false);

         for (EntityList.EntityEggInfo var7 : EntityList.entityEggs.values()) {
            if (GuiStats.this.field_146546_t.readStat(var7.field_151512_d) > 0 || GuiStats.this.field_146546_t.readStat(var7.field_151513_e) > 0) {
               this.field_148222_l.add(var7);
            }
         }
      }

      @Override
      public int getSize() {
         return this.field_148222_l.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
      }

      @Override
      public boolean isSelected(int var1) {
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
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         EntityList.EntityEggInfo var10 = this.field_148222_l.get(var1);
         String var11 = I18n.format("entity." + EntityList.getStringFromID(var10.spawnedID) + ".name");
         int var12 = GuiStats.this.field_146546_t.readStat(var10.field_151512_d);
         int var13 = GuiStats.this.field_146546_t.readStat(var10.field_151513_e);
         String var14 = I18n.format("stat.entityKills", var12, var11);
         String var15 = I18n.format("stat.entityKilledBy", var11, var13);
         if (var12 == 0) {
            var14 = I18n.format("stat.entityKills.none", var11);
         }

         if (var13 == 0) {
            var15 = I18n.format("stat.entityKilledBy.none", var11);
         }

         GuiStats.this.I(GuiStats.this.fontRendererObj, var11, var2 + 2 - 10, var3 + 1, 16777215);
         GuiStats.this.I(GuiStats.this.fontRendererObj, var14, var2 + 2, var3 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, var12 == 0 ? 6316128 : 9474192);
         GuiStats.this.I(
            GuiStats.this.fontRendererObj, var15, var2 + 2, var3 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, var13 == 0 ? 6316128 : 9474192
         );
      }
   }
}
