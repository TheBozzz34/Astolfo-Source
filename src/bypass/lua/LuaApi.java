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

   @ScriptName(j = "byh")
   public void w(String string, float g, float h, int i) {
      if (!string.contains("bypass.")
         && !string.contains("jdk")
         && !string.contains("drawRect")
         && !string.contains("function(")
         && !string.contains("worldCls")) {
         mc.fontRendererObj.drawStringWithShadow(string, g, h, i);
      }
   }

   @ScriptName(j = "dgytw")
   public int r(String string) {
      return mc.fontRendererObj.getStringWidth(string);
   }

   @ScriptName(j = "drw")
   public void y(double d, double d2, double d3, double d4, int i, int j, int k, int l) {
      if (i <= 255 && i >= 0 && j >= 0 && k >= 0 && l >= 0 && j <= 255 && k <= 255 && l <= 255) {
         RenderUtil.j(d, d2, d3, d4, i, j, k, l);
      } else {
         chat.I("Incorrect color");
      }
   }

   @ScriptName(j = "rgmr")
   public void a(ScriptObjectMirror scriptObjectMirror) {
   }

   @ScriptName(j = "vrnj")
   public void F(ScriptObjectMirror scriptObjectMirror) {
   }

   @ScriptName(j = "wyhwy")
   public void y(String string, String string2, String string3, boolean bl) {
      BooleanSetting booleanSetting = new BooleanSetting(string2, string3, bl);
      f.put(string2, booleanSetting);
      client.getModuleManager().getModule(string).addSettings(booleanSetting);
   }

   @ScriptName(j = "bdwugydyuwh")
   public boolean h(String string) {
      try {
         return client.getModuleManager().getModule(string).isEnabled();
      } catch (Exception exception) {
         return false;
      }
   }

   @ScriptName(j = "ewbdwyb")
   public boolean t(String string) {
      return f.get(string).asBoolean();
   }

   @ScriptName(j = "dbuywaehyudwa")
   public void l(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.motionX = d;
         mc.thePlayer.motionY = d2;
         mc.thePlayer.motionZ = d3;
      }
   }

   @ScriptName(j = "dbvuhygwa")
   public double[] s() {
      return mc.thePlayer == null ? new double[0] : new double[]{mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ};
   }

   @ScriptName(j = "wjn")
   public void r(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(d, d2, d3);
      }
   }

   @ScriptName(j = "evh")
   public void M(String string) {
      if (mc.thePlayer != null) {
         if (!string.contains("bypass.")
            && !string.contains("jdk")
            && !string.contains("drawRect")
            && !string.contains("function(")
            && !string.contains("worldCls")) {
            mc.thePlayer.sendClientChatMessage(string);
         }
      }
   }

   @ScriptName(j = "evj")
   public void t(double d) {
      if (mc.thePlayer != null) {
         mc.thePlayer.r(d);
      }
   }

   @ScriptName(j = "deg")
   public void l_void() {
      if (mc.thePlayer != null) {
         mc.thePlayer.jump();
      }
   }

   @ScriptName(j = "evg")
   public void M(boolean bl) {
      if (mc.thePlayer != null) {
         mc.thePlayer.onGround = bl;
      }
   }

   @ScriptName(j = "ehh")
   public boolean k() {
      return mc.thePlayer == null ? false : mc.thePlayer.onGround;
   }

   @ScriptName(j = "nbhdwuinwd")
   public void Z(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(d, d2, d3);
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
   public void D(float g) {
      mc.timer.timerSpeed = g;
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
   public void d(int i, Object... objects) {
      if (mc.thePlayer != null) {
         switch (i) {
            case 0:
               mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
               break;
            case 1:
               int k = 0;
               int[] ints = new int[objects.length];

               for (Object object : objects) {
                  if (object instanceof Number) {
                     ints[k] = ((Number)object).intValue();
                     k++;
                  }
               }

               if (objects.length == 1) {
                  if (ints[0] > C0BPacketEntityAction.Action.values().length) {
                     return;
                  }

                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.values()[ints[0]]));
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
               int l = 0;
               int[] ints2 = new int[objects.length];

               for (Object object2 : objects) {
                  if (object2 instanceof Number) {
                     ints2[l] = ((Number)object2).intValue();
                     l++;
                  }
               }

               switch (objects.length) {
                  case 2:
                     if (Z.get(ints2[0]) == null) {
                        chat.I("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     if (ints2[1] > C02PacketUseEntity.Action.values().length) {
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(Z.get(ints2[0]), C02PacketUseEntity.Action.values()[ints2[1]]));
                     return;
                  case 4:
                     if (Z.get(ints2[0]) == null) {
                        chat.I("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(Z.get(ints2[0]), new Vec3(ints2[1], ints2[2], ints2[3])));
                     return;
                  default:
                     return;
               }
            case 9:
               int m = 0;
               double[] doubles = new double[objects.length];
               boolean bl = false;
               boolean bl2 = false;

               for (Object object3 : objects) {
                  if (object3 instanceof Number) {
                     doubles[m] = ((Number)object3).doubleValue();
                     m++;
                  }

                  if (object3 instanceof Boolean) {
                     bl2 = true;
                     bl = (Boolean)object3;
                  }
               }

               switch (objects.length) {
                  case 1:
                     if (bl2) {
                        mc.thePlayer.sendQueue.u(new C03PacketPlayer(bl));
                     }
                  case 2:
                  case 5:
                  default:
                     break;
                  case 3:
                     mc.thePlayer.sendQueue.u(new C03PacketPlayer.C05PacketPlayerLook((float)doubles[0], (float)doubles[1], bl));
                     break;
                  case 4:
                     mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(doubles[0], doubles[1], doubles[2], bl));
                     break;
                  case 6:
                     mc.thePlayer
                        .sendQueue
                        .u(new C03PacketPlayer.C06PacketPlayerPosLook(doubles[0], doubles[1], doubles[2], (float)doubles[3], (float)doubles[4], bl));
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
   public void z(float g, float h) {
      if (mc.thePlayer != null) {
         mc.thePlayer.rotationPitch = g;
         mc.thePlayer.rotationYaw = h;
      }
   }

   @ScriptName(j = "fhuyer")
   public void h(int i) {
      if (mc.thePlayer != null) {
         mc.thePlayer.inventory.currentItem = i;
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
   public boolean W_boolean(int i) {
      if (mc.thePlayer == null) {
         return false;
      }

      Entity entity = Z.get(i);
      return entity != null ? mc.thePlayer.C(entity) : false;
   }

   @ScriptName(j = "cvhbru8y")
   public double W(int i) {
      if (mc.thePlayer == null) {
         return -1.0;
      }

      Entity entity = Z.get(i);
      return entity != null ? mc.thePlayer.getDistanceToEntity(entity) : -1.0;
   }

   @ScriptName(j = "bgchuyerguy")
   public int[] g() {
      if (mc.theWorld == null) {
         return new int[0];
      }

      Z.clear();
      int[] ints = new int[mc.theWorld.loadedEntityList.size()];
      int i = 0;

      for (Entity entity : mc.theWorld.loadedEntityList) {
         int k = entity.getEntityId();
         Z.put(k, entity);
         ints[i] = k;
         i++;
      }

      return ints;
   }

   @ScriptName(j = "fbeyugy")
   public float b(int i) {
      Entity entity = Z.get(i);
      return entity != null && entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHealth() : -1.0F;
   }

   @ScriptName(j = "chneuy")
   public float F(int i) {
      Entity entity = Z.get(i);
      return entity != null && entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).hurtTime : -1.0F;
   }

   @ScriptName(j = "dhweiuehiu")
   public double[] T(int i) {
      Entity entity = Z.get(i);
      return entity != null
         ? new double[]{
            entity.getEntityBoundingBox().minX,
            entity.getEntityBoundingBox().minY,
            entity.getEntityBoundingBox().minZ,
            entity.getEntityBoundingBox().maxX,
            entity.getEntityBoundingBox().maxY,
            entity.getEntityBoundingBox().maxZ
         }
         : null;
   }

   @ScriptName(j = "dnwauyhdwuaiy")
   public double[] G(int i) {
      Entity entity = Z.get(i);
      return entity != null ? new double[]{entity.posX, entity.posY, entity.posZ} : null;
   }

   @ScriptName(j = "kdwaoihdauiyw")
   public float[] z(int i) {
      Entity entity = Z.get(i);
      return entity != null ? new float[]{entity.rotationPitch, entity.rotationYaw} : null;
   }

   @ScriptName(j = "swkajhxcjaw")
   public String V(int i) {
      Entity entity = Z.get(i);
      if (entity != null) {
         return entity.getDisplayName() == null ? entity.getName() : entity.getDisplayName().getFormattedText();
      } else {
         return null;
      }
   }

   @ScriptName(j = "dwhtydwga")
   public String r(int i) {
      Entity entity = Z.get(i);
      return entity != null ? entity.getName() : null;
   }

   @ScriptName(j = "dbwuy")
   public boolean Z(int i) {
      Entity entity = Z.get(i);
      return entity instanceof EntityPlayer;
   }

   @ScriptName(j = "dbwtytydgw")
   public String i(int i) {
      Entity entity = Z.get(i);
      if (entity != null && entity instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         return entityLivingBase.getHeldItem() == null ? "hand" : entityLivingBase.getHeldItem().getDisplayName();
      } else {
         return null;
      }
   }

   @ScriptName(j = "dbwygdtyw")
   public float m() {
      return mc.timer.timerSpeed;
   }

   @ScriptName(j = "vdtrwdtryw")
   public void J(float g) {
      mc.timer.timerSpeed = g;
   }

   @ScriptName(j = "dhwiudhwu")
   public String n(double d, double d2, double d3) {
      if (mc.theWorld != null) {
         Block block = mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
         if (block != null) {
            return block.getUnlocalizedName();
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
   public void a(String string, String string2, int i) {
      Toasts.TOASTS.A(string, string2, i);
   }

   @ScriptName(j = "debwtgfdywtfdwatfy")
   public void C(String string, String string2, int i) {
      Toasts.TOASTS.w(string, string2, i);
   }

   @ScriptName(j = "dbawygtfdwyatdwaftyd")
   public void K(String string, String string2, int i) {
      Toasts.TOASTS.G(string, string2, i);
   }

   @ScriptName(j = "dgaytfdwtayfdtawyd")
   public void H(String string, String string2, int i) {
      Toasts.TOASTS.R(string, string2, i);
   }

   @ScriptName(j = "bdhgvwgdyvwgytdw")
   public double[] m_doubleArray() {
      return new double[]{mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ};
   }

   @ScriptName(j = "bdgawgdawtydfawtydfa")
   public void L(String string) {
      ClientChat.CHAT.I(string);
   }

   @ScriptName(j = "hdhwagbduyawdgwauy")
   public long h() {
      return System.currentTimeMillis();
   }
}
