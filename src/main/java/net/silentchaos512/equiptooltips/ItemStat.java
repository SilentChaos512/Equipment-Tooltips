package net.silentchaos512.equiptooltips;

import net.silentchaos512.gear.api.stats.CommonItemStats;

import java.util.function.Supplier;

public enum ItemStat {
    ARMOR_PROTECTION(6, () -> () -> CommonItemStats.ARMOR),
    ARMOR_TOUGHNESS(7, 0, false, () -> () -> CommonItemStats.ARMOR_TOUGHNESS),
    DURABILITY(0, 0, false, () -> () -> CommonItemStats.DURABILITY),
    ENCHANTABILITY(10, () -> () -> CommonItemStats.ENCHANTABILITY),
    HARVEST_LEVEL(1, -1, false, () -> () -> CommonItemStats.HARVEST_LEVEL),
    HARVEST_SPEED(2, () -> () -> CommonItemStats.HARVEST_SPEED),
    MAGIC_DAMAGE(4, 0, false, () -> () -> CommonItemStats.MAGIC_DAMAGE),
    //MAGIC_PROTECTION(9, 0, false),
    MELEE_DAMAGE(3, () -> () -> CommonItemStats.MELEE_DAMAGE),
    MELEE_SPEED(5, () -> () -> CommonItemStats.ATTACK_SPEED),
    RANGED_DAMAGE(8, () -> () -> CommonItemStats.RANGED_DAMAGE),
    RANGED_SPEED(5, () -> () -> CommonItemStats.RANGED_SPEED);

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
