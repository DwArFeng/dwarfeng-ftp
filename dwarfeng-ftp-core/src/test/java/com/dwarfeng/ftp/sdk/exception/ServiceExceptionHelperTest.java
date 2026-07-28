package com.dwarfeng.ftp.sdk.exception;

import com.dwarfeng.ftp.stack.exception.FtpConnectException;
import com.dwarfeng.ftp.stack.exception.FtpException;
import com.dwarfeng.subgrade.basic.impl.exception.MapServiceExceptionMapper;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ServiceExceptionHelper} 的单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class ServiceExceptionHelperTest {

    private static ServiceException.Code code(int code, String tip) {
        return new ServiceException.Code(code, tip);
    }

    @Test
    void shouldProvideDestinationsUsableByMapper() {
        Map<Class<? extends Exception>, Supplier<ServiceException.Code>> destination =
                ServiceExceptionHelper.putDefaultDestination(null);
        MapServiceExceptionMapper mapper = new MapServiceExceptionMapper(
                destination, () -> new ServiceException.Code(-1, "default")
        );

        ServiceException mappedException = mapper.map(new FtpConnectException());
        ServiceException.Code expectedCode = ServiceExceptionCodeSuppliers.FTP_CONNECT_FAILED.get();

        assertEquals(expectedCode.getCode(), mappedException.getCode().getCode());
        assertEquals(expectedCode.getTip(), mappedException.getCode().getTip());
    }

    @Test
    void shouldResolveDirectDestinationForEveryMapping() {
        AtomicInteger sequence = new AtomicInteger();
        Map<Class<? extends Exception>, Supplier<ServiceException.Code>> destination = new HashMap<>();
        destination.put(FtpConnectException.class, () -> code(sequence.incrementAndGet(), "direct"));
        MapServiceExceptionMapper mapper = new MapServiceExceptionMapper(destination, () -> code(-1, "default"));

        assertEquals(1, mapper.map(new FtpConnectException()).getCode().getCode());
        assertEquals(2, mapper.map(new FtpConnectException()).getCode().getCode());
    }

    @Test
    void shouldResolveCachedParentDestinationForEveryMapping() {
        AtomicInteger sequence = new AtomicInteger();
        Map<Class<? extends Exception>, Supplier<ServiceException.Code>> destination = new HashMap<>();
        destination.put(FtpException.class, () -> code(sequence.incrementAndGet(), "parent"));
        MapServiceExceptionMapper mapper = new MapServiceExceptionMapper(destination, () -> code(-1, "default"));

        assertEquals(1, mapper.map(new FtpConnectException()).getCode().getCode());
        assertEquals(2, mapper.map(new FtpConnectException()).getCode().getCode());
    }

    @Test
    void shouldResolveDefaultDestinationForEveryMapping() {
        AtomicInteger sequence = new AtomicInteger();
        MapServiceExceptionMapper mapper = new MapServiceExceptionMapper(
                new HashMap<>(), () -> code(sequence.incrementAndGet(), "default")
        );

        assertEquals(1, mapper.map(new Exception()).getCode().getCode());
        assertEquals(2, mapper.map(new Exception()).getCode().getCode());
    }

}
