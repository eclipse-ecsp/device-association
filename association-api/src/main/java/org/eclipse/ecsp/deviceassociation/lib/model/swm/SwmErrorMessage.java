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

package org.eclipse.ecsp.deviceassociation.lib.model.swm;

import java.util.ArrayList;

/**
 * Represents an error message in the SWM (Software Management) system.
 */
public class SwmErrorMessage {

    ArrayList<Object> parameters = new ArrayList<Object>();
    private String localizationMessageId;
    private String localizedMessage;

    /**
     * Gets the localization message ID of the error message.
     *
     * @return The localization message ID.
     */
    public String getLocalizationMessageId() {
        return localizationMessageId;
    }

    /**
     * Sets the localization message ID of the error message.
     *
     * @param localizationMessageId The localization message ID to set.
     */
    public void setLocalizationMessageId(String localizationMessageId) {
        this.localizationMessageId = localizationMessageId;
    }

    /**
     * Gets the parameters associated with the error message.
     *
     * @return The parameters.
     */
    public ArrayList<Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters associated with the error message.
     *
     * @param parameters The parameters to set.
     */
    public void setParameters(ArrayList<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the localized message of the error message.
     *
     * @return The localized message.
     */
    public String getLocalizedMessage() {
        return localizedMessage;
    }

    /**
     * Sets the localized message of the error message.
     *
     * @param localizedMessage The localized message to set.
     */
    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    /**
     * Returns a string representation of the SwmErrorMessage object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "SwmErrorMessage [localizationMessageId=" + localizationMessageId + ", parameters=" + parameters 
            +            ", localizedMessage="
            + localizedMessage + "]";
    }

}
