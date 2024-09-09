package com.knkevin.model_tools.key_bindings;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.items.ModItems;
import com.knkevin.model_tools.packets.PacketHandler;
import com.knkevin.model_tools.packets.PlaceModelPacket;
import com.knkevin.model_tools.packets.UndoModelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import org.joml.Vector3f;

import static com.knkevin.model_tools.items.HammerModes.*;
import static com.knkevin.model_tools.key_bindings.KeyBindings.*;

public class KeyActions {



    public static void mouseScrollEvent(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.getMainHandItem().getItem().equals(ModItems.MODEL_HAMMER.get()) || Main.model == null) return;
        handleMouseScroll((float) event.getScrollDelta());
        event.setCanceled(true);
    }

    public static void checkKeys() {
        Player player = Minecraft.getInstance().player;
        boolean holdingHammer = player != null && player.getMainHandItem().getItem().equals(ModItems.MODEL_HAMMER.get());
        boolean modelLoaded = Main.model != null;
        if (PLACE_KEY.consumeClick() && holdingHammer && modelLoaded) placeModel();
        if (UNDO_KEY.consumeClick() && holdingHammer && modelLoaded) undoModel();
        if (ROTATE_KEY.consumeClick() && holdingHammer) setModeRotate(player);
        if (SCALE_KEY.consumeClick() && holdingHammer) setModeScale(player);
        if (TRANSLATE_KEY.consumeClick() && holdingHammer) setModeTranslate(player);
        if (X_AXIS_KEY.consumeClick() && holdingHammer) setAxisX(player);
        if (Y_AXIS_KEY.consumeClick() && holdingHammer) setAxisY(player);
        if (Z_AXIS_KEY.consumeClick() && holdingHammer) setAxisZ(player);
        if (TOGGLE_VIEW_KEY.consumeClick() && holdingHammer) toggleViewMode(player);
    }

    public static void placeModel() {
        PacketHandler.INSTANCE.sendToServer(new PlaceModelPacket());
    }

    public static void undoModel() {
        PacketHandler.INSTANCE.sendToServer(new UndoModelPacket());
    }

    public static void setModeRotate(Player player) {
        transformMode = TransformMode.ROTATE;
        selectedAxis = Axis.Y;
        player.sendSystemMessage(Component.literal("Set mode to rotation."));
    }

    public static void setModeScale(Player player) {
        transformMode = TransformMode.SCALE;
        selectedAxis = Axis.ALL;
        player.sendSystemMessage(Component.literal("Set mode to scaling."));
    }

    public static void setModeTranslate(Player player) {
        transformMode = TransformMode.TRANSLATE;
        selectedAxis = Axis.Y;
        player.sendSystemMessage(Component.literal("Set mode to translation."));
    }

    public static void setAxisX(Player player) {
        selectedAxis = Axis.X;
        player.sendSystemMessage(Component.literal("Set axis to x-axis."));
    }

    public static void setAxisY(Player player) {
        selectedAxis = Axis.Y;
        player.sendSystemMessage(Component.literal("Set axis to y-axis."));
    }

    public static void setAxisZ(Player player) {
        selectedAxis = Axis.Z;
        player.sendSystemMessage(Component.literal("Set axis to z-axis."));
    }

    public static void toggleViewMode(Player player) {
        if (Main.model == null) return;
        if (viewMode == ViewMode.BOX) {
            viewMode = ViewMode.BLOCKS;
            Main.model.applyScale(1);
            player.sendSystemMessage(Component.literal("Viewing blocks preview."));
        } else {
            viewMode = ViewMode.BOX;
            player.sendSystemMessage(Component.literal("Viewing box outline."));
        }
    }

    public static void handleMouseScroll(float value) {
        if (Main.model == null) return;
        switch (transformMode) {
            case TRANSLATE -> Main.model.move(selectedAxis.name, value);
            case ROTATE -> Main.model.applyAxisRotation(selectedAxis.name, 5 * value);
            case SCALE -> {
                Vector3f size = new Vector3f(Main.model.maxCorner).sub(Main.model.minCorner);
                Vector3f scale = new Vector3f();
                if (selectedAxis == Axis.ALL)
                    scale.set(2/size.get(size.maxComponent())*value);
                else
                    scale.setComponent(selectedAxis.component, 2/(Main.model.maxCorner.get(selectedAxis.component) - Main.model.minCorner.get(selectedAxis.component)) * value);
                Main.model.setScale(Main.model.scale.x + scale.x, Main.model.scale.y + scale.y, Main.model.scale.z + scale.z);
            }
        }
    }
}
