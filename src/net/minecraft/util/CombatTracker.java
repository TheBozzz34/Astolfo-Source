package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker {
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private final EntityLivingBase fighter;
   private int field_94555_c;
   private int field_152775_d;
   private int field_152776_e;
   private boolean field_94552_d;
   private boolean field_94553_e;
   private String field_94551_f;
   private static final long a = 9715138478196L;

   public CombatTracker(EntityLivingBase var1) {
      this.fighter = var1;
   }

   public void func_94545_a() {
      this.func_94542_g();
      if (this.fighter.isOnLadder()) {
         Block var4 = this.fighter
            .worldObj
            .getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ))
            .getBlock();
         if (var4 == Blocks.ladder) {
            this.field_94551_f = "ladder";
         } else if (var4 == Blocks.vine) {
            this.field_94551_f = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.field_94551_f = "water";
      }
   }

   public void trackDamage(DamageSource var1, float var2, float var3) {
      this.reset();
      this.func_94545_a();
      CombatEntry var7 = new CombatEntry(var1, this.fighter.ticksExisted, var2, var3, this.field_94551_f, this.fighter.fallDistance);
      this.combatEntries.add(var7);
      this.field_94555_c = this.fighter.ticksExisted;
      this.field_94553_e = true;
      if (var7.isLivingDamageSrc() && !this.field_94552_d && this.fighter.isEntityAlive()) {
         this.field_94552_d = true;
         this.field_152775_d = this.fighter.ticksExisted;
         this.field_152776_e = this.field_152775_d;
         this.fighter.sendEnterCombat();
      }
   }

   public IChatComponent getDeathMessage() {
      if (this.combatEntries.size() == 0) {
         return new ChatComponentTranslation("death.attack.generic", this.fighter.getDisplayName());
      }

      CombatEntry var4 = this.func_94544_f();
      CombatEntry var5 = this.combatEntries.get(this.combatEntries.size() - 1);
      IChatComponent var6 = var5.getDamageSrcDisplayName();
      Entity var7 = var5.getDamageSrc().getEntity();
      IChatComponent var8;
      if (var4 != null && var5.getDamageSrc() == DamageSource.fall) {
         IChatComponent var9 = var4.getDamageSrcDisplayName();
         if (var4.getDamageSrc() == DamageSource.fall || var4.getDamageSrc() == DamageSource.outOfWorld) {
            var8 = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(var4), this.fighter.getDisplayName());
         } else if (var9 != null && (var6 == null || !var9.equals(var6))) {
            Entity var12 = var4.getDamageSrc().getEntity();
            ItemStack var11 = var12 instanceof EntityLivingBase ? ((EntityLivingBase)var12).getHeldItem() : null;
            if (var11 != null && var11.hasDisplayName()) {
               var8 = new ChatComponentTranslation("death.fell.assist.item", this.fighter.getDisplayName(), var9, var11.getChatComponent());
            } else {
               var8 = new ChatComponentTranslation("death.fell.assist", this.fighter.getDisplayName(), var9);
            }
         } else if (var6 != null) {
            ItemStack var10 = var7 instanceof EntityLivingBase ? ((EntityLivingBase)var7).getHeldItem() : null;
            if (var10 != null && var10.hasDisplayName()) {
               var8 = new ChatComponentTranslation("death.fell.finish.item", this.fighter.getDisplayName(), var6, var10.getChatComponent());
            } else {
               var8 = new ChatComponentTranslation("death.fell.finish", this.fighter.getDisplayName(), var6);
            }
         } else {
            var8 = new ChatComponentTranslation("death.fell.killer", this.fighter.getDisplayName());
         }
      } else {
         var8 = var5.getDamageSrc().getDeathMessage(this.fighter);
      }

      return var8;
   }

   public EntityLivingBase func_94550_c() {
      EntityLivingBase var4 = null;
      EntityPlayer var5 = null;
      float var6 = 0.0F;
      float var7 = 0.0F;

      for (CombatEntry var9 : this.combatEntries) {
         if (var9.getDamageSrc().getEntity() instanceof EntityPlayer && (var5 == null || var9.func_94563_c() > var7)) {
            var7 = var9.func_94563_c();
            var5 = (EntityPlayer)var9.getDamageSrc().getEntity();
         }

         if (var9.getDamageSrc().getEntity() instanceof EntityLivingBase && (var4 == null || var9.func_94563_c() > var6)) {
            var6 = var9.func_94563_c();
            var4 = (EntityLivingBase)var9.getDamageSrc().getEntity();
         }
      }

      return var5 != null && var7 >= var6 / 3.0F ? var5 : var4;
   }

   private CombatEntry func_94544_f() {
      CombatEntry var4 = null;
      CombatEntry var5 = null;
      byte var6 = 0;
      float var7 = 0.0F;

      for (int var8 = 0; var8 < this.combatEntries.size(); var8++) {
         CombatEntry var9 = this.combatEntries.get(var8);
         CombatEntry var10 = var8 > 0 ? this.combatEntries.get(var8 - 1) : null;
         if ((var9.getDamageSrc() == DamageSource.fall || var9.getDamageSrc() == DamageSource.outOfWorld)
            && var9.getDamageAmount() > 0.0F
            && (var4 == null || var9.getDamageAmount() > var7)) {
            if (var8 > 0) {
               var4 = var10;
            } else {
               var4 = var9;
            }

            var7 = var9.getDamageAmount();
         }

         if (var9.func_94562_g() != null && (var5 == null || var9.func_94563_c() > var6)) {
            var5 = var9;
         }
      }

      if (var7 > 5.0F && var4 != null) {
         return var4;
      } else {
         return var6 > 5 && var5 != null ? var5 : null;
      }
   }

   private String func_94548_b(CombatEntry var1) {
      return var1.func_94562_g() == null ? "generic" : var1.func_94562_g();
   }

   public int func_180134_f() {
      return this.field_94552_d ? this.fighter.ticksExisted - this.field_152775_d : this.field_152776_e - this.field_152775_d;
   }

   private void func_94542_g() {
      this.field_94551_f = null;
   }

   public void reset() {
      int var4 = this.field_94552_d ? 300 : 100;
      if (this.field_94553_e && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.field_94555_c > var4)) {
         boolean var5 = this.field_94552_d;
         this.field_94553_e = false;
         this.field_94552_d = false;
         this.field_152776_e = this.fighter.ticksExisted;
         if (var5) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }
   }

   public EntityLivingBase getFighter() {
      return this.fighter;
   }
}
