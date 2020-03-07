package com.github.mikesafonov.smpp.core.reciever;

/**
 * @author Mike Safonov
 */
class ResponseClientRebindTaskTest {

//    @Test
//    void shouldThrowNPE() {
//        assertThrows(NullPointerException.class, () -> new ResponseClientRebindTask(null));
//    }
//
//    @Test
//    void shouldReconnectIfSessionIsNull() {
//        ResponseClient responseClient = mock(ResponseClient.class);
//        ResponseClientRebindTask responseClientRebindTask = new ResponseClientRebindTask(responseClient);
//
//        when(responseClient.getSession()).thenReturn(null);
//
//        responseClientRebindTask.run();
//
//        verify(responseClient, times(1)).reconnect();
//    }
//
//    @Test
//    void shouldDoNothingIfSessionIsBinding() {
//        ResponseClient responseClient = mock(ResponseClient.class);
//        SmppSession smppSession = mock(SmppSession.class);
//        ResponseClientRebindTask responseClientRebindTask = new ResponseClientRebindTask(responseClient);
//
//        when(responseClient.getSession()).thenReturn(smppSession);
//        when(smppSession.isBinding()).thenReturn(true);
//        when(smppSession.isBound()).thenReturn(false);
//
//        responseClientRebindTask.run();
//
//        verify(responseClient, times(0)).reconnect();
//    }
//
//    @Test
//    void shouldDoNothingIfSessionIsBoundAndClientInProcess() {
//        ResponseClient responseClient = mock(ResponseClient.class);
//        SmppSession smppSession = mock(SmppSession.class);
//        ResponseClientRebindTask responseClientRebindTask = new ResponseClientRebindTask(responseClient);
//
//        when(responseClient.getSession()).thenReturn(smppSession);
//        when(responseClient.isInProcess()).thenReturn(true);
//        when(smppSession.isBinding()).thenReturn(false);
//        when(smppSession.isBound()).thenReturn(true);
//
//        responseClientRebindTask.run();
//
//        verify(responseClient, times(0)).reconnect();
//    }
//
//    @Test
//    void shouldReconnectIfSessionIsBoundAndClientNotInProcess() {
//        ResponseClient responseClient = mock(ResponseClient.class);
//        SmppSession smppSession = mock(SmppSession.class);
//        ResponseClientRebindTask responseClientRebindTask = new ResponseClientRebindTask(responseClient);
//
//        when(responseClient.getSession()).thenReturn(smppSession);
//        when(responseClient.isInProcess()).thenReturn(false);
//        when(smppSession.isBinding()).thenReturn(false);
//        when(smppSession.isBound()).thenReturn(true);
//
//        responseClientRebindTask.run();
//
//        verify(responseClient, times(1)).reconnect();
//    }
//
//    @Test
//    void shouldReconnectIfSessionNotBoundAndNotBinding() {
//        ResponseClient responseClient = mock(ResponseClient.class);
//        SmppSession smppSession = mock(SmppSession.class);
//        ResponseClientRebindTask responseClientRebindTask = new ResponseClientRebindTask(responseClient);
//
//        when(responseClient.getSession()).thenReturn(smppSession);
//        when(smppSession.isBinding()).thenReturn(false);
//        when(smppSession.isBound()).thenReturn(false);
//
//        responseClientRebindTask.run();
//
//        verify(responseClient, times(1)).reconnect();
//    }
//

}
