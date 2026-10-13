package bypass.event.player;

import bypass.event.CancellableEvent;
import bypass.event.EventState;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.AxisAlignedBB;

public class MotionEvent extends CancellableEvent {
   private EventState state;
   private NetHandlerPlayClient netHandler;
   private AxisAlignedBB boundingBox;
   public boolean onGround;
   public double x;
   public double y;
   public double z;
   public double minY;
   public float yaw;
   public float pitch;
   private boolean s;
   private double lastX;
   private double lastY;
   private double lastZ;
   private float lastYaw;
   private float lastPitch;
   private int positionUpdateTicks;
   private static final long b = 25027395791350L;

   public MotionEvent(
      NetHandlerPlayClient var1,
      AxisAlignedBB var2,
      boolean var3,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      double var12,
      double var14,
      double var16,
      float var18,
      float var19,
      int var20,
      EventState var21
   ) {
      this.netHandler = var1;
      this.boundingBox = var2;
      this.minY = var2.minY;
      this.onGround = var3;
      this.x = var4;
      this.y = var6;
      this.z = var8;
      this.yaw = var10;
      this.pitch = var11;
      this.lastX = var12;
      this.lastY = var14;
      this.lastZ = var16;
      this.lastYaw = var18;
      this.lastPitch = var19;
      this.positionUpdateTicks = var20;
      this.state = var21;
   }

   public MotionEvent(EventState var1, NetHandlerPlayClient var2) {
      this.state = var1;
      this.netHandler = var2;
   }

   public EventState getState() {
      return this.state;
   }

   public NetHandlerPlayClient getNetHandler() {
      return this.netHandler;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public double getMinY() {
      return this.minY;
   }

   public void setMinY(double var1) {
      this.minY = var1;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setOnGround(boolean var1) {
      this.onGround = var1;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double var1) {
      this.x = var1;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double var1) {
      this.y = var1;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double var1) {
      this.z = var1;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float var1) {
      this.yaw = var1;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public boolean J_boolean() {
      return this.s;
   }

   public void A(boolean var1) {
      this.s = var1;
   }

   public double getLastX() {
      return this.lastX;
   }

   public void setLastX(double var1) {
      this.lastX = var1;
   }

   public double getLastY() {
      return this.lastY;
   }

   public void setLastY(double var1) {
      this.lastY = var1;
   }

   public double getLastZ() {
      return this.lastZ;
   }

   public void setLastZ(double var1) {
      this.lastZ = var1;
   }

   public float getLastYaw() {
      return this.lastYaw;
   }

   public void setLastYaw(float var1) {
      this.lastYaw = var1;
   }

   public float getLastPitch() {
      return this.lastPitch;
   }

   public void setLastPitch(float var1) {
      this.lastPitch = var1;
   }

   public int getPositionUpdateTicks() {
      return this.positionUpdateTicks;
   }

   public boolean isMoved() {
      double var4 = this.x - this.lastX;
      double var6 = this.y - this.lastY;
      double var8 = this.z - this.lastZ;
      return Math.abs(Math.pow(var4, 2.0) + Math.pow(var6, 2.0) + Math.pow(var8, 2.0)) > 9.0E-4;
   }

   public boolean isRotated() {
      double var4 = this.yaw - this.lastYaw;
      double var6 = this.lastPitch - this.lastPitch;
      return Math.abs(var4) != 0.0 || Math.abs(var6) != 0.0;
   }
}
