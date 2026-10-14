package bypass.auth.net;

import bypass.util.collection.ShapedMap;

public class ChannelGroupBuilder {
   private final ShapedMap<String, Channel> h = new ShapedMap<>();

   public ChannelGroupBuilder H(String string, Channel channel) {
      this.h.put(string, channel);
      return this;
   }

   public ImmutableChannelGroup K() {
      return new ImmutableChannelGroup(this);
   }

   public static ShapedMap L(ChannelGroupBuilder channelGroupBuilder) {
      return channelGroupBuilder.h;
   }
}
