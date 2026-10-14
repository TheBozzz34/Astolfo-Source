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

   public static DamageSource causeMobDamage(EntityLivingBase mob) {
      return new EntityDamageSource("mob", mob);
   }

   public static DamageSource causePlayerDamage(EntityPlayer player) {
      return new EntityDamageSource("player", player);
   }

   public static DamageSource causeArrowDamage(EntityArrow arrow, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("arrow", arrow, indirectEntityIn).setProjectile();
   }

   public static DamageSource causeFireballDamage(EntityFireball fireball, Entity indirectEntityIn) {
      return indirectEntityIn == null
         ? new EntityDamageSourceIndirect("onFire", fireball, fireball).K().setProjectile()
         : new EntityDamageSourceIndirect("fireball", fireball, indirectEntityIn).K().setProjectile();
   }

   public static DamageSource causeThrownDamage(Entity source, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("thrown", source, indirectEntityIn).setProjectile();
   }

   public static DamageSource causeIndirectMagicDamage(Entity source, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("indirectMagic", source, indirectEntityIn).j().setMagicDamage();
   }

   public static DamageSource causeThornsDamage(Entity source) {
      return new EntityDamageSource("thorns", source).setIsThornsDamage().setMagicDamage();
   }

   public static DamageSource setExplosionSource(Explosion explosionIn) {
      return explosionIn != null && explosionIn.getExplosivePlacedBy() != null
         ? new EntityDamageSource("explosion.player", explosionIn.getExplosivePlacedBy()).setDifficultyScaled().setExplosion()
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

   public DamageSource(String damageTypeIn) {
      this.damageType = damageTypeIn;
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

   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      EntityLivingBase entityLivingBase = entityLivingBaseIn.getAttackingEntity();
      String string = "death.attack." + this.damageType;
      String string2 = string + ".player";
      return entityLivingBase != null && StatCollector.canTranslate(string2)
         ? new ChatComponentTranslation(string2, entityLivingBaseIn.getDisplayName(), entityLivingBase.getDisplayName())
         : new ChatComponentTranslation(string, entityLivingBaseIn.getDisplayName());
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
      Entity entity = this.getEntity();
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;
   }
}
