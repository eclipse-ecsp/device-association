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
 
package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Represents a page of device information.
 *
 * @param <T> the type of devices in the page
 */
@Data
@AllArgsConstructor
public class DeviceInfoPage<T> {

    static final HcpPageable EMPTY_PAGEABLE = new HcpPageable(0, 0, 0L);
    static final HcpPageable SINGLE_PAGEABLE = new HcpPageable(0, 1, 1L);

    @JsonInclude(JsonInclude.Include.NON_NULL)
    final DeviceInfoAggregateFactoryData aggregate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    final List<T> devices;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    final HcpPageable page;

    /**
     * Provides empty page info. { "number": 0, "size": 0 "total": 0 }
     *
     * @return empty page info.
     */
    public static HcpPageable emptyPageable() {
        return EMPTY_PAGEABLE;
    }

    /**
     * Provides page info with single page element. { "number": 0, "size": 1
     * "total": 1 }
     *
     * @return the single page info
     */
    public static HcpPageable singlePageable() {
        return SINGLE_PAGEABLE;
    }

    /**
     * Simple holder for the pagination info.
     */
    @Data
    @AllArgsConstructor
    public static class HcpPageable {
        Integer number;
        Integer size;
        Long total;
    }
}
