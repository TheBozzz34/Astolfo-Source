package bypass.module;

public interface Toggleable {
   void toggle();

   void setEnabled(boolean var1);

   boolean isEnabled();

   int getKey();

   void setKey(int var1);

   void onEnable();

   void onDisable();
}
