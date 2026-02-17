package yes.mediumdifficulty.elytratime;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.item.Items.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;


public class ElytraTimeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerEvents();
        registerKeybindings();

        ElytraTime.LOGGER.info("Initialised on client");
    }

    private static final KeyBinding.Category ELYTRA_CATEGORY =
        KeyBinding.Category.create(Identifier.of("elytratime", "controls"));

    private void registerKeybindings() {
        KeyBinding printTime = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.elytratime.show",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                ELYTRA_CATEGORY
        ));

        KeyBinding openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytratime.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                ELYTRA_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (printTime.wasPressed() && client.player != null) {
                var found = Util.findElytra(client.player);

                if (found.isPresent())
                    client.player.sendMessage(Text.literal(
                            Util.formatTimePercent(found.get(),ClientTextUtils.getTimeReportFormat(), ClientTextUtils.getTimeFormat(), MinecraftClient.getInstance().world))
                            .formatted(Formatting.GREEN),false);
                else
                    client.player.sendMessage(Text.translatable("message.elytratime.no_elytra").formatted(Formatting.RED),false);
            }

            if (openConfig.wasPressed()) {
                client.setScreen(ConfigMenu.build(client.currentScreen));
            }
        });
    }

    private static void registerEvents() {
        ItemTooltipCallback.EVENT.register((itemStack, context, type, lines) -> {
            if (itemStack.isOf(Items.ELYTRA) && ElytraTime.config.tooltipEnabled) {
                lines.add(1, Text.literal(
                        Util.formatTimePercent(itemStack, ClientTextUtils.getTooltipFormat(), ClientTextUtils.getTimeFormat(), MinecraftClient.getInstance().world))
                        .formatted(Formatting.GREEN));
            }
        });
    }
}
