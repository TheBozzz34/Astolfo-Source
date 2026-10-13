package bypass.util;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

public final class TopFileChooser extends JFileChooser {
   public TopFileChooser(File var1) {
      super(var1);
   }

   @Override
   public JDialog createDialog(Component var1) throws HeadlessException {
      JDialog var2 = super.createDialog(var1);
      var2.setLocationByPlatform(true);
      var2.setAlwaysOnTop(true);
      return var2;
   }
}
