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

package org.eclipse.ecsp.auth.lib.dao;

import org.eclipse.ecsp.auth.lib.rest.model.DeviceSeqNoDetailsResponse;
import org.eclipse.ecsp.auth.lib.rest.model.SeqNoHidMapInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceCommonDao.
 */
public class DeviceCommonDaoTest {

    private static final String SELECT_DEVICE_ID_HARMANID = "select  \"ID\", \"HarmanID\" from public.\"Device\"";
    private static final int INDEX = 2;
    private static final int RETURN_VALUE = 10;


    @InjectMocks
    private DeviceCommonDao deviceCommonDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getDeviceSeqNoAndHarmanIdDetailsTest() {
        List<SeqNoHidMapInfo> seqNoHidMapInfoList = new ArrayList<>();
        SeqNoHidMapInfo seqNoHidMapInfo = new SeqNoHidMapInfo();
        seqNoHidMapInfo.setHarmanId("H1");
        seqNoHidMapInfo.setId(1);

        seqNoHidMapInfoList.add(seqNoHidMapInfo);

        DeviceSeqNoDetailsResponse deviceSeqNoDetailsResponse = new DeviceSeqNoDetailsResponse();
        deviceSeqNoDetailsResponse.setSeqNoHidMapInfo(seqNoHidMapInfoList);
        Mockito.doReturn(deviceSeqNoDetailsResponse).when(namedParameterJdbcTemplate).query(SELECT_DEVICE_ID_HARMANID,
            new ResultSetExtractor<DeviceSeqNoDetailsResponse>() {

                @Override
                public DeviceSeqNoDetailsResponse extractData(ResultSet rs) throws SQLException {
                    List<SeqNoHidMapInfo> seqNoHidMapInfoList = new ArrayList<SeqNoHidMapInfo>();
                    while (rs.next()) {
                        SeqNoHidMapInfo seqNoHidMapInfo = new SeqNoHidMapInfo();
                        seqNoHidMapInfo.setId(rs.getLong(1));
                        seqNoHidMapInfo.setHarmanId(rs.getString(INDEX));
                        seqNoHidMapInfoList.add(seqNoHidMapInfo);
                    }
                    DeviceSeqNoDetailsResponse deviceSeqNoDetailsRes = new DeviceSeqNoDetailsResponse();

                    deviceSeqNoDetailsRes.setSeqNoHidMapInfo(seqNoHidMapInfoList);
                    return deviceSeqNoDetailsRes;
                }
            });
        DeviceSeqNoDetailsResponse actualDeviceSeqNoDetailsResponse =
                deviceCommonDao.getDeviceSeqNoAndHarmanIdDetails();
        Assertions.assertNull(actualDeviceSeqNoDetailsResponse);
    }

    @Test
    public void healthCheckTest() {
        Mockito.doReturn(RETURN_VALUE).when(namedParameterJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Integer.class));
        int health = deviceCommonDao.healthCheck();
        assertEquals(RETURN_VALUE, health);
    }

    @Test
    public void healthCheckTest_null() {
        Mockito.doReturn(null).when(namedParameterJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Integer.class));
        int health = deviceCommonDao.healthCheck();
        assertEquals(0, health);
    }
}