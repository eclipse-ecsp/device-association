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

package org.eclipse.ecsp.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents the statistics of a record collection.
 *
 * @param <E> the type of the aggregate object
 */
@JsonInclude(Include.NON_NULL)
public class RecordStats<E> {
    /**
     * Total number of records per page.
     */
    int size;

    /**
     * Page Number.
     */
    int page;

    /**
     * Total number of records.
     */
    long total;

    /**
     * Any object like aggregate.
     */
    private E aggregate;

    /**
     * Gets the total number of records per page.
     *
     * @return the size of records per page
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the total number of records per page.
     *
     * @param size the size of records per page
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets the page number.
     *
     * @return the page number
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the page number.
     *
     * @param page the page number
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the total number of records.
     *
     * @return the total number of records
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the total number of records.
     *
     * @param total the total number of records
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * Gets the aggregate object.
     *
     * @return the aggregate object
     */
    public E getAggregate() {
        return aggregate;
    }

    /**
     * Sets the aggregate object.
     *
     * @param aggregate the aggregate object
     */
    public void setAggregate(E aggregate) {
        this.aggregate = aggregate;
    }
}