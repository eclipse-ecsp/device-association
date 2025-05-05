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
    /**
     * Represents the International Mobile Equipment Identity (IMEI) of the device.
     * Associated parameter: "a.imei".
     */
    IMEI("a.imei"),
    /**
     * Represents the serial number of the device.
     * Associated parameter: "a.serial_number".
     */
    SERIAL_NUMBER("a.serial_number"),
    /**
     * Represents the unique device ID (Harman ID).
     * Associated parameter: "b.harman_id".
     */
    DEVICE_ID("b.harman_id"),
    /**
     * Represents the user ID associated with the device.
     * Associated parameter: "b.user_id".
     */
    USER_ID("b.user_id"),
    /**
     * Represents the Service Set Identifier (SSID) of the device.
     * Associated parameter: "a.ssid".
     */
    SSID("a.ssid"),
    /**
     * Represents the Integrated Circuit Card Identifier (ICCID) of the device.
     * Associated parameter: "a.iccid".
     */
    ICCID("a.iccid"),
    /**
     * Represents the Mobile Station International Subscriber Directory Number (MSISDN).
     * Associated parameter: "a.msisdn".
     */
    MSISDN("a.msisdn"),
    /**
     * Represents the International Mobile Subscriber Identity (IMSI) of the device.
     * Associated parameter: "a.imsi".
     */
    IMSI("a.imsi"),
    /**
     * Represents the Basic Service Set Identifier (BSSID) of the device.
     * Associated parameter: "a.bssid".
     */
    BSSID("a.bssid"),
    /**
     * Represents the type of association for the device.
     * Associated parameter: "b.association_type".
     */
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
