package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
   private final List<JsonException.Entry> field_151383_a = Lists.newArrayList();
   private final String exceptionMessage;
   private static final long a = 17609647062526L;

   public JsonException(String message) {
      this.field_151383_a.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public JsonException(String message, Throwable cause) {
      super(cause);
      this.field_151383_a.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public void func_151380_a(String string) {
      this.field_151383_a.get(0).func_151373_a(string);
   }

   public void func_151381_b(String string) {
      this.field_151383_a.get(0).field_151376_a = string;
      this.field_151383_a.add(0, new JsonException.Entry());
   }

   @Override
   public String getMessage() {
      return "Invalid " + this.field_151383_a.get(this.field_151383_a.size() - 1).toString() + ": " + this.exceptionMessage;
   }

   public static JsonException func_151379_a(Exception exception) {
      if (exception instanceof JsonException) {
         return (JsonException)exception;
      }

      String string = exception.getMessage();
      if (exception instanceof FileNotFoundException) {
         string = "File not found";
      }

      return new JsonException(string, exception);
   }

   public static class Entry {
      private String field_151376_a = null;
      private final List<String> field_151375_b = Lists.newArrayList();
      private static final long a = 117029071211596L;

      public Entry() {
      }

      private void func_151373_a(String string) {
         this.field_151375_b.add(0, string);
      }

      public String func_151372_b() {
         return StringUtils.join(this.field_151375_b, "->");
      }

      @Override
      public String toString() {
         return this.field_151376_a != null
            ? (!this.field_151375_b.isEmpty() ? this.field_151376_a + " " + this.func_151372_b() : this.field_151376_a)
            : (!this.field_151375_b.isEmpty() ? "(Unknown file) " + this.func_151372_b() : "(Unknown file)");
      }
   }
}
