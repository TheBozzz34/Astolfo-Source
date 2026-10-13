package bypass;

import net.minecraft.client.Minecraft;

public interface ClientAccess {
   Minecraft mc = Minecraft.getMinecraft();
   Astolfo client = Astolfo.INSTANCE;
   ClientChat chat = ClientChat.CHAT;
   Toasts toasts = Toasts.TOASTS;
}
