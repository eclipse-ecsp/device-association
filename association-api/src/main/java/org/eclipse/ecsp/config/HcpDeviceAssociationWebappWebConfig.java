/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.config;

import org.eclipse.ecsp.deviceassociation.springmvc.rest.support.ControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for the HcpDeviceAssociationWebappWebConfig.
 * This class configures the web application and handles exceptions, message converters, and resource handlers.
 */
@Configuration
@EnableWebMvc
public class HcpDeviceAssociationWebappWebConfig implements WebMvcConfigurer {

    /**
     * The constant representing the path for Swagger resources.
     * This is used to define the base path for accessing Swagger-related resources
     * within the application.
     */
    public static final String SWAGGER_RESOURCES_PATH = "swagger";

    /**
     * Configures the handler exception resolvers for the web application.
     * This method adds a custom exception resolver, ControllerExceptionHandler,
     * to handle unhandled exceptions in controllers.
     *
     * @param exceptionResolvers the list of handler exception resolvers
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        // Enabling handling of unhandled exception in controllers
        exceptionResolvers.add(new ControllerExceptionHandler());
    }

    /**
     * Configures the message converters used by the web application.
     *
     * @param converters the list of HttpMessageConverter instances
     */
    @Override
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(jsonConverter);
        //super.configureMessageConverters(converters);
    }

    /**
     * Adds resource handlers for serving Swagger resources.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + SWAGGER_RESOURCES_PATH + "/**").addResourceLocations("/swagger/");
    }

}