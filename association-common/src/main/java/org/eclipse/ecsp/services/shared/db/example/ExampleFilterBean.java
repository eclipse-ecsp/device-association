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

package org.eclipse.ecsp.services.shared.db.example;

import org.eclipse.ecsp.services.shared.db.FilterField;
import org.eclipse.ecsp.services.shared.db.FilterField.Range;

import java.util.List;

/**
 * This class represents an example filter bean used for filtering data based on various criteria.
 * It contains fields for make, model, year, and color filters.
 */
public class ExampleFilterBean {
    @FilterField(dbname = "make")
    private long makeId;

    @FilterField(dbname = "model")
    private List<Long> modelId;

    @FilterField(dbname = "year", range = Range.MIN)
    private int yearFrom;

    @FilterField(dbname = "year", range = Range.MAX)
    private int yearTo;

    @FilterField()
    private List<String> color;

    /**
     * Gets the make ID.
     *
     * @return the make ID
     */
    public long getMakeId() {
        return makeId;
    }

    /**
     * Sets the make ID.
     *
     * @param makeId the make ID to set
     */
    public void setMakeId(long makeId) {
        this.makeId = makeId;
    }

    /**
     * Gets the model IDs.
     *
     * @return the model IDs
     */
    public List<Long> getModelId() {
        return modelId;
    }

    /**
     * Sets the model IDs.
     *
     * @param modelId the model IDs to set
     */
    public void setModelId(List<Long> modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the starting year.
     *
     * @return the starting year
     */
    public int getYearFrom() {
        return yearFrom;
    }

    /**
     * Sets the starting year.
     *
     * @param year the starting year to set
     */
    public void setYearFrom(int year) {
        this.yearFrom = year;
    }

    /**
     * Gets the ending year.
     *
     * @return the ending year
     */
    public int getYearTo() {
        return yearTo;
    }

    /**
     * Sets the ending year.
     *
     * @param yearTo the ending year to set
     */
    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    /**
     * Gets the colors.
     *
     * @return the colors
     */
    public List<String> getColor() {
        return color;
    }

    /**
     * Sets the colors.
     *
     * @param color the colors to set
     */
    public void setColor(List<String> color) {
        this.color = color;
    }
}
