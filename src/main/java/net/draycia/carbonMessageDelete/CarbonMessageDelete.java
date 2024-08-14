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

public final class CarbonMessageDelete extends JavaPlugin {

    private CarbonChat carbonChat;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.carbonChat = CarbonChatProvider.carbonChat();

        this.carbonChat.eventHandler().subscribe(CarbonChatEvent.class, event -> {
            event.renderers().add(KeyedRenderer.keyedRenderer(Key.key("message_delete"), (sender, recipient, message, original) -> {
                if (!(recipient instanceof CarbonPlayer rPlayer) || !rPlayer.hasPermission("carbon.message_delete")) {
                    return message;
                }

                final Component prefix = getConfig().getComponent("button_format", MiniMessage.miniMessage()).clickEvent(ClickEvent.callback(player -> {
                    deleteMessage(event.signedMessage().signature());
                }));
                
                return Component.text("").append(prefix).append(message);
            }));
        });
    }

    @Override
    public void onDisable() {

    }

    public void deleteMessage(final SignedMessage.Signature signature) {
        for (final CarbonPlayer player : this.carbonChat.server().players()) {
            player.deleteMessage(signature);
        }
    }

}
