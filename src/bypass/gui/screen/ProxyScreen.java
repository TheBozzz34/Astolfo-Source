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
   private static Map<Integer, HostAndPort> reachableProxies = new TreeMap<>(Comparator.comparingInt(integer -> integer));
   private static Set<HostAndPort> uncheckedProxies = new HashSet<>();
   private ProxySource proxySource = new GatherProxySource();
   public static final Map<String, ImageTexture> flagTextureCache = new HashMap<>();
   private static HostAndPort selectedProxy;
   private GuiButton refreshButton;
   private int currentPage;
   public static final ExecutorService proxyCheckExecutor = Executors.newFixedThreadPool(35);
   private boolean unusedFlag;
   private static boolean proxiesChecked;

   @Override
   public void initGui() {
      if (uncheckedProxies.isEmpty()) {
         this.scrapeProxies();
      }

      byte b = 125;
      byte b2 = 20;
      byte b3 = 1;
      int i = this.width / 2 - b / 2;
      int j = this.height - b2 - 4;
      this.buttonList.clear();
      this.buttonList.add(this.refreshButton = new GuiButton(0, i - b / 2 - b3 / 2, j - b2 / 2, b, b2, "Refresh proxies"));
      this.buttonList.add(new GuiButton(1, i + b / 2 + b3 / 2 + 3, j - b2 / 2, b, b2, "Select proxy"));
      this.buttonList.add(new GuiButton(2, this.width - b, 20, b, b2, "Clear proxy"));
      this.buttonList.add(new GuiButton(3, i, j - b2 / 2 - 22, b, b2, "Custom proxy"));
      this.buttonList.add(new GuiButton(4, i + b + 6 + b / 2, j - 10, b / 2, b2, "->"));
      this.buttonList.add(new GuiButton(5, i - b - b3 - b3 / 2, j - 10, b / 2, b2, "<-"));
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int i = this.getPageCount();
      if (this.currentPage > i) {
         this.currentPage = 0;
      }

      this.mc.fontRendererObj.drawStringWithShadow("Proxy Manager", this.width / 2 - this.mc.fontRendererObj.getStringWidth("Proxy Manager") / 2, 1.0F, -1);
      String string = String.format("%s", proxiesChecked ? "§aProxies checked" : "§7Checking proxies... (" + uncheckedProxies.size() + ")");
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
      this.mc
         .fontRendererObj
         .drawStringWithShadow(
            String.format("Page (%s/%s)", this.currentPage, i),
            this.width - this.mc.fontRendererObj.getStringWidth(String.format("Page (%s/%s)", this.currentPage, this.getPageCount())) - 3,
            45.0F,
            -1
         );
      GL11.glPushMatrix();
      GL11.glScalef(0.7F, 0.7F, 0.7F);
      GlStateManager.enableBlend();
      int j = 0;
      int k = 0;
      byte b = 10;
      int l = this.height / b;
      int m = this.width / 2 + 87;
      if (!proxiesChecked) {
         this.checkProxies();
      }

      if (proxiesChecked) {
         try {
            for (Entry entry : reachableProxies.entrySet()) {
               if (j < l * this.currentPage) {
                  k = ++j;
               } else {
                  if (j >= l * (this.currentPage + 1)) {
                     break;
                  }

                  int n = (j - k) * b + 30;
                  RenderUtil.drawRect(m, n, m + 175, n + 10, new Color(1, 1, 1, 50).getRGB());
                  if (selectedProxy == entry.getValue()) {
                     RenderUtil.drawRect(m, n, m + 175, n + 10, new Color(37, 37, 37, 255).getRGB());
                  }

                  ImageTexture imageTexture = getFlagTexture("http://api.hostip.info/flag.php?ip=" + ((HostAndPort)entry.getValue()).getHost());
                  ResourceLocation resourceLocation = imageTexture != null && imageTexture.location != null ? imageTexture.location : null;
                  if (resourceLocation != null) {
                     Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                     Gui.drawModalRectWithCustomSizedTexture(m - 16, n + 1, 0.0F, 0.0F, 16, 8, 16.0F, 8.0F);
                  }

                  this.drawPingIcon(m + 175, 0, n, (Integer)entry.getKey());
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("§f%sms", entry.getKey()),
                        m + 165 - this.mc.fontRendererObj.getStringWidth(String.format("§f%sms", entry.getKey())) - 3,
                        n + 1,
                        -1
                     );
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("%s%s:%s", ((HostAndPort)entry.getValue()).getHost(), EnumChatFormatting.GRAY, ((HostAndPort)entry.getValue()).getPort()),
                        m + 3,
                        n + 1,
                        -1
                     );
                  j++;
               }
            }
         } catch (ConcurrentModificationException concurrentModificationException) {
            System.out.println("fuck you whore, we like fortnite");
         }
      }

      GL11.glPopMatrix();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static ImageTexture getFlagTexture(String string) {
      if (flagTextureCache.containsKey(string)) {
         ImageTexture imageTexture = flagTextureCache.get(string);
         if (imageTexture.image != null && imageTexture.location == null) {
            imageTexture.location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(string, new DynamicTexture(imageTexture.image));
            return null;
         } else {
            return imageTexture;
         }
      } else {
         ImageTexture imageTexture2 = new ImageTexture();
         flagTextureCache.put(string, imageTexture2);
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
                     imageTexture.image = ImageIO.read(inputStream);
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
      byte b = 7;
      int k = this.height / b;
      int l = 0;
      int m = this.width / 2 - 87;
      if (proxiesChecked) {
         try {
            for (HostAndPort hostAndPort : reachableProxies.values()) {
               if (i < k * this.currentPage) {
                  i++;
                  j++;
                  l = i;
               } else {
                  if (i >= k * (this.currentPage + 1)) {
                     break;
                  }

                  if (i >= 70) {
                     i = 0;
                     l = 0;
                  }

                  int n = (i - l) * b + 20;
                  if (mouseX >= m && mouseY >= n && mouseX <= m + 175 && mouseY <= n + 10) {
                     selectedProxy = hostAndPort;
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
            this.scrapeProxies();
            proxiesChecked = false;
            uncheckedProxies.clear();
            reachableProxies.clear();
            break;
         case 1:
            if (selectedProxy != null) {
               Astolfo.INSTANCE.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(selectedProxy.getHost(), selectedProxy.getPort())));
            }
            break;
         case 2:
            Astolfo.INSTANCE.setProxy(Proxy.NO_PROXY);
            break;
         case 3:
            Minecraft.getMinecraft().displayGuiScreen(new ProxyLoginScreen());
            break;
         case 4:
            int i = this.getPageCount();
            this.currentPage += 2;
            if (this.currentPage >= i - 1) {
               this.currentPage = i - 1;
            }

            selectedProxy = null;
         case 5:
            this.currentPage--;
            if (this.currentPage < 0) {
               this.currentPage = 0;
            }

            selectedProxy = null;
      }

      super.actionPerformed(button);
   }

   private void scrapeProxies() {
      EnumSet enumSet = EnumSet.noneOf(ProxyType.class);
      enumSet.add(ProxyType.SOCKS4);
      enumSet.add(ProxyType.SOCKS5);
      uncheckedProxies = this.proxySource.scrap(enumSet);
   }

   private void checkProxies() {
      if (!uncheckedProxies.isEmpty()) {
         proxyCheckExecutor.submit(() -> {
            for (HostAndPort hostAndPort : uncheckedProxies) {
               try {
                  if (!reachableProxies.containsValue(hostAndPort) && !this.isAlreadyReachable(hostAndPort)) {
                     if (proxiesChecked) {
                        break;
                     }

                     InetAddress inetAddress = InetAddress.getByName(hostAndPort.getHost());
                     long l = System.currentTimeMillis();
                     if (inetAddress.isReachable(500)) {
                        int i = (int)(System.currentTimeMillis() - l);
                        if (!this.isAlreadyReachable(hostAndPort)) {
                           reachableProxies.put(i, hostAndPort);
                        }

                        uncheckedProxies.remove(hostAndPort);
                     } else {
                        uncheckedProxies.remove(hostAndPort);
                     }
                  } else {
                     uncheckedProxies.remove(hostAndPort);
                  }
               } catch (IOException ioException) {
                  uncheckedProxies.remove(hostAndPort);
               }
            }

            proxiesChecked = true;
         });
      }
   }

   private boolean isAlreadyReachable(HostAndPort hostAndPort) {
      for (HostAndPort hostAndPort2 : reachableProxies.values()) {
         if (hostAndPort2.equals(hostAndPort)) {
            return true;
         }
      }

      return false;
   }

   public void drawPingIcon(int i, int j, int k, int l) {
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

   public static String readStreamToString(InputStream inputStream) {
      Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
   }

   private int getPageCount() {
      int i = reachableProxies.size();
      return MathHelper.ceiling_float_int((float)i / (this.height / 10));
   }
}
