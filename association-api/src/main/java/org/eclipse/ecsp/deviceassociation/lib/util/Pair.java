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

/**
 * A generic class representing a pair of elements.
 *
 * @param <E1> the type of the first element
 * @param <E2> the type of the second element
 */
public class Pair<E1, E2> {
    private E1 element1;
    private E2 element2;

    /**
     * Constructs a new Pair object with the specified elements.
     *
     * @param element1 the first element
     * @param element2 the second element
     */
    public Pair(E1 element1, E2 element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    /**
     * Returns the first element of the pair.
     *
     * @return the first element
     */
    public E1 getElement1() {
        return element1;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param element1 the new value for the first element
     */
    public void setElement1(E1 element1) {
        this.element1 = element1;
    }

    /**
     * Returns the second element of the pair.
     *
     * @return the second element
     */
    public E2 getElement2() {
        return element2;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param element2 the new value for the second element
     */
    public void setElement2(E2 element2) {
        this.element2 = element2;
    }
}
