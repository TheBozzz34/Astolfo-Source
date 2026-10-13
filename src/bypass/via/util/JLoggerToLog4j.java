package bypass.via.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JLoggerToLog4j extends Logger {
   private final org.apache.logging.log4j.Logger J;
   private static final long a = 128350561184852L;

   public JLoggerToLog4j(org.apache.logging.log4j.Logger var1) {
      super("logger", null);
      this.J = var1;
   }

   @Override
   public void log(LogRecord var1) {
      this.log(var1.getLevel(), var1.getMessage());
   }

   @Override
   public void log(Level var1, String var2) {
      if (var1 == Level.FINE) {
         this.J.debug(var2);
      } else if (var1 == Level.WARNING) {
         this.J.warn(var2);
      } else if (var1 == Level.SEVERE) {
         this.J.error(var2);
      } else if (var1 == Level.INFO) {
         this.J.info(var2);
      } else {
         this.J.trace(var2);
      }
   }

   @Override
   public void log(Level var1, String var2, Object var3) {
      if (var1 == Level.FINE) {
         this.J.debug(var2, var3);
      } else if (var1 == Level.WARNING) {
         this.J.warn(var2, var3);
      } else if (var1 == Level.SEVERE) {
         this.J.error(var2, var3);
      } else if (var1 == Level.INFO) {
         this.J.info(var2, var3);
      } else {
         this.J.trace(var2, var3);
      }
   }

   @Override
   public void log(Level var1, String var2, Object[] var3) {
      this.log(var1, MessageFormat.format(var2, var3));
   }

   @Override
   public void log(Level var1, String var2, Throwable var3) {
      if (var1 == Level.FINE) {
         this.J.debug(var2, var3);
      } else if (var1 == Level.WARNING) {
         this.J.warn(var2, var3);
      } else if (var1 == Level.SEVERE) {
         this.J.error(var2, var3);
      } else if (var1 == Level.INFO) {
         this.J.info(var2, var3);
      } else {
         this.J.trace(var2, var3);
      }
   }
}
