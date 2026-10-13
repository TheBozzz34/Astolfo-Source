package bypass.hypixel;

import com.google.gson.annotations.SerializedName;
import net.hypixel.api.reply.AbstractReply;

public class KillsDeathsReply extends AbstractReply {
   @SerializedName("kills")
   private long p;
   @SerializedName("deaths")
   private long P;
}
