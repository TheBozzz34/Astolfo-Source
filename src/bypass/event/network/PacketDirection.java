package bypass.event.network;

public enum PacketDirection {
   RECEIVED,
   OUTGOING;

   private static final PacketDirection[] C = new PacketDirection[]{RECEIVED, OUTGOING};
}
