package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final IChatComponent value;
   private static final long a = 125928444258670L;

   public HoverEvent(HoverEvent.Action var1, IChatComponent var2) {
      this.action = var1;
      this.value = var2;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public IChatComponent getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         HoverEvent var5 = (HoverEvent)var1;
         if (this.action != var5.action) {
            return false;
         }

         if (this.value != null) {
            if (!this.value.equals(var5.value)) {
               return false;
            }
         } else if (var5.value != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var4 = this.action.hashCode();
      return 31 * var4 + (this.value != null ? this.value.hashCode() : 0);
   }

   public enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ACHIEVEMENT("show_achievement", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map<String, HoverEvent.Action> nameMapping = Maps.newHashMap();
      private final boolean allowedInChat;
      private final String canonicalName;

      Action(String var3, boolean var4) {
         this.canonicalName = var3;
         this.allowedInChat = var4;
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      public static HoverEvent.Action getValueByCanonicalName(String var0) {
         return nameMapping.get(var0);
      }

      static {
         for (HoverEvent.Action var14 : values()) {
            nameMapping.put(var14.getCanonicalName(), var14);
         }
      }
   }
}
