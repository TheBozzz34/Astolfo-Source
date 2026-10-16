package bypass.unused;

import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.ListSetting;
import bypass.setting.ListSettingBuilder;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.setting.StringSetting;

public class SettingExamples {
   public static BooleanSetting booleanSetting = new BooleanSetting("Boolean", "desc", true);
   public static EnumSetting<ExampleEnum> enumSetting = new EnumSetting<>("Enum", "desc", ExampleEnum.TEST_1);
   public static MapSetting map = new MapSettingBuilder("Map")
      .ordered()
      .add("test_1", new NumberSetting<>("Byte", "desc", (byte)1))
      .add("test_2", new NumberSetting<>("Double", "desc", 1.0))
      .add("test_3", new NumberSetting<>("Float", "desc", 1.0F))
      .add("test_4", new NumberSetting<>("Integer", "desc", 1))
      .add("test_5", new NumberSetting<>("Long", "desc", 1L))
      .add("test_6", new NumberSetting<>("Short", "desc", (short)1))
      .add("test_7", new StringSetting("String", "desc", "Example"))
      .add("test_8", new BooleanSetting("Boolean", "desc", true))
      .build();
   public static ListSetting list = new ListSettingBuilder("List")
      .withEmptyList()
      .addSetting(new NumberSetting<>("Byte", "desc", (byte)1))
      .addSetting(new NumberSetting<>("Double", "desc", 1.0))
      .addSetting(new NumberSetting<>("Float", "desc", 1.0F))
      .addSetting(new NumberSetting<>("Integer", "desc", 1))
      .addSetting(new NumberSetting<>("Long", "desc", 1L))
      .addSetting(new NumberSetting<>("Short", "desc", (short)1))
      .addSetting(new StringSetting("String", "desc", "Example"))
      .addSetting(new BooleanSetting("Boolean", "desc", true))
      .build();
   public static NumberSetting<Byte> byteSetting = new NumberSetting<>("Byte", "desc", (byte)1);
   public static NumberSetting<Double> doubleSetting = new NumberSetting<>("Double", "desc", 1.0);
   public static NumberSetting<Float> floatSetting = new NumberSetting<>("Float", "desc", 1.0F);
   public static NumberSetting<Integer> integer = new NumberSetting<>("Integer", "desc", 1);
   public static NumberSetting<Long> longSetting = new NumberSetting<>("Long", "desc", 1L);
   public static NumberSetting<Short> shortSetting = new NumberSetting<>("Short", "desc", (short)1);
   public static StringSetting string = new StringSetting("String", "desc", "Example");
}
