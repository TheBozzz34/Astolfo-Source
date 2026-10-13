package bypass.auth;

import bypass.util.RandomUtils;
import java.util.ArrayList;
import java.util.Arrays;

public class AuthEndpoint {
   public static ArrayList<Integer> PORTS = new ArrayList<>(Arrays.asList(29367));
   public static boolean x;
   private final String host;
   private PortChoice port;
   private AuthConnection connection;
   private static final long a = 91597366608243L;

   public AuthEndpoint(String var1) {
      this.host = var1;
      this.choosePort();
   }

   public AuthConnection B() {
      if (this.connection == null) {
         this.connection = new AuthConnection();
      }

      return this.connection;
   }

   public void E(AuthConnection var1) {
      this.choosePort();
      this.connection = var1;
   }

   public String getHost() {
      return this.host;
   }

   public PortChoice getPort() {
      return this.port;
   }

   private void choosePort() {
      int var1 = RandomUtils.SECURE_RANDOM.nextInt(PORTS.size());
      this.port = new PortChoice(var1, PORTS.get(var1));
   }

   public static void resetPorts() {
      PORTS = new ArrayList<>(Arrays.asList(29367));
   }
}
