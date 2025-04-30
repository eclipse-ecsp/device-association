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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;

/**
 * The QualifierGenerator class is responsible for generating qualifiers and VINs for device association.
 * It provides a static method to generate qualifiers and VINs based on a given serial number.
 * The generated qualifiers and VINs are used for device identification and authentication.
 */
@Slf4j
public class QualifierGenerator {

    private static final String AEAD_STRING = "aadstr";
    private static final int MAX_VALUE = 2;
    private static final int STR_LENGTH = 5;
    private static final int GCM_LENGTH = 8;
    private static final int RANDOM_YEAR_CONSTANT = 2000;
    private static final int RANDOM_MAKE_CONSTANT = 1000;

    /**
     * Constructs a new instance of the QualifierGenerator class.
     */
    private QualifierGenerator() {

    }

    /**
     * Generates a Qualifier object containing a qualifier, serial number, and VIN.
     *
     * @param serialNumber the serial number used to generate the qualifier
     * @return a Qualifier object containing the generated qualifier, serial number, and VIN
     * @throws InvalidKeyException if the key is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     * @throws IllegalBlockSizeException if the block size is invalid
     * @throws BadPaddingException if the padding is invalid
     */
    public static Qualifier generateQualifierAndVin(String serialNumber) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {

        int i = 0;
        String associatedData;
        byte[] assocData;

        String vin = generateVin();

        String ivin = vin + i;
        String text = ivin + "@" + serialNumber + "@002";
        String jstring = String.valueOf(serialNumber);
        String keydata = "HarmanActTESTV" + jstring.substring(0, MAX_VALUE);

        byte[] key = keydata.getBytes(StandardCharsets.UTF_8);

        int len = serialNumber.trim().length();
        if (len < STR_LENGTH) {
            log.debug("serialNumber length < 5");
            String part = serialNumber + StringUtils.repeat("x", STR_LENGTH - len);
            associatedData = part + AEAD_STRING + part;
            assocData = associatedData.getBytes(StandardCharsets.UTF_8);
        } else {
            log.debug("serialNumber length > 5");
            String part1 = serialNumber.substring(0, STR_LENGTH);
            String part2 = serialNumber.substring(len - STR_LENGTH, len);
            associatedData = part1 + AEAD_STRING + part2;
            assocData = associatedData.getBytes(StandardCharsets.UTF_8);
        }
        byte[] encrypted = encrypt(key, text.getBytes(StandardCharsets.UTF_8), assocData);

        String qualif = DatatypeConverter.printBase64Binary(encrypted);
        Qualifier qualifier = new Qualifier();
        qualifier.setSerialNumber(serialNumber);
        qualifier.setQualifier(qualif);
        qualifier.setVin(ivin);
        return qualifier;

    }

    /**
     * Encrypts the given data using the provided key and associated data.
     *
     * @param key             the encryption key
     * @param unencrypted     the data to be encrypted
     * @param associatedData  the associated data used for authentication (optional)
     * @return the encrypted data
     * @throws NoSuchAlgorithmException             if the specified algorithm is not available
     * @throws NoSuchPaddingException               if the specified padding scheme is not available
     * @throws InvalidKeyException                  if the specified key is invalid
     * @throws InvalidAlgorithmParameterException   if the specified algorithm parameters are invalid
     * @throws IllegalBlockSizeException            if the block size is invalid
     * @throws BadPaddingException                   if the padding is invalid
     */
    private static byte[] encrypt(byte[] key, byte[] unencrypted, byte[] associatedData)
        throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {
        final int Gcm_Tag_Length = 16;
        // Set up the cipher and encrypt
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new
            GCMParameterSpec(Gcm_Tag_Length * GCM_LENGTH, key));

        if (associatedData != null && associatedData.length > 0) {
            log.debug("Associated Data is NOT NULL. Updating AAD.");
            cipher.updateAAD(associatedData);
        }
        return cipher.doFinal(unencrypted);
    }

    /**
     * Generates a random VIN (Vehicle Identification Number) based on the provided yearMap and index.
     *
     * @return The generated VIN string.
     */
    private static String generateVin() {
        // get a random year
        SecureRandom random = new SecureRandom();
        int selectedYear = RANDOM_YEAR_CONSTANT + random.nextInt(LocalDate.now().getYear() - RANDOM_YEAR_CONSTANT + 1);
        int selectedMake = new SecureRandom().nextInt(RANDOM_MAKE_CONSTANT);
        int selectedModel = new SecureRandom().nextInt(RANDOM_MAKE_CONSTANT);
        return "TESTVIN_Make:Make" + selectedMake + "_Model:Model" + selectedModel + "_Year:"
            +  selectedYear + "_Type:Car_";
    }

    /**
     * Represents a qualifier for device association.
     */
    public static class Qualifier {
        private String qualifierData;
        private String vin;
        private String serialNumber;

        /**
         * Gets the qualifier.
         *
         * @return the qualifier
         */
        public String getQualifier() {
            return qualifierData;
        }

        /**
         * Sets the qualifier.
         *
         * @param qualifierData the qualifier to set
         */
        public void setQualifier(String qualifierData) {
            this.qualifierData = qualifierData;
        }

        /**
         * Gets the VIN.
         *
         * @return the VIN
         */
        public String getVin() {
            return vin;
        }

        /**
         * Sets the VIN.
         *
         * @param vin the VIN to set
         */
        public void setVin(String vin) {
            this.vin = vin;
        }

        /**
         * Gets the serial number.
         *
         * @return the serial number
         */
        public String getSerialNumber() {
            return serialNumber;
        }

        /**
         * Sets the serial number.
         *
         * @param serialNumber the serial number to set
         */
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
    }

}
