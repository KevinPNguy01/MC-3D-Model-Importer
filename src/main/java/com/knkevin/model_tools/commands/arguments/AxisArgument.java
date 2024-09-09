package com.knkevin.model_tools.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An ArgumentType for specifying which axis to apply a transformation on.
 */
public class AxisArgument implements ArgumentType<String> {
    /**
     * The 3 valid argument cases.
     */
    private static final Collection<String> EXAMPLES = List.of("x", "y", "z");

    /**
     * Thrown if the argument did not match a valid case.
     */
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((commandException) -> Component.literal("Invalid Axis"));

    /**
     * @return A new AxisArgument.
     */
    public static AxisArgument axisArg() {
        return new AxisArgument();
    }

    /**
     * @param command The command to get the argument from.
     * @param axis The name of the argument.
     * @return The argument as a String.
     */
    public static String getAxis(CommandContext<CommandSourceStack> command, String axis) {
        return command.getArgument(axis, String.class);
    }

    /**
     * @return The argument as a String if it matched a valid case.
     */
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        if (!EXAMPLES.contains(s)) throw ERROR_INVALID.createWithContext(reader, s);
        return s;
    }

    /**
     * Lists the valid cases provided in EXAMPLES.
     */
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> command, SuggestionsBuilder builder) {
        return command.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(EXAMPLES.stream(), builder) : Suggestions.empty();
    }
}
