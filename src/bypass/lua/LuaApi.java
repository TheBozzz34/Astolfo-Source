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
   public static final Map<Integer, Entity> entitiesById = new HashMap<>();
   public static final Map<String, Setting<?>> settingsByName = new HashMap<>();

   @ScriptName("byh")
   public void drawString(String string, float f, float g, int i) {
      if (!string.contains("bypass.")
         && !string.contains("jdk")
         && !string.contains("drawRect")
         && !string.contains("function(")
         && !string.contains("worldCls")) {
         mc.fontRendererObj.drawStringWithShadow(string, f, g, i);
      }
   }

   @ScriptName("dgytw")
   public int getStringWidth(String string) {
      return mc.fontRendererObj.getStringWidth(string);
   }

   @ScriptName("drw")
   public void drawRect(double d, double d2, double d3, double d4, int i, int j, int k, int l) {
      if (i <= 255 && i >= 0 && j >= 0 && k >= 0 && l >= 0 && j <= 255 && k <= 255 && l <= 255) {
         RenderUtil.drawRectRgba(d, d2, d3, d4, i, j, k, l);
      } else {
         chat.print("Incorrect color");
      }
   }

   @ScriptName("rgmr")
   public void registerScriptObjectStub(ScriptObjectMirror scriptObjectMirror) {
   }

   @ScriptName("vrnj")
   public void handleScriptObjectStub(ScriptObjectMirror scriptObjectMirror) {
   }

   @ScriptName("wyhwy")
   public void addBooleanSetting(String string, String string2, String string3, boolean bl) {
      BooleanSetting booleanSetting = new BooleanSetting(string2, string3, bl);
      settingsByName.put(string2, booleanSetting);
      client.getModuleManager().getModule(string).addSettings(booleanSetting);
   }

   @ScriptName("bdwugydyuwh")
   public boolean isModuleEnabled(String string) {
      try {
         return client.getModuleManager().getModule(string).isEnabled();
      } catch (Exception exception) {
         return false;
      }
   }

   @ScriptName("ewbdwyb")
   public boolean getBooleanSetting(String string) {
      return settingsByName.get(string).asBoolean();
   }

   @ScriptName("dbuywaehyudwa")
   public void setMotion(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.motionX = d;
         mc.thePlayer.motionY = d2;
         mc.thePlayer.motionZ = d3;
      }
   }

   @ScriptName("dbvuhygwa")
   public double[] getMotion() {
      return mc.thePlayer == null ? new double[0] : new double[]{mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ};
   }

   @ScriptName("wjn")
   public void setPlayerPosition(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(d, d2, d3);
      }
   }

   @ScriptName("evh")
   public void sendChatMessage(String string) {
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

   @ScriptName("evj")
   public void setSpeed(double d) {
      if (mc.thePlayer != null) {
         mc.thePlayer.r(d);
      }
   }

   @ScriptName("deg")
   public void l_void() {
      if (mc.thePlayer != null) {
         mc.thePlayer.jump();
      }
   }

   @ScriptName("evg")
   public void setOnGround(boolean bl) {
      if (mc.thePlayer != null) {
         mc.thePlayer.onGround = bl;
      }
   }

   @ScriptName("ehh")
   public boolean isOnGround() {
      return mc.thePlayer == null ? false : mc.thePlayer.onGround;
   }

   @ScriptName("nbhdwuinwd")
   public void setPosition(double d, double d2, double d3) {
      if (mc.thePlayer != null) {
         mc.thePlayer.setPosition(d, d2, d3);
      }
   }

   @ScriptName("bhgdyuwdhuyw")
   public double[] getPosition() {
      return mc.thePlayer == null ? new double[0] : new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
   }

   @ScriptName("ftyegh")
   public boolean isOnLadder() {
      return mc.thePlayer == null ? false : mc.thePlayer.isOnLadder();
   }

   @ScriptName("eio")
   public void setTimer(float f) {
      mc.timer.timerSpeed = f;
   }

   @ScriptName("dfbgeuwy")
   public int getTicksExisted() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.ticksExisted;
   }

   @ScriptName("bduyhw")
   public int getHurtTime() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.hurtTime;
   }

   @ScriptName("ejf")
   public void sendPacket(int i, Object... objects) {
      if (mc.thePlayer != null) {
         switch (i) {
            case 0:
               mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
               break;
            case 1:
               int j = 0;
               int[] ints = new int[objects.length];

               for (Object object : objects) {
                  if (object instanceof Number) {
                     ints[j] = ((Number)object).intValue();
                     j++;
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
               int k = 0;
               int[] ints2 = new int[objects.length];

               for (Object object2 : objects) {
                  if (object2 instanceof Number) {
                     ints2[k] = ((Number)object2).intValue();
                     k++;
                  }
               }

               switch (objects.length) {
                  case 2:
                     if (entitiesById.get(ints2[0]) == null) {
                        chat.print("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     if (ints2[1] > C02PacketUseEntity.Action.values().length) {
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entitiesById.get(ints2[0]), C02PacketUseEntity.Action.values()[ints2[1]]));
                     return;
                  case 4:
                     if (entitiesById.get(ints2[0]) == null) {
                        chat.print("Entity not found, remember to call world.getEntities()");
                        return;
                     }

                     mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entitiesById.get(ints2[0]), new Vec3(ints2[1], ints2[2], ints2[3])));
                     return;
                  default:
                     return;
               }
            case 9:
               int l = 0;
               double[] doubles = new double[objects.length];
               boolean bl = false;
               boolean bl2 = false;

               for (Object object3 : objects) {
                  if (object3 instanceof Number) {
                     doubles[l] = ((Number)object3).doubleValue();
                     l++;
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

   @ScriptName("wbhuy")
   public int getKillAuraTargetId() {
      return KillAura.target != null ? KillAura.target.getEntityId() : -1;
   }

   @ScriptName("vnruiy")
   public float[] G_floatArray() {
      return mc.thePlayer == null ? new float[0] : new float[]{mc.thePlayer.moveForward, mc.thePlayer.moveStrafing};
   }

   @ScriptName("doiwjdiowdjwio")
   public float[] getRotations() {
      return mc.thePlayer == null ? new float[0] : new float[]{mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw};
   }

   @ScriptName("dbwhydgwydw")
   public void setRotations(float f, float g) {
      if (mc.thePlayer != null) {
         mc.thePlayer.rotationPitch = f;
         mc.thePlayer.rotationYaw = g;
      }
   }

   @ScriptName("fhuyer")
   public void setHeldItemSlot(int i) {
      if (mc.thePlayer != null) {
         mc.thePlayer.inventory.currentItem = i;
         mc.playerController.u();
      }
   }

   @ScriptName("fvgutyeu")
   public int getHeldItemSlot() {
      return mc.thePlayer == null ? 0 : mc.thePlayer.inventory.currentItem;
   }

   @ScriptName("hsdwhh")
   public boolean isUsingItem() {
      return mc.thePlayer == null ? false : mc.thePlayer.isUsingItem();
   }

   @ScriptName("dhbewuy")
   public String getHeldItemName() {
      if (mc.thePlayer == null) {
         return "";
      } else {
         return mc.thePlayer.getHeldItem() != null ? mc.thePlayer.getHeldItem().getDisplayName() : "";
      }
   }

   @ScriptName("wgytw")
   public boolean W_boolean(int i) {
      if (mc.thePlayer == null) {
         return false;
      }

      Entity entity = entitiesById.get(i);
      return entity != null ? mc.thePlayer.C(entity) : false;
   }

   @ScriptName("cvhbru8y")
   public double getDistanceToEntity(int i) {
      if (mc.thePlayer == null) {
         return -1.0;
      }

      Entity entity = entitiesById.get(i);
      return entity != null ? mc.thePlayer.getDistanceToEntity(entity) : -1.0;
   }

   @ScriptName("bgchuyerguy")
   public int[] getEntityIds() {
      if (mc.theWorld == null) {
         return new int[0];
      }

      entitiesById.clear();
      int[] ints = new int[mc.theWorld.loadedEntityList.size()];
      int i = 0;

      for (Entity entity : mc.theWorld.loadedEntityList) {
         int j = entity.getEntityId();
         entitiesById.put(j, entity);
         ints[i] = j;
         i++;
      }

      return ints;
   }

   @ScriptName("fbeyugy")
   public float getEntityHealth(int i) {
      Entity entity = entitiesById.get(i);
      return entity != null && entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHealth() : -1.0F;
   }

   @ScriptName("chneuy")
   public float getEntityHurtTime(int i) {
      Entity entity = entitiesById.get(i);
      return entity != null && entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).hurtTime : -1.0F;
   }

   @ScriptName("dhweiuehiu")
   public double[] getEntityBoundingBox(int i) {
      Entity entity = entitiesById.get(i);
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

   @ScriptName("dnwauyhdwuaiy")
   public double[] getEntityPosition(int i) {
      Entity entity = entitiesById.get(i);
      return entity != null ? new double[]{entity.posX, entity.posY, entity.posZ} : null;
   }

   @ScriptName("kdwaoihdauiyw")
   public float[] getEntityRotations(int i) {
      Entity entity = entitiesById.get(i);
      return entity != null ? new float[]{entity.rotationPitch, entity.rotationYaw} : null;
   }

   @ScriptName("swkajhxcjaw")
   public String getEntityDisplayName(int i) {
      Entity entity = entitiesById.get(i);
      if (entity != null) {
         return entity.getDisplayName() == null ? entity.getName() : entity.getDisplayName().getFormattedText();
      } else {
         return null;
      }
   }

   @ScriptName("dwhtydwga")
   public String getEntityName(int i) {
      Entity entity = entitiesById.get(i);
      return entity != null ? entity.getName() : null;
   }

   @ScriptName("dbwuy")
   public boolean isEntityPlayer(int i) {
      Entity entity = entitiesById.get(i);
      return entity instanceof EntityPlayer;
   }

   @ScriptName("dbwtytydgw")
   public String getEntityHeldItemName(int i) {
      Entity entity = entitiesById.get(i);
      if (entity != null && entity instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         return entityLivingBase.getHeldItem() == null ? "hand" : entityLivingBase.getHeldItem().getDisplayName();
      } else {
         return null;
      }
   }

   @ScriptName("dbwygdtyw")
   public float getTimerSpeed() {
      return mc.timer.timerSpeed;
   }

   @ScriptName("vdtrwdtryw")
   public void setTimerSpeed(float f) {
      mc.timer.timerSpeed = f;
   }

   @ScriptName("dhwiudhwu")
   public String getBlockName(double d, double d2, double d3) {
      if (mc.theWorld != null) {
         Block block = mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
         if (block != null) {
            return block.getUnlocalizedName();
         }
      }

      return null;
   }

   @ScriptName("gdywydgw")
   public int getPlayerEntityId() {
      return mc.thePlayer.getEntityId();
   }

   @ScriptName("duywgdywgdywg")
   public String getPlayerName() {
      return mc.thePlayer.getName();
   }

   @ScriptName("dwaydgywaudawdhaw")
   public void showWarningToast(String string, String string2, int i) {
      Toasts.TOASTS.warningTimed(string, string2, i);
   }

   @ScriptName("debwtgfdywtfdwatfy")
   public void showSuccessToast(String string, String string2, int i) {
      Toasts.TOASTS.successTimed(string, string2, i);
   }

   @ScriptName("dbawygtfdwyatdwaftyd")
   public void showInfoToast(String string, String string2, int i) {
      Toasts.TOASTS.infoTimed(string, string2, i);
   }

   @ScriptName("dgaytfdwtayfdtawyd")
   public void showDangerToast(String string, String string2, int i) {
      Toasts.TOASTS.dangerTimed(string, string2, i);
   }

   @ScriptName("bdhgvwgdyvwgytdw")
   public double[] m_doubleArray() {
      return new double[]{mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ};
   }

   @ScriptName("bdgawgdawtydfawtydfa")
   public void printChatMessage(String string) {
      ClientChat.CHAT.print(string);
   }

   @ScriptName("hdhwagbduyawdgwauy")
   public long getCurrentTimeMillis() {
      return System.currentTimeMillis();
   }
}
