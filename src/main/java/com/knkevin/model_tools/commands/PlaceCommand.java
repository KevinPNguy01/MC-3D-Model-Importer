package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Handles logic dealing with placing the loaded Model through a command.
 */
public class PlaceCommand {
    /**
     * Attempts to place the loaded Model.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int place(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        int blocksPlaced = Main.model.placeBlocks(command.getSource().getLevel());
        command.getSource().sendSystemMessage(Component.literal("Successfully placed " + blocksPlaced + " blocks."));
        return 1;
    }
}
