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

import org.eclipse.ecsp.services.shared.db.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * The Main class is the entry point of the program.
 */
public class Main {
    public static final long MAKE_ID = 100L;
    public static final long ID_1 = 10L;
    public static final long ID_2 = 20L;
    public static final long ID_3 = 30L;
    public static final int FROM_YEAR = 2010;
    public static final int TO_YEAR = 2013;

    private Main() {

    }

    /**
     * The main method is the entry point of the program.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        ExampleFilterBean bean = new ExampleFilterBean();
        bean.setMakeId(MAKE_ID);
        List<Long> modelIds = new ArrayList<>();
        modelIds.add(ID_1);
        modelIds.add(ID_2);
        modelIds.add(ID_3);
        bean.setModelId(modelIds);
        List<String> colors = new ArrayList<>();
        colors.add("white");
        colors.add("blue");
        colors.add("red");
        bean.setColor(colors);
        bean.setYearFrom(FROM_YEAR);
        bean.setYearTo(TO_YEAR);
        Filter.buildWhereClause(bean);
    }
}
