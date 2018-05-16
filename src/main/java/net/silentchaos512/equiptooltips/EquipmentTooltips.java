package net.silentchaos512.equiptooltips;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EquipmentTooltips.MOD_ID, name = EquipmentTooltips.MOD_NAME, version = EquipmentTooltips.VERSION, dependencies = EquipmentTooltips.DEPENDENCIES, guiFactory = "net.silentchaos512.equiptooltips.GuiFactoryET", clientSideOnly = true)
public class EquipmentTooltips {

  public static final String MOD_ID = "equipmenttooltips";
  public static final String MOD_NAME = "Equipment Tooltips";
  public static final String VERSION = "@VERSION@";
  public static final int BUILD_NUM = 0;
  public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);";
  public static final String RESOURCE_PREFIX = MOD_ID + ":";

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.init(event.getSuggestedConfigurationFile());
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(TooltipHandler.instance);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

    Config.save();
  }

  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    if (event.getModID().equalsIgnoreCase(EquipmentTooltips.MOD_ID)) {
      Config.load();
      Config.save();
    }
  }
}
