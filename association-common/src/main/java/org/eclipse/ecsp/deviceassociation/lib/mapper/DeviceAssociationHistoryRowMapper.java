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

import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping a row from the database result set to a DeviceAssociationHistory object.
 * It implements the RowMapper interface and provides the mapRow method to perform the mapping.
 */
public class DeviceAssociationHistoryRowMapper implements RowMapper<DeviceAssociationHistory> {

    /**
     * Maps a row from the ResultSet to a DeviceAssociationHistory object.
     *
     * @param rs     the ResultSet containing the row data
     * @param rowNum the row number
     * @return the mapped DeviceAssociationHistory object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public DeviceAssociationHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeviceAssociationHistory deviceAssociation = new DeviceAssociationHistory();
        deviceAssociation.setAssociatedBy(rs.getString("associated_by"));
        deviceAssociation.setAssociatedOn(rs.getTimestamp("associated_on"));
        deviceAssociation.setAssociationStatus(AssociationStatus.valueOf(rs.getString("association_status")));
        deviceAssociation.setDisassociatedBy(rs.getString("disassociated_by"));
        deviceAssociation.setDisassociatedOn(rs.getTimestamp("disassociated_on"));
        deviceAssociation.setHarmanId(rs.getString("harman_id"));
        deviceAssociation.setModifiedBy(rs.getString("modified_by"));
        deviceAssociation.setModifiedOn(rs.getTimestamp("modified_on"));
        deviceAssociation.setSerialNumber(rs.getString("serial_number"));
        deviceAssociation.setUserId(rs.getString("user_id"));
        return deviceAssociation;
    }

}