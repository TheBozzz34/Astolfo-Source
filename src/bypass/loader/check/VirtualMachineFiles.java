package bypass.loader.check;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class VirtualMachineFiles {
   private final List<File> files = Arrays.asList(
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\Vmmouse.sys"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vm3dgl.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vmdum.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vm3dver.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vmtray.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VMToolsHook.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vmmousever.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vmhgfs.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\vmGuestLib.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VmGuestLibJava.dll"),
      new File("C:\\\\windows\\\\System32\\\\Driversvmhgfs.dll"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VBoxMouse.sys"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VBoxGuest.sys"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VBoxSF.sys"),
      new File("C:\\\\windows\\\\System32\\\\Drivers\\\\VBoxVideo.sys"),
      new File("C:\\\\windows\\\\System32\\\\vboxdisp.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxhook.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxmrxnp.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxogl.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglarrayspu.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglcrutil.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglerrorspu.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglfeedbackspu.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglpackspu.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxoglpassthroughspu.dll"),
      new File("C:\\\\windows\\\\System32\\\\vboxservice.exe"),
      new File("C:\\\\windows\\\\System32\\\\vboxtray.exe"),
      new File("C:\\\\windows\\\\System32\\\\VBoxControl.exe")
   );

   public List<File> getFiles() {
      return this.files;
   }
}
