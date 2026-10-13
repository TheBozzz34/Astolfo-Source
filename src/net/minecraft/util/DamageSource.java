package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static DamageSource inFire = new DamageSource("inFire").K();
   public static DamageSource lightningBolt = new DamageSource("lightningBolt");
   public static DamageSource onFire = new DamageSource("onFire").j().K();
   public static DamageSource lava = new DamageSource("lava").K();
   public static DamageSource inWall = new DamageSource("inWall").j();
   public static DamageSource drown = new DamageSource("drown").j();
   public static DamageSource starve = new DamageSource("starve").j().p();
   public static DamageSource cactus = new DamageSource("cactus");
   public static DamageSource fall = new DamageSource("fall").j();
   public static DamageSource outOfWorld = new DamageSource("outOfWorld").j().x();
   public static DamageSource generic = new DamageSource("generic").j();
   public static DamageSource magic = new DamageSource("magic").j().setMagicDamage();
   public static DamageSource wither = new DamageSource("wither").j();
   public static DamageSource anvil = new DamageSource("anvil");
   public static DamageSource fallingBlock = new DamageSource("fallingBlock");
   private boolean isUnblockable;
   private boolean isDamageAllowedInCreativeMode;
   private boolean damageIsAbsolute;
   private float hungerDamage = 0.3F;
   private boolean fireDamage;
   private boolean projectile;
   private boolean difficultyScaled;
   private boolean magicDamage;
   private boolean explosion;
   public String damageType;
   private static final long b = 98355004242524L;

   public static DamageSource causeMobDamage(EntityLivingBase var0) {
      return new EntityDamageSource("mob", var0);
   }

   public static DamageSource causePlayerDamage(EntityPlayer var0) {
      return new EntityDamageSource("player", var0);
   }

   public static DamageSource causeArrowDamage(EntityArrow var0, Entity var1) {
      return new EntityDamageSourceIndirect("arrow", var0, var1).setProjectile();
   }

   public static DamageSource causeFireballDamage(EntityFireball var0, Entity var1) {
      return var1 == null
         ? new EntityDamageSourceIndirect("onFire", var0, var0).K().setProjectile()
         : new EntityDamageSourceIndirect("fireball", var0, var1).K().setProjectile();
   }

   public static DamageSource causeThrownDamage(Entity var0, Entity var1) {
      return new EntityDamageSourceIndirect("thrown", var0, var1).setProjectile();
   }

   public static DamageSource causeIndirectMagicDamage(Entity var0, Entity var1) {
      return new EntityDamageSourceIndirect("indirectMagic", var0, var1).j().setMagicDamage();
   }

   public static DamageSource causeThornsDamage(Entity var0) {
      return new EntityDamageSource("thorns", var0).setIsThornsDamage().setMagicDamage();
   }

   public static DamageSource setExplosionSource(Explosion var0) {
      return var0 != null && var0.getExplosivePlacedBy() != null
         ? new EntityDamageSource("explosion.player", var0.getExplosivePlacedBy()).setDifficultyScaled().setExplosion()
         : new DamageSource("explosion").setDifficultyScaled().setExplosion();
   }

   public boolean isProjectile() {
      return this.projectile;
   }

   public DamageSource setProjectile() {
      this.projectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.explosion;
   }

   public DamageSource setExplosion() {
      this.explosion = true;
      return this;
   }

   public boolean isUnblockable() {
      return this.isUnblockable;
   }

   public float getHungerDamage() {
      return this.hungerDamage;
   }

   public boolean canHarmInCreative() {
      return this.isDamageAllowedInCreativeMode;
   }

   public boolean isDamageAbsolute() {
      return this.damageIsAbsolute;
   }

   public DamageSource(String var1) {
      this.damageType = var1;
   }

   public Entity getSourceOfDamage() {
      return this.getEntity();
   }

   public Entity getEntity() {
      return null;
   }

   public DamageSource j() {
      this.isUnblockable = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource x() {
      this.isDamageAllowedInCreativeMode = true;
      return this;
   }

   public DamageSource p() {
      this.damageIsAbsolute = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public DamageSource K() {
      this.fireDamage = true;
      return this;
   }

   public IChatComponent getDeathMessage(EntityLivingBase var1) {
      EntityLivingBase var4 = var1.getAttackingEntity();
      String var5 = "death.attack." + this.damageType;
      String var6 = var5 + ".player";
      return var4 != null && StatCollector.canTranslate(var6)
         ? new ChatComponentTranslation(var6, var1.getDisplayName(), var4.getDisplayName())
         : new ChatComponentTranslation(var5, var1.getDisplayName());
   }

   public boolean isFireDamage() {
      return this.fireDamage;
   }

   public String getDamageType() {
      return this.damageType;
   }

   public DamageSource setDifficultyScaled() {
      this.difficultyScaled = true;
      return this;
   }

   public boolean isDifficultyScaled() {
      return this.difficultyScaled;
   }

   public boolean isMagicDamage() {
      return this.magicDamage;
   }

   public DamageSource setMagicDamage() {
      this.magicDamage = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity var4 = this.getEntity();
      return var4 instanceof EntityPlayer && ((EntityPlayer)var4).capabilities.isCreativeMode;
   }
}
