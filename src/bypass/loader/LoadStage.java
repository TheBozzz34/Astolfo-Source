package bypass.loader;

public interface LoadStage {
   StageValue getValue();

   void setState(int var1);

   int getState();
}
