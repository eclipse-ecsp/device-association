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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Configuration class for the Postgres database.
 * This class provides the configuration for the DataSource bean and manages the refresh of the DataSource with updated
 * properties.
 * It also defines the connection pool properties for the Postgres database.
 */
@Configuration
@EnableScheduling
public class PostgresDbConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDbConfig.class);
    private static final StopWatch STOPWATCH = new StopWatch();
    private static final String VAULT_POSTGRES_USERNAME_KEY = "username";
    private static final String VAULT_POSTGRES_PASS_KEY = "password";
    private static final String VAULT_POSTGRES_LEASE_DURATION = "lease_duration";
    boolean postgresVaultEnabled;
    @Autowired
    @Lazy
    private EnvConfig<DeviceAssocationProperty> envConfig;
    private volatile boolean isRefreshInProgress = false;
    private String userName;
    private String password;
    private long leaseDuration;
    private DataSource dataSource = null;
    private static final int SLEEP_TIME = 50;
    private static final int MILLIS = 1000;

    /**
     * Number of Connections a pool will try to acquire upon startup. Should be between minPoolSize and maxPoolSize.
     * Default: 3
     */
    private int initialPoolSize;

    /**
     * Minimum number of Connections a pool will maintain at any given time.
     * Default: 3
     */
    private int minPoolSize;

    /**
     * Maximum number of Connections a pool will maintain at any given time.
     */
    private int maxPoolSize;

    /**
     * Seconds a Connection can remain pooled but unused before being discarded. Zero means idle connections never
     * expire.
     * In second, after that time it will release the unused connections
     */
    private int maxIdleTime;

    /**
     * Determines how many connections at a time c3p0 will try to acquire when the pool is exhausted.
     */
    private int acquireIncrement;

    /**
     * If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this
     * number of seconds.
     */
    private int idleConnectionTestPeriod;

    /**
     * Creates and configures the DataSource bean.
     *
     * @return the configured DataSource
     */
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataSource dataSource() {
        LOGGER.debug("## In dataSource creation method......................");
        while (isRefreshInProgress) {
            LOGGER.info("## DataSource vault refresh in progress...........");
            // Sleep the thread for milli secs and again check the progress status
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("## DataSource refresh thread interrupted......");
                Thread.currentThread().interrupt();
            }
        }
        if (null == dataSource) {
            throw new ApiTechnicalException("## Datasource was not set properly.");
        }
        return dataSource;
    }

    /**
     * Loads the Postgres properties from the environment configuration.
     */
    @PostConstruct
    private void loadPostgresProperties() {
        //Connection pool properties
        initialPoolSize = envConfig.getIntegerValue(DeviceAssocationProperty.INITIAL_POOL_SIZE);
        minPoolSize = envConfig.getIntegerValue(DeviceAssocationProperty.MIN_POOL_SIZE);
        maxPoolSize = envConfig.getIntegerValue(DeviceAssocationProperty.MAX_POOL_SIZE);
        maxIdleTime = envConfig.getIntegerValue(DeviceAssocationProperty.MAX_IDLE_TIME);
        acquireIncrement = envConfig.getIntegerValue(DeviceAssocationProperty.ACQUIRE_INCREMENT);
        idleConnectionTestPeriod = envConfig.getIntegerValue(DeviceAssocationProperty.IDLE_CONNECTION_TEST_PERIOD);
        userName = envConfig.getStringValue(DeviceAssocationProperty.POSTGRES_USERNAME);
        password = envConfig.getStringValue(DeviceAssocationProperty.POSTGRES_PASSWORD);
        dataSource = refreshDataSource();
    }

    /**
     * Refreshes the DataSource with the updated properties.
     *
     * @return the refreshed DataSource
     */
    private DataSource refreshDataSource() {
        cleanupDataSource(dataSource);
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(envConfig.getStringValue(DeviceAssocationProperty.POSTGRES_DRIVER_CLASS_NAME));
            cpds.setJdbcUrl(envConfig.getStringValue(DeviceAssocationProperty.POSTGRES_URL));
            cpds.setUser(userName);
            cpds.setPassword(password);
            cpds.setInitialPoolSize(initialPoolSize);
            cpds.setMinPoolSize(minPoolSize);
            cpds.setMaxPoolSize(maxPoolSize);
            cpds.setMaxIdleTime(maxIdleTime);
            cpds.setAcquireIncrement(acquireIncrement);
            cpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
            LOGGER.info("## Vehicle Association - ConnectionPool properties: initialPoolSize: {}, minPoolSize:{}, "
                    + "maxPoolSize: {}, maxIdleTime: {}, acquireIncrement: {}, idleConnectionTestPeriod: {}",
                cpds.getInitialPoolSize(),
                cpds.getMinPoolSize(), cpds.getMaxPoolSize(), cpds.getMaxIdleTime(), cpds.getAcquireIncrement(),
                cpds.getIdleConnectionTestPeriod());
        } catch (PropertyVetoException e) {
            throw new ApiTechnicalException(
                "## Exception while creating connection pool for Vehicle Association component, Error: ",
                    e.getMessage());
        }
        return cpds;
    }

    /**
     * Cleans up the DataSource.
     *
     * @param ds the DataSource to clean up
     */
    private void cleanupDataSource(DataSource ds) {
        if (ds instanceof ComboPooledDataSource cpds) {
            cpds.close();
        }
    }
}
