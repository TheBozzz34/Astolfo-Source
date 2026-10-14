package bypass.module;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractModule implements ModuleInfo {
   private String name;
   private String displayName;
   private Category category;
   private static final long ab = 69248643412176L;

   public AbstractModule(String string, Category category) {
      this.name = string;
      this.displayName = string;
      this.category = category;
   }

   public AbstractModule(String string, String string2, Category category) {
      this.name = string;
      this.displayName = string2;
      this.category = category;
   }

   public AbstractModule() {
      this.name = "";
      this.displayName = "";
      this.category = Category.HIDDEN;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public void setName(String string) {
      this.name = string;
   }

   @Override
   public String getDisplayName() {
      return StringUtils.isEmpty(this.displayName) && this.name.length() > 3
         ? (this.displayName = this.getName().replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2"))
         : this.displayName;
   }

   public void setDisplayName(String string) {
      this.displayName = string;
   }

   @Override
   public Category getCategory() {
      return this.category;
   }
}
