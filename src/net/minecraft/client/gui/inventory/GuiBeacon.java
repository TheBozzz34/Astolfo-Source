package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
   private IInventory tileBeacon;
   private GuiBeacon.ConfirmButton beaconConfirmButton;
   private boolean buttonsNotDrawn;
   private static final long bb = 94906670433915L;

   public GuiBeacon(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBeacon(var1, var2));
      this.tileBeacon = var2;
      this.xSize = 230;
      this.ySize = 219;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
      this.buttonList.add(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
      this.buttonsNotDrawn = true;
      this.beaconConfirmButton.enabled = false;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      int var4 = this.tileBeacon.getField(0);
      int var5 = this.tileBeacon.getField(1);
      int var6 = this.tileBeacon.getField(2);
      if (this.buttonsNotDrawn && var4 >= 0) {
         this.buttonsNotDrawn = false;

         for (int var7 = 0; var7 <= 2; var7++) {
            int var8 = TileEntityBeacon.effectsList[var7].length;
            int var9 = var8 * 22 + (var8 - 1) * 2;

            for (int var10 = 0; var10 < var8; var10++) {
               int var11 = TileEntityBeacon.effectsList[var7][var10].id;
               GuiBeacon.PowerButton var12 = new GuiBeacon.PowerButton(
                  var7 << 8 | var11, this.guiLeft + 76 + var10 * 24 - var9 / 2, this.guiTop + 22 + var7 * 25, var11, var7
               );
               this.buttonList.add(var12);
               if (var7 >= var4) {
                  var12.enabled = false;
               } else if (var11 == var5) {
                  var12.func_146140_b(true);
               }
            }
         }

         byte var13 = 3;
         int var14 = TileEntityBeacon.effectsList[var13].length + 1;
         int var15 = var14 * 22 + (var14 - 1) * 2;

         for (int var16 = 0; var16 < var14 - 1; var16++) {
            int var18 = TileEntityBeacon.effectsList[var13][var16].id;
            GuiBeacon.PowerButton var19 = new GuiBeacon.PowerButton(
               var13 << 8 | var18, this.guiLeft + 167 + var16 * 24 - var15 / 2, this.guiTop + 47, var18, var13
            );
            this.buttonList.add(var19);
            if (var13 >= var4) {
               var19.enabled = false;
            } else if (var18 == var6) {
               var19.func_146140_b(true);
            }
         }

         if (var5 > 0) {
            GuiBeacon.PowerButton var17 = new GuiBeacon.PowerButton(
               var13 << 8 | var5, this.guiLeft + 167 + (var14 - 1) * 24 - var15 / 2, this.guiTop + 47, var5, var13
            );
            this.buttonList.add(var17);
            if (var13 >= var4) {
               var17.enabled = false;
            } else if (var5 == var6) {
               var17.func_146140_b(true);
            }
         }
      }

      this.beaconConfirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && var5 > 0;
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == -2) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (var1.id == -1) {
         String var5 = "MC|Beacon";
         PacketBuffer var6 = new PacketBuffer(Unpooled.buffer());
         var6.writeInt(this.tileBeacon.getField(1));
         var6.writeInt(this.tileBeacon.getField(2));
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(var5, var6));
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (var1 instanceof GuiBeacon.PowerButton) {
         if (((GuiBeacon.PowerButton)var1).func_146141_c()) {
            return;
         }

         int var8 = var1.id;
         int var9 = var8 & 0xFF;
         int var7 = var8 >> 8;
         if (var7 < 3) {
            this.tileBeacon.setField(1, var9);
         } else {
            this.tileBeacon.setField(2, var9);
         }

         this.buttonList.clear();
         this.initGui();
         this.updateScreen();
      }
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      RenderHelper.disableStandardItemLighting();
      this.d(this.fontRendererObj, I18n.format("tile.beacon.primary"), 62, 10, 14737632);
      this.d(this.fontRendererObj, I18n.format("tile.beacon.secondary"), 169, 10, 14737632);

      for (GuiButton var7 : this.buttonList) {
         if (var7.isMouseOver()) {
            var7.drawButtonForegroundLayer(var1 - this.guiLeft, var2 - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(beaconGuiTextures);
      int var6 = (this.width - this.xSize) / 2;
      int var7 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var6, var7, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.emerald), var6 + 42, var7 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond), var6 + 42 + 22, var7 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.gold_ingot), var6 + 42 + 44, var7 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_ingot), var6 + 42 + 66, var7 + 109);
      this.itemRender.zLevel = 0.0F;
   }

   public static class Button extends GuiButton {
      private final ResourceLocation field_146145_o;
      private final int field_146144_p;
      private final int field_146143_q;
      private boolean field_146142_r;
      private static final long c = 98523336813137L;

      public Button(int var1, int var2, int var3, ResourceLocation var4, int var5, int var6) {
         super(var1, var2, var3, 22, 22, "");
         this.field_146145_o = var4;
         this.field_146144_p = var5;
         this.field_146143_q = var6;
      }

      @Override
      public void drawButton(Minecraft var1, int var2, int var3) {
         if (this.visible) {
            var1.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
            short var7 = 219;
            int var8 = 0;
            if (!this.enabled) {
               var8 += this.width * 2;
            } else if (this.field_146142_r) {
               var8 += this.width * 1;
            } else if (this.hovered) {
               var8 += this.width * 3;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, var8, var7, this.width, this.height);
            if (!GuiBeacon.beaconGuiTextures.equals(this.field_146145_o)) {
               var1.getTextureManager().bindTexture(this.field_146145_o);
            }

            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.field_146144_p, this.field_146143_q, 18, 18);
         }
      }

      public boolean func_146141_c() {
         return this.field_146142_r;
      }

      public void func_146140_b(boolean var1) {
         this.field_146142_r = var1;
      }
   }

   public class CancelButton extends GuiBeacon.Button {
      private static final long d = 101733445542950L;

      public CancelButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.beaconGuiTextures, 112, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int var1, int var2) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), var1, var2);
      }
   }

   public class ConfirmButton extends GuiBeacon.Button {
      private static final long d = 124393627272492L;

      public ConfirmButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.beaconGuiTextures, 90, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int var1, int var2) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done"), var1, var2);
      }
   }

   public class PowerButton extends GuiBeacon.Button {
      private final int field_146149_p;
      private final int field_146148_q;
      private static final long d = 101815826522959L;

      public PowerButton(int var2, int var3, int var4, int var5, int var6) {
         super(
            var2,
            var3,
            var4,
            GuiContainer.inventoryBackground,
            0 + Potion.potionTypes[var5].getStatusIconIndex() % 8 * 18,
            198 + Potion.potionTypes[var5].getStatusIconIndex() / 8 * 18
         );
         this.field_146149_p = var5;
         this.field_146148_q = var6;
      }

      @Override
      public void drawButtonForegroundLayer(int var1, int var2) {
         String var6 = I18n.format(Potion.potionTypes[this.field_146149_p].getName());
         if (this.field_146148_q >= 3 && this.field_146149_p != Potion.regeneration.id) {
            var6 = var6 + " II";
         }

         GuiBeacon.this.drawCreativeTabHoveringText(var6, var1, var2);
      }
   }
}
