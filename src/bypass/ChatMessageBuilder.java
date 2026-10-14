package bypass;

import bypass.chat.AlignedChatComponent;
import bypass.chat.TextAlignment;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class ChatMessageBuilder implements ClientAccess {
   private ChatComponentText b;
   private boolean N;
   private boolean f;
   private ChatStyle d;
   private ChatComponentStyle q;
   public final ClientChat k;

   public ChatMessageBuilder(ClientChat clientChat) {
      this.k = clientChat;
      this.b = new ChatComponentText("");
      this.N = false;
      this.d = new ChatStyle();
      this.q = new ChatComponentText("");
   }

   public ChatMessageBuilder i(String string) {
      this.w();
      this.q = new ChatComponentText(string);
      this.d = new ChatStyle();
      if (this.f) {
         Matcher matcher2 = ClientChat.N().matcher(string);

         while (matcher2.find()) {
            this.m(string.substring(matcher2.start(1), matcher2.end(0)));
         }
      }

      if (this.N) {
         this.M(ClientChat.K(this.k));
      }

      return this;
   }

   public ChatMessageBuilder S() {
      this.f = true;
      return this;
   }

   public ChatMessageBuilder M(EnumChatFormatting enumChatFormatting) {
      this.d.setColor(enumChatFormatting);
      return this;
   }

   public ChatMessageBuilder M() {
      this.d.setBold(true);
      return this;
   }

   public ChatMessageBuilder V() {
      this.d.setItalic(true);
      return this;
   }

   public ChatMessageBuilder F() {
      this.d.setStrikethrough(true);
      return this;
   }

   public ChatMessageBuilder j() {
      this.d.setUnderlined(true);
      return this;
   }

   public ChatMessageBuilder V(String string) {
      this.d.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, string));
      return this;
   }

   public ChatMessageBuilder y(String string) {
      this.d.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(string)));
      return this;
   }

   public ChatMessageBuilder m(String string) {
      this.d.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, string));
      return this;
   }

   public ChatMessageBuilder g(TextAlignment textAlignment) {
      this.w();
      this.b.appendSibling(new AlignedChatComponent(this.k, textAlignment));
      return this;
   }

   public ChatMessage N() {
      this.w();
      this.resolveColumns();
      return new ChatMessage(this.k, this.b);
   }

   private void w() {
      if (this.q != null) {
         this.b.appendSibling(this.q.setChatStyle(this.d));
         this.q = null;
      }
   }

   private void resolveColumns() {
      if (!this.b.getSiblings().stream().noneMatch(iChatComponent -> iChatComponent instanceof AlignedChatComponent)) {
         int i = MathHelper.floor_float(MathHelper.floor_float(mc.gameSettings.l6 * 280.0F + 40.0F) / mc.gameSettings.chatScale);
         List list = GuiUtilRenderComponents.splitText(this.b, i, mc.fontRendererObj, false, false);
         ChatComponentText chatComponentText = this.b;
         chatComponentText.getSiblings().add(0, new AlignedChatComponent(this.k, TextAlignment.CENTER));
         int j = 0;
         ArrayList arrayList = new ArrayList();
         ChatComponentText chatComponentText2 = new ChatComponentText("");

         for (IChatComponent iChatComponent2 : chatComponentText) {
            chatComponentText2.getSiblings().add(new ChatComponentText(iChatComponent2.getUnformattedText()).setChatStyle(iChatComponent2.getChatStyle()));
            if (iChatComponent2 instanceof AlignedChatComponent) {
               AlignedChatComponent alignedChatComponent = (AlignedChatComponent)iChatComponent2;
               String string = GuiUtilRenderComponents.func_178909_a(chatComponentText2.getFormattedText(), false);
               alignedChatComponent.x = mc.fontRendererObj.getStringWidth(string);
               j += alignedChatComponent.x;
               arrayList.add(alignedChatComponent);
               chatComponentText2 = new ChatComponentText("");
            }
         }

         int l = i - j;
         int m = arrayList.size();
         int n = l / m;

         for (int i2 = 0; i2 < arrayList.size(); i2++) {
            AlignedChatComponent alignedChatComponent2 = (AlignedChatComponent)arrayList.get(i2);
            AlignedChatComponent alignedChatComponent3 = i2 > 0 ? (AlignedChatComponent)arrayList.get(i2 - 1) : null;
            alignedChatComponent2.x = n;
            switch (alignedChatComponent2.f) {
               case LEFT:
                  alignedChatComponent3.x -= n;
                  alignedChatComponent2.x += n;
                  break;
               case RIGHT:
                  alignedChatComponent3.x += n;
                  alignedChatComponent2.x -= n;
            }
         }
      }
   }

   public static ChatComponentText Y(ChatMessageBuilder chatMessageBuilder) {
      return chatMessageBuilder.b;
   }

   public static boolean m(ChatMessageBuilder chatMessageBuilder, boolean bl) {
      return chatMessageBuilder.N = bl;
   }
}
