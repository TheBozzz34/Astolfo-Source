package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public final class AutoClicker extends Module {
   private final NumberSetting<Integer> minCPS = new NumberSetting<>("Min-CPS", "Minimal CPS", 8, 1, 20);
   private final NumberSetting<Integer> maxCPS = new NumberSetting<>("Max-CPS", "Maximum CPS", 12, 1, 20);
   private final BooleanSetting blockCheck = new BooleanSetting("Block-Check", "Stops autoclicking if you aim at a block", true);
   private final BooleanSetting swordCheck = new BooleanSetting("Sword-Check", "Stops autoclicking if you dont have sword on your hand", false);
   private final MapSetting block = new MapSettingBuilder("Block")
      .ordered()
      .description("push velocity")
      .add("Enable", new BooleanSetting("Enable", "Enable block hitting", true))
      .add("Chance", new NumberSetting<>("Chance", "Push back amount", 0, 1, 100))
      .build();
   private int S;
   private final Stopwatch J = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      long l = c ^ 32826558056167L;
      this.p();
      if (this.maxCPS.getValue() < this.minCPS.getValue()) {
         this.maxCPS.setValue(this.minCPS.getValue());
      }

      if (!this.swordCheck.getValue() || GameUtil.isHoldingSword(mc.thePlayer)) {
         if (!mc.isGamePaused() && mc.currentScreen == null && mc.inGameHasFocus && Mouse.isButtonDown(0)) {
            if (this.blockCheck.getValue()
               && mc.objectMouseOver != null
               && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
               && !mc.thePlayer.capabilities.isCreativeMode) {
               KeyBinding.onTick(mc.gameSettings.lu.getKeyCode());
               mc.gameSettings.lu.pressed = true;
               return;
            }

            if (this.J.hasElapsed(this.S)) {
               this.J.reset();
               KeyBinding.onTick(mc.gameSettings.lu.getKeyCode());
               mc.gameSettings.lu.pressed = false;
               if (this.block.getSetting("Enable").asBoolean() && GameUtil.isHoldingSword(mc.thePlayer) && Mouse.isButtonDown(1)) {
                  KeyBinding.onTick(mc.gameSettings.Z7.getKeyCode());
                  mc.gameSettings.Z7.pressed = true;
                  long m = 4858544727439837639L ^ l;
                  float f = 100 - this.block.getSetting("Chance").asInt();
                  boolean bl = RandomUtils.RANDOM.nextFloat() > (f != 0.0F ? f / 100.0F : -0.1);
                  if (bl) {
                     KeyBinding.onTick(mc.gameSettings.Z7.getKeyCode());
                     mc.gameSettings.Z7.pressed = false;
                  }
               }

               double d = this.minCPS.getValue().intValue();
               double e = this.maxCPS.getValue() - this.minCPS.getValue();
               double d2 = 1.0 / (d + Math.random() * e);
               this.S = (int)Math.floor(d2 * 1000.0);
            }
         }
      }
   };
   private static final long c = 2072982790672L;

   public AutoClicker(Category category) {
      super(category);
      this.addSettings(this.block, this.minCPS, this.maxCPS, this.blockCheck, this.swordCheck);
   }

   @Override
   public void p() {
      this.setDisplayName("Auto Clicker " + EnumChatFormatting.GRAY + this.minCPS.getValue() + "-" + this.maxCPS.getValue());
      super.p();
   }
}
