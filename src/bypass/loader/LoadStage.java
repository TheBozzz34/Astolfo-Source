package bypass.loader;

public interface LoadStage {
   StageValue getValue();

   void setState(int i);

   int getState();
}
