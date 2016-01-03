package jeresources.jei.ore;

import jeresources.api.utils.ColorHelper;
import jeresources.api.utils.conditionals.Conditional;
import jeresources.config.Settings;
import jeresources.jei.JEIConfig;
import jeresources.reference.Resources;
import jeresources.utils.Font;
import jeresources.utils.RenderHelper;
import jeresources.utils.TranslationHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class JEIOreCategory implements IRecipeCategory
{
    private static final int X_OFFSPRING = 59;
    private static final int Y_OFFSPRING = 52;
    private static final int X_AXIS_SIZE = 90;
    private static final int Y_AXIS_SIZE = 40;
    private static final int X_ITEM = 8;
    private static final int Y_ITEM = 6;

    private static int CYCLE_TIME = (int) (20 * Settings.CYCLE_TIME);

    public static void reloadSettings()
    {
        CYCLE_TIME = (int) (20 * Settings.CYCLE_TIME);
    }

    @Override
    public void drawExtras(int recipe)
    {

    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe)
    {
        currenttip = super.handleTooltip(gui, currenttip, recipe);

        if (GuiContainerManager.shouldShowTooltip(gui) && currenttip.size() == 0)
        {
            Point offset = gui.getRecipePosition(recipe);
            Point pos = GuiDraw.getMousePosition();
            Point relMouse = new Point(pos.x - gui.guiLeft - offset.x, pos.y - gui.guiTop - offset.y);
            // Check if we are inside the coordinate system
            if (relMouse.x > X_OFFSPRING && relMouse.x < X_OFFSPRING + X_AXIS_SIZE &&
                relMouse.y > Y_OFFSPRING - Y_AXIS_SIZE && relMouse.y < Y_OFFSPRING)
            {
                OreWrapper cachedOre = (OreWrapper) arecipes.get(recipe);
                float[] chances = cachedOre.oreMatchEntry.getChances();
                double space = X_AXIS_SIZE / (chances.length * 1D);
                // Calculate the hovered over y value
                int index = (int) ((relMouse.x - X_OFFSPRING) / space);
                int yValue = Math.max(0, index + cachedOre.oreMatchEntry.getMinY() - Settings.EXTRA_RANGE + 1);
                if (index >= 0 && index < chances.length)
                    currenttip.add("Y: " + yValue + String.format(" (%.2f%%)", chances[index] * 100));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe)
    {
        OreWrapper cachedOre = (OreWrapper) arecipes.get(recipe);
        if (stack != null && cachedOre.contains(stack))
        {
            if (cachedOre.oreMatchEntry.isSilkTouchNeeded(stack))
                currenttip.add(Conditional.silkTouch.toString());
            if (gui.isMouseOver(cachedOre.getResult(), recipe))
                currenttip.addAll(cachedOre.getRestrictions());
        }
        return currenttip;
    }

    @Nonnull
    @Override
    public String getUid()
    {
        return JEIConfig.ORE;
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return TranslationHelper.translateToLocal("ner.ore.title");
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return Resources.Gui.JeiBackground.ORE;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        RenderHelper.drawArrow(X_OFFSPRING, Y_OFFSPRING, X_OFFSPRING + X_AXIS_SIZE, Y_OFFSPRING, ColorHelper.GRAY);
        RenderHelper.drawArrow(X_OFFSPRING, Y_OFFSPRING, X_OFFSPRING, Y_OFFSPRING - Y_AXIS_SIZE, ColorHelper.GRAY);
        OreWrapper cachedOre = (OreWrapper) arecipes.get(recipe);
        float[] array = cachedOre.oreMatchEntry.getChances();
        double max = 0;
        for (double d : array)
            if (d > max) max = d;
        double xPrev = X_OFFSPRING;
        double yPrev = Y_OFFSPRING;
        double space = X_AXIS_SIZE / ((array.length - 1) * 1D);
        for (int i = 0; i < array.length; i++)
        {
            double value = array[i];
            double y = Y_OFFSPRING - ((value / max) * Y_AXIS_SIZE);
            if (i > 0) // Only draw a line after the first element (cannot draw line with only one point)
            {
                double x = xPrev + space;
                RenderHelper.drawLine(xPrev, yPrev, x, y, cachedOre.getLineColor());
                xPrev = x;
            }
            yPrev = y;
        }

        Font.small.print("0%", X_OFFSPRING - 10, Y_OFFSPRING - 7);
        Font.small.print(String.format("%.2f", max * 100) + "%", X_OFFSPRING - 20, Y_OFFSPRING - Y_AXIS_SIZE);
        int minY = cachedOre.oreMatchEntry.getMinY() - Settings.EXTRA_RANGE;
        Font.small.print(minY < 0 ? 0 : minY, X_OFFSPRING - 3, Y_OFFSPRING + 2);
        int maxY = cachedOre.oreMatchEntry.getMaxY() + Settings.EXTRA_RANGE;
        Font.small.print(maxY > 255 ? 255 : maxY, X_OFFSPRING + X_AXIS_SIZE, Y_OFFSPRING + 2);
        Font.small.print(TranslationHelper.translateToLocal("ner.ore.bestY") + ": " + cachedOre.oreMatchEntry.getBestY(), X_ITEM - 2, Y_ITEM + 20);
    }

    @Override
    public void drawAnimations(Minecraft minecraft)
    {

    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper)
    {

    }

}