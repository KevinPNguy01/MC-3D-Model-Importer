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
 * An ArgumentType for specifying either to apply or to set a transformation.
 */
public class ApplySetArgument implements ArgumentType<String> {
    /**
     * The 2 valid argument cases.
     */
    private static final Collection<String> EXAMPLES = List.of("apply", "set");

    /**
     * Thrown if the argument did not match a valid case.
     */
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_260119_) -> Component.literal("Invalid Argument"));

    /**
     * @return A new ApplySetArgument.
     */
    public static ApplySetArgument applySetArg() {
        return new ApplySetArgument();
    }

    /**
     * @param command The command to get the argument from.
     * @param applySet The name of the argument.
     * @return The argument as a String.
     */
    public static String getApplySet(CommandContext<CommandSourceStack> command, String applySet) {
        return command.getArgument(applySet, String.class);
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
