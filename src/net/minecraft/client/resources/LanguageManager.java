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

   public LanguageManager(IMetadataSerializer theMetadataSerializerIn, String currentLanguageIn) {
      this.theMetadataSerializer = theMetadataSerializerIn;
      this.currentLanguage = currentLanguageIn;
      I18n.setLocale(currentLocale);
   }

   public void parseLanguageMetadata(List<IResourcePack> resourcesPacks) {
      this.languageMap.clear();

      for (IResourcePack iResourcePack : resourcesPacks) {
         try {
            LanguageMetadataSection languageMetadataSection = iResourcePack.getPackMetadata(this.theMetadataSerializer, "language");
            if (languageMetadataSection != null) {
               for (Language language2 : languageMetadataSection.getLanguages()) {
                  if (!this.languageMap.containsKey(language2.getLanguageCode())) {
                     this.languageMap.put(language2.getLanguageCode(), language2);
                  }
               }
            }
         } catch (RuntimeException runtimeException) {
            logger.warn("Unable to parse metadata section of resourcepack: " + iResourcePack.getPackName(), runtimeException);
         } catch (IOException ioException) {
            logger.warn("Unable to parse metadata section of resourcepack: " + iResourcePack.getPackName(), ioException);
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      ArrayList arrayList = Lists.newArrayList("en_US");
      if (!"en_US".equals(this.currentLanguage)) {
         arrayList.add(this.currentLanguage);
      }

      currentLocale.loadLocaleDataFiles(resourceManager, arrayList);
      StringTranslate.replaceWith(currentLocale.properties);
   }

   public boolean isCurrentLocaleUnicode() {
      return currentLocale.isUnicode();
   }

   public boolean isCurrentLanguageBidirectional() {
      return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
   }

   public void setCurrentLanguage(Language currentLanguageIn) {
      this.currentLanguage = currentLanguageIn.getLanguageCode();
   }

   public Language getCurrentLanguage() {
      return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("en_US");
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languageMap.values());
   }
}
