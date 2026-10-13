package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;
   private static final long a = 4510911729302L;

   public ClickEvent(ClickEvent.Action var1, String var2) {
      this.action = var1;
      this.value = var2;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         ClickEvent var5 = (ClickEvent)var1;
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
      return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var5 = this.action.hashCode();
      return 31 * var5 + (this.value != null ? this.value.hashCode() : 0);
   }

   public enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      TWITCH_USER_INFO("twitch_user_info", false),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true);

      private static final Map<String, ClickEvent.Action> nameMapping = Maps.newHashMap();
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

      public static ClickEvent.Action getValueByCanonicalName(String var0) {
         return nameMapping.get(var0);
      }

      static {
         for (ClickEvent.Action var20 : values()) {
            nameMapping.put(var20.getCanonicalName(), var20);
         }
      }
   }
}
