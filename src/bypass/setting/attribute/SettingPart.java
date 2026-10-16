package bypass.setting.attribute;

public enum SettingPart {
   ALL,
   LABEL,
   DESCRIPTION,
   VALUE;

   private static final SettingPart[] VALUES = new SettingPart[]{ALL, LABEL, DESCRIPTION, VALUE};
}
