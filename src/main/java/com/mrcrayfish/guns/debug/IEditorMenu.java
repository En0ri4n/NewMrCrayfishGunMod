package com.mrcrayfish.guns.debug;

import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public interface IEditorMenu
{
    Component getEditorLabel();

    void getEditorWidgets(List<Pair<Component, Supplier<IDebugWidget>>> widgets);
}
