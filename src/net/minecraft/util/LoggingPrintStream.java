package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String domain;
   private static final long a = 68534076299193L;

   public LoggingPrintStream(String var1, OutputStream var2) {
      super(var2);
      this.domain = var1;
   }

   @Override
   public void println(String var1) {
      this.logString(var1);
   }

   @Override
   public void println(Object var1) {
      this.logString(String.valueOf(var1));
   }

   private void logString(String var1) {
      StackTraceElement[] var4 = Thread.currentThread().getStackTrace();
      StackTraceElement var5 = var4[Math.min(3, var4.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.domain, var5.getFileName(), var5.getLineNumber(), var1);
   }
}
