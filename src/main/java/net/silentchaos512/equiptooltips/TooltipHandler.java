package net.silentchaos512.equiptooltips;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.utils.MathUtils;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class TooltipHandler extends AbstractGui {
    public static final ResourceLocation TEXTURE = new ResourceLocation(EquipmentTooltips.MOD_ID, "textures/gui/hud.png");

    public static final TooltipHandler INSTANCE = new TooltipHandler();

    private int lastWidth = 0;
    public final boolean isTinkersLoaded;

    public TooltipHandler() {
        this.isTinkersLoaded = false; //Loader.isModLoaded("tconstruct");
    }

    private static boolean isShiftDown() {
        long handle = Minecraft.getInstance().mainWindow.getHandle();
        return InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTooltip(RenderTooltipEvent.PostText event) {
        if (Config.CLIENT.checkShiftKey.get()) {
            boolean shiftPressed = isShiftDown();
            boolean hideOnShift = Config.CLIENT.hideOnShift.get();
            if ((hideOnShift && shiftPressed) || (!hideOnShift && !shiftPressed)) {
                return;
            }
        }

        ItemStack stack = event.getStack();
        if (stack.isEmpty()) return;

        EquipmentStats hoveredStats = new EquipmentStats(stack);
        if (hoveredStats.getItemType() == ItemType.UNKNOWN) return;

        renderBackground(event);

        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = event.getFontRenderer();

        // Get currently equipped item
        ItemStack currentEquip = ItemStack.EMPTY;
        if (hoveredStats.getItemType() == ItemType.ARMOR) {
            // Armor slot (may not be "armor")
            EquipmentSlotType slot = ((ArmorItem) stack.getItem()).getEquipmentSlot();
            for (ItemStack itemstack : mc.player.getArmorInventoryList()) {
                Item item = itemstack.getItem();
                if (!itemstack.isEmpty() && item instanceof ArmorItem && ((ArmorItem) item).getEquipmentSlot() == slot) {
                    currentEquip = itemstack;
                }
            }
        } else if (hoveredStats.getItemType() != ItemType.SGEAR_PART) {
            // Tool or weapon
            currentEquip = mc.player.getHeldItemMainhand();
        }

        EquipmentStats equippedStats = currentEquip.isEmpty() ? null : new EquipmentStats(currentEquip);

        double scale = 0.75;
        int x = (int) ((event.getX() + Config.CLIENT.offsetX.get()) / scale);
        int y = (int) ((event.getY() - 16 + Config.CLIENT.offsetY.get()) / scale);
        if (Config.CLIENT.positionOnBottom.get()) {
            y += event.getHeight() / scale + 28;
        }

        GlStateManager.pushMatrix();
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.scaled(scale, scale, scale);

        mc.textureManager.bindTexture(TEXTURE);

        // Render stats
        for (ItemStat stat : hoveredStats.getItemType().displayStats) {
            if (stat.shouldRender(hoveredStats)) {
                x = renderStat(mc, fontRenderer, x, y, stat, hoveredStats, equippedStats);
            }
        }

        lastWidth = (int) (x * scale - event.getX());

        GlStateManager.popMatrix();
    }

    private int renderStat(Minecraft mc, FontRenderer fontRenderer, int x, int y, ItemStat stat, EquipmentStats hovered, @Nullable EquipmentStats equipped) {
        if (!stat.shouldRender(hovered)) return x;

        mc.textureManager.bindTexture(TEXTURE);

        // Draw stat icon
        blit(x, y, 16 * stat.iconIndex, 240, 16, 16);
        x += 18;

        float hoveredStat = hovered.getStat(stat);
        float equippedStat = equipped == null ? stat.defaultValue : equipped.getStat(stat);

        // Draw stat value
        String text = formatStat(hoveredStat);
        fontRenderer.drawStringWithShadow(text, x, y + 5, 0xFFFFFF);
        x += fontRenderer.getStringWidth(text);
        mc.textureManager.bindTexture(TEXTURE);

        // Draw comparison arrow (if appropriate)
        if (equipped == null || hovered.getItemType() != equipped.getItemType()) {
            x += 5;
        } else if (hoveredStat > equippedStat) {
            // Up arrow
            blit(x, y, 224, 240, 16, 16);
            x += 18;
        } else if (hoveredStat < equippedStat) {
            // Down arrow
            blit(x, y, 240, 240, 16, 16);
            x += 18;
        } else {
            // Dash
            blit(x, y, 208, 240, 16, 16);
            x += 18;
        }

        return x;
    }

    private static final String FORMAT_INT = "%d";
    private static final String FORMAT_FLOAT = "%.2f";

    private static String formatStat(float value) {
        if (MathUtils.doublesEqual(value, (int) value))
            return String.format(FORMAT_INT, (int) value);
        return String.format(FORMAT_FLOAT, value);
    }

    private void renderBackground(RenderTooltipEvent.PostText event) {
        int left = event.getX() - 1 + Config.CLIENT.offsetX.get();
        int top = event.getY() - 17 + Config.CLIENT.offsetY.get();
        int right = Math.max(event.getX() + lastWidth, event.getX() + event.getWidth() + 1) + Config.CLIENT.offsetX.get();
        int bottom = event.getY() - 4 + Config.CLIENT.offsetY.get();
        if (Config.CLIENT.positionOnBottom.get()) {
            top += event.getHeight() + 21;
            bottom += event.getHeight() + 21;
        }
        fill(left, top, right, bottom, Config.CLIENT.backgroundColor.get());
    }
}
