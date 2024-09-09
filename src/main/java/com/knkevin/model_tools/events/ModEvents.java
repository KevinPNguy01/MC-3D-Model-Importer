package com.knkevin.model_tools.events;

import com.knkevin.model_tools.Main;
import com.knkevin.model_tools.commands.ModelCommand;
import com.knkevin.model_tools.items.ModItems;
import com.knkevin.model_tools.models.utils.Palette;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.server.command.ConfigCommand;

import java.io.File;

public class ModEvents {
    /**
     * Events fired on the Forge bus.
     */
    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModForgeEvents {
        @SubscribeEvent
        public static void registerCommands(final RegisterCommandsEvent event) {
            new ModelCommand(event.getDispatcher());
            ConfigCommand.register(event.getDispatcher());
        }
    }

    /**
     * Events fired on the Mod bus.
     */
    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModModEvents {
        @SubscribeEvent
        public static void commonSetup(final FMLCommonSetupEvent event) {
            //Palette.paletteToText(new File("1.19.4.jar"));
            Palette.loadPaletteFromText(Palette.fileName);
            File folder = new File("models");
            if (!folder.exists()) folder.mkdir();
        }

        @SubscribeEvent
        public static void addCreativeTab(CreativeModeTabEvent.BuildContents event) {
            if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
                event.accept(ModItems.MODEL_HAMMER);
        }
    }
}
