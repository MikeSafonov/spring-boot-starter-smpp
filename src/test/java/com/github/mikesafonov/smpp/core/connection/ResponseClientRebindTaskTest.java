package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ResponseClientRebindTaskTest {
    private SmppSession session;
    private SessionReconnector reconnector;
    private ResponseClientRebindTask rebindTask;

    @BeforeEach
    void setUp() {
        session = mock(SmppSession.class);
        reconnector = mock(SessionReconnector.class);
        rebindTask = new ResponseClientRebindTask(session, reconnector);
    }

    @Test
    void shouldReconnectWhenSessionIsNull() {
        rebindTask = new ResponseClientRebindTask(null, reconnector);

        rebindTask.run();

        verify(reconnector).reconnect();
    }

    @Test
    void shouldReconnectWhenSessionIsBound(){
        when(session.isBound()).thenReturn(true);

        rebindTask.run();

        verify(reconnector).reconnect();
    }

    @Test
    void shouldReconnectWhenSessionIsNotBoundAndIsNotBinding(){
        when(session.isBound()).thenReturn(false);
        when(session.isBinding()).thenReturn(false);

        rebindTask.run();

        verify(reconnector).reconnect();
    }

    @Test
    void shouldNotReconnectWhenSessionIsNotBinding(){
        when(session.isBound()).thenReturn(false);
        when(session.isBinding()).thenReturn(true);

        rebindTask.run();

        verifyNoInteractions(reconnector);
    }
}
