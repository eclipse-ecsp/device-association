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

package org.eclipse.ecsp.deviceassociation.dto.swm;

/**
 * The Link class represents a hyperlink in a web resource.
 */
public class Link {
    private String url;
    /**
     * The relation attribute of the link.
     */
    private String rel;
    private Object type;

    /**
     * Returns the URL of the link.
     *
     * @return The URL of the link.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the link.
     *
     * @param url The URL of the link.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the relation attribute of the link.
     *
     * @return The relation attribute of the link.
     */
    public String getRel() {
        return rel;
    }

    /**
     * Sets the relation attribute of the link.
     *
     * @param rel The relation attribute of the link.
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * Returns the type of the link.
     *
     * @return The type of the link.
     */
    public Object getType() {
        return type;
    }

    /**
     * Sets the type of the link.
     *
     * @param type The type of the link.
     */
    public void setType(Object type) {
        this.type = type;
    }
}
