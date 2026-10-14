package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String domain;

   public LoggingPrintStream(String domainIn, OutputStream outStream) {
      super(outStream);
      this.domain = domainIn;
   }

   @Override
   public void println(String string) {
      this.logString(string);
   }

   @Override
   public void println(Object object) {
      this.logString(String.valueOf(object));
   }

   private void logString(String string) {
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.domain, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), string);
   }
}
