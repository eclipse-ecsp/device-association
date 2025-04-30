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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Configuration class for setting up the data source.
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class DataSourceConfig {

    /**
     * Creates and configures the Postgres data source.
     *
     * @return the configured data source
     */
    @Bean(name = "dataSource")
    public DataSource dataSource() {

        DataSource dataSource = null;
        log.error("Entering into Datasource");
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            log.error("ENV Context ::: " + envContext.toString());
            dataSource = (DataSource) envContext.lookup("jdbc/postgres");
            log.error("DataSource ::: " + dataSource.getClass().getName());
        } catch (NamingException e) {
            log.error("Error while create DB connection : ", e);
        }
        log.error("Exiting from Datasource");
        return dataSource;
    }
}
