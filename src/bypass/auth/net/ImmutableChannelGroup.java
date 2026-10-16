package bypass.auth.net;

import java.util.Map;

public final class ImmutableChannelGroup implements ChannelGroup {
   private Map<String, Channel> channels;

   public ImmutableChannelGroup(ChannelGroupBuilder channelGroupBuilder) {
      this.channels = ChannelGroupBuilder.getChannels(channelGroupBuilder);
   }

   public static ChannelGroupBuilder newBuilder() {
      return new ChannelGroupBuilder();
   }

   @Override
   public void addChannel(Channel channel) {
      throw new IllegalStateException();
   }

   @Override
   public void removeChannel(Channel channel) {
      throw new IllegalStateException();
   }

   @Override
   public void clearChannels() {
      throw new IllegalStateException();
   }

   @Override
   public Channel get(String string) {
      return this.channels.get(string);
   }
}
