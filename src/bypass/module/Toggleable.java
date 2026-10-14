package bypass.module;

public interface Toggleable {
   void toggle();

   void setEnabled(boolean bl);

   boolean isEnabled();

   int getKey();

   void setKey(int i);

   void onEnable();

   void onDisable();
}
