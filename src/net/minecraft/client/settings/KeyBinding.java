package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

public class KeyBinding implements Comparable<KeyBinding> {
   private static final List<KeyBinding> keybindArray = Lists.newArrayList();
   private static final IntHashMap<KeyBinding> hash = new IntHashMap<>();
   private static final Set<String> keybindSet = Sets.newHashSet();
   private final String keyDescription;
   private final int keyCodeDefault;
   private final String keyCategory;
   private int keyCode;
   public boolean pressed;
   private int pressTime;
   private static final long a = 62620788192000L;

   public static void onTick(int var0) {
      if (var0 != 0) {
         KeyBinding var4 = hash.lookup(var0);
         if (var4 != null) {
            var4.pressTime++;
         }
      }
   }

   public static void setKeyBindState(int var0, boolean var1) {
      if (var0 != 0) {
         KeyBinding var5 = hash.lookup(var0);
         if (var5 != null) {
            var5.pressed = var1;
         }
      }
   }

   public static void unPressAllKeys() {
      for (KeyBinding var4 : keybindArray) {
         var4.unpressKey();
      }
   }

   public static void resetKeyBindingArrayAndHash() {
      hash.clearMap();

      for (KeyBinding var4 : keybindArray) {
         hash.addKey(var4.keyCode, var4);
      }
   }

   public static Set<String> getKeybinds() {
      return keybindSet;
   }

   public KeyBinding(String var1, int var2, String var3) {
      this.keyDescription = var1;
      this.keyCode = var2;
      this.keyCodeDefault = var2;
      this.keyCategory = var3;
      keybindArray.add(this);
      hash.addKey(var2, this);
      keybindSet.add(var3);
   }

   public boolean isKeyDown() {
      return this.pressed;
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public boolean h() {
      if (this.pressTime == 0) {
         return false;
      }

      this.pressTime--;
      return true;
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.pressed = false;
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public int getKeyCodeDefault() {
      return this.keyCodeDefault;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setKeyCode(int var1) {
      this.keyCode = var1;
   }

   public int compareTo(KeyBinding var1) {
      int var5 = I18n.format(this.keyCategory).compareTo(I18n.format(var1.keyCategory));
      if (var5 == 0) {
         var5 = I18n.format(this.keyDescription).compareTo(I18n.format(var1.keyDescription));
      }

      return var5;
   }
}
