package com.knkevin.model_tools.items;

import com.knkevin.model_tools.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * The Model Hammer is an item to be used when manipulating and viewing the loaded 3d model.
 * Players can right-click on a block to set the new position of the Model.
 */
public class ModelHammerItem extends Item {
    public ModelHammerItem(Item.Properties properties) {
        super(properties);
    }

    /**
     * Run when the player right-clicks on a block.
     * @param context The context for this event.
     * @return SUCCESS if there is a Model loaded, FAIL otherwise.
     */
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        if (Main.model == null || level.isClientSide) return InteractionResult.FAIL;
        BlockPos blockPos = context.getClickedPos();
        Vec3i normal = context.getClickedFace().getNormal();

        //Calculate the position to be one block off the clicked face, centered in that block position.
        Vector3f pos = new Vector3f(
                blockPos.getX() + normal.getX() + .5f,
                blockPos.getY() + normal.getY() + .5f,
                blockPos.getZ() + normal.getZ() + .5f
        );

        //The size of the Model.
        Vector3f size = new Vector3f(Main.model.maxCorner).mul(Main.model.scale);

        //Find the new minimum and maximum corners of the Model's bounding box after it has been rotated.
        Vector3f minCorner = new Vector3f();
        Vector3f maxCorner = new Vector3f();
        for (int mask = 0; mask < 8; ++mask) {
            Vector3f corner = new Vector3f(size.x, size.y, size.z).mul((mask & 1) == 1 ? -1 : 1, (mask & 2) == 2 ? -1 : 1, (mask & 4) == 4 ? -1 : 1).rotate(Main.model.rotation);
            minCorner.min(corner);
            maxCorner.max(corner);
        }

        //Offset the new position of the model from the clicked face so that it is just touching it.
        Direction direction = context.getClickedFace();
        switch (direction) {
            case UP -> pos.y -= (int) minCorner.y;
            case DOWN -> pos.y -= (int) maxCorner.y;
            case NORTH -> pos.z -= (int) maxCorner.z;
            case SOUTH -> pos.z -= (int) minCorner.z;
            case WEST -> pos.x -= (int) maxCorner.x;
            case EAST -> pos.x -= (int) minCorner.x;
        }
        Main.model.position.set(pos.x, pos.y, pos.z);

        Player player = context.getPlayer();
        if (player != null) player.sendSystemMessage(Component.literal("Set model position to " + pos.x + ", " + pos.y + ", " + pos.z));
        return InteractionResult.SUCCESS;
    }
}
