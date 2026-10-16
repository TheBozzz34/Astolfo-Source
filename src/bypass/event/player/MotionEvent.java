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
   private boolean extraFlag;
   private double lastX;
   private double lastY;
   private double lastZ;
   private float lastYaw;
   private float lastPitch;
   private int positionUpdateTicks;

   public MotionEvent(
      NetHandlerPlayClient netHandlerPlayClient,
      AxisAlignedBB axisAlignedBB,
      boolean bl,
      double d,
      double d2,
      double d3,
      float f,
      float g,
      double d4,
      double d5,
      double d6,
      float h,
      float f2,
      int i,
      EventState eventState
   ) {
      this.netHandler = netHandlerPlayClient;
      this.boundingBox = axisAlignedBB;
      this.minY = axisAlignedBB.minY;
      this.onGround = bl;
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.yaw = f;
      this.pitch = g;
      this.lastX = d4;
      this.lastY = d5;
      this.lastZ = d6;
      this.lastYaw = h;
      this.lastPitch = f2;
      this.positionUpdateTicks = i;
      this.state = eventState;
   }

   public MotionEvent(EventState eventState, NetHandlerPlayClient netHandlerPlayClient) {
      this.state = eventState;
      this.netHandler = netHandlerPlayClient;
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

   public void setMinY(double d) {
      this.minY = d;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setOnGround(boolean bl) {
      this.onGround = bl;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double d) {
      this.x = d;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double d) {
      this.y = d;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double d) {
      this.z = d;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float f) {
      this.yaw = f;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public boolean isExtraFlag() {
      return this.extraFlag;
   }

   public void setExtraFlag(boolean bl) {
      this.extraFlag = bl;
   }

   public double getLastX() {
      return this.lastX;
   }

   public void setLastX(double d) {
      this.lastX = d;
   }

   public double getLastY() {
      return this.lastY;
   }

   public void setLastY(double d) {
      this.lastY = d;
   }

   public double getLastZ() {
      return this.lastZ;
   }

   public void setLastZ(double d) {
      this.lastZ = d;
   }

   public float getLastYaw() {
      return this.lastYaw;
   }

   public void setLastYaw(float f) {
      this.lastYaw = f;
   }

   public float getLastPitch() {
      return this.lastPitch;
   }

   public void setLastPitch(float f) {
      this.lastPitch = f;
   }

   public int getPositionUpdateTicks() {
      return this.positionUpdateTicks;
   }

   public boolean isMoved() {
      double d = this.x - this.lastX;
      double e = this.y - this.lastY;
      double d2 = this.z - this.lastZ;
      return Math.abs(Math.pow(d, 2.0) + Math.pow(e, 2.0) + Math.pow(d2, 2.0)) > 9.0E-4;
   }

   public boolean isRotated() {
      double d = this.yaw - this.lastYaw;
      double e = this.lastPitch - this.lastPitch;
      return Math.abs(d) != 0.0 || Math.abs(e) != 0.0;
   }
}
