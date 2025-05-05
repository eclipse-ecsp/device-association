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

package org.eclipse.ecsp.deviceassociation.lib.service;

import org.eclipse.ecsp.deviceassociation.lib.exception.DuplicateDeviceAssociationRequestException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * This class represents the Device Association Service V2.
 * It provides methods for associating devices, fetching associated devices, and retrieving association details.
 */
@Service
@Transactional
public class DeviceAssociationServiceV2 extends AbstractDeviceAssociationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationServiceV2.class);

    
    /**
     * Associates a device with a user based on the provided request.
     * 
     * <p>This method checks if the device is already associated with the user. 
     * If it is, a {@link DuplicateDeviceAssociationRequestException} is thrown. 
     * Otherwise, it disassociates the device from any existing users and creates 
     * a new association for the device with the specified user.</p>
     *
     * @param associateDeviceRequest The request containing the details of the 
     *                               device and user to be associated.
     * @return An {@link AssociateDeviceResponse} containing the ID of the 
     *         created association and the status of the association process.
     * @throws DuplicateDeviceAssociationRequestException If the device is 
     *         already associated with the specified user.
     * @throws ObserverMessageProcessFailureException If there is a failure 
     *         while processing observer messages during the association process.
     */
    public AssociateDeviceResponse associateDevice(AssociateDeviceRequest associateDeviceRequest)
            throws DuplicateDeviceAssociationRequestException, ObserverMessageProcessFailureException {
        LOGGER.info("associateDevice - start: {}", associateDeviceRequest);
        if (deviceAssociationDao.isDeviceCurrentlyAssociatedToUser(associateDeviceRequest.getSerialNumber(),
            associateDeviceRequest.getUserId())) {
            throw new DuplicateDeviceAssociationRequestException();
        }

        disassociateExistingUsers(associateDeviceRequest.getSerialNumber(), associateDeviceRequest.getUserId(), false);

        DeviceAssociation deviceAssociation = associate(associateDeviceRequest);

        LOGGER.info("associateDevice -exit: {}", deviceAssociation);

        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.ASSOCIATION_INITIATED);
    }

    /**
     * Associates a device with a user.
     *
     * @param associateDeviceRequest The request object containing the device and user information.
     * @return The associated device.
     */
    private DeviceAssociation associate(AssociateDeviceRequest associateDeviceRequest) {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber(associateDeviceRequest.getSerialNumber());
        deviceAssociation.setUserId(associateDeviceRequest.getUserId());
        deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setModifiedBy(associateDeviceRequest.getUserId());
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setAssociatedBy(associateDeviceRequest.getUserId());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociationDao.insert(deviceAssociation);
        deviceAssociationDao.insertDeviceState(deviceAssociation);
        return deviceAssociation;
    }

    /**
     * Retrieves the list of associated devices for a user.
     *
     * @param userId The ID of the user.
     * @return The list of associated devices.
     */
    public List<DeviceAssociation> getAssociatedDevicesForUser(String userId) {
        return deviceAssociationDao.fetchAssociatedDevices(userId);
    }

    /**
     * Retrieves the association details for a specific association ID and user.
     *
     * @param associationId The ID of the association.
     * @param userId The ID of the user.
     * @return The association details.
     */
    @Override
    public DeviceAssociation getAssociationDetails(long associationId, String userId) {
        DeviceAssociation deviceAssociation = deviceAssociationDao.find(associationId, userId);
        if (deviceAssociation == null || deviceAssociation.getHarmanId() == null) {
            return deviceAssociation;
        }
        deviceAssociation.setDeviceAttributes(getDeviceAttributes(deviceAssociation.getHarmanId()));
        return deviceAssociation;
    }
}