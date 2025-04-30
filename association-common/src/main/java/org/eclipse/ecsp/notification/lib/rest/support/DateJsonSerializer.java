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

package org.eclipse.ecsp.notification.lib.rest.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class is a custom JSON serializer for the Timestamp class.
 * It serializes a Timestamp object to a formatted date string in the format "yyyy-MM-dd'T'HH:mm:ss.SSS".
 */
public class DateJsonSerializer extends JsonSerializer<Timestamp> {

    /**
     * Serializes a Timestamp object to a formatted date string.
     *
     * @param value    The Timestamp object to be serialized.
     * @param gen      The JsonGenerator used to write the serialized value.
     * @param provider The SerializerProvider used to access serializers for serializing nested objects.
     * @throws IOException If an I/O error occurs during serialization.
     */
    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = formatter.format(value);
        gen.writeString(formattedDate);
    }
}