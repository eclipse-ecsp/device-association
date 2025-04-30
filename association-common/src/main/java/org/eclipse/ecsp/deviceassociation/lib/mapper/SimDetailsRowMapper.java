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

package org.eclipse.ecsp.deviceassociation.lib.mapper;

import org.eclipse.ecsp.deviceassociation.lib.model.SimDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping a row from the ResultSet to a SimDetails object.
 */
public class SimDetailsRowMapper implements RowMapper<SimDetails> {

    /**
     * Maps a row from the ResultSet to a SimDetails object.
     *
     * @param rs     the ResultSet containing the data
     * @param rowNum the row number
     * @return the mapped SimDetails object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public SimDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        SimDetails simDetails = new SimDetails();

        simDetails.setTranId(rs.getString("tran_id"));
        simDetails.setTranStatus(rs.getString("tran_status"));
        return simDetails;
    }

}
