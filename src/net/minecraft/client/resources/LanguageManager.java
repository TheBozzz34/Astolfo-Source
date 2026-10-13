package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements IResourceManagerReloadListener {
   private static final Logger logger = LogManager.getLogger();
   private final IMetadataSerializer theMetadataSerializer;
   private String currentLanguage;
   public static final Locale currentLocale = new Locale();
   private Map<String, Language> languageMap = Maps.newHashMap();
   private static final long a = 61043761627334L;

   public LanguageManager(IMetadataSerializer var1, String var2) {
      this.theMetadataSerializer = var1;
      this.currentLanguage = var2;
      I18n.setLocale(currentLocale);
   }

   public void parseLanguageMetadata(List<IResourcePack> var1) {
      this.languageMap.clear();

      for (IResourcePack var6 : var1) {
         try {
            LanguageMetadataSection var7 = var6.getPackMetadata(this.theMetadataSerializer, "language");
            if (var7 != null) {
               for (Language var9 : var7.getLanguages()) {
                  if (!this.languageMap.containsKey(var9.getLanguageCode())) {
                     this.languageMap.put(var9.getLanguageCode(), var9);
                  }
               }
            }
         } catch (RuntimeException var10) {
            logger.warn("Unable to parse metadata section of resourcepack: " + var6.getPackName(), var10);
         } catch (IOException var11) {
            logger.warn("Unable to parse metadata section of resourcepack: " + var6.getPackName(), var11);
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      ArrayList var5 = Lists.newArrayList("en_US");
      if (!"en_US".equals(this.currentLanguage)) {
         var5.add(this.currentLanguage);
      }

      currentLocale.loadLocaleDataFiles(var1, var5);
      StringTranslate.replaceWith(currentLocale.properties);
   }

   public boolean isCurrentLocaleUnicode() {
      return currentLocale.isUnicode();
   }

   public boolean isCurrentLanguageBidirectional() {
      return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
   }

   public void setCurrentLanguage(Language var1) {
      this.currentLanguage = var1.getLanguageCode();
   }

   public Language getCurrentLanguage() {
      return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("en_US");
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languageMap.values());
   }
}
