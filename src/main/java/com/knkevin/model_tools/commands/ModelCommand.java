package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.commands.arguments.ApplySetArgument;
import com.knkevin.model_tools.commands.arguments.ModelFileArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static com.knkevin.model_tools.commands.arguments.AxisArgument.axisArg;
import static com.knkevin.model_tools.commands.arguments.DirectionArgument.directionArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * The main class for the "model" command.
 */
public class ModelCommand {
    /**
     * Registers the "model" command and all of its subcommands.
     * @param dispatcher CommandDispatcher to register commands.
     */
    public ModelCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("model")
            .then(literal("load").then(argument("filename", ModelFileArgument.modelFileArgument()).executes(LoadCommand::load)))
            .then(literal("place").executes(PlaceCommand::place))
            .then(literal("undo").executes(UndoCommand::undo))
            .then(literal("scale").then(argument("applySet", ApplySetArgument.applySetArg())
                .then(argument("scale", floatArg()).executes(ScaleCommand::scaleAll))
                .then(argument("x-scale", floatArg()).then(argument("y-scale", floatArg()).then(argument("z-scale", floatArg()).executes(ScaleCommand::scale))))
                .then(argument("axis", axisArg()).then(argument("scale", floatArg()).executes(ScaleCommand::scaleAxis)))
            ))
            .then(literal("rotate")
                .then(argument("x-angle", floatArg()).then(argument("y-angle", floatArg()).then(argument("z-angle", floatArg()).executes(RotateCommand::rotate))))
                .then(argument("axis", axisArg()).then(argument("angle", floatArg()).executes(RotateCommand::rotateAxis))
            ))
            .then(literal("move")
                .then(argument("distance", floatArg()).executes(MoveCommand::move)
                    .then(argument("direction", directionArg()).executes(MoveCommand::moveDirection))
                )
            )
        );
    }

    /**
     * Runs when a command tried to execute without a loaded Model.
     * @param command The executed command.
     * @return 0
     */
    protected static int noModelLoaded(CommandContext<CommandSourceStack> command) {
        command.getSource().sendSystemMessage(Component.literal("Error: No model has been loaded."));
        return 0;
    }
}
