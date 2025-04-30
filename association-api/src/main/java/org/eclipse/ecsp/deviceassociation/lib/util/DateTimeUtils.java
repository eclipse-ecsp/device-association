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

package org.eclipse.ecsp.deviceassociation.lib.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtils {
    public static final int VALUE_1000 = 1000;
    public static final int VALUE_60 = 60;

    /**
     * Private constructor to prevent instantiation.
     */
    private DateTimeUtils() {

    }

    /**
     * Returns the ISO date format for the given timestamp.
     *
     * @param timestamp the timestamp to convert
     * @return the ISO date format
     */
    public static String getIsoDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return df.format(timestamp);
    }

    /**
     * Returns the GMT offset in minutes for the given timestamp.
     *
     * @param timestamp the timestamp to calculate the GMT offset for
     * @return the GMT offset in minutes
     */
    public static int getGmtOffset(Timestamp timestamp) {
        if (timestamp == null) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return (calendar.get(Calendar.ZONE_OFFSET) / VALUE_1000) / VALUE_60;
    }

}
