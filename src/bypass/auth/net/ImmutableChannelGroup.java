package bypass.auth.net;

import java.util.Map;

public final class ImmutableChannelGroup implements ChannelGroup {
   private Map<String, Channel> x;

   public ImmutableChannelGroup(ChannelGroupBuilder channelGroupBuilder) {
      this.x = ChannelGroupBuilder.L(channelGroupBuilder);
   }

   public static ChannelGroupBuilder g() {
      return new ChannelGroupBuilder();
   }

   @Override
   public void i(Channel channel) {
      throw new IllegalStateException();
   }

   @Override
   public void q(Channel channel) {
      throw new IllegalStateException();
   }

   @Override
   public void c() {
      throw new IllegalStateException();
   }

   @Override
   public Channel get(String string) {
      return this.x.get(string);
   }
}
