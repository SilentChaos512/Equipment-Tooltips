package net.silentchaos512.equiptooltips;

import net.minecraftforge.fml.loading.FMLPaths;
import net.silentchaos512.utils.config.BooleanValue;
import net.silentchaos512.utils.config.ConfigSpecWrapper;
import net.silentchaos512.utils.config.IntValue;

public class Config {
    private static final ConfigSpecWrapper WRAPPER = ConfigSpecWrapper.create(
            FMLPaths.CONFIGDIR.get().resolve("equipment-tooltips-client.toml"));

    public static final Client CLIENT = new Client(WRAPPER);

    public static class Client {
        public final BooleanValue checkShiftKey;
        public final BooleanValue hideOnShift;
        public final IntValue backgroundColor;
        public final BooleanValue positionOnBottom;
        public final IntValue offsetX;
        public final IntValue offsetY;

        public Client(ConfigSpecWrapper wrapper) {
            checkShiftKey = wrapper
                    .builder("controls.checkShiftKey")
                    .comment("If true, stat icon visibility is changed by holding shift.",
                            "Otherwise, they are always visible.")
                    .define(false);
            hideOnShift = wrapper
                    .builder("controls.hideOnShift")
                    .comment("Only applies if checkShiftKey is enabled.",
                            "If this is true, stat icons are hidden when shift is held.",
                            "Otherwise, stat icons are shown only when shift is held.")
                    .define(false);
            backgroundColor = wrapper
                    .builder("display.backgroundColor")
                    .comment("Background color, including alpha (AARRGGBB)")
                    .defineColorInt(0xC0100010);
            positionOnBottom = wrapper
                    .builder("display.positionOnBottom")
                    .comment("Move the stat icons below the tooltip window, instead of on top of it")
                    .define(false);
            offsetX = wrapper
                    .builder("display.offsetX")
                    .comment("Fine-tune the position of the icons line")
                    .defineInRange(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            offsetY = wrapper
                    .builder("display.offsetY")
                    .comment("Fine-tune the position of the icons line")
                    .defineInRange(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
    }

    public static void init() {
        WRAPPER.validate();
        WRAPPER.validate();
    }
}
