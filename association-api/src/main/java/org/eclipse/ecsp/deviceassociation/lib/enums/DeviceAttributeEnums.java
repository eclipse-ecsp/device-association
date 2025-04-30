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

package org.eclipse.ecsp.deviceassociation.lib.enums;

/**
 * This enum represents the different attributes of a device.
 */
public enum DeviceAttributeEnums {
    IMEI("a.imei"),
    SERIAL_NUMBER("a.serial_number"),
    DEVICE_ID("b.harman_id"),
    USER_ID("b.user_id"),
    SSID("a.ssid"),
    ICCID("a.iccid"),
    MSISDN("a.msisdn"),
    IMSI("a.imsi"),
    BSSID("a.bssid"),
    ASSOCIATION_TYPE("b.association_type");

    private String param;

    /**
     * Constructs a DeviceAttributeEnums with the specified parameter.
     *
     * @param param the parameter associated with the device attribute
     */
    private DeviceAttributeEnums(String param) {
        this.param = param;
    }

    /**
     * Returns the parameter associated with the device attribute.
     *
     * @return the parameter associated with the device attribute
     */
    public String getString() {
        return param;
    }
}
