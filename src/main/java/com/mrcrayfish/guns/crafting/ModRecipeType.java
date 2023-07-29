package com.mrcrayfish.guns.crafting;

import net.minecraft.world.item.crafting.RecipeType;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class ModRecipeType
{
    public static final RecipeType<WorkbenchRecipe> WORKBENCH = RecipeType.register("cgm:workbench");

    // Does nothing but trigger loading of static fields
    public static void init() {}
}
