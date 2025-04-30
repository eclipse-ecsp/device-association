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

package org.eclipse.ecsp.deviceassociation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmptyLong;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static lombok.AccessLevel.PRIVATE;

/**
 * Represents a request to update an association.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
@FieldDefaults(level = PRIVATE)
@Builder
public class AssociationUpdateRequest {
    @NullOrNotEmpty(message = "assocType is not allowed be empty")
    String assocType;
    @NullOrNotEmptyLong(message = "startTime is not allowed be empty")
    long startTime;
    @NullOrNotEmptyLong(message = "endTime is not allowed be empty")
    long endTime;
}
