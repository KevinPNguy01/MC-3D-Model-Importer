package com.knkevin.model_tools.key_bindings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {
    public static final String KEY_CATEGORY = "key.category.model_tools.3d_models";
    public static final String KEY_PLACE = "key.model_tools.place";
    public static final String KEY_UNDO = "key.model_tools.undo";
    public static final String KEY_SCALE = "key.model_tools.scale";
    public static final String KEY_ROTATE = "key.model_tools.rotate";
    public static final String KEY_TRANSLATE = "key.model_tools.translate";
    public static final String KEY_X_AXIS = "key.model_tools.x_axis";
    public static final String KEY_Y_AXIS = "key.model_tools.y_axis";
    public static final String KEY_Z_AXIS = "key.model_tools.z_axis";
    public static final String KEY_TOGGLE_VIEW = "key.model_tools.toggle_view";

    public static final KeyMapping PLACE_KEY = new KeyMapping(KEY_PLACE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, KEY_CATEGORY);
    public static final KeyMapping UNDO_KEY = new KeyMapping(KEY_UNDO, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, KEY_CATEGORY);
    public static final KeyMapping SCALE_KEY = new KeyMapping(KEY_SCALE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_S, KEY_CATEGORY);
    public static final KeyMapping ROTATE_KEY = new KeyMapping(KEY_ROTATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY);
    public static final KeyMapping TRANSLATE_KEY = new KeyMapping(KEY_TRANSLATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY);
    public static final KeyMapping X_AXIS_KEY = new KeyMapping(KEY_X_AXIS, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, KEY_CATEGORY);
    public static final KeyMapping Y_AXIS_KEY = new KeyMapping(KEY_Y_AXIS, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, KEY_CATEGORY);
    public static final KeyMapping Z_AXIS_KEY = new KeyMapping(KEY_Z_AXIS, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, KEY_CATEGORY);
    public static final KeyMapping TOGGLE_VIEW_KEY = new KeyMapping(KEY_TOGGLE_VIEW, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY);
}
