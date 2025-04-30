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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

/**
 * Represents the current device data.
 */
public class CurrentDeviceDataPojo {
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NullOrNotEmpty(message = "serialNumber is not allowed be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "iccid is not allowed be empty")
    private String iccid;
    @NullOrNotEmpty(message = "bssid is not allowed be empty")
    private String bssid;
    @NullOrNotEmpty(message = "msisdn is not allowed be empty")
    private String msisdn;
    @NullOrNotEmpty(message = "imsi is not allowed be empty")
    private String imsi;
    @NullOrNotEmpty(message = "state is not allowed be empty")
    private String state;

    /**
     * Constructs a new CurrentDeviceDataPojo instance.
     */
    public CurrentDeviceDataPojo() {

    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the ICCID of the device.
     *
     * @return The ICCID.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID of the device.
     *
     * @param iccid The ICCID to set.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the BSSID of the device.
     *
     * @return The BSSID.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID of the device.
     *
     * @param bssid The BSSID to set.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the MSISDN of the device.
     *
     * @return The MSISDN.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the device.
     *
     * @param msisdn The MSISDN to set.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI of the device.
     *
     * @return The IMSI.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI of the device.
     *
     * @param imsi The IMSI to set.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the state of the device.
     *
     * @return The state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns a string representation of the CurrentDeviceDataPojo object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "CurrentDeviceDataPojo [imei=" + imei + ", serialNumber=" + serialNumber + ", iccid=" + iccid 
            +            ", bssid=" + bssid
            + ", msisdn=" + msisdn + ", imsi=" + imsi + ", state=" + state + "]";
    }

}
