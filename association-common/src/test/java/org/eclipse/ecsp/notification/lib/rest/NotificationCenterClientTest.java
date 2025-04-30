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

package org.eclipse.ecsp.notification.lib.rest;

import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for NotificationCenterClient.
 */
public class NotificationCenterClientTest {

    @InjectMocks
    NotificationCenterClient notificationCenterClient;

    @Mock
    RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getUserProfileTest_NullUserProfile() {
        String userId = "User123";
        String ncBaseUrl = "url";
        Mockito.doReturn(null).when(restTemplate).getForObject(ncBaseUrl, UserProfile.class);
        UserProfile userProfile = notificationCenterClient.getUserProfile(userId, ncBaseUrl);
        Assertions.assertNull(userProfile);
    }

    @Test
    public void getUserProfileTest_ValidUserProfile() {
        String userId = "User123";
        String ncBaseUrl = "url";
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("User123");
        Mockito.doReturn(userProfile).when(restTemplate).getForObject(Mockito.anyString(), Mockito.any());
        UserProfile actualUserProfile = notificationCenterClient.getUserProfile(userId, ncBaseUrl);
        Assertions.assertEquals(userProfile, actualUserProfile);
    }

    @Test
    public void getUserProfileTest_Exception() {
        String userId = "User123";
        String ncBaseUrl = "url";
        Mockito.doThrow(RestClientException.class).when(restTemplate).getForObject(Mockito.anyString(), Mockito.any());
        UserProfile userProfile = notificationCenterClient.getUserProfile(userId, ncBaseUrl);
        Assertions.assertNull(userProfile);
    }

    @Test
    public void callNotifCenterNonRegisteredUserApiTest_Exception() {
        Locale locale = new Locale("ENGLISH");
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("User123");
        userProfile.setLocale(locale);
        userProfile.setDefaultPhoneNumber("123456");
        String ncBaseUrl = "url";
        String notificationId = "notif123";
        notificationCenterClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
        Assertions.assertNotNull(userProfile);
    }

    @Test
    public void callNotifCenterNonRegisteredUserApiTest_Ok() {
        Locale locale = new Locale("ENGLISH");
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("User123");
        userProfile.setLocale(locale);
        userProfile.setDefaultPhoneNumber("123456");
        String ncBaseUrl = "url";
        String notificationId = "notif123";
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        notificationCenterClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
        Assertions.assertEquals(response, restTemplate.exchange(Mockito.anyString(), Mockito.any(),
                Mockito.any(), (Class) Mockito.any()));
    }

    @Test
    public void callNotifCenterNonRegisteredUserApiTest_Bad_Request() {
        Locale locale = new Locale("ENGLISH");
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("User123");
        userProfile.setLocale(locale);
        userProfile.setDefaultPhoneNumber("123456");
        String ncBaseUrl = "url";
        String notificationId = "notif123";
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        notificationCenterClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
        Assertions.assertEquals(response, restTemplate.exchange(Mockito.anyString(), Mockito.any(),
                Mockito.any(), (Class) Mockito.any()));
    }

    @Test
    public void callNotifCenterNonRegisteredUserApiTest_JsonProcessingException() {
        Locale locale = new Locale("ENGLISH");
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("User123");
        userProfile.setLocale(locale);
        userProfile.setDefaultPhoneNumber("123456");
        String ncBaseUrl = "url";
        String notificationId = "notif123";
        Mockito.doThrow(RestClientException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        notificationCenterClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
        Assertions.assertThrows(RestClientException.class, () -> restTemplate.exchange(Mockito.anyString(),
                Mockito.any(), Mockito.any(), (Class) Mockito.any()));
    }
}