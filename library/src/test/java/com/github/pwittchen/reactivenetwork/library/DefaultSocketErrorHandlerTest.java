package com.github.pwittchen.reactivenetwork.library;

import com.github.pwittchen.reactivenetwork.library.internet.socket.DefaultSocketErrorHandler;
import com.github.pwittchen.reactivenetwork.library.internet.socket.SocketErrorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class DefaultSocketErrorHandlerTest {

  @Test public void shouldHandleErrorDuringClosingSocket() {
    // given
    SocketErrorHandler handler = spy(new DefaultSocketErrorHandler());
    Exception exception = new Exception("error during closing socket");

    // when
    handler.handleErrorDuringClosingSocket(exception);

    // then
    verify(handler, times(1)).handleErrorDuringClosingSocket(exception);
  }
}
