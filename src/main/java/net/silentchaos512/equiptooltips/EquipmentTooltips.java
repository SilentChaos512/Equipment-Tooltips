package net.silentchaos512.equiptooltips;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(EquipmentTooltips.MOD_ID)
public class EquipmentTooltips {
    public static final String MOD_ID = "equipmenttooltips";
    public static final String MOD_NAME = "Equipment Tooltips";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public EquipmentTooltips() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Config.init();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(EquipmentTooltips::onCommonSetup);
            MinecraftForge.EVENT_BUS.register(TooltipHandler.INSTANCE);
        }
    }

    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion()) || !FMLLoader.isProduction();
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        SGearProxy.detectSilentGear();
    }
}
