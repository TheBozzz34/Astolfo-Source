package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.DamageParticle;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.NumberSetting;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public final class DamageParticles extends Module {
   private final Map<Integer, Float> O = new HashMap<>();
   private final NumberSetting<Integer> duration = new NumberSetting<>("Duration", "Duration of each particle", 20, 1, 70);
   private final NumberSetting<Float> gravity = new NumberSetting<>("Gravity", "Gravity applied", 0.1F, 0.01F, 1.0F);
   private final NumberSetting<Float> size = new NumberSetting<>("Size", "Size of each particle", 3.0F, 0.1F, 10.0F);
   private final BooleanSetting grow = new BooleanSetting("Grow", "Grows particles", true);
   private final BooleanSetting random = new BooleanSetting("Random", "Random position", true);
   private final BooleanSetting staticSetting = new BooleanSetting("Static", "Stays", false);
   private final BooleanSetting hearts = new BooleanSetting("Hearts", "Show hearts inside of full health value", false);
   private final ColorSetting healColor = new ColorSetting("Heal-Color", "Heal-Color", Color.GREEN);
   private final ColorSetting damageColor = new ColorSetting("Damage-Color", "Damage-Color", Color.RED);
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         for (Entity var6 : mc.theWorld.loadedEntityList) {
            if (var6 instanceof EntityLivingBase && !(var6 instanceof EntityPlayerSP)) {
               EntityLivingBase var7 = (EntityLivingBase)var6;
               if (!Double.isNaN(var7.getHealth())) {
                  int var8 = (int)Math.ceil(var7.getHealth());
                  Float var9 = this.O.get(var7.getEntityId());
                  if (var9 != null) {
                     int var10 = (int)Math.ceil(var9.floatValue());
                     this.g(var6, var10 - var8);
                  }

                  this.O.put(var6.getEntityId(), var7.getHealth());
               }
            }
         }
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var1x -> this.O.clear();
   private static final long c = 56396412993634L;

   public DamageParticles(Category var1) {
      super(var1);
      this.addSettings(this.healColor, this.damageColor, this.duration, this.gravity, this.size, this.grow, this.random, this.staticSetting, this.hearts);
      this.setHidden(false);
   }

   private void g(Entity var1, int var2) {
      if (var2 != 0) {
         World var5 = var1.worldObj;
         double var6 = this.random.getValue() ? var5.rand.nextGaussian() * 0.02 : 0.0;
         double var8 = 0.5;
         double var10 = this.random.getValue() ? var5.rand.nextGaussian() * 0.02 : 0.0;
         DamageParticle var12 = new DamageParticle(
            var2,
            var5,
            var1.posX,
            var1.posY + var1.height,
            var1.posZ,
            var6,
            var8,
            var10,
            this.gravity.getValue(),
            this.size.getValue(),
            this.duration.getValue(),
            this.grow.getValue(),
            this.staticSetting.getValue(),
            this.hearts.getValue(),
            this.healColor.getValue(),
            this.damageColor.getValue()
         );
         mc.effectRenderer.addEffect(var12);
      }
   }
}
