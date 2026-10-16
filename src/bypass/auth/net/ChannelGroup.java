package bypass.auth.net;

public interface ChannelGroup {
   void addChannel(Channel channel);

   void removeChannel(Channel channel);

   void clearChannels();

   Channel get(String string);
}
