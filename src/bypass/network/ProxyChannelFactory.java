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

   public ProxyChannelFactory(Proxy proxy) {
      this.l = proxy;
   }

   public OioSocketChannel newChannel() {
      if (this.l.address() != null && ((InetSocketAddress)this.l.address()).getPort() == (int)e) {
         Socket socket = new Socket(this.l);
         return new OioSocketChannel(socket);
      }

      Socket socket2 = new Socket(this.l);
      Class class2 = socket2.getClass();

      try {
         Field field = class2.getDeclaredField("impl");
         field.setAccessible(true);
         SocketImpl socketImpl = (SocketImpl)field.get(socket2);
         if (socketImpl.getClass().getSimpleName().equalsIgnoreCase("PlainSocketImpl")) {
            return new OioSocketChannel(socket2);
         }

         Class class3 = socketImpl.getClass();
         Method method = class3.getDeclaredMethod("setV4");
         method.setAccessible(true);
         method.invoke(socketImpl);
         field.set(socket2, socketImpl);
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return new OioSocketChannel(socket2);
   }
}
