package bypass.module;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractModule implements ModuleInfo {
   private String name;
   private String displayName;
   private Category category;
   private static final long ab = 69248643412176L;

   public AbstractModule(String var1, Category var2) {
      this.name = var1;
      this.displayName = var1;
      this.category = var2;
   }

   public AbstractModule(String var1, String var2, Category var3) {
      this.name = var1;
      this.displayName = var2;
      this.category = var3;
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

   public void setName(String var1) {
      this.name = var1;
   }

   @Override
   public String getDisplayName() {
      return StringUtils.isEmpty(this.displayName) && this.name.length() > 3
         ? (this.displayName = this.getName().replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2"))
         : this.displayName;
   }

   public void setDisplayName(String var1) {
      this.displayName = var1;
   }

   @Override
   public Category getCategory() {
      return this.category;
   }
}
