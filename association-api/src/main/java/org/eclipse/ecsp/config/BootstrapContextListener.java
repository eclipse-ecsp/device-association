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

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.config.EnvConfigLocation;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BootstrapContextListener class implements the ServletContextListener interface
 * to handle the initialization and destruction of the web application context.
 * It detects the configuration directory and sets the path for the application.
 */
public class BootstrapContextListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapContextListener.class);
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String CATALINA_HOME = "catalina.home";

    /**
     * Called when the ServletContext is initialized.
     * Sets the application configuration location based on the context path and detects the config directory.
     *
     * @param sce The ServletContextEvent object containing the ServletContext that is being initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String confDirectoryName = sce.getServletContext().getContextPath();
        EnvConfigLocation.INSTANCE.setPath(null);
        String configPath = detectConfigDirectory(confDirectoryName);
        EnvConfigLocation.INSTANCE.setPath(configPath);
        LOGGER.info("## {} - application configuration location:{} ", confDirectoryName, configPath);
    }

    /**
     * Detects the configuration directory path based on the provided context name.
     *
     * @param contextName The name of the web application context.
     * @return The configuration directory path.
     * @throws RuntimeException if the configuration path cannot be initialized.
     */
    private String detectConfigDirectory(String contextName) {
        String configPath = StringUtils.trimToNull(System.getProperty(CATALINA_HOME));
        if (configPath != null) {
            configPath = configPath + FILE_SEPARATOR + "conf";
        } else {
            throw new ApiTechnicalException(
                "Unable to initialize configuration for the web application contextName: " + contextName);
        }
        configPath = configPath + contextName;
        return configPath;
    }

    /**
     * Receives notification that the ServletContext is about to be destroyed.
     * This method is called when the ServletContext is being shut down.
     *
     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
