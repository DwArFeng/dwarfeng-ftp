package com.dwarfeng.ftp.core.internal.i18n;

import static com.dwarfeng.ftp.core.internal.i18n.CoreMessages.Catalog.SDK;

/**
 * Core 模块消息键。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public enum CoreMessageKey {

    SERVICE_EXCEPTION_FTP_FAILED(SDK, "service_exception.ftp_failed"),
    SERVICE_EXCEPTION_FTP_CONNECT_FAILED(SDK, "service_exception.ftp_connect_failed"),
    SERVICE_EXCEPTION_FTP_LOGIN_FAILED(SDK, "service_exception.ftp_login_failed"),
    SERVICE_EXCEPTION_FTP_FILE_FAILED(SDK, "service_exception.ftp_file_failed"),
    SERVICE_EXCEPTION_FTP_FILE_RETRIEVE_FAILED(SDK, "service_exception.ftp_file_retrieve_failed"),
    SERVICE_EXCEPTION_FTP_FILE_STORE_FAILED(SDK, "service_exception.ftp_file_store_failed"),
    SERVICE_EXCEPTION_FTP_FILE_DELETE_FAILED(SDK, "service_exception.ftp_file_delete_failed"),
    SERVICE_EXCEPTION_FTP_FILE_NOT_EXISTS(SDK, "service_exception.ftp_file_not_exists"),
    SERVICE_EXCEPTION_FTP_STREAM_OPEN_FAILED(SDK, "service_exception.ftp_stream_open_failed"),
    SERVICE_EXCEPTION_FTP_HANDLER_STOPPED(SDK, "service_exception.ftp_handler_stopped"),
    SERVICE_EXCEPTION_FTP_QOS_FAILED(SDK, "service_exception.ftp_qos_failed"),
    SERVICE_EXCEPTION_AMBIGUOUS_FTP_HANDLER(SDK, "service_exception.ambiguous_ftp_handler"),
    SERVICE_EXCEPTION_NO_FTP_HANDLER_PRESENT(SDK, "service_exception.no_ftp_handler_present"),
    SERVICE_EXCEPTION_FTP_QOS_HANDLER_NOT_FOUND(SDK, "service_exception.ftp_qos_handler_not_found");

    private final CoreMessages.Catalog catalog;
    private final String key;

    CoreMessageKey(CoreMessages.Catalog catalog, String key) {
        this.catalog = catalog;
        this.key = key;
    }

    CoreMessages.Catalog catalog() {
        return catalog;
    }

    /**
     * 获取资源消息键。
     *
     * @return 资源消息键。
     */
    public String key() {
        return key;
    }
}
