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

import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * This class implements the UserBelongingValidator interface and provides the functionality to validate user belonging.
 */
@Service
public class UserBelongingValidatorImpl implements UserBelongingValidator<Map<String, Object>> {
    /**
     * The Data Access Object (DAO) for managing device associations.
     * This is used to interact with the underlying database or persistence layer
     * to perform operations related to device associations.
     */
    @Autowired
    protected DeviceAssociationDao deviceAssociationDao;

    /**
     * Validates the user belonging based on the provided belonging map.
     *
     * @param belongingMap a map containing the details of the user belonging
     * @return true if the user belonging is valid, false otherwise
     */
    @Override
    public boolean validateUserBelonging(Map<String, Object> belongingMap) {
        return deviceAssociationDao.findCountByDetails(belongingMap);

    }
}
