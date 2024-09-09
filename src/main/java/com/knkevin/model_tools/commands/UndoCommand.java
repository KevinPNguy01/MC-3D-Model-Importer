package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/**
 * Handles logic dealing with undoing the last placement of the loaded Model.
 */
public class UndoCommand {
    /**
     * Attempts to undo the last placement of the loaded Model.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int undo(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        Main.model.undo(command.getSource().getLevel());
        Objects.requireNonNull(command.getSource().getPlayer()).sendSystemMessage(Component.literal("Undo successful."));
        return 1;
    }
}
