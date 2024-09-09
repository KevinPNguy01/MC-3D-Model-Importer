package com.knkevin.model_tools.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * An ArgumentType for specifying a model file to load.
 */
public class ModelFileArgument implements ArgumentType<String> {
    /**
     * @return A new ModelFileArgument.
     */
    public static ModelFileArgument modelFileArgument() {
        return new ModelFileArgument();
    }

    /**
     * Searches through the given directory for valid model files.
     * @param directory The directory to check for model files in.
     * @return A List of Strings representing the model files in the directory.
     */
    public static List<String> getModelFiles(File directory) {
        List<String> fileList = new ArrayList<>();
        if (directory.isDirectory())
            for (String path: Objects.requireNonNull(directory.list())) {
                path = path.toLowerCase();
                if (path.endsWith(".stl") || path.endsWith(".obj"))
                    fileList.add(path);
            }
        return fileList;
    }

    /**
     * @return The argument as a String if it matched a valid case.
     */
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    /**
     * Lists the valid model files in the "models" folder.
     */
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_259767_, SuggestionsBuilder p_259515_) {
        return p_259767_.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(getModelFiles(new File("models")).stream(), p_259515_) : Suggestions.empty();
    }
}
