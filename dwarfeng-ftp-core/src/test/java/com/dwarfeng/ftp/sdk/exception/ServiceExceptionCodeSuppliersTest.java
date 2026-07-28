package com.dwarfeng.ftp.sdk.exception;

import com.dwarfeng.ftp.base.sdk.i18n.MessageContext;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * {@link ServiceExceptionCodeSuppliers} 的单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class ServiceExceptionCodeSuppliersTest {

    private static final int DEFAULT_EXCEPTION_CODE_OFFSET = 20000;

    @AfterEach
    void tearDown() {
        ServiceExceptionCodeSuppliers.setExceptionCodeOffset(DEFAULT_EXCEPTION_CODE_OFFSET);
    }

    @Test
    void shouldResolveTipFromCurrentMessageContext() {
        ServiceException.Code englishCode = MessageContext.call(
                Locale.ENGLISH, ServiceExceptionCodeSuppliers.FTP_CONNECT_FAILED::get
        );
        ServiceException.Code chineseCode = MessageContext.call(
                Locale.SIMPLIFIED_CHINESE, ServiceExceptionCodeSuppliers.FTP_CONNECT_FAILED::get
        );

        assertEquals("ftp connect failed", englishCode.getTip());
        assertEquals("FTP 连接失败", chineseCode.getTip());
        assertNotSame(englishCode, chineseCode);
    }

    @Test
    void shouldOnlyApplyOffsetToNewCodeSnapshots() {
        ServiceException.Code originalCode = ServiceExceptionCodeSuppliers.FTP_FAILED.get();

        ServiceExceptionCodeSuppliers.setExceptionCodeOffset(DEFAULT_EXCEPTION_CODE_OFFSET + 1000);
        ServiceException.Code updatedCode = ServiceExceptionCodeSuppliers.FTP_FAILED.get();

        assertEquals(DEFAULT_EXCEPTION_CODE_OFFSET, originalCode.getCode());
        assertEquals(DEFAULT_EXCEPTION_CODE_OFFSET + 1000, updatedCode.getCode());
        assertNotSame(originalCode, updatedCode);
    }
}
