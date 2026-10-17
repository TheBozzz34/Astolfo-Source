package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
   private final EntityPlayer editingPlayer;
   private final ItemStack bookObj;
   private final boolean bookIsUnsigned;
   private boolean bookIsModified;
   private boolean bookGettingSigned;
   private int updateCount;
   private int bookImageWidth = 192;
   private int bookImageHeight = 192;
   private int bookTotalPages = 1;
   private int currPage;
   private NBTTagList bookPages;
   private String bookTitle = "";
   private List<IChatComponent> field_175386_A;
   private int field_175387_B = -1;
   private GuiScreenBook.NextPageButton buttonNextPage;
   private GuiScreenBook.NextPageButton buttonPreviousPage;
   private GuiButton buttonDone;
   private GuiButton buttonSign;
   private GuiButton buttonFinalize;
   private GuiButton buttonCancel;

   public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
      this.editingPlayer = player;
      this.bookObj = book;
      this.bookIsUnsigned = isUnsigned;
      if (book.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = book.getTagCompound();
         this.bookPages = nbtTagCompound.getTagList("pages", 8);
         if (this.bookPages != null) {
            this.bookPages = (NBTTagList)this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();
            if (this.bookTotalPages < 1) {
               this.bookTotalPages = 1;
            }
         }
      }

      if (this.bookPages == null && isUnsigned) {
         this.bookPages = new NBTTagList();
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages = 1;
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateCount++;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.bookIsUnsigned) {
         this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton")));
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done")));
         this.buttonList
            .add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton")));
         this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel")));
      } else {
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done")));
      }

      int i = (this.width - this.bookImageWidth) / 2;
      byte b = 2;
      this.buttonList.add(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, i + 120, b + 154, true));
      this.buttonList.add(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, i + 38, b + 154, false));
      this.updateButtons();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   private void updateButtons() {
      this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
      if (this.bookIsUnsigned) {
         this.buttonSign.visible = !this.bookGettingSigned;
         this.buttonCancel.visible = this.bookGettingSigned;
         this.buttonFinalize.visible = this.bookGettingSigned;
         this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
      }
   }

   private void sendBookToServer(boolean publish) throws IOException {
      if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
         while (this.bookPages.tagCount() > 1) {
            String string = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
            if (string.length() != 0) {
               break;
            }

            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
         }

         if (this.bookObj.hasTagCompound()) {
            NBTTagCompound nbtTagCompound = this.bookObj.getTagCompound();
            nbtTagCompound.setTag("pages", this.bookPages);
         } else {
            this.bookObj.setTagInfo("pages", this.bookPages);
         }

         String string2 = "MC|BEdit";
         if (publish) {
            string2 = "MC|BSign";
            this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
            this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

            for (int i = 0; i < this.bookPages.tagCount(); i++) {
               String string3 = this.bookPages.getStringTagAt(i);
               ChatComponentText chatComponentText = new ChatComponentText(string3);
               string3 = IChatComponent.Serializer.componentToJson(chatComponentText);
               this.bookPages.set(i, new NBTTagString(string3));
            }

            this.bookObj.setItem(Items.written_book);
         }

         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeItemStackToBuffer(this.bookObj);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(string2, packetBuffer));
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.sendBookToServer(false);
         } else if (button.id == 3 && this.bookIsUnsigned) {
            this.bookGettingSigned = true;
         } else if (button.id == 1) {
            if (this.currPage < this.bookTotalPages - 1) {
               this.currPage++;
            } else if (this.bookIsUnsigned) {
               this.addNewPage();
               if (this.currPage < this.bookTotalPages - 1) {
                  this.currPage++;
               }
            }
         } else if (button.id == 2) {
            if (this.currPage > 0) {
               this.currPage--;
            }
         } else if (button.id == 5 && this.bookGettingSigned) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (button.id == 4 && this.bookGettingSigned) {
            this.bookGettingSigned = false;
         }

         this.updateButtons();
      }
   }

   private void addNewPage() {
      if (this.bookPages != null && this.bookPages.tagCount() < 50) {
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages++;
         this.bookIsModified = true;
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.bookIsUnsigned) {
         if (this.bookGettingSigned) {
            this.keyTypedInTitle(typedChar, keyCode);
         } else {
            this.keyTypedInBook(typedChar, keyCode);
         }
      }
   }

   private void keyTypedInBook(char typedChar, int keyCode) {
      if (GuiScreen.isKeyComboCtrlV(keyCode)) {
         this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
      } else {
         switch (keyCode) {
            case 14:
               String string = this.pageGetCurrent();
               if (string.length() > 0) {
                  this.pageSetCurrent(string.substring(0, string.length() - 1));
               }

               return;
            case 28:
            case 156:
               this.pageInsertIntoCurrent("\n");
               return;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                  this.pageInsertIntoCurrent(Character.toString(typedChar));
               }
         }
      }
   }

   private void keyTypedInTitle(char c, int i) throws IOException {
      switch (i) {
         case 14:
            if (!this.bookTitle.isEmpty()) {
               this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
               this.updateButtons();
            }

            return;
         case 28:
         case 156:
            if (!this.bookTitle.isEmpty()) {
               this.sendBookToServer(true);
               this.mc.displayGuiScreen((GuiScreen)null);
            }

            return;
         default:
            if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(c)) {
               this.bookTitle = this.bookTitle + Character.toString(c);
               this.updateButtons();
               this.bookIsModified = true;
            }
      }
   }

   private String pageGetCurrent() {
      return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
   }

   private void pageSetCurrent(String string) {
      if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
         this.bookPages.set(this.currPage, new NBTTagString(string));
         this.bookIsModified = true;
      }
   }

   private void pageInsertIntoCurrent(String string) {
      String string2 = this.pageGetCurrent();
      String string3 = string2 + string;
      int i = this.fontRendererObj.splitStringWidth(string3 + "" + EnumChatFormatting.BLACK + "_", 118);
      if (i <= 128 && string3.length() < 256) {
         this.pageSetCurrent(string3);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(bookGuiTextures);
      int i = (this.width - this.bookImageWidth) / 2;
      byte b = 2;
      this.drawTexturedModalRect(i, b, 0, 0, this.bookImageWidth, this.bookImageHeight);
      if (this.bookGettingSigned) {
         String string = this.bookTitle;
         if (this.bookIsUnsigned) {
            if (this.updateCount / 6 % 2 == 0) {
               string = string + "" + EnumChatFormatting.BLACK + "_";
            } else {
               string = string + "" + EnumChatFormatting.GRAY + "_";
            }
         }

         String string2 = I18n.format("book.editTitle");
         int j = this.fontRendererObj.getStringWidth(string2);
         this.fontRendererObj.drawStringNoShadow(string2, i + 36 + (116 - j) / 2, b + 16 + 16, 0);
         int k = this.fontRendererObj.getStringWidth(string);
         this.fontRendererObj.drawStringNoShadow(string, i + 36 + (116 - k) / 2, b + 48, 0);
         String string3 = I18n.format("book.byAuthor", this.editingPlayer.getName());
         int l = this.fontRendererObj.getStringWidth((java.lang.String)(string3));
         this.fontRendererObj.drawStringNoShadow(EnumChatFormatting.DARK_GRAY + string3, i + 36 + (116 - l) / 2, b + 48 + 10, 0);
         String string4 = I18n.format("book.finalizeWarning");
         this.fontRendererObj.drawSplitString(string4, i + 36, b + 80, 116, 0);
      } else {
         String string5 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
         String string6 = "";
         if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            string6 = this.bookPages.getStringTagAt(this.currPage);
         }

         if (this.bookIsUnsigned) {
            if (this.fontRendererObj.getBidiFlag()) {
               string6 = string6 + "_";
            } else if (this.updateCount / 6 % 2 == 0) {
               string6 = string6 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               string6 = string6 + "" + EnumChatFormatting.GRAY + "_";
            }
         } else if (this.field_175387_B != this.currPage) {
            if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound())) {
               try {
                  IChatComponent iChatComponent = IChatComponent.Serializer.jsonToComponent(string6);
                  this.field_175386_A = iChatComponent != null ? GuiUtilRenderComponents.splitText(iChatComponent, 116, this.fontRendererObj, true, true) : null;
               } catch (JsonParseException jsonParseException) {
                  this.field_175386_A = null;
               }
            } else {
               ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
               this.field_175386_A = Lists.newArrayList(chatComponentText);
            }

            this.field_175387_B = this.currPage;
         }

         int m = this.fontRendererObj.getStringWidth(string5);
         this.fontRendererObj.drawStringNoShadow(string5, i - m + this.bookImageWidth - 44, b + 16, 0);
         if (this.field_175386_A == null) {
            this.fontRendererObj.drawSplitString(string6, i + 36, b + 16 + 16, 116, 0);
         } else {
            int n = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

            for (int i2 = 0; i2 < n; i2++) {
               IChatComponent iChatComponent2 = this.field_175386_A.get(i2);
               this.fontRendererObj.drawStringNoShadow(iChatComponent2.getUnformattedText(), i + 36, b + 16 + 16 + i2 * this.fontRendererObj.FONT_HEIGHT, 0);
            }

            IChatComponent iChatComponent3 = this.func_175385_b(mouseX, mouseY);
            if (iChatComponent3 != null) {
               this.handleComponentHover(iChatComponent3, mouseX, mouseY);
            }
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         IChatComponent iChatComponent = this.func_175385_b(mouseX, mouseY);
         if (this.handleComponentClick(iChatComponent)) {
            return;
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public boolean handleComponentClick(IChatComponent component) {
      ClickEvent clickEvent = component == null ? null : component.getChatStyle().getChatClickEvent();
      if (clickEvent == null) {
         return false;
      }

      if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String string = clickEvent.getValue();

         try {
            int i = Integer.parseInt(string) - 1;
            if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
               this.currPage = i;
               this.updateButtons();
               return true;
            }
         } catch (Throwable throwable) {
         }

         return false;
      } else {
         boolean bl = super.handleComponentClick(component);
         if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return bl;
      }
   }

   public IChatComponent func_175385_b(int i, int j) {
      if (this.field_175386_A == null) {
         return null;
      }

      int k = i - (this.width - this.bookImageWidth) / 2 - 36;
      int l = j - 2 - 16 - 16;
      if (k >= 0 && l >= 0) {
         int m = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());
         if (k <= 116 && l < this.mc.fontRendererObj.FONT_HEIGHT * m + m) {
            int n = l / this.mc.fontRendererObj.FONT_HEIGHT;
            if (n >= 0 && n < this.field_175386_A.size()) {
               IChatComponent iChatComponent = this.field_175386_A.get(n);
               int i2 = 0;

               for (IChatComponent iChatComponent2 : iChatComponent) {
                  if (iChatComponent2 instanceof ChatComponentText) {
                     i2 += this.mc.fontRendererObj.getStringWidth(((ChatComponentText)iChatComponent2).getChatComponentText_TextValue());
                     if (i2 > k) {
                        return iChatComponent2;
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class NextPageButton extends GuiButton {
      private final boolean field_146151_o;

      public NextPageButton(int i, int j, int k, boolean bl) {
         super(i, j, k, 23, 13, "");
         this.field_146151_o = bl;
      }

      @Override
      public void drawButton(Minecraft mcIn, int mouseX, int mouseY) {
         if (this.visible) {
            boolean bl = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mcIn.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
            byte b = 0;
            short s = 192;
            if (bl) {
               b += 23;
            }

            if (!this.field_146151_o) {
               s += 13;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, b, s, 23, 13);
         }
      }
   }
}
