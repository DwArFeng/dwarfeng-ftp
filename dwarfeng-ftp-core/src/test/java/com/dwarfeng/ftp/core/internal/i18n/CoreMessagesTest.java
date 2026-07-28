package com.dwarfeng.ftp.core.internal.i18n;

import com.dwarfeng.ftp.base.sdk.i18n.MessageContext;
import com.dwarfeng.ftp.base.sdk.i18n.Messages;
import com.dwarfeng.ftp.base.stack.i18n.MessageCatalog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CoreMessages} 的单元测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class CoreMessagesTest {

    @Test
    void resolvesSimplifiedChineseMessagesFromEveryCatalog() {
        assertEquals(
                "FTP 文件操作失败",
                resolveWith(Locale.SIMPLIFIED_CHINESE, CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_FAILED)
        );
    }

    @Test
    void resolvesEnglishFallbackMessagesFromEveryCatalog() {
        assertEquals(
                "ftp file failed",
                CoreMessages.message(Locale.US, CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_FAILED)
        );
        assertEquals(
                "ftp connect failed",
                resolveWith(Locale.US, CoreMessageKey.SERVICE_EXCEPTION_FTP_CONNECT_FAILED)
        );
    }

    @Test
    void degradesMissingMessageKeyForEveryCatalog() {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            assertEquals(
                    "!unknown.message.key!",
                    Messages.resolve(catalog.messageCatalog(), "unknown.message.key", Locale.US)
            );
        }
    }

    @Test
    void keepsMessageKeyResourcesAlignedByCatalog() throws IOException {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            var catalogKeys = Arrays.stream(CoreMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .toList();
            Set<String> enumKeys = catalogKeys.stream()
                    .map(CoreMessageKey::key)
                    .collect(Collectors.toUnmodifiableSet());
            assertEquals(catalogKeys.size(), enumKeys.size(), () -> "Duplicate message key in catalog: " + catalog);
            assertEquals(enumKeys, loadKeys(catalog, ".properties"));
            assertEquals(enumKeys, loadKeys(catalog, "_zh_CN.properties"));
        }
    }

    @Test
    void cachesOneMessageCatalogPerCatalog() {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            MessageCatalog messageCatalog = catalog.messageCatalog();
            Arrays.stream(CoreMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .forEach(key -> assertSame(messageCatalog, key.catalog().messageCatalog()));
        }
    }

    private String resolveWith(Locale locale, CoreMessageKey key, Object... args) {
        return MessageContext.call(locale, () -> CoreMessages.message(key, args));
    }

    private Set<String> loadKeys(CoreMessages.Catalog catalog, String resourceSuffix) throws IOException {
        String resourceName = catalog.messageCatalog().baseName().replace('.', '/') + resourceSuffix;
        try (InputStream inputStream = CoreMessages.class.getModule().getResourceAsStream(resourceName)) {
            assertNotNull(inputStream, () -> "Missing message resource: " + resourceName);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return properties.stringPropertyNames();
        }
    }
}
