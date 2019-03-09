package net.silentchaos512.equiptooltips;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.api.stats.ItemStat;

import java.util.function.Supplier;

final class SGearProxy {
    private static boolean modLoaded = false;

    private SGearProxy() {
        throw new IllegalAccessError("Utility class");
    }

    static void detectSilentGear() {
        modLoaded = ModList.get().isLoaded("silentgear");
        if (modLoaded) {
            EquipmentTooltips.LOGGER.info("Detected Silent Gear!");
        }
    }

    static boolean isLoaded() {
        return modLoaded;
    }

    static String getGradeString(ItemStack stack) {
        if (modLoaded) return SGearCompat.getGradeString(stack);
        return "N/A";
    }

    static int getPartTier(ItemStack stack) {
        if (modLoaded) return SGearCompat.getPartTier(stack);
        return -1;
    }

    static boolean isMainPart(ItemStack stack) {
        if (modLoaded) return SGearCompat.isMainPart(stack);
        return false;
    }

    static boolean isGearItem(ItemStack stack) {
        if (modLoaded) return SGearCompat.isGearItem(stack);
        return false;
    }

    static boolean isGearRangedWeapon(ItemStack stack) {
        if (modLoaded) return SGearCompat.isGearRangedWeapon(stack);
        return false;
    }

    static float getStat(ItemStack stack, Supplier<Supplier<ItemStat>> stat) {
        if (modLoaded) return SGearCompat.getStat(stack, stat);
        return 0;
    }

    static float getMagicDamageStat(ItemStack stack) {
        if (modLoaded) return SGearCompat.getMagicDamageStat(stack);
        return 0;
    }

    static float getRangedDamage(ItemStack stack) {
        if (modLoaded) return SGearCompat.getRangedDamage(stack);
        return 0;
    }

    public static float getRangedSpeed(ItemStack stack) {
        if (modLoaded) return SGearCompat.getRangedSpeed(stack);
        return 0;
    }
}
