package com.mrcrayfish.guns.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mrcrayfish.guns.common.config.WeaponConfigurations;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.server.command.ConfigCommand;

public class CreloadCommand extends ConfigCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("creload")
                .requires((commandSender) -> commandSender.hasPermission(4))
                .executes(CreloadCommand::reloadConfigs));
    }

    private static int reloadConfigs(CommandContext<CommandSourceStack> commandSender)
    {
        int[] infos = WeaponConfigurations.load(commandSender.getSource().getServer());
        commandSender.getSource().sendSuccess(new TranslatableComponent("commands.creload.success", infos[0], infos[1], infos[2]), true);
        return 1;
    }
}
