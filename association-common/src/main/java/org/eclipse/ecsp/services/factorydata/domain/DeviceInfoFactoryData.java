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

package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Represents the factory data for a device information.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoFactoryData {
    @JsonIgnore
    private Long id = 0L; // Sets 0L to be sure that is others parts of the code
    // work as before
    private Timestamp manufacturingDate;
    private String model;
    private String imei;
    private String serialNumber;
    private String platformVersion;
    private String iccid;
    private String ssid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private Timestamp recordDate;
    private String factoryAdmin;
    private Timestamp createdDate;
    private String state;
    private Boolean stolen;
    private Boolean faulty;
    private String packageSerialNumber;
    private String deviceId;
    private String vin;
    private String deviceType;
}
