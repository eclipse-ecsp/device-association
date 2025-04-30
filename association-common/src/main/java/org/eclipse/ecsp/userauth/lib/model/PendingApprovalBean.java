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

package org.eclipse.ecsp.userauth.lib.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * Represents a pending approval bean.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PendingApprovalBean {
    private long adminUserId;
    private String adminUserName;
    private long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String approveUserUrl;
    private List<String> appNames;
    private Timestamp createdAt;
    private Boolean isExistingUser;

    /**
     * Returns a string representation of the PendingApprovalBean object.
     *
     * @return The string representation of the PendingApprovalBean object.
     */
    @Override
    public String toString() {
        return "PendingApprovalBean [adminUserId=" + adminUserId + ", adminUserName=" + adminUserName + ", userId=" 
            +            userId
            + ", userName=" + userName + ", firstName=" + firstName + ", lastName=" + lastName + ", approveUserUrl=" 
            +            approveUserUrl
            + ", appNames=" + appNames + ", createdAt=" + createdAt + ", isExistingUser=" + isExistingUser + "]";
    }
}
