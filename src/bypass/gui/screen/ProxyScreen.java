package bypass.gui.screen;

import bypass.Astolfo;
import bypass.render.ImageTexture;
import bypass.render.RenderUtil;
import bypass.util.GatherProxySource;
import bypass.util.HostAndPort;
import bypass.util.ProxySource;
import bypass.util.ProxyType;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ProxyScreen extends GuiScreen {
   private static Map<Integer, HostAndPort> u = new TreeMap<>(Comparator.comparingInt(integer -> integer));
   private static Set<HostAndPort> B = new HashSet<>();
   private ProxySource G = new GatherProxySource();
   public static final Map<String, ImageTexture> K = new HashMap<>();
   private static HostAndPort p;
   private GuiButton R;
   private int b;
   public static final ExecutorService x = Executors.newFixedThreadPool(35);
   private boolean w;
   private static boolean O;
   private static final long a = 48303834838772L;

   @Override
   public void initGui() {
      if (B.isEmpty()) {
         this.N();
      }

      byte b = 125;
      byte b2 = 20;
      byte b3 = 1;
      int i = this.width / 2 - b / 2;
      int j = this.height - b2 - 4;
      this.buttonList.clear();
      this.buttonList.add(this.R = new GuiButton(0, i - b / 2 - b3 / 2, j - b2 / 2, b, b2, "Refresh proxies"));
      this.buttonList.add(new GuiButton(1, i + b / 2 + b3 / 2 + 3, j - b2 / 2, b, b2, "Select proxy"));
      this.buttonList.add(new GuiButton(2, this.width - b, 20, b, b2, "Clear proxy"));
      this.buttonList.add(new GuiButton(3, i, j - b2 / 2 - 22, b, b2, "Custom proxy"));
      this.buttonList.add(new GuiButton(4, i + b + 6 + b / 2, j - 10, b / 2, b2, "->"));
      this.buttonList.add(new GuiButton(5, i - b - b3 - b3 / 2, j - 10, b / 2, b2, "<-"));
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      long l = a ^ 45508637356001L;
      this.drawDefaultBackground();
      int i = this.Z();
      if (this.b > i) {
         this.b = 0;
      }

      this.mc.fontRendererObj.drawStringWithShadow("Proxy Manager", this.width / 2 - this.mc.fontRendererObj.getStringWidth("Proxy Manager") / 2, 1.0F, -1);
      String string = String.format("%s", O ? "§aProxies checked" : "§7Checking proxies... (" + B.size() + ")");
      this.mc.fontRendererObj.drawStringWithShadow(string, this.width / 2 - this.mc.fontRendererObj.getStringWidth(string) / 2, 9.0F, -1);
      String string2 = String.format(
         "Current proxy: %s",
         Astolfo.INSTANCE.getProxy() == Proxy.NO_PROXY
            ? "No proxy"
            : ((InetSocketAddress)Astolfo.INSTANCE.getProxy().address()).getAddress()
               + ":"
               + ((InetSocketAddress)Astolfo.INSTANCE.getProxy().address()).getPort()
      );
      this.mc.fontRendererObj.drawStringWithShadow(string2, this.width - this.mc.fontRendererObj.getStringWidth(string2), 5.0F, -1);
      long m = 7980724088846442325L ^ l;
      this.mc
         .fontRendererObj
         .drawStringWithShadow(
            String.format("Page (%s/%s)", this.b, i),
            this.width - this.mc.fontRendererObj.getStringWidth(String.format("Page (%s/%s)", this.b, this.Z())) - 3,
            45.0F,
            -1
         );
      GL11.glPushMatrix();
      GL11.glScalef(0.7F, 0.7F, 0.7F);
      GlStateManager.enableBlend();
      int k = 0;
      int n = 0;
      byte b2 = 10;
      int i2 = this.height / b2;
      int i3 = this.width / 2 + 87;
      if (!O) {
         this.checkProxies();
      }

      if (O) {
         try {
            for (Entry entry : u.entrySet()) {
               if (k < i2 * this.b) {
                  n = ++k;
               } else {
                  if (k >= i2 * (this.b + 1)) {
                     break;
                  }

                  int i4 = (k - n) * b2 + 30;
                  RenderUtil.drawRect(i3, i4, i3 + 175, i4 + 10, new Color(1, 1, 1, 50).getRGB());
                  if (p == entry.getValue()) {
                     RenderUtil.drawRect(i3, i4, i3 + 175, i4 + 10, new Color(37, 37, 37, 255).getRGB());
                  }

                  ImageTexture imageTexture = L("http://api.hostip.info/flag.php?ip=" + ((HostAndPort)entry.getValue()).getHost());
                  ResourceLocation resourceLocation = imageTexture != null && imageTexture.j != null ? imageTexture.j : null;
                  if (resourceLocation != null) {
                     Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                     Gui.drawModalRectWithCustomSizedTexture(i3 - 16, i4 + 1, 0.0F, 0.0F, 16, 8, 16.0F, 8.0F);
                  }

                  this.I(i3 + 175, 0, i4, (Integer)entry.getKey());
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("§f%sms", entry.getKey()),
                        i3 + 165 - this.mc.fontRendererObj.getStringWidth(String.format("§f%sms", entry.getKey())) - 3,
                        i4 + 1,
                        -1
                     );
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("%s%s:%s", ((HostAndPort)entry.getValue()).getHost(), EnumChatFormatting.GRAY, ((HostAndPort)entry.getValue()).getPort()),
                        i3 + 3,
                        i4 + 1,
                        -1
                     );
                  k++;
               }
            }
         } catch (ConcurrentModificationException concurrentModificationException) {
            System.out.println("fuck you whore, we like fortnite");
         }
      }

      GL11.glPopMatrix();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static ImageTexture L(String string) {
      if (K.containsKey(string)) {
         ImageTexture imageTexture = K.get(string);
         if (imageTexture.H != null && imageTexture.j == null) {
            imageTexture.j = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(string, new DynamicTexture(imageTexture.H));
            return null;
         } else {
            return imageTexture;
         }
      } else {
         ImageTexture imageTexture2 = new ImageTexture();
         K.put(string, imageTexture2);
         download(imageTexture2, string);
         return imageTexture2;
      }
   }

   public static void download(ImageTexture imageTexture, String string) {
      new Thread(
            () -> {
               try {
                  HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(string).openConnection();
                  httpURLConnection.setRequestProperty(
                     "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"
                  );
                  if (httpURLConnection.getResponseCode() == 200) {
                     InputStream inputStream = httpURLConnection.getInputStream();
                     imageTexture.H = ImageIO.read(inputStream);
                  }
               } catch (IOException ioException) {
                  System.err.println("An error occurred. URL: " + string);
                  ioException.printStackTrace();
               }
            }
         )
         .start();
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      int i = 0;
      int j = 0;
      byte b2 = 7;
      int k = this.height / b2;
      int l = 0;
      int m = this.width / 2 - 87;
      if (O) {
         try {
            for (HostAndPort hostAndPort : u.values()) {
               if (i < k * this.b) {
                  i++;
                  j++;
                  l = i;
               } else {
                  if (i >= k * (this.b + 1)) {
                     break;
                  }

                  if (i >= 70) {
                     i = 0;
                     l = 0;
                  }

                  int n = (i - l) * b2 + 20;
                  if (mouseX >= m && mouseY >= n && mouseX <= m + 175 && mouseY <= n + 10) {
                     p = hostAndPort;
                  }

                  i++;
               }
            }
         } catch (ConcurrentModificationException concurrentModificationException) {
            System.out.println("WE LIKE FORTNITE");
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.N();
            O = false;
            B.clear();
            u.clear();
            break;
         case 1:
            if (p != null) {
               Astolfo.INSTANCE.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(p.getHost(), p.getPort())));
            }
            break;
         case 2:
            Astolfo.INSTANCE.setProxy(Proxy.NO_PROXY);
            break;
         case 3:
            Minecraft.getMinecraft().displayGuiScreen(new ProxyLoginScreen());
            break;
         case 4:
            int i = this.Z();
            this.b += 2;
            if (this.b >= i - 1) {
               this.b = i - 1;
            }

            p = null;
         case 5:
            this.b--;
            if (this.b < 0) {
               this.b = 0;
            }

            p = null;
      }

      super.actionPerformed(button);
   }

   private void N() {
      EnumSet enumSet = EnumSet.noneOf(ProxyType.class);
      enumSet.add(ProxyType.SOCKS4);
      enumSet.add(ProxyType.SOCKS5);
      B = this.G.scrap(enumSet);
   }

   private void checkProxies() {
      if (!B.isEmpty()) {
         x.submit(() -> {
            for (HostAndPort hostAndPort : B) {
               try {
                  if (!u.containsValue(hostAndPort) && !this.k(hostAndPort)) {
                     if (O) {
                        break;
                     }

                     InetAddress inetAddress = InetAddress.getByName(hostAndPort.getHost());
                     long l = System.currentTimeMillis();
                     if (inetAddress.isReachable(500)) {
                        int i = (int)(System.currentTimeMillis() - l);
                        if (!this.k(hostAndPort)) {
                           u.put(i, hostAndPort);
                        }

                        B.remove(hostAndPort);
                     } else {
                        B.remove(hostAndPort);
                     }
                  } else {
                     B.remove(hostAndPort);
                  }
               } catch (IOException ioException) {
                  B.remove(hostAndPort);
               }
            }

            O = true;
         });
      }
   }

   private boolean k(HostAndPort hostAndPort) {
      for (HostAndPort hostAndPort2 : u.values()) {
         if (hostAndPort2.equals(hostAndPort)) {
            return true;
         }
      }

      return false;
   }

   public void I(int i, int j, int k, int l) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte b = 0;
      byte b2 = 0;
      if (l < 0) {
         b2 = 5;
      } else if (l < 150) {
         b2 = 0;
      } else if (l < 300) {
         b2 = 1;
      } else if (l < 600) {
         b2 = 2;
      } else if (l < 1000) {
         b2 = 3;
      } else {
         b2 = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(j + i - 11, k, 0 + b * 10, 176 + b2 * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   public static String o(InputStream inputStream) {
      Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
   }

   private int Z() {
      int i = u.size();
      return MathHelper.ceiling_float_int((float)i / (this.height / 10));
   }

   static {
      long l = a ^ 57287977161539L;
      long m = 3796444336627603900L ^ l;
   }
}
