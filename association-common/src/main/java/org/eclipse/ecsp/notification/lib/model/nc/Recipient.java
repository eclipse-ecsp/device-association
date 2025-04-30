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

package org.eclipse.ecsp.notification.lib.model.nc;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Represents a recipient for notifications.
 */
@JsonInclude(NON_EMPTY)
public class Recipient {
    private String email;
    private String sms;
    private String brand;
    private String locale;
    private Map<String, String> data;

    /**
     * Constructor for the Recipient class.
     *
     * @param email  the email address of the recipient
     * @param sms    the SMS number of the recipient
     * @param brand  the brand associated with the recipient
     * @param locale the locale of the recipient
     * @param data   additional data associated with the recipient
     */
    public Recipient(String email, String sms, String brand, String locale, Map<String, String> data) {
        super();
        this.email = email;
        this.sms = sms;
        this.brand = brand;
        this.locale = locale;
        this.data = data;
    }

    /**
     * Default constructor for the Recipient class.
     */
    public Recipient() {
    }

    /**
     * Gets the email address of the recipient.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the recipient.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the SMS number of the recipient.
     *
     * @return the SMS number
     */
    public String getSms() {
        return sms;
    }

    /**
     * Sets the SMS number of the recipient.
     *
     * @param sms the SMS number to set
     */
    public void setSms(String sms) {
        this.sms = sms;
    }

    /**
     * Gets the brand associated with the recipient.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the brand associated with the recipient.
     *
     * @param brand the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Gets the locale of the recipient.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale of the recipient.
     *
     * @param locale the locale to set
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Gets the additional data associated with the recipient.
     *
     * @return the data
     */
    public Map<String, String> getData() {
        return data;
    }

    /**
     * Sets the additional data associated with the recipient.
     *
     * @param data the data to set
     */
    public void setData(Map<String, String> data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the Recipient object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Recipient [email=" + email + ", sms=" + sms + ", brand=" + brand + ", locale=" + locale + ", data="
            + data + "]";
    }
}
