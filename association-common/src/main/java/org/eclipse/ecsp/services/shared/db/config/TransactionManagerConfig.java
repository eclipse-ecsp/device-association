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

package org.eclipse.ecsp.services.shared.db.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * Configuration class for the transaction manager.
 */
@Configurable
@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig implements TransactionManagementConfigurer {

    private final DataSource dataSource;

    /**
     * Configuration class for managing transactions.
     * This class initializes the transaction manager with the provided data source.
     *
     * @param dataSource the data source to be used for transaction management
     */
    public TransactionManagerConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new instance of the transaction manager.
     *
     * @return The transaction manager.
     */
    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * Retrieves the transaction manager for annotation-driven transaction management.
     *
     * @return The transaction manager.
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
}
