package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.commands.arguments.AxisArgument;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Handles logic dealing with rotated the loaded Model through a command.
 */
public class RotateCommand {
    /**
     * Attempts to rotate the loaded Model by the angles specified by the command.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int rotate(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        float xAngle = FloatArgumentType.getFloat(command, "x-angle"), yAngle = FloatArgumentType.getFloat(command, "y-angle"), zAngle = FloatArgumentType.getFloat(command, "z-angle");
        Main.model.applyRotation(xAngle, yAngle, zAngle);
        Component message = Component.literal("Rotated model by [" + xAngle + "," + yAngle + "," + zAngle + "].");
        command.getSource().sendSystemMessage(message);
        return 1;
    }

    /**
     * Attempts to rotate the loaded Model by the axis and angle specified by the command.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int rotateAxis(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        String axis = AxisArgument.getAxis(command, "axis");
        float angle = FloatArgumentType.getFloat(command, "angle");
        Main.model.applyAxisRotation(axis, angle);
        Component message = Component.literal("Rotated around " + axis + "-axis by " + angle + " degrees.");
        command.getSource().sendSystemMessage(message);
        return 1;
    }
}
