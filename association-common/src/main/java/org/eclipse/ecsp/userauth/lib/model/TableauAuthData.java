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

/**
 * Represents authentication data for Tableau.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableauAuthData {
    String username;
    String url;
    String clientIp;

    /**
     * Returns a string representation of the TableauAuthData object.
     *
     * @return A string representation of the TableauAuthData object, including the username, url, and client IP.
     */
    @Override
    public String toString() {
        return "TableauAuthData [username=" + username + ", url=" + url + ", client_ip=" + clientIp + "]";
    }


}
