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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceDetail;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping the result set from the database to an instance of AssociationDetailsResponse.
 * It implements the RowMapper interface and provides the mapRow method to perform the mapping.
 */
@Slf4j
public class AssociationDetailsResponseMapper implements RowMapper<AssociationDetailsResponse> {

    /**
     * Maps a row from the ResultSet to an AssociationDetailsResponse object.
     *
     * @param rs The ResultSet containing the data to be mapped.
     * @param rowNum The row number of the ResultSet.
     * @return The mapped AssociationDetailsResponse object.
     * @throws SQLException If an error occurs while accessing the ResultSet.
     */
    @Override
    public AssociationDetailsResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(rs.getLong("id"));
        associationDetailsResponse.setUserId(rs.getString("user_id"));
        associationDetailsResponse.setSerialNumber(rs.getString("serial_number"));
        associationDetailsResponse.setAssociationStatus(rs.getString("association_status"));
        associationDetailsResponse.setAssociatedOn(rs.getString("associated_on"));
        associationDetailsResponse.setDisassociatedOn(rs.getString("disassociated_on"));
        // For now the same as harman_id.
        associationDetailsResponse.setVehicleId(rs.getString("harman_id"));
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setHarmanId(rs.getString("harman_id"));
        deviceDetail.setImei(rs.getString("imei"));
        deviceDetail.setSsid(rs.getString("ssid"));
        deviceDetail.setIccid(rs.getString("iccid"));
        deviceDetail.setMsisdn(rs.getString("msisdn"));
        deviceDetail.setImsi(rs.getString("imsi"));
        deviceDetail.setBssid(rs.getString("bssid"));
        deviceDetail.setState(rs.getString("state"));
        deviceDetail.setStolen(rs.getBoolean("isStolen"));
        deviceDetail.setFaulty(rs.getBoolean("isFaulty"));
        try {
            deviceDetail.setSoftwareVersion(rs.getString("software_version"));
        } catch (final SQLException e) {
            // Do nothing.
        }

        associationDetailsResponse.setDeviceDetail(deviceDetail);
        log.info("Pojo prepared successfully:" + associationDetailsResponse);
        return associationDetailsResponse;
    }
}
