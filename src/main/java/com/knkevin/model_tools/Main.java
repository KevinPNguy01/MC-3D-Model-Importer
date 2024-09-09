package com.knkevin.model_tools;

import com.knkevin.model_tools.commands.arguments.ModCommandArguments;
import com.knkevin.model_tools.items.ModItems;
import com.knkevin.model_tools.models.Model;
import com.knkevin.model_tools.packets.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(Main.MODID)
public class Main {
    @Nullable
    public static Model model;
    public static final String MODID = "model_tools";

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModCommandArguments.register(modEventBus);
        PacketHandler.init();
    }
}
