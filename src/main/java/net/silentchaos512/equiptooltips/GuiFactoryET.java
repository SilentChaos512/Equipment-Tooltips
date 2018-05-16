package net.silentchaos512.equiptooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiFactoryET implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    return new GuiConfigET(parentScreen);
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return null;
  }

  public static class GuiConfigET extends GuiConfig {

    public GuiConfigET(GuiScreen parentScreen) {

      super(parentScreen, getAllElements(), EquipmentTooltips.MOD_ID, false, false, EquipmentTooltips.MOD_NAME + " Config");
    }

    public static List<IConfigElement> getAllElements() {

      List<IConfigElement> list = new ArrayList<>();

      Set<String> categories = Config.getConfiguration().getCategoryNames();
      for (String str : categories)
        if (!str.contains("."))
          list.add(new DummyConfigElement.DummyCategoryElement(str, str, new ConfigElement(Config.getCategory(str)).getChildElements()));

      return list;
    }
  }
}
