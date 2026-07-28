package com.dwarfeng.ftp.stack.struct;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FtpConfig} 的单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class FtpConfigTest {

    @Test
    void shouldKeepClassSemanticsAndBeanAccessors() {
        FtpConfig ftpConfig = new FtpConfig.Builder("localhost", "user", "password").build();

        assertFalse(FtpConfig.class.isRecord());
        assertEquals("localhost", ftpConfig.getHost());
        assertEquals(FtpConfig.Builder.DEFAULT_PORT, ftpConfig.getPort());
        assertEquals("user", ftpConfig.getUsername());
        assertEquals("password", ftpConfig.getPassword());
        assertEquals(FtpConfig.Builder.DEFAULT_SERVER_CHARSET, ftpConfig.getServerCharset());
    }

    @Test
    void constructorShouldKeepParameterValidation() {
        assertThrows(IllegalArgumentException.class, () -> new FtpConfig(
                "localhost", -1, "user", "password", FtpConfig.Builder.DEFAULT_SERVER_CHARSET,
                FtpConfig.Builder.DEFAULT_CONNECT_TIMEOUT, FtpConfig.Builder.DEFAULT_NOOP_INTERVAL,
                FtpConfig.Builder.DEFAULT_BUFFER_SIZE, FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH,
                FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_PREFIX, FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_SUFFIX,
                FtpConfig.Builder.DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE, FtpConfig.Builder.DEFAULT_DATA_CONNECTION_MODE,
                FtpConfig.Builder.DEFAULT_DATA_TIMEOUT,
                FtpConfig.Builder.DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST,
                FtpConfig.Builder.DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT
        ));
    }
}
