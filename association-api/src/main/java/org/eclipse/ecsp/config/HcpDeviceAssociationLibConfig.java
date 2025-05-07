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

import jakarta.annotation.Resource;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.common.config.EnvConfigLoader;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Configuration class for HcpDeviceAssociationLib.
 */
@Configuration
@ImportAutoConfiguration(RestTemplateAutoConfiguration.class)
public class HcpDeviceAssociationLibConfig {

    @Resource(name = "envNotifConfigLoader")
    private EnvConfigLoader<DeviceAssocationProperty> envConfigLoader;

    /**
     * Creates an instance of EnvConfigLoader for DeviceAssocationProperty.
     *
     * @return The EnvConfigLoader instance.
     */
    @Bean(name = "envNotifConfigLoader")
    public EnvConfigLoader<DeviceAssocationProperty> envConfigLoader() {
        return new EnvConfigLoader<>(DeviceAssocationProperty.class, "deviceassociation");
    }

    /**
     * Retrieves the server configuration from the EnvConfigLoader.
     *
     * @return The server configuration.
     */
    @Bean(name = "envConfig")
    public EnvConfig<DeviceAssocationProperty> notificationConfig() {
        return envConfigLoader.getServerConfig();
    }

    
    /**
     * Creates and configures a {@link SpringAuthRestClient} bean.
     *
     * @param envConfig The environment configuration containing properties for the device association.
     *                  Specifically, it provides the base URL for the Spring Authentication service
     *                  through {@link DeviceAssocationProperty#SPRING_AUTH_BASE_URL}.
     * @return A configured instance of {@link SpringAuthRestClient}.
     */
    @Bean
    public SpringAuthRestClient springAuthRestClient(EnvConfig<DeviceAssocationProperty> envConfig) {
        return new SpringAuthRestClient(envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_BASE_URL));
    }

    /**
     * Creates and configures a {@link MethodValidationPostProcessor} bean.
     *
     * <p>
     * This bean enables method-level validation for beans annotated with
     * validation constraints (e.g., {@code @Valid}, {@code @NotNull}, etc.).
     * It ensures that validation rules are enforced on method parameters and
     * return values.
     * </p>
     *
     * @return a configured {@link MethodValidationPostProcessor} instance
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
