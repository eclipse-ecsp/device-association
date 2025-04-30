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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmptyLong;

/**
 * Represents a delegate association request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class DelegateAssociationRequest {

    @NullOrNotEmpty(message = "serialNumber is not allowed be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NullOrNotEmpty(message = "imsi is not allowed be empty")
    private String imsi;
    @NullOrNotEmpty(message = "ssid is not allowed be empty")
    private String ssid;
    @NullOrNotEmpty(message = "iccid is not allowed be empty")
    private String iccid;
    @NullOrNotEmpty(message = "msisdn is not allowed be empty")
    private String msisdn;
    @NullOrNotEmpty(message = "bssid is not allowed be empty")
    private String bssid;
    @NotBlank(message = "associationType is required and not allowed be empty")
    private String associationType;
    @NullOrNotEmpty(message = "userId is not allowed be empty")
    @JsonAlias("userId")
    private String delegationUserId;
    @NullOrNotEmpty(message = "email is not allowed be empty")
    private String email;
    @NullOrNotEmptyLong(message = "startTimestamp is not allowed be empty")
    private long startTimestamp;
    @NullOrNotEmptyLong(message = "endTimestamp is not allowed be empty")
    private long endTimestamp;
    @JsonIgnore
    private String userId;

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the serial number.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the IMEI.
     *
     * @return The IMEI.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI.
     *
     * @param imei The IMEI to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the IMSI.
     *
     * @return The IMSI.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI.
     *
     * @param imsi The IMSI to set.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the SSID.
     *
     * @return The SSID.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID.
     *
     * @param ssid The SSID to set.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the ICCID.
     *
     * @return The ICCID.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID.
     *
     * @param iccid The ICCID to set.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN.
     *
     * @return The MSISDN.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN.
     *
     * @param msisdn The MSISDN to set.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the BSSID.
     *
     * @return The BSSID.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID.
     *
     * @param bssid The BSSID to set.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the association type.
     *
     * @return The association type.
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the association type.
     *
     * @param associationType The association type to set.
     */
    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    /**
     * Gets the email.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the delegation user ID.
     *
     * @return The delegation user ID.
     */
    public String getDelegationUserId() {
        return delegationUserId;
    }

    /**
     * Sets the delegation user ID.
     *
     * @param delegationUserId The delegation user ID to set.
     */
    public void setDelegationUserId(String delegationUserId) {
        this.delegationUserId = delegationUserId;
    }

    /**
     * Gets the start timestamp.
     *
     * @return The start timestamp.
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Sets the start timestamp.
     *
     * @param startTimestamp The start timestamp to set.
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Gets the end timestamp.
     *
     * @return The end timestamp.
     */
    public long getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Sets the end timestamp.
     *
     * @param endTimestamp The end timestamp to set.
     */
    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    /**
     * Returns a string representation of the DelegateAssociationRequest object.
     *
     * @return A string representation of the DelegateAssociationRequest object.
     */
    @Override
    public String toString() {
        return "DelegateAssociationRequest [userId=" + userId + ", serialNumber=" + serialNumber + ", imei=" + imei
            + ", imsi=" + imsi + ", ssid=" + ssid + ", iccid=" + iccid + ", msisdn=" + msisdn + ", bssid=" + bssid
            + ", associationType=" + associationType + ", delegationUserId=" + delegationUserId + ", email=" + email
            + ", startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp + "]";
    }

    /**
     * Converts the DelegateAssociationRequest object to an AssociateDeviceRequest object.
     *
     * @return The converted AssociateDeviceRequest object.
     */
    public AssociateDeviceRequest toAssociationRequest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid(this.getBssid());
        associateDeviceRequest.setIccid(this.iccid);
        associateDeviceRequest.setImei(this.imei);
        associateDeviceRequest.setImsi(this.imsi);
        associateDeviceRequest.setMsisdn(this.msisdn);
        associateDeviceRequest.setSerialNumber(this.serialNumber);
        associateDeviceRequest.setSsid(this.ssid);
        associateDeviceRequest.setUserId(this.delegationUserId);
        return associateDeviceRequest;
    }

}
