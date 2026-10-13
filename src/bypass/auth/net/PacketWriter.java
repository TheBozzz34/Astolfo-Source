package bypass.auth.net;

public interface PacketWriter<T> {
   default void write(T var1, PacketBuffer var2) {
   }
}
