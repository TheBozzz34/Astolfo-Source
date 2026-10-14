package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final ModelBook MODEL_BOOK = new ModelBook();
   private final InventoryPlayer playerInventory;
   private Random random = new Random();
   private ContainerEnchantment container;
   public int field_147073_u;
   public float field_147071_v;
   public float field_147069_w;
   public float field_147082_x;
   public float field_147081_y;
   public float field_147080_z;
   public float field_147076_A;
   public ItemStack field_147077_B;
   private final IWorldNameable field_175380_I;
   private static final long bb = 21062339473539L;

   public GuiEnchantment(InventoryPlayer inventory, World worldIn, IWorldNameable iWorldNameable) {
      super(new ContainerEnchantment(inventory, worldIn));
      this.playerInventory = inventory;
      this.container = (ContainerEnchantment)this.inventorySlots;
      this.field_175380_I = iWorldNameable;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.D(this.field_175380_I.getDisplayName().getUnformattedText(), 12.0F, 5.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.func_147068_g();
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;

      for (int k = 0; k < 3; k++) {
         int l = mouseX - (i + 60);
         int m = mouseY - (j + 14 + 19 * k);
         if (l >= 0 && m >= 0 && l < 108 && m < 19 && this.container.enchantItem(this.mc.thePlayer, k)) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, k);
         }
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      long l = bb ^ 140536394508358L;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.viewport(
         (scaledResolution.getScaledWidth() - 320) / 2 * scaledResolution.getScaleFactor(),
         (scaledResolution.getScaledHeight() - 240) / 2 * scaledResolution.getScaleFactor(),
         320 * scaledResolution.getScaleFactor(),
         240 * scaledResolution.getScaleFactor()
      );
      GlStateManager.translate(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float f = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(0.0F, 3.3F, -16.0F);
      GlStateManager.scale(f, f, f);
      float g = 5.0F;
      GlStateManager.scale(g, g, g);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
      GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
      float h = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * partialTicks;
      GlStateManager.translate((1.0F - h) * 0.2F, (1.0F - h) * 0.1F, (1.0F - h) * 0.25F);
      GlStateManager.rotate(-(1.0F - h) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      float f2 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.25F;
      float f3 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.75F;
      f2 = (f2 - MathHelper.truncateDoubleToInt(f2)) * 1.6F - 0.3F;
      f3 = (f3 - MathHelper.truncateDoubleToInt(f3)) * 1.6F - 0.3F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      MODEL_BOOK.render((Entity)null, 0.0F, f2, f3, h, 0.0F, 0.0625F);
      long m = 5778989311847396488L ^ l;
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      m = 5492193014962982679L ^ l;
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.xpSeed);
      int k = this.container.getLapisAmount();

      for (int n = 0; n < 3; n++) {
         int i2 = i + 60;
         int i3 = i2 + 20;
         byte b = 86;
         String string = EnchantmentNameParts.getInstance().generateNewRandomName();
         this.zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int i4 = this.container.enchantLevels[n];
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (i4 == 0) {
            this.drawTexturedModalRect(i2, j + 14 + 19 * n, 0, 185, 108, 19);
         } else {
            String string2 = "" + i4;
            FontRenderer fontRenderer = this.mc.standardGalacticFontRenderer;
            m = 2733236796270204104L ^ l;
            int i5 = 6839882;
            if ((k < n + 1 || this.mc.thePlayer.experienceLevel < i4) && !this.mc.thePlayer.capabilities.isCreativeMode) {
               this.drawTexturedModalRect(i2, j + 14 + 19 * n, 0, 185, 108, 19);
               this.drawTexturedModalRect(i2 + 1, j + 15 + 19 * n, 16 * n, 239, 16, 16);
               fontRenderer.drawSplitString(string, i3, j + 16 + 19 * n, b, (i5 & 16711422) >> 1);
               i5 = 4226832;
            } else {
               int i6 = mouseX - (i + 60);
               int i7 = mouseY - (j + 14 + 19 * n);
               if (i6 >= 0 && i7 >= 0 && i6 < 108 && i7 < 19) {
                  this.drawTexturedModalRect(i2, j + 14 + 19 * n, 0, 204, 108, 19);
                  i5 = 16777088;
               } else {
                  this.drawTexturedModalRect(i2, j + 14 + 19 * n, 0, 166, 108, 19);
               }

               this.drawTexturedModalRect(i2 + 1, j + 15 + 19 * n, 16 * n, 223, 16, 16);
               fontRenderer.drawSplitString(string, i3, j + 16 + 19 * n, b, i5);
               i5 = 8453920;
            }

            fontRenderer = this.mc.fontRendererObj;
            fontRenderer.drawStringWithShadow(string2, i3 + 86 - fontRenderer.getStringWidth(string2), j + 16 + 19 * n + 7, i5);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      boolean bl = this.mc.thePlayer.capabilities.isCreativeMode;
      int i = this.container.getLapisAmount();

      for (int j = 0; j < 3; j++) {
         int k = this.container.enchantLevels[j];
         int l = this.container.enchantmentIds[j];
         int m = j + 1;
         if (this.isPointInRegion(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0 && l >= 0) {
            ArrayList arrayList = Lists.newArrayList();
            if (l >= 0 && Enchantment.getEnchantmentById(l & 0xFF) != null) {
               String string = Enchantment.getEnchantmentById(l & 0xFF).getTranslatedName((l & 0xFF00) >> 8);
               arrayList.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.format("container.enchant.clue", string));
            }

            if (!bl) {
               if (l >= 0) {
                  arrayList.add("");
               }

               if (this.mc.thePlayer.experienceLevel < k) {
                  arrayList.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.container.enchantLevels[j]);
               } else {
                  String string2 = "";
                  if (m == 1) {
                     string2 = I18n.format("container.enchant.lapis.one");
                  } else {
                     string2 = I18n.format("container.enchant.lapis.many", m);
                  }

                  if (i >= m) {
                     arrayList.add(EnumChatFormatting.GRAY.toString() + "" + string2);
                  } else {
                     arrayList.add(EnumChatFormatting.RED.toString() + "" + string2);
                  }

                  if (m == 1) {
                     string2 = I18n.format("container.enchant.level.one");
                  } else {
                     string2 = I18n.format("container.enchant.level.many", m);
                  }

                  arrayList.add(EnumChatFormatting.GRAY.toString() + "" + string2);
               }
            }

            this.drawHoveringText(arrayList, mouseX, mouseY);
            break;
         }
      }
   }

   public void func_147068_g() {
      ItemStack itemStack = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(itemStack, this.field_147077_B)) {
         this.field_147077_B = itemStack;

         do {
            this.field_147082_x = this.field_147082_x + (this.random.nextInt(4) - this.random.nextInt(4));
         } while (!(this.field_147071_v > this.field_147082_x + 1.0F) && !(this.field_147071_v < this.field_147082_x - 1.0F));
      }

      this.field_147073_u++;
      this.field_147069_w = this.field_147071_v;
      this.field_147076_A = this.field_147080_z;
      boolean bl = false;

      for (int i = 0; i < 3; i++) {
         if (this.container.enchantLevels[i] != 0) {
            bl = true;
         }
      }

      if (bl) {
         this.field_147080_z += 0.2F;
      } else {
         this.field_147080_z -= 0.2F;
      }

      this.field_147080_z = MathHelper.clamp_float(this.field_147080_z, 0.0F, 1.0F);
      float f = (this.field_147082_x - this.field_147071_v) * 0.4F;
      float g = 0.2F;
      f = MathHelper.clamp_float(f, -g, g);
      this.field_147081_y = this.field_147081_y + (f - this.field_147081_y) * 0.9F;
      this.field_147071_v = this.field_147071_v + this.field_147081_y;
   }
}
