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

   public AuthEndpoint(String string) {
      this.host = string;
      this.choosePort();
   }

   public AuthConnection B() {
      if (this.connection == null) {
         this.connection = new AuthConnection();
      }

      return this.connection;
   }

   public void E(AuthConnection authConnection) {
      this.choosePort();
      this.connection = authConnection;
   }

   public String getHost() {
      return this.host;
   }

   public PortChoice getPort() {
      return this.port;
   }

   private void choosePort() {
      int i = RandomUtils.SECURE_RANDOM.nextInt(PORTS.size());
      this.port = new PortChoice(i, PORTS.get(i));
   }

   public static void resetPorts() {
      PORTS = new ArrayList<>(Arrays.asList(29367));
   }
}
