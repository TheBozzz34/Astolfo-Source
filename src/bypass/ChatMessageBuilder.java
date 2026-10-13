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
   private static final long c = 96288178624678L;

   public ChatMessageBuilder(ClientChat var1) {
      this.k = var1;
      this.b = new ChatComponentText("");
      this.N = false;
      this.d = new ChatStyle();
      this.q = new ChatComponentText("");
   }

   public ChatMessageBuilder i(String var1) {
      this.w();
      this.q = new ChatComponentText(var1);
      this.d = new ChatStyle();
      if (this.f) {
         Matcher var4 = ClientChat.N().matcher(var1);

         while (var4.find()) {
            this.m(var1.substring(var4.start(1), var4.end(0)));
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

   public ChatMessageBuilder M(EnumChatFormatting var1) {
      this.d.setColor(var1);
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

   public ChatMessageBuilder V(String var1) {
      this.d.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, var1));
      return this;
   }

   public ChatMessageBuilder y(String var1) {
      this.d.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(var1)));
      return this;
   }

   public ChatMessageBuilder m(String var1) {
      this.d.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, var1));
      return this;
   }

   public ChatMessageBuilder g(TextAlignment var1) {
      this.w();
      this.b.appendSibling(new AlignedChatComponent(this.k, var1));
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
      if (!this.b.getSiblings().stream().noneMatch(var0 -> var0 instanceof AlignedChatComponent)) {
         int var3 = MathHelper.floor_float(MathHelper.floor_float(mc.gameSettings.l6 * 280.0F + 40.0F) / mc.gameSettings.chatScale);
         List var4 = GuiUtilRenderComponents.splitText(this.b, var3, mc.fontRendererObj, false, false);
         ChatComponentText var5 = this.b;
         var5.getSiblings().add(0, new AlignedChatComponent(this.k, TextAlignment.CENTER));
         int var6 = 0;
         ArrayList var7 = new ArrayList();
         ChatComponentText var8 = new ChatComponentText("");

         for (IChatComponent var10 : var5) {
            var8.getSiblings().add(new ChatComponentText(var10.getUnformattedText()).setChatStyle(var10.getChatStyle()));
            if (var10 instanceof AlignedChatComponent) {
               AlignedChatComponent var11 = (AlignedChatComponent)var10;
               String var12 = GuiUtilRenderComponents.func_178909_a(var8.getFormattedText(), false);
               var11.x = mc.fontRendererObj.getStringWidth(var12);
               var6 += var11.x;
               var7.add(var11);
               var8 = new ChatComponentText("");
            }
         }

         int var15 = var3 - var6;
         int var16 = var7.size();
         int var17 = var15 / var16;

         for (int var18 = 0; var18 < var7.size(); var18++) {
            AlignedChatComponent var13 = (AlignedChatComponent)var7.get(var18);
            AlignedChatComponent var14 = var18 > 0 ? (AlignedChatComponent)var7.get(var18 - 1) : null;
            var13.x = var17;
            switch (var13.f) {
               case LEFT:
                  var14.x -= var17;
                  var13.x += var17;
                  break;
               case RIGHT:
                  var14.x += var17;
                  var13.x -= var17;
            }
         }
      }
   }

   public static ChatComponentText Y(ChatMessageBuilder var0) {
      return var0.b;
   }

   public static boolean m(ChatMessageBuilder var0, boolean var1) {
      return var0.N = var1;
   }
}
