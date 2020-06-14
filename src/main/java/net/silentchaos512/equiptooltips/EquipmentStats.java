package net.silentchaos512.equiptooltips;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nonnull;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class EquipmentStats {
    private final ItemStack stack;

    @Getter private ItemType itemType = ItemType.UNKNOWN;

    @Getter private final float armorProtection;
    @Getter private final float armorToughness;
    @Getter private final int durability;
    @Getter private final int enchantability;
    @Getter private final int harvestLevel;
    @Getter private final float harvestSpeed;
    @Getter private final float magicDamage;
    @Getter private final float meleeDamage;
    @Getter private final float meleeSpeed;
    @Getter private final float rangedDamage;
    @Getter private final float rangedSpeed;

    public EquipmentStats(@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            throw new IllegalArgumentException("stack may not be invalid");

        this.stack = stack;
        Item item = stack.getItem();

        determineItemType(item);

        armorProtection = getProtection(stack);
        armorToughness = getToughness(stack);
        durability = getDurability(stack);
        enchantability = item.getItemEnchantability(stack);
        harvestLevel = getHarvestLevel(stack);
        harvestSpeed = getHarvestSpeed(stack);
        magicDamage = getMagicDamage(stack);
        meleeDamage = getMeleeDamage(stack);
        meleeSpeed = getMeleeSpeed(stack);
        rangedDamage = getRangedDamage(stack);
        rangedSpeed = getRangedSpeed(stack);
    }

    private int getDurability(@Nonnull ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return (int) SGearProxy.getStat(stack, ItemStat.DURABILITY.silentGearStat);
        return stack.isDamageable() ? stack.getMaxDamage() : 0;
    }

    private static int getHarvestLevel(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return (int) SGearProxy.getStat(stack, ItemStat.HARVEST_LEVEL.silentGearStat);

        Item item = stack.getItem();

//        if (TooltipHandler.instance.isTinkersLoaded && item instanceof slimeknights.tconstruct.library.tools.ToolCore)
//            return slimeknights.tconstruct.library.utils.ToolHelper.getHarvestLevelStat(stack);

        if (!(item instanceof ToolItem))
            return -1;

//        BlockState state = getBlockForTool(stack);
        int maxLevel = -1;
        // This doesn't work with all modded tools, but most.
        for (ToolType toolClass : ((IForgeItem) item).getToolTypes(stack)) {
            int harvestLevel = ((IForgeItem) item).getHarvestLevel(stack, toolClass, null, null);
            maxLevel = Math.max(maxLevel, harvestLevel);
        }
        return maxLevel;
    }

    private static float getHarvestSpeed(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.HARVEST_SPEED.silentGearStat);

        Item item = stack.getItem();

//        if (TooltipHandler.instance.isTinkersLoaded && item instanceof slimeknights.tconstruct.library.tools.ToolCore)
//            return slimeknights.tconstruct.library.utils.ToolHelper.getMiningSpeedStat(stack);

        // Get an appropriate blockstate for the tool (assume stone if class is unknown).
        BlockState state = getBlockForTool(stack);

        try {
            return item.getDestroySpeed(stack, state);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    private static BlockState getBlockForTool(ItemStack stack) {
        BlockState state;
        Item item = stack.getItem();
        Set<ToolType> toolClasses = item.getToolTypes(stack);

        //noinspection ChainOfInstanceofChecks
        if (item instanceof ShovelItem || toolClasses.contains(ToolType.SHOVEL))
            state = Blocks.DIRT.getDefaultState();
        else if (item instanceof AxeItem || toolClasses.contains(ToolType.AXE))
            state = Blocks.OAK_LOG.getDefaultState();
        else if (item instanceof ShearsItem)
            state = Blocks.WHITE_WOOL.getDefaultState();
        else
            state = Blocks.STONE.getDefaultState();
        return state;
    }

    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private static float getAttributeValue(ItemStack stack, EquipmentSlotType slot, UUID modifierId) {
        for (Entry<String, AttributeModifier> entry : stack.getAttributeModifiers(slot).entries()) {
            AttributeModifier mod = entry.getValue();
            if (mod != null && modifierId.equals(mod.getID())) {
                return (float) mod.getAmount();
            }
        }
        return 0;
    }

    private static float getAttributeValue(ItemStack stack, EquipmentSlotType slot, UUID modifierId, String modifierKey) {
        for (Entry<String, AttributeModifier> entry : stack.getAttributeModifiers(slot).entries()) {
            AttributeModifier mod = entry.getValue();
            if (mod != null && modifierId.equals(mod.getID()) && modifierKey.equals(entry.getKey())) {
                return (float) mod.getAmount();
            }
        }
        return 0;
    }

    private static float getMeleeDamage(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.MELEE_DAMAGE.silentGearStat);

        return getAttributeValue(stack, EquipmentSlotType.MAINHAND, ATTACK_DAMAGE_MODIFIER) + 1f;
    }

    private static float getMagicDamage(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.MAGIC_DAMAGE.silentGearStat);
        return 0;
    }

    private static float getMeleeSpeed(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.MELEE_SPEED.silentGearStat);

        return getAttributeValue(stack, EquipmentSlotType.MAINHAND, ATTACK_SPEED_MODIFIER) + 4f;
    }

    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };

    private static float getProtection(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.ARMOR_PROTECTION.silentGearStat);

        if (!(stack.getItem() instanceof ArmorItem)) return 0;

        ArmorItem armorItem = (ArmorItem) stack.getItem();
        EquipmentSlotType slot = armorItem.getEquipmentSlot();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];

        return getAttributeValue(stack, slot, uuid, SharedMonsterAttributes.ARMOR.getName());
    }

    private static float getToughness(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.ARMOR_TOUGHNESS.silentGearStat);

        if (!(stack.getItem() instanceof ArmorItem)) return 0f;

        ArmorItem armorItem = (ArmorItem) stack.getItem();
        EquipmentSlotType slot = armorItem.getEquipmentSlot();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];

        return getAttributeValue(stack, slot, uuid, SharedMonsterAttributes.ARMOR_TOUGHNESS.getName());
    }

    private static float getRangedDamage(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.RANGED_DAMAGE.silentGearStat);

        if (SGearProxy.isGearRangedWeapon(stack))
            return SGearProxy.getRangedDamage(stack);
        if (stack.getItem() instanceof BowItem)
            return 2;
        return 0;
    }

    private static float getRangedSpeed(ItemStack stack) {
        if (SGearProxy.isMainPart(stack))
            return SGearProxy.getStat(stack, ItemStat.RANGED_SPEED.silentGearStat);

        if (SGearProxy.isGearRangedWeapon(stack))
            return SGearProxy.getRangedSpeed(stack);
        if (stack.getItem() instanceof BowItem)
            return 1;
        return 0;
    }

    private void determineItemType(Item item) {
//        boolean isTinkersHarvestTool = TooltipHandler.instance.isTinkersLoaded
//                && item instanceof slimeknights.tconstruct.library.tools.AoeToolCore;
//        boolean isTinkersWeapon = TooltipHandler.instance.isTinkersLoaded
//                && item instanceof slimeknights.tconstruct.library.tools.SwordCore;
//        boolean isTinkersBow = TooltipHandler.instance.isTinkersLoaded
//                && item instanceof slimeknights.tconstruct.library.tools.ranged.BowCore;
        boolean isTinkersHarvestTool = false;
        boolean isTinkersWeapon = false;
        boolean isTinkersBow = false;

        Set<ToolType> toolTypes = item.getToolTypes(stack);

        // Get item type
        if (SGearProxy.isMainPart(stack))
            this.itemType = ItemType.SGEAR_PART;
        else if (item instanceof PickaxeItem || toolTypes.contains(ToolType.PICKAXE) || isTinkersHarvestTool)
            this.itemType = ItemType.PICKAXE;
        else if (item instanceof ShovelItem || toolTypes.contains(ToolType.SHOVEL))
            this.itemType = ItemType.SHOVEL;
        else if (item instanceof AxeItem || toolTypes.contains(ToolType.AXE))
            this.itemType = ItemType.AXE;
        else if (item instanceof SwordItem || isTinkersWeapon)
            this.itemType = ItemType.SWORD;
        else if (item instanceof BowItem || isTinkersBow)
            this.itemType = ItemType.BOW;
        else if (item instanceof ArmorItem)
            this.itemType = ItemType.ARMOR;
        else if (item instanceof HoeItem)
            this.itemType = ItemType.HOE;
        else if (item instanceof FishingRodItem)
            this.itemType = ItemType.FISHING_ROD;
        else if (item instanceof ShearsItem)
            this.itemType = ItemType.SHEARS;
//        else if (item instanceof ItemShield)
//            this.itemType = ItemType.SHIELD;
        else if (item instanceof ToolItem)
            this.itemType = ItemType.GENERIC_HARVEST;
        else if (this.stack.isDamageable())
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
//            case MAGIC_PROTECTION:
//                return magicProtection;
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

}
