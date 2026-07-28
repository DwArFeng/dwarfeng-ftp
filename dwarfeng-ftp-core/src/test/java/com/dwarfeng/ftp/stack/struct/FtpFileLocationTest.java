package com.dwarfeng.ftp.stack.struct;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FtpFileLocation} 的单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class FtpFileLocationTest {

    @Test
    void shouldKeepClassSemanticsAndBeanAccessors() {
        String[] filePaths = {"directory", "subdirectory"};
        FtpFileLocation ftpFileLocation = new FtpFileLocation(filePaths, "file.txt");

        assertFalse(FtpFileLocation.class.isRecord());
        assertSame(filePaths, ftpFileLocation.getFilePaths());
        assertEquals("file.txt", ftpFileLocation.getFileName());
    }
}
