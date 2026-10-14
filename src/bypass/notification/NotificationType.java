package bypass.notification;

import bypass.render.ClientColor;
import java.awt.Color;
import net.minecraft.util.ResourceLocation;

public enum NotificationType {
   SUCCESS(ClientColor.GREEN.getColor(), -13710223, new ResourceLocation("contexts/SUCCESS.png")),
   INFO(ClientColor.BLUE.getColor(), -5592406, new ResourceLocation("contexts/INFO.png")),
   WARNING(ClientColor.ORANGE.getColor(), -932849, new ResourceLocation("contexts/WARNING.png")),
   DANGER(ClientColor.RED.getColor(), -4179669, new ResourceLocation("contexts/DANGER.png"));

   public final int color;
   public final ResourceLocation texture;
   public final Color consoleColor;
   private static final NotificationType[] f = new NotificationType[]{SUCCESS, INFO, WARNING, DANGER};

   NotificationType(Color color, int j, ResourceLocation resourceLocation) {
      this.consoleColor = color;
      this.color = j;
      this.texture = resourceLocation;
   }
}
