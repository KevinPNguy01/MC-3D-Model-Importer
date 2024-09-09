package com.knkevin.model_tools.commands;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.models.Model;
import com.knkevin.model_tools.models.ObjModel;
import com.knkevin.model_tools.models.StlAsciiModel;
import com.knkevin.model_tools.models.StlBinaryModel;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FilenameUtils;
import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles logic dealing with loading a new Model through a command.
 */
public class LoadCommand {
    /**
     * Attempts to load a Model from the file specified by the command.
     * @param command The executed command.
     * @return A 1 or 0 representing the success of the command.
     */
    protected static int load(CommandContext<CommandSourceStack> command) {
        if (!Minecraft.getInstance().isSingleplayer()) {
            command.getSource().sendSystemMessage(Component.literal("Error: Models can only be loaded in single-player!"));
            return 0;
        }
        try {
            String fileName = StringArgumentType.getString(command, "filename");
            Main.model = loadModel(new File("models/" + fileName));
            command.getSource().sendSystemMessage(Component.literal(fileName + " loaded successfully."));
            return 1;
        } catch (Exception e) {
            command.getSource().sendSystemMessage(Component.literal("Error: The model could not be loaded."));
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param file A File to a 3D model.
     * @return A Model constructed from the file.
     * @throws IOException The File could not be opened.
     * @throws ValueException The File was not an stl or obj file.
     */
    public static Model loadModel(File file) throws IOException, ValueException {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (extension.equals("obj"))
            return new ObjModel(file);
        if (extension.equals("stl")) {
            if (isStlAscii(file))
                return new StlAsciiModel(file);
            return new StlBinaryModel(file);
        }
        throw new ValueException("Error: The file is not a valid model type.");
    }

    /**
     * @param file A File to an stl model.
     * @return Whether the File is in ascii format.
     * @throws IOException The file could not be opened.
     */
    private static boolean isStlAscii(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            return br.readLine().strip().startsWith("solid");
        }
    }
}
