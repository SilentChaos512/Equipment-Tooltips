package net.silentchaos512.equiptooltips;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;

import java.util.function.Supplier;

final class SGearCompat {
    private SGearCompat() {
        throw new IllegalAccessError("Utility class");
    }

    static String getGradeString(ItemStack stack) {
        MaterialGrade grade = MaterialGrade.fromStack(stack);
        return grade.name();
    }

    static int getPartTier(ItemStack stack) {
        PartData part = PartData.fromStackFast(stack);
        return part != null ? part.getPart().getTier() : -1;
    }

    static boolean isMainPart(ItemStack stack) {
        PartData part = PartData.fromStackFast(stack);
        return part != null && part.getType() == PartType.MAIN;
    }

    static boolean isGearItem(ItemStack stack) {
        return stack.getItem() instanceof ICoreItem;
    }

    static boolean isGearRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof ICoreRangedWeapon;
    }

    static float getMagicDamageStat(ItemStack stack) {
        return GearData.getStat(stack, ItemStats.MAGIC_DAMAGE);
    }

    static float getRangedDamage(ItemStack stack) {
        return GearData.getStat(stack, ItemStats.RANGED_DAMAGE);
    }

    public static float getRangedSpeed(ItemStack stack) {
        return GearData.getStat(stack, ItemStats.RANGED_SPEED);
    }

    public static float getStat(ItemStack stack, Supplier<Supplier<ItemStat>> stat) {
        if (isMainPart(stack)) {
            PartData part = PartData.from(stack);
            if (part != null) {
                return part.computeStat(stat.get().get());
            }
        }
        return GearData.getStat(stack, stat.get().get());
    }
}
