package net.silentchaos512.equiptooltips;

import static net.silentchaos512.equiptooltips.ItemStat.*;

public enum ItemType {
    ARMOR("armor", DURABILITY, ARMOR_PROTECTION, ARMOR_TOUGHNESS),
    AXE("axe", DURABILITY, HARVEST_SPEED, MELEE_DAMAGE, MELEE_SPEED),
    BOW("bow", DURABILITY, RANGED_DAMAGE, RANGED_SPEED),
    FISHING_ROD("fishing_rod", DURABILITY),
    GENERIC_DAMAGEABLE("generic_damageable", DURABILITY),
    GENERIC_HARVEST("generic_harvest", DURABILITY, HARVEST_SPEED),
    HOE("hoe", DURABILITY),
    PICKAXE("pickaxe", DURABILITY, HARVEST_LEVEL, HARVEST_SPEED),
    SHEARS("shears", DURABILITY, HARVEST_SPEED),
    //        SHIELD("shield", DURABILITY, MAGIC_PROTECTION),
    SHOVEL("shovel", DURABILITY, HARVEST_LEVEL, HARVEST_SPEED),
    SWORD("sword", DURABILITY, MELEE_DAMAGE, MAGIC_DAMAGE, MELEE_SPEED),
    SGEAR_PART("sgear_part", DURABILITY, HARVEST_LEVEL, HARVEST_SPEED, MELEE_DAMAGE, ARMOR_PROTECTION),
    UNKNOWN("unknown", DURABILITY);

    public final String key;
    public final ItemStat[] displayStats;

    ItemType(String key, ItemStat... itemStats) {
        this.key = key;
        this.displayStats = itemStats;
    }
}
