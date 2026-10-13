package bypass.lua;

import bypass.ClientAccess;
import bypass.ClientChat;
import bypass.Toasts;
import bypass.module.combat.KillAura;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class LuaApi implements ClientAccess {
   public static final Map<Integer, Entity> Z = new HashMap<>();
   public static final Map<String, Setting<?>> f = new HashMap<>();
   private static final long b = 79788896861913L;

   @ScriptName(j = "byh")
   public void w(String var1, float var2, float var3, int var4) {
      if (!var1.contains("bypass.") && !var1.contains("jdk") && !var1.contains("drawRect") && !var1.contains("function(") && !var1.contains("worldCls")) {
         mc.fontRendererObj.drawStringWithShadow(var1, var2, var3, var4);
      }
   }

   @ScriptName(j = "dgytw")
   public int r(String var1) {
      return mc.fontRendererObj.getStringWidth(var1);
   }

   @ScriptName(j = "drw")
   public void y(double var1, double var3, double var5, double var7, int var9, int var10, int var11, int var12) {
      if (var9 <= 255 && var9 >= 0 && var10 >= 0 && var11 >= 0 && var12 >= 0 && var10 <= 255 && var11 <= 255 && var12 <= 255) {
         RenderUtil.j(var1, var3, var5, var7, var9, var10, var11, var12);
      } else {
         chat.I("Incorrect color");
      }
   }

   @ScriptName(j = "rgmr")
   public void a(ScriptObjectMirror var1) {
   }

   @ScriptName(j = "vrnj")
   public void F(ScriptObjectMirror var1) {
   }

   @ScriptName(j = "wyhwy")
   public void y(String var1, String var2, String var3, boolean var4) {
      long var5 = b ^ 15643489397635L;
      long var7 = var5 ^ 125193056102633L;
      BooleanSetting var11 = new BooleanSetting(var2, var3, var4);
      f.put(var2, var11);
      client.getModuleManager().getModule(var1).addSettings(var11);
   }

   @ScriptName(j = "bdwugydyuwh")
   public boolean h(String var1) {
      long var2 = b ^ 41396736821464L;
      long var4 = var2 ^ 99234729216946L;

      try {
         return client.getModuleManager().getModule(var1).isEnabled();
      } catch (Exception var7) {
         return false;
      }
   }

   @ScriptName(j = "ewbdwyb")
   public boolean t(String var1) {
      return f.get(var1).asBoolean();
   }

   @ScriptName(j = "dbuywaehyudwa")
   public void l(double var1, double var3, double var5) {
      if (mc.thePlayer != null) {
         mc.thePlayer.motionX = var1;
         mc.thePlayer.motionY = var3;
         mc.thePlayer.motionZ = var5;
      }
   }

   @ScriptName(j = "dbvuhygwa")
   public double[] s() {
      return mc.thePlayer == null ? new double[0] : new double[]{mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ};
   }

   @ScriptName(j = "wjn")
   public void r(double var1, double var3, double var5) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(var1, var3, var5);
      }
   }

   @ScriptName(j = "evh")
   public void M(String var1) {
      if (mc.thePlayer != null) {
         if (!var1.contains("bypass.") && !var1.contains("jdk") && !var1.contains("drawRect") && !var1.contains("function(") && !var1.contains("worldCls")) {
            mc.thePlayer.sendClientChatMessage(var1);
         }
      }
   }

   @ScriptName(j = "evj")
   public void t(double var1) {
      if (mc.thePlayer != null) {
         mc.thePlayer.r(var1);
      }
   }

   @ScriptName(j = "deg")
   public void l_void() {
      if (mc.thePlayer != null) {
         mc.thePlayer.jump();
      }
   }

   @ScriptName(j = "evg")
   public void M(boolean var1) {
      if (mc.thePlayer != null) {
         mc.thePlayer.onGround = var1;
      }
   }

   @ScriptName(j = "ehh")
   public boolean k() {
      return mc.thePlayer == null ? false : mc.thePlayer.onGround;
   }

   @ScriptName(j = "nbhdwuinwd")
   public void Z(double var1, double var3, double var5) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(var1, var3, var5);
      }
   }

   @ScriptName(j = "bhgdyuwdhuyw")
   public double[] U() {
      return mc.thePlayer == null ? new double[0] : new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
   }

   @ScriptName(j = "ftyegh")
   public boolean R() {
      return mc.thePlayer == null ? false : mc.thePlayer.isOnLadder();
   }

   @ScriptName(j = "eio")
   public void D(float var1) {
      mc.timer.timerSpeed = var1;
   }

   @ScriptName(j = "dfbgeuwy")
   public int L() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.ticksExisted;
   }

   @ScriptName(j = "bduyhw")
   public int l() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.hurtTime;
   }

   @ScriptName(j = "ejf")
   public void d(int var1, Object... var2) {
      if (mc.thePlayer != null) {
         switch (var1) {
            case 0:
               mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
               break;
            case 1:
               int var6 = 0;
               int[] var7 = new int[var2.length];

               for (Object var23 : var2) {
                  if (var23 instanceof Number) {
                     var7[var6] = ((Number)var23).intValue();
                     var6++;
                  }
               }

               if (var2.length == 1) {
                  if (var7[0] > C0BPacketEntityAction.Action.values().length) {
                     return;
                  }

                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.values()[var7[0]]));
               }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               break;
            case 8:
               int var8 = 0;
               int[] var9 = new int[var2.length];

               for (Object var25 : var2) {
                  if (var25 instanceof Number) {
                     var9[var8] = ((Number)var25).intValue();
                     var8++;
                  }
               }

               switch (var2.length) {
                  case 2:
                     if (Z.get(var9[0]) == null) {
                        chat.I("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     if (var9[1] > C02PacketUseEntity.Action.values().length) {
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(Z.get(var9[0]), C02PacketUseEntity.Action.values()[var9[1]]));
                     return;
                  case 4:
                     if (Z.get(var9[0]) == null) {
                        chat.I("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(Z.get(var9[0]), new Vec3(var9[1], var9[2], var9[3])));
                     return;
                  default:
                     return;
               }
            case 9:
               int var10 = 0;
               double[] var11 = new double[var2.length];
               boolean var12 = false;
               boolean var13 = false;

               for (Object var17 : var2) {
                  if (var17 instanceof Number) {
                     var11[var10] = ((Number)var17).doubleValue();
                     var10++;
                  }

                  if (var17 instanceof Boolean) {
                     var13 = true;
                     var12 = (Boolean)var17;
                  }
               }

               switch (var2.length) {
                  case 1:
                     if (var13) {
                        mc.thePlayer.sendQueue.u(new C03PacketPlayer(var12));
                     }
                  case 2:
                  case 5:
                  default:
                     break;
                  case 3:
                     mc.thePlayer.sendQueue.u(new C03PacketPlayer.C05PacketPlayerLook((float)var11[0], (float)var11[1], var12));
                     break;
                  case 4:
                     mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var11[0], var11[1], var11[2], var12));
                     break;
                  case 6:
                     mc.thePlayer
                        .sendQueue
                        .u(new C03PacketPlayer.C06PacketPlayerPosLook(var11[0], var11[1], var11[2], (float)var11[3], (float)var11[4], var12));
               }
         }
      }
   }

   @ScriptName(j = "wbhuy")
   public int Z() {
      return KillAura.target != null ? KillAura.target.getEntityId() : -1;
   }

   @ScriptName(j = "vnruiy")
   public float[] G_floatArray() {
      return mc.thePlayer == null ? new float[0] : new float[]{mc.thePlayer.moveForward, mc.thePlayer.moveStrafing};
   }

   @ScriptName(j = "doiwjdiowdjwio")
   public float[] i() {
      return mc.thePlayer == null ? new float[0] : new float[]{mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw};
   }

   @ScriptName(j = "dbwhydgwydw")
   public void z(float var1, float var2) {
      if (mc.thePlayer != null) {
         mc.thePlayer.rotationPitch = var1;
         mc.thePlayer.rotationYaw = var2;
      }
   }

   @ScriptName(j = "fhuyer")
   public void h(int var1) {
      if (mc.thePlayer != null) {
         mc.thePlayer.inventory.currentItem = var1;
         mc.playerController.u();
      }
   }

   @ScriptName(j = "fvgutyeu")
   public int e() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.inventory.currentItem;
   }

   @ScriptName(j = "hsdwhh")
   public boolean F() {
      return mc.thePlayer == null ? false : mc.thePlayer.isUsingItem();
   }

   @ScriptName(j = "dhbewuy")
   public String n() {
      if (mc.thePlayer == null) {
         return "";
      } else {
         return mc.thePlayer.getHeldItem() != null ? mc.thePlayer.getHeldItem().getDisplayName() : "";
      }
   }

   @ScriptName(j = "wgytw")
   public boolean W_boolean(int var1) {
      if (mc.thePlayer == null) {
         return false;
      }

      Entity var5 = Z.get(var1);
      return var5 != null ? mc.thePlayer.C(var5) : false;
   }

   @ScriptName(j = "cvhbru8y")
   public double W(int var1) {
      if (mc.thePlayer == null) {
         return -1.0;
      }

      Entity var5 = Z.get(var1);
      return var5 != null ? mc.thePlayer.getDistanceToEntity(var5) : -1.0;
   }

   @ScriptName(j = "bgchuyerguy")
   public int[] g() {
      if (mc.theWorld == null) {
         return new int[0];
      }

      Z.clear();
      int[] var4 = new int[mc.theWorld.loadedEntityList.size()];
      int var5 = 0;

      for (Entity var7 : mc.theWorld.loadedEntityList) {
         int var8 = var7.getEntityId();
         Z.put(var8, var7);
         var4[var5] = var8;
         var5++;
      }

      return var4;
   }

   @ScriptName(j = "fbeyugy")
   public float b(int var1) {
      Entity var5 = Z.get(var1);
      return var5 != null && var5 instanceof EntityLivingBase ? ((EntityLivingBase)var5).getHealth() : -1.0F;
   }

   @ScriptName(j = "chneuy")
   public float F(int var1) {
      Entity var5 = Z.get(var1);
      return var5 != null && var5 instanceof EntityLivingBase ? ((EntityLivingBase)var5).hurtTime : -1.0F;
   }

   @ScriptName(j = "dhweiuehiu")
   public double[] T(int var1) {
      Entity var4 = Z.get(var1);
      return var4 != null
         ? new double[]{
            var4.getEntityBoundingBox().minX,
            var4.getEntityBoundingBox().minY,
            var4.getEntityBoundingBox().minZ,
            var4.getEntityBoundingBox().maxX,
            var4.getEntityBoundingBox().maxY,
            var4.getEntityBoundingBox().maxZ
         }
         : null;
   }

   @ScriptName(j = "dnwauyhdwuaiy")
   public double[] G(int var1) {
      Entity var4 = Z.get(var1);
      return var4 != null ? new double[]{var4.posX, var4.posY, var4.posZ} : null;
   }

   @ScriptName(j = "kdwaoihdauiyw")
   public float[] z(int var1) {
      Entity var4 = Z.get(var1);
      return var4 != null ? new float[]{var4.rotationPitch, var4.rotationYaw} : null;
   }

   @ScriptName(j = "swkajhxcjaw")
   public String V(int var1) {
      Entity var5 = Z.get(var1);
      if (var5 != null) {
         return var5.getDisplayName() == null ? var5.getName() : var5.getDisplayName().getFormattedText();
      } else {
         return null;
      }
   }

   @ScriptName(j = "dwhtydwga")
   public String r(int var1) {
      Entity var5 = Z.get(var1);
      return var5 != null ? var5.getName() : null;
   }

   @ScriptName(j = "dbwuy")
   public boolean Z(int var1) {
      Entity var2 = Z.get(var1);
      return var2 instanceof EntityPlayer;
   }

   @ScriptName(j = "dbwtytydgw")
   public String i(int var1) {
      Entity var5 = Z.get(var1);
      if (var5 != null && var5 instanceof EntityLivingBase) {
         EntityLivingBase var6 = (EntityLivingBase)var5;
         return var6.getHeldItem() == null ? "hand" : var6.getHeldItem().getDisplayName();
      } else {
         return null;
      }
   }

   @ScriptName(j = "dbwygdtyw")
   public float m() {
      return mc.timer.timerSpeed;
   }

   @ScriptName(j = "vdtrwdtryw")
   public void J(float var1) {
      mc.timer.timerSpeed = var1;
   }

   @ScriptName(j = "dhwiudhwu")
   public String n(double var1, double var3, double var5) {
      if (mc.theWorld != null) {
         Block var10 = mc.theWorld.getBlockState(new BlockPos(var1, var3, var5)).getBlock();
         if (var10 != null) {
            return var10.getUnlocalizedName();
         }
      }

      return null;
   }

   @ScriptName(j = "gdywydgw")
   public int G() {
      return mc.thePlayer.getEntityId();
   }

   @ScriptName(j = "duywgdywgdywg")
   public String B() {
      return mc.thePlayer.getName();
   }

   @ScriptName(j = "dwaydgywaudawdhaw")
   public void a(String var1, String var2, int var3) {
      Toasts.TOASTS.A(var1, var2, var3);
   }

   @ScriptName(j = "debwtgfdywtfdwatfy")
   public void C(String var1, String var2, int var3) {
      Toasts.TOASTS.w(var1, var2, var3);
   }

   @ScriptName(j = "dbawygtfdwyatdwaftyd")
   public void K(String var1, String var2, int var3) {
      Toasts.TOASTS.G(var1, var2, var3);
   }

   @ScriptName(j = "dgaytfdwtayfdtawyd")
   public void H(String var1, String var2, int var3) {
      Toasts.TOASTS.R(var1, var2, var3);
   }

   @ScriptName(j = "bdhgvwgdyvwgytdw")
   public double[] m_doubleArray() {
      return new double[]{mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ};
   }

   @ScriptName(j = "bdgawgdawtydfawtydfa")
   public void L(String var1) {
      ClientChat.CHAT.I(var1);
   }

   @ScriptName(j = "hdhwagbduyawdgwauy")
   public long h() {
      return System.currentTimeMillis();
   }
}
