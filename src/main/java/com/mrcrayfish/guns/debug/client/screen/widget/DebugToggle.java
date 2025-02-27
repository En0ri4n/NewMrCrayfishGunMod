package com.mrcrayfish.guns.debug.client.screen.widget;

import com.mrcrayfish.guns.debug.IDebugWidget;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 *
 * Transformed and adapted as needed by: En0ri4n
 */
public class DebugToggle extends DebugButton implements IDebugWidget
{
    private boolean enabled;
    private final Consumer<Boolean> callback;

    public DebugToggle(boolean initialValue, Consumer<Boolean> callback)
    {
        super(TextComponent.EMPTY, btn -> ((DebugToggle) btn).toggle());
        this.enabled = initialValue;
        this.callback = callback;
        this.updateMessage();
    }

    private void toggle()
    {
        this.enabled = !this.enabled;
        this.updateMessage();
        this.callback.accept(this.enabled);
    }

    private void updateMessage()
    {
        this.setMessage(this.enabled ? new TextComponent("On") : new TextComponent("Off"));
    }
}
