package net.silentchaos512.equiptooltips;

import net.silentchaos512.gear.api.stats.ItemStats;

import java.util.function.Supplier;

public enum ItemStat {
    ARMOR_PROTECTION(6, () -> () -> ItemStats.ARMOR),
    ARMOR_TOUGHNESS(7, 0, false, () -> () -> ItemStats.ARMOR_TOUGHNESS),
    DURABILITY(0, 0, false, () -> () -> ItemStats.DURABILITY),
    ENCHANTABILITY(10, () -> () -> ItemStats.ENCHANTABILITY),
    HARVEST_LEVEL(1, -1, false, () -> () -> ItemStats.HARVEST_LEVEL),
    HARVEST_SPEED(2, () -> () -> ItemStats.HARVEST_SPEED),
    MAGIC_DAMAGE(4, 0, false, () -> () -> ItemStats.MAGIC_DAMAGE),
    //MAGIC_PROTECTION(9, 0, false),
    MELEE_DAMAGE(3, () -> () -> ItemStats.MELEE_DAMAGE),
    MELEE_SPEED(5, () -> () -> ItemStats.ATTACK_SPEED),
    RANGED_DAMAGE(8, () -> () -> ItemStats.RANGED_DAMAGE),
    RANGED_SPEED(5, () -> () -> ItemStats.RANGED_SPEED);

    public final int iconIndex;
    public final float defaultValue;
    public final boolean displayIfDefault;
    public final Supplier<Supplier<net.silentchaos512.gear.api.stats.ItemStat>> silentGearStat;

    ItemStat(int iconIndex, Supplier<Supplier<net.silentchaos512.gear.api.stats.ItemStat>> silentGearStat) {
        this(iconIndex, 0f, true, silentGearStat);
    }

    ItemStat(int iconIndex, float defaultValue, boolean displayIfDefault, Supplier<Supplier<net.silentchaos512.gear.api.stats.ItemStat>> silentGearStat) {
        this.iconIndex = iconIndex;
        this.defaultValue = defaultValue;
        this.displayIfDefault = displayIfDefault;
        this.silentGearStat = silentGearStat;
    }

    public boolean shouldRender(EquipmentStats stats) {
        if (this.displayIfDefault) return true;
        float value = stats.getStat(this);
        return value > this.defaultValue;
    }
}
