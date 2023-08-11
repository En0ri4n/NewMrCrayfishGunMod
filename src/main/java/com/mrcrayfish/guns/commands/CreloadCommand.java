package com.mrcrayfish.guns.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mrcrayfish.guns.common.config.GunConfigs;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.S2CMessageUpdateGunsAndAmmos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.network.PacketDistributor;
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
        PacketHandler.getPlayChannel().send(PacketDistributor.ALL.noArg(), new S2CMessageUpdateGunsAndAmmos());
        return 1;
    }
}
