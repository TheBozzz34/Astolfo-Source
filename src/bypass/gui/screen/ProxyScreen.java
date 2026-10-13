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
   private static Map<Integer, HostAndPort> u = new TreeMap<>(Comparator.comparingInt(var0 -> var0));
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

      byte var5 = 125;
      byte var6 = 20;
      byte var7 = 1;
      int var8 = this.width / 2 - var5 / 2;
      int var9 = this.height - var6 - 4;
      this.buttonList.clear();
      this.buttonList.add(this.R = new GuiButton(0, var8 - var5 / 2 - var7 / 2, var9 - var6 / 2, var5, var6, "Refresh proxies"));
      this.buttonList.add(new GuiButton(1, var8 + var5 / 2 + var7 / 2 + 3, var9 - var6 / 2, var5, var6, "Select proxy"));
      this.buttonList.add(new GuiButton(2, this.width - var5, 20, var5, var6, "Clear proxy"));
      this.buttonList.add(new GuiButton(3, var8, var9 - var6 / 2 - 22, var5, var6, "Custom proxy"));
      this.buttonList.add(new GuiButton(4, var8 + var5 + 6 + var5 / 2, var9 - 10, var5 / 2, var6, "->"));
      this.buttonList.add(new GuiButton(5, var8 - var5 - var7 - var7 / 2, var9 - 10, var5 / 2, var6, "<-"));
      super.initGui();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = a ^ 45508637356001L;
      this.drawDefaultBackground();
      int var7 = this.Z();
      if (this.b > var7) {
         this.b = 0;
      }

      this.mc.fontRendererObj.drawStringWithShadow("Proxy Manager", this.width / 2 - this.mc.fontRendererObj.getStringWidth("Proxy Manager") / 2, 1.0F, -1);
      String var8 = String.format("%s", O ? "§aProxies checked" : "§7Checking proxies... (" + B.size() + ")");
      this.mc.fontRendererObj.drawStringWithShadow(var8, this.width / 2 - this.mc.fontRendererObj.getStringWidth(var8) / 2, 9.0F, -1);
      String var9 = String.format(
         "Current proxy: %s",
         Astolfo.INSTANCE.getProxy() == Proxy.NO_PROXY
            ? "No proxy"
            : ((InetSocketAddress)Astolfo.INSTANCE.getProxy().address()).getAddress()
               + ":"
               + ((InetSocketAddress)Astolfo.INSTANCE.getProxy().address()).getPort()
      );
      this.mc.fontRendererObj.drawStringWithShadow(var9, this.width - this.mc.fontRendererObj.getStringWidth(var9), 5.0F, -1);
      long var10000 = 7980724088846442325L ^ var4;
      this.mc
         .fontRendererObj
         .drawStringWithShadow(
            String.format("Page (%s/%s)", this.b, var7),
            this.width - this.mc.fontRendererObj.getStringWidth(String.format("Page (%s/%s)", this.b, this.Z())) - 3,
            45.0F,
            -1
         );
      GL11.glPushMatrix();
      GL11.glScalef(0.7F, 0.7F, 0.7F);
      GlStateManager.enableBlend();
      int var10 = 0;
      int var11 = 0;
      byte var12 = 10;
      int var13 = this.height / var12;
      int var14 = this.width / 2 + 87;
      if (!O) {
         this.checkProxies();
      }

      if (O) {
         try {
            for (Entry var16 : u.entrySet()) {
               if (var10 < var13 * this.b) {
                  var11 = ++var10;
               } else {
                  if (var10 >= var13 * (this.b + 1)) {
                     break;
                  }

                  int var17 = (var10 - var11) * var12 + 30;
                  RenderUtil.drawRect(var14, var17, var14 + 175, var17 + 10, new Color(1, 1, 1, 50).getRGB());
                  if (p == var16.getValue()) {
                     RenderUtil.drawRect(var14, var17, var14 + 175, var17 + 10, new Color(37, 37, 37, 255).getRGB());
                  }

                  ImageTexture var18 = L("http://api.hostip.info/flag.php?ip=" + ((HostAndPort)var16.getValue()).getHost());
                  ResourceLocation var19 = var18 != null && var18.j != null ? var18.j : null;
                  if (var19 != null) {
                     Minecraft.getMinecraft().getTextureManager().bindTexture(var19);
                     Gui.drawModalRectWithCustomSizedTexture(var14 - 16, var17 + 1, 0.0F, 0.0F, 16, 8, 16.0F, 8.0F);
                  }

                  this.I(var14 + 175, 0, var17, (Integer)var16.getKey());
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("§f%sms", var16.getKey()),
                        var14 + 165 - this.mc.fontRendererObj.getStringWidth(String.format("§f%sms", var16.getKey())) - 3,
                        var17 + 1,
                        -1
                     );
                  this.mc
                     .fontRendererObj
                     .drawStringWithShadow(
                        String.format("%s%s:%s", ((HostAndPort)var16.getValue()).getHost(), EnumChatFormatting.GRAY, ((HostAndPort)var16.getValue()).getPort()),
                        var14 + 3,
                        var17 + 1,
                        -1
                     );
                  var10++;
               }
            }
         } catch (ConcurrentModificationException var20) {
            System.out.println("fuck you whore, we like fortnite");
         }
      }

      GL11.glPopMatrix();
      super.drawScreen(var1, var2, var3);
   }

   public static ImageTexture L(String var0) {
      if (K.containsKey(var0)) {
         ImageTexture var5 = K.get(var0);
         if (var5.H != null && var5.j == null) {
            var5.j = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(var0, new DynamicTexture(var5.H));
            return null;
         } else {
            return var5;
         }
      } else {
         ImageTexture var4 = new ImageTexture();
         K.put(var0, var4);
         download(var4, var0);
         return var4;
      }
   }

   public static void download(ImageTexture var0, String var1) {
      new Thread(
            () -> {
               try {
                  HttpURLConnection var5 = (HttpURLConnection)new URL(var1).openConnection();
                  var5.setRequestProperty(
                     "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"
                  );
                  if (var5.getResponseCode() == 200) {
                     InputStream var6 = var5.getInputStream();
                     var0.H = ImageIO.read(var6);
                  }
               } catch (IOException var7) {
                  System.err.println("An error occurred. URL: " + var1);
                  var7.printStackTrace();
               }
            }
         )
         .start();
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      int var7 = 0;
      int var8 = 0;
      byte var9 = 7;
      int var10 = this.height / var9;
      int var11 = 0;
      int var12 = this.width / 2 - 87;
      if (O) {
         try {
            for (HostAndPort var14 : u.values()) {
               if (var7 < var10 * this.b) {
                  var7++;
                  var8++;
                  var11 = var7;
               } else {
                  if (var7 >= var10 * (this.b + 1)) {
                     break;
                  }

                  if (var7 >= 70) {
                     var7 = 0;
                     var11 = 0;
                  }

                  int var15 = (var7 - var11) * var9 + 20;
                  if (var1 >= var12 && var2 >= var15 && var1 <= var12 + 175 && var2 <= var15 + 10) {
                     p = var14;
                  }

                  var7++;
               }
            }
         } catch (ConcurrentModificationException var16) {
            System.out.println("WE LIKE FORTNITE");
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      switch (var1.id) {
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
            int var5 = this.Z();
            this.b += 2;
            if (this.b >= var5 - 1) {
               this.b = var5 - 1;
            }

            p = null;
         case 5:
            this.b--;
            if (this.b < 0) {
               this.b = 0;
            }

            p = null;
      }

      super.actionPerformed(var1);
   }

   private void N() {
      EnumSet var1 = EnumSet.noneOf(ProxyType.class);
      var1.add(ProxyType.SOCKS4);
      var1.add(ProxyType.SOCKS5);
      B = this.G.scrap(var1);
   }

   private void checkProxies() {
      if (!B.isEmpty()) {
         x.submit(() -> {
            for (HostAndPort var5 : B) {
               try {
                  if (!u.containsValue(var5) && !this.k(var5)) {
                     if (O) {
                        break;
                     }

                     InetAddress var6 = InetAddress.getByName(var5.getHost());
                     long var7 = System.currentTimeMillis();
                     if (var6.isReachable(500)) {
                        int var9 = (int)(System.currentTimeMillis() - var7);
                        if (!this.k(var5)) {
                           u.put(var9, var5);
                        }

                        B.remove(var5);
                     } else {
                        B.remove(var5);
                     }
                  } else {
                     B.remove(var5);
                  }
               } catch (IOException var10) {
                  B.remove(var5);
               }
            }

            O = true;
         });
      }
   }

   private boolean k(HostAndPort var1) {
      for (HostAndPort var6 : u.values()) {
         if (var6.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public void I(int var1, int var2, int var3, int var4) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte var8 = 0;
      byte var9 = 0;
      if (var4 < 0) {
         var9 = 5;
      } else if (var4 < 150) {
         var9 = 0;
      } else if (var4 < 300) {
         var9 = 1;
      } else if (var4 < 600) {
         var9 = 2;
      } else if (var4 < 1000) {
         var9 = 3;
      } else {
         var9 = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(var2 + var1 - 11, var3, 0 + var8 * 10, 176 + var9 * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   public static String o(InputStream var0) {
      Scanner var4 = new Scanner(var0).useDelimiter("\\A");
      return var4.hasNext() ? var4.next() : "";
   }

   private int Z() {
      int var3 = u.size();
      return MathHelper.ceiling_float_int((float)var3 / (this.height / 10));
   }

   static {
      long var20 = a ^ 57287977161539L;
      long var10000 = 3796444336627603900L ^ var20;
   }
}
