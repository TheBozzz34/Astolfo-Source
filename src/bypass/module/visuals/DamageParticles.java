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
   private final Map<Integer, Float> lastHealthById = new HashMap<>();
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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayerSP)) {
               EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
               if (!Double.isNaN(entityLivingBase.getHealth())) {
                  int i = (int)Math.ceil(entityLivingBase.getHealth());
                  Float float2 = this.lastHealthById.get(entityLivingBase.getEntityId());
                  if (float2 != null) {
                     int j = (int)Math.ceil(float2.floatValue());
                     this.spawnDamageParticle(entity, j - i);
                  }

                  this.lastHealthById.put(entity.getEntityId(), entityLivingBase.getHealth());
               }
            }
         }
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.lastHealthById.clear();

   public DamageParticles(Category category) {
      super(category);
      this.addSettings(this.healColor, this.damageColor, this.duration, this.gravity, this.size, this.grow, this.random, this.staticSetting, this.hearts);
      this.setHidden(false);
   }

   private void spawnDamageParticle(Entity entity, int i) {
      if (i != 0) {
         World world = entity.worldObj;
         double d = this.random.getValue() ? world.rand.nextGaussian() * 0.02 : 0.0;
         double e = 0.5;
         double d2 = this.random.getValue() ? world.rand.nextGaussian() * 0.02 : 0.0;
         DamageParticle damageParticle = new DamageParticle(
            i,
            world,
            entity.posX,
            entity.posY + entity.height,
            entity.posZ,
            d,
            e,
            d2,
            this.gravity.getValue(),
            this.size.getValue(),
            this.duration.getValue(),
            this.grow.getValue(),
            this.staticSetting.getValue(),
            this.hearts.getValue(),
            this.healColor.getValue(),
            this.damageColor.getValue()
         );
         mc.effectRenderer.addEffect(damageParticle);
      }
   }
}
