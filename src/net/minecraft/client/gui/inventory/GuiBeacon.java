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

   public GuiBeacon(InventoryPlayer playerInventory, IInventory tileBeaconIn) {
      super(new ContainerBeacon(playerInventory, tileBeaconIn));
      this.tileBeacon = tileBeaconIn;
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
      int i = this.tileBeacon.getField(0);
      int j = this.tileBeacon.getField(1);
      int k = this.tileBeacon.getField(2);
      if (this.buttonsNotDrawn && i >= 0) {
         this.buttonsNotDrawn = false;

         for (int l = 0; l <= 2; l++) {
            int m = TileEntityBeacon.effectsList[l].length;
            int n = m * 22 + (m - 1) * 2;

            for (int i2 = 0; i2 < m; i2++) {
               int i3 = TileEntityBeacon.effectsList[l][i2].id;
               GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
                  l << 8 | i3, this.guiLeft + 76 + i2 * 24 - n / 2, this.guiTop + 22 + l * 25, i3, l
               );
               this.buttonList.add(powerButton);
               if (l >= i) {
                  powerButton.enabled = false;
               } else if (i3 == j) {
                  powerButton.func_146140_b(true);
               }
            }
         }

         byte b = 3;
         int i4 = TileEntityBeacon.effectsList[b].length + 1;
         int i5 = i4 * 22 + (i4 - 1) * 2;

         for (int i6 = 0; i6 < i4 - 1; i6++) {
            int i7 = TileEntityBeacon.effectsList[b][i6].id;
            GuiBeacon.PowerButton powerButton2 = new GuiBeacon.PowerButton(
               b << 8 | i7, this.guiLeft + 167 + i6 * 24 - i5 / 2, this.guiTop + 47, i7, b
            );
            this.buttonList.add(powerButton2);
            if (b >= i) {
               powerButton2.enabled = false;
            } else if (i7 == k) {
               powerButton2.func_146140_b(true);
            }
         }

         if (j > 0) {
            GuiBeacon.PowerButton powerButton3 = new GuiBeacon.PowerButton(
               b << 8 | j, this.guiLeft + 167 + (i4 - 1) * 24 - i5 / 2, this.guiTop + 47, j, b
            );
            this.buttonList.add(powerButton3);
            if (b >= i) {
               powerButton3.enabled = false;
            } else if (j == k) {
               powerButton3.func_146140_b(true);
            }
         }
      }

      this.beaconConfirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && j > 0;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == -2) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button.id == -1) {
         String string = "MC|Beacon";
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.tileBeacon.getField(1));
         packetBuffer.writeInt(this.tileBeacon.getField(2));
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(string, packetBuffer));
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button instanceof GuiBeacon.PowerButton) {
         if (((GuiBeacon.PowerButton)button).func_146141_c()) {
            return;
         }

         int i = button.id;
         int j = i & 0xFF;
         int k = i >> 8;
         if (k < 3) {
            this.tileBeacon.setField(1, j);
         } else {
            this.tileBeacon.setField(2, j);
         }

         this.buttonList.clear();
         this.initGui();
         this.updateScreen();
      }
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      RenderHelper.disableStandardItemLighting();
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.secondary"), 169, 10, 14737632);

      for (GuiButton guiButton : this.buttonList) {
         if (guiButton.isMouseOver()) {
            guiButton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(beaconGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.emerald), i + 42, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond), i + 42 + 22, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.gold_ingot), i + 42 + 44, j + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_ingot), i + 42 + 66, j + 109);
      this.itemRender.zLevel = 0.0F;
   }

   public static class Button extends GuiButton {
      private final ResourceLocation field_146145_o;
      private final int field_146144_p;
      private final int field_146143_q;
      private boolean field_146142_r;

      public Button(int i, int j, int k, ResourceLocation resourceLocation, int l, int m) {
         super(i, j, k, 22, 22, "");
         this.field_146145_o = resourceLocation;
         this.field_146144_p = l;
         this.field_146143_q = m;
      }

      @Override
      public void drawButton(Minecraft mcIn, int mouseX, int mouseY) {
         if (this.visible) {
            mcIn.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            short s = 219;
            int i = 0;
            if (!this.enabled) {
               i += this.width * 2;
            } else if (this.field_146142_r) {
               i += this.width * 1;
            } else if (this.hovered) {
               i += this.width * 3;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, i, s, this.width, this.height);
            if (!GuiBeacon.beaconGuiTextures.equals(this.field_146145_o)) {
               mcIn.getTextureManager().bindTexture(this.field_146145_o);
            }

            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.field_146144_p, this.field_146143_q, 18, 18);
         }
      }

      public boolean func_146141_c() {
         return this.field_146142_r;
      }

      public void func_146140_b(boolean bl) {
         this.field_146142_r = bl;
      }
   }

   public class CancelButton extends GuiBeacon.Button {
      public CancelButton(int i, int j, int k) {
         super(i, j, k, GuiBeacon.beaconGuiTextures, 112, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), mouseX, mouseY);
      }
   }

   public class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int i, int j, int k) {
         super(i, j, k, GuiBeacon.beaconGuiTextures, 90, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done"), mouseX, mouseY);
      }
   }

   public class PowerButton extends GuiBeacon.Button {
      private final int field_146149_p;
      private final int field_146148_q;

      public PowerButton(int i, int j, int k, int l, int m) {
         super(
            i,
            j,
            k,
            GuiContainer.inventoryBackground,
            0 + Potion.potionTypes[l].getStatusIconIndex() % 8 * 18,
            198 + Potion.potionTypes[l].getStatusIconIndex() / 8 * 18
         );
         this.field_146149_p = l;
         this.field_146148_q = m;
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         String string = I18n.format(Potion.potionTypes[this.field_146149_p].getName());
         if (this.field_146148_q >= 3 && this.field_146149_p != Potion.regeneration.id) {
            string = string + " II";
         }

         GuiBeacon.this.drawCreativeTabHoveringText(string, mouseX, mouseY);
      }
   }
}
