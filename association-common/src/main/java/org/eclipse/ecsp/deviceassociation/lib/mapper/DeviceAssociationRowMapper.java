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
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping a row of the ResultSet to a DeviceAssociation object.
 */
public class DeviceAssociationRowMapper implements RowMapper<DeviceAssociation> {

    /**
     * Maps a row of the ResultSet to a DeviceAssociation object.
     *
     * @param rs     the ResultSet object containing the data from the database
     * @param rowNum the current row number
     * @return the mapped DeviceAssociation object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public DeviceAssociation mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociatedBy(rs.getString("associated_by"));
        deviceAssociation.setAssociatedOn(rs.getTimestamp("associated_on"));
        deviceAssociation.setAssociationStatus(AssociationStatus.valueOf(rs.getString("association_status")));
        deviceAssociation.setDisassociatedBy(rs.getString("disassociated_by"));
        deviceAssociation.setDisassociatedOn(rs.getTimestamp("disassociated_on"));
        deviceAssociation.setHarmanId(rs.getString("harman_id"));
        try {
            deviceAssociation.setAssociationType(rs.getString("association_type"));
        } catch (Exception e) {
            // Do Nothing
        }
        try {
            deviceAssociation.setStartTimeStamp(rs.getTimestamp("start_timestamp").getTime());
        } catch (Exception e) {
            // Do Nothing
        }
        try {
            deviceAssociation.setEndTimeStamp(rs.getTimestamp("end_timestamp").getTime());
        } catch (Exception e) {
            // Do Nothing
        }
        // For now the same as harman_id.
        deviceAssociation.setVehicleId(rs.getString("harman_id"));
        deviceAssociation.setId(rs.getLong("id"));
        deviceAssociation.setModifiedBy(rs.getString("modified_by"));
        deviceAssociation.setModifiedOn(rs.getTimestamp("modified_on"));
        deviceAssociation.setSerialNumber(rs.getString("serial_number"));
        deviceAssociation.setUserId(rs.getString("user_id"));
        deviceAssociation.setFactoryId((rs.getLong("factory_data")));
        try {
            deviceAssociation.setSoftwareVersion(rs.getString("software_version"));
        } catch (final SQLException e) {
            // Do nothing.
        }

        return deviceAssociation;
    }
}
