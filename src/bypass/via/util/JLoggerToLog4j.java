package bypass.via.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JLoggerToLog4j extends Logger {
   private final org.apache.logging.log4j.Logger J;
   private static final long a = 128350561184852L;

   public JLoggerToLog4j(org.apache.logging.log4j.Logger logger) {
      super("logger", null);
      this.J = logger;
   }

   @Override
   public void log(LogRecord logRecord) {
      this.log(logRecord.getLevel(), logRecord.getMessage());
   }

   @Override
   public void log(Level level, String string) {
      if (level == Level.FINE) {
         this.J.debug(string);
      } else if (level == Level.WARNING) {
         this.J.warn(string);
      } else if (level == Level.SEVERE) {
         this.J.error(string);
      } else if (level == Level.INFO) {
         this.J.info(string);
      } else {
         this.J.trace(string);
      }
   }

   @Override
   public void log(Level level, String string, Object object) {
      if (level == Level.FINE) {
         this.J.debug(string, object);
      } else if (level == Level.WARNING) {
         this.J.warn(string, object);
      } else if (level == Level.SEVERE) {
         this.J.error(string, object);
      } else if (level == Level.INFO) {
         this.J.info(string, object);
      } else {
         this.J.trace(string, object);
      }
   }

   @Override
   public void log(Level level, String string, Object[] objects) {
      this.log(level, MessageFormat.format(string, objects));
   }

   @Override
   public void log(Level level, String string, Throwable throwable) {
      if (level == Level.FINE) {
         this.J.debug(string, throwable);
      } else if (level == Level.WARNING) {
         this.J.warn(string, throwable);
      } else if (level == Level.SEVERE) {
         this.J.error(string, throwable);
      } else if (level == Level.INFO) {
         this.J.info(string, throwable);
      } else {
         this.J.trace(string, throwable);
      }
   }
}
