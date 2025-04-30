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

import java.io.Serializable;
import java.util.Locale;

/**
 * Represents a user profile.
 */
public class UserProfile implements Serializable {
    private String userId;
    private String firstName;
    private String lastName;
    private Locale locale;
    private String defaultEmail;
    private String defaultPhoneNumber;
    private boolean consent;

    /**
     * Default constructor.
     */
    public UserProfile() {

    }

    /**
     * Constructs a new UserProfile object with the specified parameters.
     *
     * @param userId             the user ID
     * @param firstName          the first name
     * @param lastName           the last name
     * @param locale             the locale
     * @param defaultEmail       the default email
     * @param defaultPhoneNumber the default phone number
     * @param consent            the consent
     */
    public UserProfile(String userId, String firstName, String lastName, Locale locale, String defaultEmail,
                       String defaultPhoneNumber, boolean consent) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.locale = locale;
        this.defaultEmail = defaultEmail;
        this.defaultPhoneNumber = defaultPhoneNumber;
        this.consent = consent;
    }

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
     * Gets the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the locale of the user.
     *
     * @return The locale of the user.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale of the user.
     *
     * @param locale The locale to set.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the default email of the user.
     *
     * @return The default email of the user.
     */
    public String getDefaultEmail() {
        return defaultEmail;
    }

    /**
     * Sets the default email of the user.
     *
     * @param defaultEmail The default email to set.
     */
    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    /**
     * Gets the default phone number of the user.
     *
     * @return The default phone number of the user.
     */
    public String getDefaultPhoneNumber() {
        return defaultPhoneNumber;
    }

    /**
     * Sets the default phone number of the user.
     *
     * @param defaultPhoneNumber The default phone number to set.
     */
    public void setDefaultPhoneNumber(String defaultPhoneNumber) {
        this.defaultPhoneNumber = defaultPhoneNumber;
    }

    /**
     * Checks if the user has given consent.
     *
     * @return True if the user has given consent, false otherwise.
     */
    public boolean isConsent() {
        return consent;
    }

    /**
     * Sets the consent status of the user.
     *
     * @param consent The consent status to set.
     */
    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    /**
     * Returns a string representation of the user profile.
     *
     * @return A string representation of the user profile.
     */
    @Override
    public String toString() {
        return "UserProfile{" 
            +            "userId='" + userId + '\'' 
            +            ", firstName='" + firstName + '\'' 
            +            ", lastName='" + lastName + '\'' 
            +            ", locale=" + locale 
            +            ", defaultEmail='" + defaultEmail + '\'' 
            +            ", defaultPhoneNumber='" + defaultPhoneNumber + '\'' 
            +            ", consent=" + consent 
            +            '}';
    }
}
