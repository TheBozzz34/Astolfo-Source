package bypass.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketImpl;

public class ProxyChannelFactory implements ChannelFactory<OioSocketChannel> {
   private Proxy l;
   private static final long a = 111967031101865L;
   private static final long e = 4231176096667672464L;

   public ProxyChannelFactory(Proxy var1) {
      this.l = var1;
   }

   public OioSocketChannel newChannel() {
      if (this.l.address() != null && ((InetSocketAddress)this.l.address()).getPort() == (int)e) {
         Socket var12 = new Socket(this.l);
         return new OioSocketChannel(var12);
      }

      Socket var5 = new Socket(this.l);
      Class var6 = var5.getClass();

      try {
         Field var8 = var6.getDeclaredField("impl");
         var8.setAccessible(true);
         SocketImpl var9 = (SocketImpl)var8.get(var5);
         if (var9.getClass().getSimpleName().equalsIgnoreCase("PlainSocketImpl")) {
            return new OioSocketChannel(var5);
         }

         Class var10 = var9.getClass();
         Method var7 = var10.getDeclaredMethod("setV4");
         var7.setAccessible(true);
         var7.invoke(var9);
         var8.set(var5, var9);
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      return new OioSocketChannel(var5);
   }
}
