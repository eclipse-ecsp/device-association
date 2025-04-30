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

package org.eclipse.ecsp.services.shared.db;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.eclipse.ecsp.common.CommonConstants.HARMAN_ID;
import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * This class is responsible for mapping the result set of a database query to a HcpInfo object.
 */
public class HcpInfoMapper implements RowMapper<HcpInfo> {

    /**
     * Maps a row from the ResultSet to a HcpInfo object.
     *
     * @param rs     the ResultSet containing the data
     * @param rowNum the current row number
     * @return the mapped HcpInfo object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public HcpInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setId(rs.getLong(ID));
        hcpInfo.setHarmanId(rs.getString(HARMAN_ID));
        hcpInfo.setSerialNumber(rs.getString("SerialNumber"));
        hcpInfo.setVin(rs.getString("VIN"));
        hcpInfo.setFactoryId(rs.getString("factory_data"));
        return hcpInfo;
    }

}
