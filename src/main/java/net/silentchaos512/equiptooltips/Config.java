package net.silentchaos512.equiptooltips;

import java.io.File;

import com.google.common.primitives.UnsignedInts;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class Config {

  public static final String CAT_DISPLAY = "display";
  public static final String CAT_POSITION = "position";

  public static boolean DISPLAY_CHECK_KEY;
  private static final boolean DISPLAY_CHECK_KEY_DEFAULT = false;
  private static final String DISPLAY_CHECK_KEY_COMMENT = "Show/hide the stat icons when shift is pressed.";

  public static boolean DISPLAY_HIDE_ON_KEY;
  private static final boolean DISPLAY_HIDE_ON_KEY_DEFAULT = false;
  private static final String DISPLAY_HIDE_ON_KEY_COMMENT = "Only applies if the Check Key Press config is enabled. If true, stat icons are hidden when shift is held. If false, stat icons are shown when shift is held.";

  public static int BACKGROUND_COLOR;
  private static final int BACKGROUND_COLOR_DEFAULT = 0xC0100010;
  private static final String BACKGROUND_COLOR_COMMENT = "The background color, including alpha (AARRGGBB)";

  public static boolean POSITION_ON_BOTTOM;
  private static final boolean POSITION_ON_BOTTOM_DEFAULT = false;
  private static final String POSITION_ON_BOTTOM_COMMENT = "Move the stat icons line to the bottom of the tooltip window.";

  public static int POSITION_X_OFFSET;
  public static int POSITION_Y_OFFSET;
  private static final int POSITION_OFFSET_DEFAULT = 0;
  private static final String POSITION_OFFSET_COMMENT = "Allows the stat icons line's position to be fine-tuned.";

  static Configuration config;

  public static void init(File file) {

    config = new Configuration(file);
    load();
  }

  public static void load() {

    DISPLAY_CHECK_KEY = config.getBoolean("Check Key Press", CAT_DISPLAY, DISPLAY_CHECK_KEY_DEFAULT, DISPLAY_CHECK_KEY_COMMENT);
    DISPLAY_HIDE_ON_KEY = config.getBoolean("Hide On Key", CAT_DISPLAY, DISPLAY_CHECK_KEY_DEFAULT, DISPLAY_CHECK_KEY_COMMENT);
    BACKGROUND_COLOR = getColor("Background Color", CAT_DISPLAY, BACKGROUND_COLOR_DEFAULT, true, BACKGROUND_COLOR_COMMENT);
    POSITION_ON_BOTTOM = config.getBoolean("Move To Bottom", CAT_POSITION, POSITION_ON_BOTTOM_DEFAULT, POSITION_OFFSET_COMMENT);
    POSITION_X_OFFSET = config.getInt("Offset X", CAT_POSITION, POSITION_OFFSET_DEFAULT, Integer.MIN_VALUE, Integer.MAX_VALUE, POSITION_OFFSET_COMMENT);
    POSITION_Y_OFFSET = config.getInt("Offset Y", CAT_POSITION, POSITION_OFFSET_DEFAULT, Integer.MIN_VALUE, Integer.MAX_VALUE, POSITION_OFFSET_COMMENT);
  }

  public static void save() {

    if (config.hasChanged())
      config.save();
  }

  public static ConfigCategory getCategory(String category) {

    return config.getCategory(category);
  }

  public static Configuration getConfiguration() {

    return config;
  }

  public static int getColor(String key, String category, int defaultValue, boolean includeAlpha, String comment) {

    String format = String.format(includeAlpha ? "%08x" : "%06x", defaultValue);
    String str = config.getString(key, category, format, comment);
    try {
      int result = UnsignedInts.parseUnsignedInt(str, 16);
      if (includeAlpha && (result & 0xFF000000) == 0)
        result |= 0xFF000000;
      return result;
    } catch (NumberFormatException ex) {
      System.out.println("[Equipment Tooltips] Warning: Could not parse color code \"" + str + "\"");
      return defaultValue;
    }
  }
}
