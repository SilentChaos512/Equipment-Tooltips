package net.silentchaos512.equiptooltips;

import java.io.File;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {

  public static final String CAT_POSITION = "position";

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
}
