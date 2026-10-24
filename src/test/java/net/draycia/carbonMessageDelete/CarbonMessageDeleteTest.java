package net.draycia.carbonMessageDelete;

import java.lang.reflect.Field;
import java.util.List;
import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.chat.SignedMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarbonMessageDeleteTest {
    @Test
    void absentAndSystemMessagesHaveNoDeletableSignature() {
        assertNull(CarbonMessageDelete.signature(null));
        assertNull(CarbonMessageDelete.signature(SignedMessage.system("unsigned", null)));
    }

    @Test
    void signedMessagesRetainTheirExactSignature() {
        SignedMessage message = mock(SignedMessage.class);
        SignedMessage.Signature signature = SignedMessage.signature(new byte[256]);
        when(message.signature()).thenReturn(signature);
        assertSame(signature, CarbonMessageDelete.signature(message));
    }

    @Test
    void absentSignatureDoesNotReachRecipients() throws Exception {
        CarbonChat carbon = mock(CarbonChat.class);
        CarbonMessageDelete plugin = plugin(carbon);
        assertDoesNotThrow(() -> plugin.deleteMessage(null));
        verifyNoInteractions(carbon);
    }

    @Test
    void signedDeletionStillReachesEveryRecipient() throws Exception {
        CarbonChat carbon = mock(CarbonChat.class, RETURNS_DEEP_STUBS);
        CarbonPlayer first = mock(CarbonPlayer.class);
        CarbonPlayer second = mock(CarbonPlayer.class);
        var server = carbon.server();
        doReturn(List.of(first, second)).when(server).players();
        SignedMessage.Signature signature = SignedMessage.signature(new byte[256]);
        plugin(carbon).deleteMessage(signature);
        verify(first).deleteMessage(same(signature));
        verify(second).deleteMessage(same(signature));
    }

    private CarbonMessageDelete plugin(CarbonChat carbon) throws Exception {
        CarbonMessageDelete plugin = mock(CarbonMessageDelete.class, CALLS_REAL_METHODS);
        Field field = CarbonMessageDelete.class.getDeclaredField("carbonChat");
        field.setAccessible(true);
        field.set(plugin, carbon);
        return plugin;
    }
}
