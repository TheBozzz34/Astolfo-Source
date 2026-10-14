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

   public CombatTracker(EntityLivingBase fighterIn) {
      this.fighter = fighterIn;
   }

   public void func_94545_a() {
      this.func_94542_g();
      if (this.fighter.isOnLadder()) {
         Block block = this.fighter
            .worldObj
            .getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ))
            .getBlock();
         if (block == Blocks.ladder) {
            this.field_94551_f = "ladder";
         } else if (block == Blocks.vine) {
            this.field_94551_f = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.field_94551_f = "water";
      }
   }

   public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
      this.reset();
      this.func_94545_a();
      CombatEntry combatEntry = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.field_94551_f, this.fighter.fallDistance);
      this.combatEntries.add(combatEntry);
      this.field_94555_c = this.fighter.ticksExisted;
      this.field_94553_e = true;
      if (combatEntry.isLivingDamageSrc() && !this.field_94552_d && this.fighter.isEntityAlive()) {
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

      CombatEntry combatEntry = this.func_94544_f();
      CombatEntry combatEntry2 = this.combatEntries.get(this.combatEntries.size() - 1);
      IChatComponent iChatComponent = combatEntry2.getDamageSrcDisplayName();
      Entity entity = combatEntry2.getDamageSrc().getEntity();
      IChatComponent iChatComponent2;
      if (combatEntry != null && combatEntry2.getDamageSrc() == DamageSource.fall) {
         IChatComponent iChatComponent3 = combatEntry.getDamageSrcDisplayName();
         if (combatEntry.getDamageSrc() == DamageSource.fall || combatEntry.getDamageSrc() == DamageSource.outOfWorld) {
            iChatComponent2 = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(combatEntry), this.fighter.getDisplayName());
         } else if (iChatComponent3 != null && (iChatComponent == null || !iChatComponent3.equals(iChatComponent))) {
            Entity entity2 = combatEntry.getDamageSrc().getEntity();
            ItemStack itemStack = entity2 instanceof EntityLivingBase ? ((EntityLivingBase)entity2).getHeldItem() : null;
            if (itemStack != null && itemStack.hasDisplayName()) {
               iChatComponent2 = new ChatComponentTranslation("death.fell.assist.item", this.fighter.getDisplayName(), iChatComponent3, itemStack.getChatComponent());
            } else {
               iChatComponent2 = new ChatComponentTranslation("death.fell.assist", this.fighter.getDisplayName(), iChatComponent3);
            }
         } else if (iChatComponent != null) {
            ItemStack itemStack2 = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItem() : null;
            if (itemStack2 != null && itemStack2.hasDisplayName()) {
               iChatComponent2 = new ChatComponentTranslation("death.fell.finish.item", this.fighter.getDisplayName(), iChatComponent, itemStack2.getChatComponent());
            } else {
               iChatComponent2 = new ChatComponentTranslation("death.fell.finish", this.fighter.getDisplayName(), iChatComponent);
            }
         } else {
            iChatComponent2 = new ChatComponentTranslation("death.fell.killer", this.fighter.getDisplayName());
         }
      } else {
         iChatComponent2 = combatEntry2.getDamageSrc().getDeathMessage(this.fighter);
      }

      return iChatComponent2;
   }

   public EntityLivingBase func_94550_c() {
      EntityLivingBase entityLivingBase = null;
      EntityPlayer entityPlayer = null;
      float f = 0.0F;
      float g = 0.0F;

      for (CombatEntry combatEntry : this.combatEntries) {
         if (combatEntry.getDamageSrc().getEntity() instanceof EntityPlayer && (entityPlayer == null || combatEntry.func_94563_c() > g)) {
            g = combatEntry.func_94563_c();
            entityPlayer = (EntityPlayer)combatEntry.getDamageSrc().getEntity();
         }

         if (combatEntry.getDamageSrc().getEntity() instanceof EntityLivingBase && (entityLivingBase == null || combatEntry.func_94563_c() > f)) {
            f = combatEntry.func_94563_c();
            entityLivingBase = (EntityLivingBase)combatEntry.getDamageSrc().getEntity();
         }
      }

      return entityPlayer != null && g >= f / 3.0F ? entityPlayer : entityLivingBase;
   }

   private CombatEntry func_94544_f() {
      CombatEntry combatEntry = null;
      CombatEntry combatEntry2 = null;
      byte b = 0;
      float f = 0.0F;

      for (int i = 0; i < this.combatEntries.size(); i++) {
         CombatEntry combatEntry3 = this.combatEntries.get(i);
         CombatEntry combatEntry4 = i > 0 ? this.combatEntries.get(i - 1) : null;
         if ((combatEntry3.getDamageSrc() == DamageSource.fall || combatEntry3.getDamageSrc() == DamageSource.outOfWorld)
            && combatEntry3.getDamageAmount() > 0.0F
            && (combatEntry == null || combatEntry3.getDamageAmount() > f)) {
            if (i > 0) {
               combatEntry = combatEntry4;
            } else {
               combatEntry = combatEntry3;
            }

            f = combatEntry3.getDamageAmount();
         }

         if (combatEntry3.func_94562_g() != null && (combatEntry2 == null || combatEntry3.func_94563_c() > b)) {
            combatEntry2 = combatEntry3;
         }
      }

      if (f > 5.0F && combatEntry != null) {
         return combatEntry;
      } else {
         return b > 5 && combatEntry2 != null ? combatEntry2 : null;
      }
   }

   private String func_94548_b(CombatEntry combatEntry) {
      return combatEntry.func_94562_g() == null ? "generic" : combatEntry.func_94562_g();
   }

   public int func_180134_f() {
      return this.field_94552_d ? this.fighter.ticksExisted - this.field_152775_d : this.field_152776_e - this.field_152775_d;
   }

   private void func_94542_g() {
      this.field_94551_f = null;
   }

   public void reset() {
      int i = this.field_94552_d ? 300 : 100;
      if (this.field_94553_e && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.field_94555_c > i)) {
         boolean bl = this.field_94552_d;
         this.field_94553_e = false;
         this.field_94552_d = false;
         this.field_152776_e = this.fighter.ticksExisted;
         if (bl) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }
   }

   public EntityLivingBase getFighter() {
      return this.fighter;
   }
}
