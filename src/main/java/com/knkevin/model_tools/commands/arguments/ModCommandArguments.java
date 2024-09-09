package com.knkevin.model_tools.commands.arguments;

import com.knkevin.model_tools.Main;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCommandArguments {
    public static DeferredRegister<ArgumentTypeInfo<?, ?>> argTypeRegistry = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Main.MODID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> AXIS_ARGUMENT =
            argTypeRegistry.register("axis_argument", () -> ArgumentTypeInfos.registerByClass(AxisArgument.class, SingletonArgumentInfo.contextFree(AxisArgument::new)));

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> APPLYSET_ARGUMENT =
            argTypeRegistry.register("apply_set_argument", () -> ArgumentTypeInfos.registerByClass(ApplySetArgument.class, SingletonArgumentInfo.contextFree(ApplySetArgument::new)));

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> MODELFILE_ARGUMENT =
            argTypeRegistry.register("model_file_argument", () -> ArgumentTypeInfos.registerByClass(ModelFileArgument.class, SingletonArgumentInfo.contextFree(ModelFileArgument::new)));

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> DIRECTION_ARGUMENT =
            argTypeRegistry.register("direction_argument", () -> ArgumentTypeInfos.registerByClass(DirectionArgument.class, SingletonArgumentInfo.contextFree(DirectionArgument::new)));

    public static void register(IEventBus eventbus) {
        argTypeRegistry.register(eventbus);
    }
}
