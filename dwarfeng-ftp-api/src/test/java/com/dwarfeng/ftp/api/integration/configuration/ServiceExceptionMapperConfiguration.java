package com.dwarfeng.ftp.api.integration.configuration;

import com.dwarfeng.subgrade.basic.impl.exception.MapServiceExceptionMapper;
import com.dwarfeng.subgrade.basic.sdk.exception.ServiceExceptionHelper;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 服务异常映射配置。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
@Configuration
public class ServiceExceptionMapperConfiguration {

    @Bean
    public MapServiceExceptionMapper mapServiceExceptionMapper() {
        Map<Class<? extends Exception>, Supplier<ServiceException.Code>> destination =
                ServiceExceptionHelper.putDefaultDestination(null);
        destination = com.dwarfeng.ftp.sdk.exception.ServiceExceptionHelper.putDefaultDestination(destination);
        destination = com.dwarfeng.springtelqos.sdk.exception.ServiceExceptionHelper.putDefaultDestination(destination);
        return new MapServiceExceptionMapper(
                destination, com.dwarfeng.subgrade.basic.sdk.exception.ServiceExceptionCodeSuppliers.UNDEFINED
        );
    }
}
