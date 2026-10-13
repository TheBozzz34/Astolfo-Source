package bypass.auth.net;

import java.util.Map;

public final class ImmutableChannelGroup implements ChannelGroup {
   private Map<String, Channel> x;

   public ImmutableChannelGroup(ChannelGroupBuilder var1) {
      this.x = ChannelGroupBuilder.L(var1);
   }

   public static ChannelGroupBuilder g() {
      return new ChannelGroupBuilder();
   }

   @Override
   public void i(Channel var1) {
      throw new IllegalStateException();
   }

   @Override
   public void q(Channel var1) {
      throw new IllegalStateException();
   }

   @Override
   public void c() {
      throw new IllegalStateException();
   }

   @Override
   public Channel get(String var1) {
      return this.x.get(var1);
   }
}
