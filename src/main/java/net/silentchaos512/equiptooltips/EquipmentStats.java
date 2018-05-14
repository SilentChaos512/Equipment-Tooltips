package net.silentchaos512.equiptooltips;

import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.ARMOR_PROTECTION;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.ARMOR_TOUGHNESS;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.DURABILITY;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.HARVEST_LEVEL;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.HARVEST_SPEED;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.MAGIC_DAMAGE;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.MAGIC_PROTECTION;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.MELEE_DAMAGE;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.MELEE_SPEED;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.RANGED_DAMAGE;
import static net.silentchaos512.equiptooltips.EquipmentStats.ItemStat.RANGED_SPEED;

import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.silentchaos512.gems.api.ITool;
import net.silentchaos512.gems.item.tool.ItemGemBow;
import net.silentchaos512.gems.item.tool.ItemGemShield;
import net.silentchaos512.gems.util.ToolHelper;

public class EquipmentStats {

  private final ItemStack stack;

  @Getter(value = AccessLevel.PUBLIC)
  private ItemType itemType = ItemType.UNKNOWN;

  @Getter(value = AccessLevel.PUBLIC)
  private float armorProtection = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float armorToughness = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private int durability = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private int enchantability = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private int harvestLevel = -1;
  @Getter(value = AccessLevel.PUBLIC)
  private float harvestSpeed = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float magicDamage = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float magicProtection = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float meleeDamage = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float meleeSpeed = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float rangedDamage = 0;
  @Getter(value = AccessLevel.PUBLIC)
  private float rangedSpeed = 0;

  public EquipmentStats(@Nonnull ItemStack stack) {

    if (stack.isEmpty())
      throw new IllegalArgumentException("stack may not be invalid");

    this.stack = stack;
    Item item = stack.getItem();

    determineItemType(item);

    armorProtection = getProtection(stack);
    armorToughness = getToughness(stack);
    durability = stack.isItemStackDamageable() ? stack.getMaxDamage() : 0;
    enchantability = item.getItemEnchantability(stack);
    harvestLevel = getHarvestLevel(stack);
    harvestSpeed = getHarvestSpeed(stack);
    magicDamage = getMagicDamage(stack);
    magicProtection = TooltipHandler.instance.isSilentsGemsLoaded && item instanceof ItemGemShield
        ? 100f * ToolHelper.getMagicProtection(stack) : 0f;
    meleeDamage = getMeleeDamage(stack);
    meleeSpeed = getMeleeSpeed(stack);
    rangedDamage = TooltipHandler.instance.isSilentsGemsLoaded && item instanceof ItemGemBow
        ? 2f + ((ItemGemBow) item).getArrowDamage(stack) : 2f;
    rangedSpeed = TooltipHandler.instance.isSilentsGemsLoaded && item instanceof ItemGemBow
        ? ((ItemGemBow) item).getDrawSpeedForDisplay(stack) : 1f;
  }

  private int getHarvestLevel(ItemStack stack) {

    Item item = stack.getItem();

    if (TooltipHandler.instance.isTinkersLoaded && item instanceof slimeknights.tconstruct.library.tools.ToolCore)
      return slimeknights.tconstruct.library.utils.ToolHelper.getHarvestLevelStat(stack);

    if (!(item instanceof ItemTool))
      return -1;

    ItemTool itemTool = (ItemTool) item;
    IBlockState state = getBlockForTool(stack);
    int maxLevel = -1;
    // This doesn't work with all modded tools, but most.
    for (String toolClass : itemTool.getToolClasses(stack)) {
      int harvestLevel = itemTool.getHarvestLevel(stack, toolClass, null, state);
      maxLevel = Math.max(maxLevel, harvestLevel);
    }
    return maxLevel;
  }

  private float getHarvestSpeed(ItemStack stack) {

    Item item = stack.getItem();

    if (TooltipHandler.instance.isTinkersLoaded && item instanceof slimeknights.tconstruct.library.tools.ToolCore)
      return slimeknights.tconstruct.library.utils.ToolHelper.getMiningSpeedStat(stack);

    // Get an appropriate blockstate for the tool (assume stone if class is unknown).
    IBlockState state = getBlockForTool(stack);

    try {
      return item.getDestroySpeed(stack, state);
    } catch (NullPointerException ex) {
      return 0;
    }
  }

  private IBlockState getBlockForTool(ItemStack stack) {

    IBlockState state;
    Item item = stack.getItem();
    Set<String> toolClasses = item.getToolClasses(stack);

    if (item instanceof ItemSpade || toolClasses.contains("shovel"))
      state = Blocks.DIRT.getDefaultState();
    else if (item instanceof ItemAxe || toolClasses.contains("axe"))
      state = Blocks.LOG.getDefaultState();
    else if (item instanceof ItemShears)
      state = Blocks.WOOL.getDefaultState();
    else
      state = Blocks.STONE.getDefaultState();
    return state;
  }

  protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
  protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

  private float getMeleeDamage(ItemStack stack) {

    Multimap<String, AttributeModifier> multimap = stack
        .getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

    for (Entry<String, AttributeModifier> entry : multimap.entries()) {
      AttributeModifier mod = entry.getValue();
      if (mod.getID().equals(ATTACK_DAMAGE_MODIFIER)) {
        return (float) mod.getAmount() + 1f;
      }
    }

    return 0f;
  }

  private float getMagicDamage(ItemStack stack) {

    Item item = stack.getItem();
    if (TooltipHandler.instance.isSilentsGemsLoaded && item instanceof ITool && ((ITool) item).isCaster(stack)) {
      return ((ITool) item).getMagicDamage(stack) + 1f;
    }
    return 0f;
  }

  private float getMeleeSpeed(ItemStack stack) {

    Multimap<String, AttributeModifier> multimap = stack
        .getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

    for (Entry<String, AttributeModifier> entry : multimap.entries()) {
      AttributeModifier mod = entry.getValue();
      if (mod.getID().equals(ATTACK_SPEED_MODIFIER)) {
        return (float) mod.getAmount() + 4f;
      }
    }

    return 0f;
  }

  private static final UUID[] ARMOR_MODIFIERS = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
      UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };

  private float getProtection(ItemStack stack) {

    if (!(stack.getItem() instanceof ItemArmor))
      return 0f;

    ItemArmor itemArmor = (ItemArmor) stack.getItem();
    EntityEquipmentSlot slot = itemArmor.armorType;
    UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];

    Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(slot);

    for (Entry<String, AttributeModifier> entry : multimap.entries()) {
      String key = entry.getKey();
      AttributeModifier mod = entry.getValue();
      if (key.equals(SharedMonsterAttributes.ARMOR.getName()) && mod.getID().equals(uuid)) {
        return (float) mod.getAmount();
      }
    }

    return 0f;
  }

  private float getToughness(ItemStack stack) {

    if (!(stack.getItem() instanceof ItemArmor))
      return 0f;

    ItemArmor itemArmor = (ItemArmor) stack.getItem();
    EntityEquipmentSlot slot = itemArmor.armorType;
    UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];

    Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(slot);

    for (Entry<String, AttributeModifier> entry : multimap.entries()) {
      String key = entry.getKey();
      AttributeModifier mod = entry.getValue();
      if (key.equals(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName())
          && mod.getID().equals(uuid)) {
        return (float) mod.getAmount();
      }
    }

    return 0f;
  }

  private void determineItemType(Item item) {

    boolean isTinkersHarvestTool = TooltipHandler.instance.isTinkersLoaded
        && item instanceof slimeknights.tconstruct.library.tools.AoeToolCore;
    boolean isTinkersWeapon = TooltipHandler.instance.isTinkersLoaded
        && item instanceof slimeknights.tconstruct.library.tools.SwordCore;
    boolean isTinkersBow = TooltipHandler.instance.isTinkersLoaded
        && item instanceof slimeknights.tconstruct.library.tools.ranged.BowCore;

    // Get item type
    if (item instanceof ItemArmor)
      this.itemType = ItemType.ARMOR;
    else if (item instanceof ItemAxe)
      this.itemType = ItemType.AXE;
    else if (item instanceof ItemBow || isTinkersBow)
      this.itemType = ItemType.BOW;
    else if (item instanceof ItemFishingRod)
      this.itemType = ItemType.FISHING_ROD;
    else if (item instanceof ItemHoe)
      this.itemType = ItemType.HOE;
    else if (item instanceof ItemPickaxe || isTinkersHarvestTool)
      this.itemType = ItemType.PICKAXE;
    else if (item instanceof ItemShears)
      this.itemType = ItemType.SHEARS;
    else if (item instanceof ItemShield)
      this.itemType = ItemType.SHIELD;
    else if (item instanceof ItemSpade)
      this.itemType = ItemType.SHOVEL;
    else if (item instanceof ItemSword || isTinkersWeapon)
      this.itemType = ItemType.SWORD;
    else if (this.stack.isItemStackDamageable())
      this.itemType = ItemType.GENERIC_DAMAGEABLE;
    else
      this.itemType = ItemType.UNKNOWN;
  }

  public float getStat(ItemStat stat) {

    switch (stat) {
      case ARMOR_PROTECTION:
        return armorProtection;
      case ARMOR_TOUGHNESS:
        return armorToughness;
      case DURABILITY:
        return durability;
      case ENCHANTABILITY:
        return enchantability;
      case HARVEST_LEVEL:
        return harvestLevel;
      case HARVEST_SPEED:
        return harvestSpeed;
      case MAGIC_DAMAGE:
        return magicDamage;
      case MAGIC_PROTECTION:
        return magicProtection;
      case MELEE_DAMAGE:
        return meleeDamage;
      case MELEE_SPEED:
        return meleeSpeed;
      case RANGED_DAMAGE:
        return rangedDamage;
      case RANGED_SPEED:
        return rangedSpeed;
      default:
        throw new IllegalArgumentException("Unknown stat: " + stat.name());
    }
  }

  public static enum ItemStat {

    ARMOR_PROTECTION(6),
    ARMOR_TOUGHNESS(7, 0, false),
    DURABILITY(0, 0, false),
    ENCHANTABILITY(10),
    HARVEST_LEVEL(1, -1, false),
    HARVEST_SPEED(2),
    MAGIC_DAMAGE(4, 0, false),
    MAGIC_PROTECTION(9, 0, false),
    MELEE_DAMAGE(3),
    MELEE_SPEED(5),
    RANGED_DAMAGE(8),
    RANGED_SPEED(5);

    public final int iconIndex;
    public final float defaultValue;
    public final boolean displayIfDefault;

    private ItemStat(int iconIndex) {

      this(iconIndex, 0f, true);
    }

    private ItemStat(int iconIndex, float defaultValue, boolean displayIfDefault) {

      this.iconIndex = iconIndex;
      this.defaultValue = defaultValue;
      this.displayIfDefault = displayIfDefault;
    }

    public boolean shouldRender(EquipmentStats stats) {

      if (this.displayIfDefault)
        return true;
      float value = stats.getStat(this);
      return value > this.defaultValue;
    }
  }

  public static enum ItemType {

    ARMOR("armor", DURABILITY, ARMOR_PROTECTION, ARMOR_TOUGHNESS),
    AXE("axe", DURABILITY, HARVEST_SPEED, MELEE_DAMAGE, MELEE_SPEED),
    BOW("bow", DURABILITY, RANGED_DAMAGE, RANGED_SPEED),
    FISHING_ROD("fishing_rod", DURABILITY),
    GENERIC_DAMAGEABLE("generic_damageable", DURABILITY),
    HOE("hoe", DURABILITY),
    PICKAXE("pickaxe", DURABILITY, HARVEST_LEVEL, HARVEST_SPEED),
    SHEARS("shears", DURABILITY, HARVEST_SPEED),
    SHIELD("shield", DURABILITY, MAGIC_PROTECTION),
    SHOVEL("shovel", DURABILITY, HARVEST_LEVEL, HARVEST_SPEED),
    SWORD("sword", DURABILITY, MELEE_DAMAGE, MAGIC_DAMAGE, MELEE_SPEED),
    UNKNOWN("unknown", DURABILITY);

    public final String key;
    public final ItemStat[] displayStats;

    private ItemType(String key, ItemStat... itemStats) {

      this.key = key;
      this.displayStats = itemStats;
    }
  }
}
