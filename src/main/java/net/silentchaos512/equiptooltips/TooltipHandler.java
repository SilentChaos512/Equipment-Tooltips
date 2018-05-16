package net.silentchaos512.equiptooltips;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.equiptooltips.EquipmentStats.ItemStat;
import net.silentchaos512.equiptooltips.EquipmentStats.ItemType;

public class TooltipHandler extends Gui {

  public static final ResourceLocation TEXTURE = new ResourceLocation(EquipmentTooltips.MOD_ID, "textures/gui/hud.png");

  public static final TooltipHandler instance = new TooltipHandler();

  private int lastWidth = 0;
  public final boolean isTinkersLoaded;
  public final boolean isSilentsGemsLoaded;

  public TooltipHandler() {

    this.isTinkersLoaded = Loader.isModLoaded("tconstruct");
    this.isSilentsGemsLoaded = Loader.isModLoaded("silentgems");
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onRenderTooltip(RenderTooltipEvent.PostText event) {

    if (Config.DISPLAY_CHECK_KEY) {
      boolean shiftPressed = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
      if ((Config.DISPLAY_HIDE_ON_KEY && shiftPressed) || (!Config.DISPLAY_HIDE_ON_KEY && !shiftPressed))
        return;
    }

    ItemStack stack = event.getStack();
    if (stack.isEmpty())
      return;

    EquipmentStats hoveredStats = new EquipmentStats(stack);
    if (hoveredStats.getItemType() == ItemType.UNKNOWN)
      return;

    renderBackground(event);

    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = event.getFontRenderer();

    // Get currently equipped item
    ItemStack currentEquip = ItemStack.EMPTY;
    if (hoveredStats.getItemType() == ItemType.ARMOR) {
      EntityEquipmentSlot slot = ((ItemArmor) stack.getItem()).armorType;
      for (ItemStack itemstack : mc.player.getArmorInventoryList())
        if (!itemstack.isEmpty() && ((ItemArmor) itemstack.getItem()).armorType == slot)
          currentEquip = itemstack;
    } else {
      currentEquip = mc.player.getHeldItemMainhand();
    }

    EquipmentStats equippedStats = currentEquip.isEmpty() ? null : new EquipmentStats(currentEquip);

    double scale = 0.75;
    int x = (int) ((event.getX() + Config.POSITION_X_OFFSET) / scale);
    int y = (int) ((event.getY() - 16 + Config.POSITION_Y_OFFSET) / scale);
    if (Config.POSITION_ON_BOTTOM)
      y += event.getHeight() / scale + 28;

    GlStateManager.pushMatrix();
    GlStateManager.color(1f, 1f, 1f, 1f);
    GlStateManager.scale(scale, scale, scale);

    mc.renderEngine.bindTexture(TEXTURE);

    // Render stats
    for (ItemStat stat : hoveredStats.getItemType().displayStats)
      if (stat.shouldRender(hoveredStats))
        x = renderStat(mc, fontRenderer, x, y, stat, hoveredStats, equippedStats);

    lastWidth = (int) (x * scale - event.getX());

    GlStateManager.popMatrix();
  }

  private int renderStat(Minecraft mc, FontRenderer fontRenderer, int x, int y, ItemStat stat,
      EquipmentStats hovered, @Nullable EquipmentStats equipped) {

    if (!stat.shouldRender(hovered))
      return x;

    mc.renderEngine.bindTexture(TEXTURE);

    // Draw stat icon
    drawTexturedModalRect(x, y, 16 * stat.iconIndex, 240, 16, 16);
    x += 18;

    float hoveredStat = hovered.getStat(stat);
    float equippedStat = equipped == null ? stat.defaultValue : equipped.getStat(stat);

    // Draw stat value
    String text = formatStat(hoveredStat);
    fontRenderer.drawStringWithShadow(text, x, y + 5, 0xFFFFFF);
    x += fontRenderer.getStringWidth(text);
    mc.renderEngine.bindTexture(TEXTURE);

    // Draw comparison arrow (if appropriate)
    if (equipped == null || hovered.getItemType() != equipped.getItemType()) {
      x += 5;
    } else if (hoveredStat > equippedStat) {
      // Up arrow
      drawTexturedModalRect(x, y, 224, 240, 16, 16);
      x += 18;
    } else if (hoveredStat < equippedStat) {
      // Down arrow
      drawTexturedModalRect(x, y, 240, 240, 16, 16);
      x += 18;
    } else {
      // Dash
      drawTexturedModalRect(x, y, 208, 240, 16, 16);
      x += 18;
    }

    return x;
  }

  public static final String FORMAT_INT = "%d";
  public static final String FORMAT_FLOAT = "%.2f";

  private String formatStat(float value) {

    if (value == (int) value)
      return String.format(FORMAT_INT, (int) value);
    return String.format(FORMAT_FLOAT, value);
  }

  private void renderBackground(RenderTooltipEvent.PostText event) {

    final int backgroundColor = 0xC0100010;
    int left = event.getX() - 1 + Config.POSITION_X_OFFSET;
    int top = event.getY() - 17 + Config.POSITION_Y_OFFSET;
    int right = Math.max(event.getX() + lastWidth, event.getX() + event.getWidth() + 1) + Config.POSITION_X_OFFSET;
    int bottom = event.getY() - 4 + Config.POSITION_Y_OFFSET;
    if (Config.POSITION_ON_BOTTOM) {
      top += event.getHeight() + 21;
      bottom += event.getHeight() + 21;
    }
    drawRect(left, top, right, bottom, backgroundColor);
  }
}
