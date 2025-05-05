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

package org.eclipse.ecsp.deviceassociation.lib.service.swm;

/**
 * The IswmCrudService interface provides methods for creating, updating, and deleting vehicles.
 *
 * @param <I> the type of input for the vehicle operations
 */
public interface IswmCrudService<I> {
    
    /**
     * Creates a vehicle based on the provided input.
     *
     * @param input The input data required to create the vehicle.
     * @return {@code true} if the vehicle was successfully created, {@code false} otherwise.
     */
    boolean createVehicle(I input);

    /**
     * Updates the details of a vehicle based on the provided input.
     *
     * @param input The input object containing the updated vehicle information.
     *              This should include all necessary details required for the update.
     * @return {@code true} if the vehicle update was successful, {@code false} otherwise.
     */
    boolean updateVehicle(I input);
    
    /**
     * Deletes a vehicle based on the provided input.
     *
     * @param input The input parameter containing the necessary information to identify
     *              the vehicle to be deleted.
     * @return {@code true} if the vehicle was successfully deleted, {@code false} otherwise.
     */
    boolean deleteVehicle(I input);
}
