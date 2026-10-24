package net.draycia.carbonMessageDelete;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.KeyedRenderer;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CarbonMessageDelete extends JavaPlugin {

    private CarbonChat carbonChat;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.carbonChat = CarbonChatProvider.carbonChat();

        this.carbonChat.eventHandler().subscribe(CarbonChatEvent.class, 1000, false, event -> {
            event.renderers().add(KeyedRenderer.keyedRenderer(Key.key("message_delete"), (sender, recipient, message, original) -> {
                if (!(recipient instanceof CarbonPlayer rPlayer) || !rPlayer.hasPermission("carbon.message_delete")) {
                    return message;
                }

                final @Nullable Component prefix;

                if (signature(event.signedMessage()) == null) {
                    prefix = getConfig().getComponent("invalid_button_format", MiniMessage.miniMessage());
                } else {
                    prefix = getConfig().getComponent("button_format", MiniMessage.miniMessage()).clickEvent(ClickEvent.callback(player -> {
                        deleteMessage(signature(event.signedMessage()));
                    }));
                }

                if (prefix == null || Component.empty().equals(prefix)) {
                    return message;
                }

                return Component.text("").append(prefix).append(message);
            }));
        });
    }

    @Override
    public void onDisable() {

    }

    static SignedMessage.@Nullable Signature signature(final @Nullable SignedMessage message) {
        return message == null ? null : message.signature();
    }

    public void deleteMessage(final SignedMessage.@Nullable Signature signature) {
        if (signature == null) {
            return;
        }
        for (final CarbonPlayer player : this.carbonChat.server().players()) {
            player.deleteMessage(signature);
        }
    }

}
