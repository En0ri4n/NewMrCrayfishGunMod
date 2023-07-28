package com.mrcrayfish.guns.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mrcrayfish.guns.common.config.GunConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
        GunConfigs.load(commandSender.getSource().getServer());
        return 1;
    }
}
