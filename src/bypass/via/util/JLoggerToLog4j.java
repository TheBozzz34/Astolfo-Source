package bypass.via.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JLoggerToLog4j extends Logger {
   private final org.apache.logging.log4j.Logger log4jLogger;

   public JLoggerToLog4j(org.apache.logging.log4j.Logger logger) {
      super("logger", null);
      this.log4jLogger = logger;
   }

   @Override
   public void log(LogRecord logRecord) {
      this.log(logRecord.getLevel(), logRecord.getMessage());
   }

   @Override
   public void log(Level level, String string) {
      if (level == Level.FINE) {
         this.log4jLogger.debug(string);
      } else if (level == Level.WARNING) {
         this.log4jLogger.warn(string);
      } else if (level == Level.SEVERE) {
         this.log4jLogger.error(string);
      } else if (level == Level.INFO) {
         this.log4jLogger.info(string);
      } else {
         this.log4jLogger.trace(string);
      }
   }

   @Override
   public void log(Level level, String string, Object object) {
      if (level == Level.FINE) {
         this.log4jLogger.debug(string, object);
      } else if (level == Level.WARNING) {
         this.log4jLogger.warn(string, object);
      } else if (level == Level.SEVERE) {
         this.log4jLogger.error(string, object);
      } else if (level == Level.INFO) {
         this.log4jLogger.info(string, object);
      } else {
         this.log4jLogger.trace(string, object);
      }
   }

   @Override
   public void log(Level level, String string, Object[] objects) {
      this.log(level, MessageFormat.format(string, objects));
   }

   @Override
   public void log(Level level, String string, Throwable throwable) {
      if (level == Level.FINE) {
         this.log4jLogger.debug(string, throwable);
      } else if (level == Level.WARNING) {
         this.log4jLogger.warn(string, throwable);
      } else if (level == Level.SEVERE) {
         this.log4jLogger.error(string, throwable);
      } else if (level == Level.INFO) {
         this.log4jLogger.info(string, throwable);
      } else {
         this.log4jLogger.trace(string, throwable);
      }
   }
}
