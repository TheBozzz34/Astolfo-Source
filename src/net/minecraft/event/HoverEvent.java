package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final IChatComponent value;
   private static final long a = 125928444258670L;

   public HoverEvent(HoverEvent.Action actionIn, IChatComponent valueIn) {
      this.action = actionIn;
      this.value = valueIn;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public IChatComponent getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object != null && this.getClass() == object.getClass()) {
         HoverEvent hoverEvent = (HoverEvent)object;
         if (this.action != hoverEvent.action) {
            return false;
         }

         if (this.value != null) {
            if (!this.value.equals(hoverEvent.value)) {
               return false;
            }
         } else if (hoverEvent.value != null) {
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
      int i = this.action.hashCode();
      return 31 * i + (this.value != null ? this.value.hashCode() : 0);
   }

   public enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ACHIEVEMENT("show_achievement", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map<String, HoverEvent.Action> nameMapping = Maps.newHashMap();
      private final boolean allowedInChat;
      private final String canonicalName;

      Action(String canonicalNameIn, boolean allowedInChatIn) {
         this.canonicalName = canonicalNameIn;
         this.allowedInChat = allowedInChatIn;
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      public static HoverEvent.Action getValueByCanonicalName(String canonicalNameIn) {
         return nameMapping.get(canonicalNameIn);
      }

      static {
         for (HoverEvent.Action action : values()) {
            nameMapping.put(action.getCanonicalName(), action);
         }
      }
   }
}
