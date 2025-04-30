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

import org.eclipse.ecsp.services.shared.db.FilterField;

/**
 * Represents a user in the system.
 */
public class User {

    @FilterField(dbname = "\"User\".\"ID\"")
    private long id;

    @FilterField(dbname = "\"User\".\"UserID\"")
    private String userId;

    @FilterField(dbname = "\"User\".\"Password\"")
    private String password;

    @FilterField(dbname = "\"User\".\"FirstName\"")
    private String firstName;

    @FilterField(dbname = "\"User\".\"LastName\"")
    private String lastName;

    @FilterField(dbname = "\"User\".\"IsValid\"")
    private boolean isValid;

    @FilterField(dbname = "\"User\".\"Email\"")
    private String email;

    @FilterField(dbname = "\"User\".\"CreatedBy\"")
    private String createdBy;

    @FilterField(dbname = "\"User\".\"UpdatedBy\"")
    private String updatedBy;

    private UserRolesView userRolesView;
    private String newPassword;
    private String displayName;
    private long changePasswordSerial;
    private String tableauUserId;
    private long tableauUserIdLong;

    /**
     * Constructs a new User object.
     */
    public User() {

    }

    /**
     * Constructs a new User object with the specified ID.
     *
     * @param id the ID of the user
     */
    public User(long id) {
        super();
        this.id = id;
    }

    /**
     * Returns the ID of the user.
     *
     * @return the ID of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the user.
     *
     * @param password the new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the validity status of the user.
     *
     * @return true if the user is valid, false otherwise
     */
    public boolean getIsValid() {
        return isValid;
    }

    /**
     * Sets the validity status of the user.
     *
     * @param isValid the validity status to set
     */
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Checks if the user is valid.
     *
     * @return true if the user is valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Sets the validity status of the user.
     *
     * @param isValid the validity status to set
     */
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the new password of the user.
     *
     * @return the new password of the user
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password for the user.
     *
     * @param newPassword the new password to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Returns the display name of the user.
     *
     * @return the display name of the user
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param displayName the new display name for the user
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user who created this object.
     *
     * @return the user who created this object
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     *
     * @param createdBy the value to set for createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns the username of the user who last updated the user information.
     *
     * @return the username of the user who last updated the user information
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the 'updatedBy' field.
     *
     * @param updatedBy The new value for the 'updatedBy' field.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Returns the UserRolesView associated with this User.
     *
     * @return the UserRolesView associated with this User
     */
    public UserRolesView getUserRolesView() {
        return userRolesView;
    }

    /**
     * Sets the user roles view for the user.
     *
     * @param userRolesView the user roles view to be set
     */
    public void setUserRolesView(UserRolesView userRolesView) {
        this.userRolesView = userRolesView;
    }

    /**
     * Returns the change password serial number for the user.
     *
     * @return the change password serial number
     */
    public long getChangePasswordSerial() {
        return changePasswordSerial;
    }

    /**
     * Sets the change password serial for the user.
     *
     * @param changePasswordSerial the new change password serial
     */
    public void setChangePasswordSerial(long changePasswordSerial) {
        this.changePasswordSerial = changePasswordSerial;
    }

    /**
     * Returns the Tableau user ID associated with this user.
     *
     * @return the Tableau user ID
     */
    public String getTableauUserId() {
        return tableauUserId;
    }

    /**
     * Sets the Tableau user ID for the user.
     *
     * @param tableauUserId the Tableau user ID to set
     */
    public void setTableauUserId(String tableauUserId) {
        this.tableauUserId = tableauUserId;
    }

    /**
     * Returns the Tableau user ID as a long value.
     *
     * @return the Tableau user ID as a long value
     */
    public long getTableauUserIdLong() {
        return tableauUserIdLong;
    }

    /**
     * Sets the Tableau user ID as a long value.
     *
     * @param tableauUserIdLong the Tableau user ID to set
     */
    public void setTableauUserIdLong(long tableauUserIdLong) {
        this.tableauUserIdLong = tableauUserIdLong;
    }

    /**
     * Returns a string representation of the User object.
     *
     * @return A string representation of the User object.
     */
    @Override
    public String toString() {
        return "User [id=" + id + ", userId=" + userId + ", password=" + password + ", firstName=" + firstName 
            +            ", lastName=" + lastName
            + ", isValid=" + isValid + ", email=" + email + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
            + ", userRolesView=" + userRolesView + ", newPassword=" + newPassword + ", displayName=" + displayName
            + ", changePasswordSerial=" + changePasswordSerial + ", tableauUserId=" + tableauUserId 
            +            ", tableauUserIdLong="
            + tableauUserIdLong + "]";
    }


}
