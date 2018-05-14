package net.silentchaos512.equiptooltips;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EquipmentTooltips.MOD_ID, name = EquipmentTooltips.MOD_NAME, version = EquipmentTooltips.VERSION, dependencies = EquipmentTooltips.DEPENDENCIES, guiFactory = "net.silentchaos512.equipmenttooltips.GuiFactoryET", clientSideOnly = true)
public class EquipmentTooltips {

  public static final String MOD_ID = "equipmenttooltips";
  public static final String MOD_NAME = "Equipment Tooltips";
  public static final String VERSION = "@VERSION@";
  public static final int BUILD_NUM = 0;
  public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);";
  public static final String RESOURCE_PREFIX = MOD_ID + ":";

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    MinecraftForge.EVENT_BUS.register(TooltipHandler.instance);
  }
}
