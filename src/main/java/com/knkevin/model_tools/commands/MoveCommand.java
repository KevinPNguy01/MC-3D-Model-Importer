package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.commands.arguments.DirectionArgument;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Handles logic dealing with moving the loaded Model through a command.
 */
public class MoveCommand {
    /**
     * Attempts to move the loaded Model in the direction of the player.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int move(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        float distance = FloatArgumentType.getFloat(command, "distance");
        Player player = command.getSource().getPlayer();
        if (player == null) return 0;
        Main.model.move(player.getDirection(), distance);
        Component message = Component.literal("Moved model by " + distance + " blocks.");
        command.getSource().sendSystemMessage(message);
        return 1;
    }

    /**
     * Attempts to move the loaded Model in the direction specified by the command.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int moveDirection(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        float distance = FloatArgumentType.getFloat(command, "distance");
        String directionString = DirectionArgument.getAxis(command, "direction");
        Direction direction = Direction.NORTH;
        switch (directionString) {
            case "east" -> direction = Direction.EAST;
            case "south" -> direction = Direction.SOUTH;
            case "west" -> direction = Direction.WEST;
            case "up" -> direction = Direction.UP;
            case "down" -> direction = Direction.DOWN;
        }
        Main.model.move(direction, distance);
        Component message = Component.literal("Moved model by " + distance + " blocks.");
        command.getSource().sendSystemMessage(message);
        return 1;
    }
}
