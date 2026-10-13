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

   public GuiEnchantment(InventoryPlayer var1, World var2, IWorldNameable var3) {
      super(new ContainerEnchantment(var1, var2));
      this.playerInventory = var1;
      this.container = (ContainerEnchantment)this.inventorySlots;
      this.field_175380_I = var3;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      this.fontRendererObj.D(this.field_175380_I.getDisplayName().getUnformattedText(), 12.0F, 5.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.func_147068_g();
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;

      for (int var9 = 0; var9 < 3; var9++) {
         int var10 = var1 - (var7 + 60);
         int var11 = var2 - (var8 + 14 + 19 * var9);
         if (var10 >= 0 && var11 >= 0 && var10 < 108 && var11 < 19 && this.container.enchantItem(this.mc.thePlayer, var9)) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, var9);
         }
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      long var4 = bb ^ 140536394508358L;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      ScaledResolution var9 = new ScaledResolution(this.mc);
      GlStateManager.viewport(
         (var9.getScaledWidth() - 320) / 2 * var9.getScaleFactor(),
         (var9.getScaledHeight() - 240) / 2 * var9.getScaleFactor(),
         320 * var9.getScaleFactor(),
         240 * var9.getScaleFactor()
      );
      GlStateManager.translate(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float var10 = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(0.0F, 3.3F, -16.0F);
      GlStateManager.scale(var10, var10, var10);
      float var11 = 5.0F;
      GlStateManager.scale(var11, var11, var11);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
      GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
      float var12 = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * var1;
      GlStateManager.translate((1.0F - var12) * 0.2F, (1.0F - var12) * 0.1F, (1.0F - var12) * 0.25F);
      GlStateManager.rotate(-(1.0F - var12) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      float var13 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * var1 + 0.25F;
      float var14 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * var1 + 0.75F;
      var13 = (var13 - MathHelper.truncateDoubleToInt(var13)) * 1.6F - 0.3F;
      var14 = (var14 - MathHelper.truncateDoubleToInt(var14)) * 1.6F - 0.3F;
      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      if (var14 > 1.0F) {
         var14 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      MODEL_BOOK.render((Entity)null, 0.0F, var13, var14, var12, 0.0F, 0.0625F);
      long var10000 = 5778989311847396488L ^ var4;
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      var10000 = 5492193014962982679L ^ var4;
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.xpSeed);
      int var15 = this.container.getLapisAmount();

      for (int var16 = 0; var16 < 3; var16++) {
         int var17 = var7 + 60;
         int var18 = var17 + 20;
         byte var19 = 86;
         String var20 = EnchantmentNameParts.getInstance().generateNewRandomName();
         this.zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int var21 = this.container.enchantLevels[var16];
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (var21 == 0) {
            this.drawTexturedModalRect(var17, var8 + 14 + 19 * var16, 0, 185, 108, 19);
         } else {
            String var22 = "" + var21;
            FontRenderer var23 = this.mc.standardGalacticFontRenderer;
            var10000 = 2733236796270204104L ^ var4;
            int var24 = 6839882;
            if ((var15 < var16 + 1 || this.mc.thePlayer.experienceLevel < var21) && !this.mc.thePlayer.capabilities.isCreativeMode) {
               this.drawTexturedModalRect(var17, var8 + 14 + 19 * var16, 0, 185, 108, 19);
               this.drawTexturedModalRect(var17 + 1, var8 + 15 + 19 * var16, 16 * var16, 239, 16, 16);
               var23.drawSplitString(var20, var18, var8 + 16 + 19 * var16, var19, (var24 & 16711422) >> 1);
               var24 = 4226832;
            } else {
               int var25 = var2 - (var7 + 60);
               int var26 = var3 - (var8 + 14 + 19 * var16);
               if (var25 >= 0 && var26 >= 0 && var25 < 108 && var26 < 19) {
                  this.drawTexturedModalRect(var17, var8 + 14 + 19 * var16, 0, 204, 108, 19);
                  var24 = 16777088;
               } else {
                  this.drawTexturedModalRect(var17, var8 + 14 + 19 * var16, 0, 166, 108, 19);
               }

               this.drawTexturedModalRect(var17 + 1, var8 + 15 + 19 * var16, 16 * var16, 223, 16, 16);
               var23.drawSplitString(var20, var18, var8 + 16 + 19 * var16, var19, var24);
               var24 = 8453920;
            }

            var23 = this.mc.fontRendererObj;
            var23.drawStringWithShadow(var22, var18 + 86 - var23.getStringWidth(var22), var8 + 16 + 19 * var16 + 7, var24);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
      boolean var7 = this.mc.thePlayer.capabilities.isCreativeMode;
      int var8 = this.container.getLapisAmount();

      for (int var9 = 0; var9 < 3; var9++) {
         int var10 = this.container.enchantLevels[var9];
         int var11 = this.container.enchantmentIds[var9];
         int var12 = var9 + 1;
         if (this.isPointInRegion(60, 14 + 19 * var9, 108, 17, var1, var2) && var10 > 0 && var11 >= 0) {
            ArrayList var13 = Lists.newArrayList();
            if (var11 >= 0 && Enchantment.getEnchantmentById(var11 & 0xFF) != null) {
               String var14 = Enchantment.getEnchantmentById(var11 & 0xFF).getTranslatedName((var11 & 0xFF00) >> 8);
               var13.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.format("container.enchant.clue", var14));
            }

            if (!var7) {
               if (var11 >= 0) {
                  var13.add("");
               }

               if (this.mc.thePlayer.experienceLevel < var10) {
                  var13.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.container.enchantLevels[var9]);
               } else {
                  String var15 = "";
                  if (var12 == 1) {
                     var15 = I18n.format("container.enchant.lapis.one");
                  } else {
                     var15 = I18n.format("container.enchant.lapis.many", var12);
                  }

                  if (var8 >= var12) {
                     var13.add(EnumChatFormatting.GRAY.toString() + "" + var15);
                  } else {
                     var13.add(EnumChatFormatting.RED.toString() + "" + var15);
                  }

                  if (var12 == 1) {
                     var15 = I18n.format("container.enchant.level.one");
                  } else {
                     var15 = I18n.format("container.enchant.level.many", var12);
                  }

                  var13.add(EnumChatFormatting.GRAY.toString() + "" + var15);
               }
            }

            this.drawHoveringText(var13, var1, var2);
            break;
         }
      }
   }

   public void func_147068_g() {
      ItemStack var4 = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(var4, this.field_147077_B)) {
         this.field_147077_B = var4;

         do {
            this.field_147082_x = this.field_147082_x + (this.random.nextInt(4) - this.random.nextInt(4));
         } while (!(this.field_147071_v > this.field_147082_x + 1.0F) && !(this.field_147071_v < this.field_147082_x - 1.0F));
      }

      this.field_147073_u++;
      this.field_147069_w = this.field_147071_v;
      this.field_147076_A = this.field_147080_z;
      boolean var5 = false;

      for (int var6 = 0; var6 < 3; var6++) {
         if (this.container.enchantLevels[var6] != 0) {
            var5 = true;
         }
      }

      if (var5) {
         this.field_147080_z += 0.2F;
      } else {
         this.field_147080_z -= 0.2F;
      }

      this.field_147080_z = MathHelper.clamp_float(this.field_147080_z, 0.0F, 1.0F);
      float var8 = (this.field_147082_x - this.field_147071_v) * 0.4F;
      float var7 = 0.2F;
      var8 = MathHelper.clamp_float(var8, -var7, var7);
      this.field_147081_y = this.field_147081_y + (var8 - this.field_147081_y) * 0.9F;
      this.field_147071_v = this.field_147071_v + this.field_147081_y;
   }
}
