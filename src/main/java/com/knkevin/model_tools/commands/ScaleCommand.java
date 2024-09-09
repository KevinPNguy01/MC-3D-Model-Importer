package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.commands.arguments.ApplySetArgument;
import com.knkevin.model_tools.commands.arguments.AxisArgument;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Handles logic dealing with scaling the loaded Model through a command.
 */
public class ScaleCommand {
    /**
     * Attempts to scale the loaded Model by the amount specified by the command.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int scale(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        float xScale = FloatArgumentType.getFloat(command, "x-scale"), yScale = FloatArgumentType.getFloat(command, "y-scale"), zScale = FloatArgumentType.getFloat(command, "z-scale");
        String applySet = ApplySetArgument.getApplySet(command, "applySet");
        switch (applySet) {
            case "apply" -> {
                Main.model.applyScale(xScale, yScale, zScale);
                Component message = Component.literal("Scaled model by [" + xScale + "," + yScale + "," + zScale + "].");
                command.getSource().sendSystemMessage(message);
            }
            case "set" -> Main.model.setScale(xScale, yScale, zScale);
        }
        return success(command);
    }

    /**
     * Attempts to scale the loaded Model by the axis and amount specified by the command..
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int scaleAxis(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        String axis = AxisArgument.getAxis(command, "axis");
        String applySet = ApplySetArgument.getApplySet(command, "applySet");
        float scale = FloatArgumentType.getFloat(command, "scale");
        switch (applySet) {
            case "apply" -> {
                Main.model.applyAxisScale(axis, scale);
                Component message = Component.literal("Scaled " + axis + "-scale by " + scale + ".");
                command.getSource().sendSystemMessage(message);
            }
            case "set" -> {
                Main.model.setAxisScale(axis, scale);
                Component message = Component.literal("Set " + axis + "-scale to " + scale + ".");
                command.getSource().sendSystemMessage(message);
            }
        }
        return ScaleCommand.success(command);
    }

    protected static int scaleAll(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        String applySet = ApplySetArgument.getApplySet(command, "applySet");
        float scale = FloatArgumentType.getFloat(command, "scale");
        switch (applySet) {
            case "apply" -> {
                Main.model.applyScale(scale);
                Component message = Component.literal("Scaled by " + scale + ".");
                command.getSource().sendSystemMessage(message);
            }
            case "set" -> {
                Main.model.setScale(scale);
                Component message = Component.literal("Set scale to " + scale + ".");
                command.getSource().sendSystemMessage(message);
            }
        }
        return ScaleCommand.success(command);
    }

    private static int success(CommandContext<CommandSourceStack> command) {
        if (Main.model == null) return ModelCommand.noModelLoaded(command);
        Component message = Component.literal("Model scale set to " + Main.model.scale + ".");
        command.getSource().sendSystemMessage(message);
        return 1;
    }
}
