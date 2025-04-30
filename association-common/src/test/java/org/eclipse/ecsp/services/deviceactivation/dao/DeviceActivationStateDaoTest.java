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

package org.eclipse.ecsp.services.deviceactivation.dao;

import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivationState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceActivationStateDao.
 */
public class DeviceActivationStateDaoTest {
    public static final long ID = 12345L;
    public static final long FACTORY_ID = 432L;
    public static final long EXPECTED_RESULT = -1L;
    private static final String CAN_DEVICE_BE_ACTIVATED =
        "select count(*)>0 from device_activation_state where serial_number=? and activation_ready = true";
    private static final String CAN_DEVICE_BE_ACTIVATED_BY_FD_ID =
        "select count(*)>0 from device_activation_state where factory_data=? and activation_ready = true";
    private static final String INSERT =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by," 
            +            "activation_ready) values(?,?,?,?)";
    private static final String INSERT_REPLACE_DEVICE_ACTIVATION_STATE =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by," 
            +            "activation_ready,factory_data) values(?,?,?,?,?)";
    private static final String FIND_ACTIVE_RECORD =
        "select id from device_activation_state where serial_number=? and activation_ready=true";
    private static final String DISABLE_RECORD =
        "update device_activation_state set activation_ready=false and deactivation_initiated_by=? and " 
            +            "deactivation_initiated_on=now() where id=?";
    private static final String FIND_ACTIVE_RECORD_SQL =
        "select id from device_activation_state where factory_data=? and activation_ready=true";
    private static final String DISABLE_ACTIVATION_READY =
        "update device_activation_state set activation_ready=false and deactivation_initiated_by=? and " 
            +            "deactivation_initiated_on=now() where factory_data=? and activation_ready=true";
    private static final String DISABLE_ACTIVATION_READY_BY_FACTORYID =
        "update device_activation_state set activation_ready=false where factory_data=? and activation_ready=true";
    private static final String GET_ASSOCIATED_VIN =
        "select vin from vin_details v  left join device_association d on v.reference_id=d.id where d.serial_number=?" 
            +            " and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String GET_TRANSACTION_ID =
        "select tran_id from sim_details s left join device_association d on s.reference_id=d.id where d" 
            +            ".serial_number=? and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String UPDATE_TRANSACTION_STATUS = "update sim_details set tran_status=? where tran_id=?";
    private static final String GET_ASSOCIATED_USER =
        "select user_id from device_association d where d.serial_number=? and d.association_status in ('ASSOCIATED')";
    private static final String GET_SIM_TRANSACTION_STATUS =
        "select tran_status from sim_details s left join device_association d on s.reference_id=d.id where d" 
            +            ".serial_number=? and d.association_status in ('ASSOCIATION_INITIATED','ASSOCIATED')";

    @InjectMocks
    private DeviceActivationStateDao deviceActivationStateDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void canBeActivatedTest() {
        String serialNumber = "12345";
        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(CAN_DEVICE_BE_ACTIVATED, new Object[]{serialNumber}, Boolean.class);
        boolean actualResult = deviceActivationStateDao.canBeActivated(serialNumber);
        assertTrue(actualResult);
    }

    @Test
    public void canBeActivatedTest_nullResponse() {
        String serialNumber = "12345";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(CAN_DEVICE_BE_ACTIVATED, new Object[]{serialNumber}, Boolean.class);
        boolean actualResult = deviceActivationStateDao.canBeActivated(serialNumber);
        assertFalse(actualResult);
    }

    @Test
    public void canBeActivatedByFactoryIdTest() {
        long factoryDataId = ID;
        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(CAN_DEVICE_BE_ACTIVATED_BY_FD_ID, new Object[]{factoryDataId}, Boolean.class);
        boolean actualResult = deviceActivationStateDao.canBeActivated(factoryDataId);
        assertTrue(actualResult);
    }

    @Test
    public void canBeActivatedByFactoryIdTest_nullResponse() {
        long factoryDataId = ID;
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(CAN_DEVICE_BE_ACTIVATED_BY_FD_ID, new Object[]{factoryDataId}, Boolean.class);
        boolean actualResult = deviceActivationStateDao.canBeActivated(factoryDataId);
        assertFalse(actualResult);
    }

    @Test
    public void insertTest() {
        DeviceActivationState activationState = new DeviceActivationState();
        activationState.setId(ID);
        activationState.setSerialNumber("123456");
        activationState.setFactoryDataId(FACTORY_ID);
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(INSERT, activationState.getSerialNumber(), activationState.getActivationInitiatedOn(),
                activationState.getActivationInitiatedBy(), activationState.isActivationReady());
        deviceActivationStateDao.insert(activationState);
        assertEquals(1, jdbcTemplate.update(INSERT, activationState.getSerialNumber(),
                activationState.getActivationInitiatedOn(), activationState.getActivationInitiatedBy(),
                activationState.isActivationReady()));
    }

    @Test
    public void insertReplaceDeviceActivationStateTest() {
        DeviceActivationState activationState = new DeviceActivationState();
        activationState.setId(ID);
        activationState.setSerialNumber("123456");
        activationState.setFactoryDataId(FACTORY_ID);
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(INSERT_REPLACE_DEVICE_ACTIVATION_STATE, activationState.getSerialNumber(),
                activationState.getActivationInitiatedOn(), activationState.getActivationInitiatedBy(),
                activationState.isActivationReady(), activationState.getFactoryDataId());
        deviceActivationStateDao.insertReplaceDeviceActivationState(activationState);
        assertEquals(1, jdbcTemplate.update(INSERT_REPLACE_DEVICE_ACTIVATION_STATE, activationState.getSerialNumber(),
                activationState.getActivationInitiatedOn(), activationState.getActivationInitiatedBy(),
                activationState.isActivationReady(), activationState.getFactoryDataId()));
    }

    @Test
    public void disableRecordTest() {
        long id = ID;
        String userId = "Test";
        Mockito.doReturn(1).when(jdbcTemplate).update(DISABLE_RECORD, userId, id);
        deviceActivationStateDao.disableRecord(id, userId);
        assertEquals(1, jdbcTemplate.update(DISABLE_RECORD, userId, id));
    }

    @Test
    public void disableActivationReadyTest() {
        long id = ID;
        String userId = "Test";
        Mockito.doReturn(1).when(jdbcTemplate).update(DISABLE_ACTIVATION_READY, userId, id);
        deviceActivationStateDao.disableActivationReady(id, userId);
        assertEquals(1, jdbcTemplate.update(DISABLE_ACTIVATION_READY, userId, id));
    }

    @Test
    public void disableActivationReadyByFacotryIdTest() {
        long id = ID;
        Mockito.doReturn(1).when(jdbcTemplate).update(DISABLE_ACTIVATION_READY_BY_FACTORYID, id);
        deviceActivationStateDao.disableActivationReadyByFacotryId(id);
        assertEquals(1, jdbcTemplate.update(DISABLE_ACTIVATION_READY_BY_FACTORYID, id));
    }

    @Test
    public void findActiveDeviceTest() {
        String serialNumber = "12345";
        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(serialNumber);
        assertEquals(1L, actualResult);
    }

    @Test
    public void findActiveDeviceThrowsExceptionTest() {
        String serialNumber = "12345";
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(serialNumber);
        assertEquals(EXPECTED_RESULT, actualResult);
    }

    @Test
    public void findActiveDeviceTest_nullId() {
        String serialNumber = "12345";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(serialNumber);
        assertEquals(EXPECTED_RESULT, actualResult);
    }

    @Test
    public void findActiveDeviceByFactoryIdTest() {
        long factoryDataId = ID;
        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD_SQL, new Object[]{factoryDataId}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(factoryDataId);
        assertEquals(1L, actualResult);
    }

    @Test
    public void findActiveDeviceByFactoryIdThrowsExceptionTest() {
        long factoryDataId = ID;
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD_SQL, new Object[]{factoryDataId}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(factoryDataId);
        assertEquals(EXPECTED_RESULT, actualResult);
    }

    @Test
    public void findActiveDeviceByFactoryIdTest_nullId() {
        long factoryDataId = ID;
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(FIND_ACTIVE_RECORD_SQL, new Object[]{factoryDataId}, Long.class);
        long actualResult = deviceActivationStateDao.findActiveDevice(factoryDataId);
        assertEquals(EXPECTED_RESULT, actualResult);
    }

    @Test
    public void findActiveDevicesTest1() {
        String serialNumber = "12345";
        List<Long> activeRecordIdList = new ArrayList<>();
        activeRecordIdList.add(ID);
        Mockito.doReturn(activeRecordIdList).when(jdbcTemplate)
            .queryForList(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        List<Long> actualResult = deviceActivationStateDao.findActiveDevices(serialNumber);
        assertNotNull(actualResult);
    }

    @Test
    public void findActiveDevicesTest2() {
        String serialNumber = "12345";
        List<Long> activeRecordIdList = new ArrayList<>();
        Mockito.doReturn(activeRecordIdList).when(jdbcTemplate)
            .queryForList(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        List<Long> actualResult = deviceActivationStateDao.findActiveDevices(serialNumber);
        assertNotNull(actualResult);
    }

    @Test
    public void findActiveDevicesThrowsExceptionTest2() {
        String serialNumber = "12345";
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate).queryForList(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        List<Long> actualResult = deviceActivationStateDao.findActiveDevices(serialNumber);
        assertNull(actualResult);
    }

    @Test
    public void getAssociatedVinTest() {
        String serialNumber = "12345";
        Mockito.doReturn("vin").when(jdbcTemplate)
            .queryForObject(GET_ASSOCIATED_VIN, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getAssociatedVin(serialNumber);
        assertEquals("vin", actualResult);
    }

    @Test
    public void getAssociatedVinThrowsExceptionTest() {
        String serialNumber = "12345";
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate)
            .queryForObject(GET_ASSOCIATED_VIN, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getAssociatedVin(serialNumber);
        assertNull(actualResult);
    }

    @Test
    public void getAssociatedUserIdTest() {
        String serialNumber = "12345";
        Mockito.doReturn("user1").when(jdbcTemplate)
            .queryForObject(GET_ASSOCIATED_USER, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getAssociatedUserId(serialNumber);
        assertEquals("user1", actualResult);
    }

    @Test
    public void getAssociatedUserIdThrowsExceptionTest() {
        String serialNumber = "12345";
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate)
            .queryForObject(GET_ASSOCIATED_USER, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getAssociatedUserId(serialNumber);
        assertNull(actualResult);
    }

    @Test
    public void getSimTransactionStatusTest() {
        String serialNumber = "12345";
        Mockito.doReturn("transactionStatus").when(jdbcTemplate)
            .queryForObject(GET_SIM_TRANSACTION_STATUS, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getSimTransactionStatus(serialNumber);
        assertEquals("transactionStatus", actualResult);
    }

    @Test
    public void getSimTransactionStatusExceptionTest() {
        String serialNumber = "12345";
        DataAccessException e = new DataAccessException("Error occurred") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };
        Mockito.doThrow(e).when(jdbcTemplate)
            .queryForObject(GET_SIM_TRANSACTION_STATUS, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getSimTransactionStatus(serialNumber);
        assertNull(actualResult);
    }

    @Test
    public void getTransactionIdTest() {
        String serialNumber = "12345";
        Mockito.doReturn("transactionId").when(jdbcTemplate)
            .queryForObject(GET_TRANSACTION_ID, new Object[]{serialNumber}, String.class);
        String actualResult = deviceActivationStateDao.getTransactionId(serialNumber);
        assertEquals("transactionId", actualResult);
    }

    @Test
    public void updateTransactionStatusTest() {
        String tranId = "12345";
        Mockito.doReturn(1).when(jdbcTemplate).update(UPDATE_TRANSACTION_STATUS, "Completed", tranId);
        deviceActivationStateDao.updateTransactionStatus(tranId);
        assertEquals(1, jdbcTemplate.update(UPDATE_TRANSACTION_STATUS, "Completed", tranId));
    }
}