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

package org.eclipse.ecsp.commons.amazon.services;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.List;

//import com.amazonaws.AmazonClientException;
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.ClientConfiguration;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.regions.Region;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.AmazonS3Exception;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.GetObjectRequest;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.transfer.TransferManager;
//import com.amazonaws.services.s3.transfer.Upload;

/**
 * This class represents an Amazon S3 client for HCP (Harman Cloud Platform).
 * It provides methods to interact with Amazon S3 services.
 */
@Component
//@Configurable
public class HcpAmazonS3Client {


    public static final String UPDATE_PKG_DIR_NAME = "ota-packages";
    public static final String VIN_LIST_DIR_NAME = "ota-vins";
    public static final String OTA_BUCKET = "ota-poc";
    public static final String CONFIG_BUCKET = "configurations";
    protected static final Logger LOGGER = LoggerFactory.getLogger(HcpAmazonS3Client.class);

    //private AmazonS3 s3Client;
    //private TransferManager transferManager;
    /**
     * This class represents an Amazon S3 client for HCP (Harman Cloud Platform).
     * It provides methods to interact with Amazon S3 services.
     */
    public HcpAmazonS3Client() {
    //loadAmazonS3Client();
    //transferManager = new TransferManager(this.s3Client);
    }

    //@Autowired
    //private AhaConfig ahaConfig;

    /**
     * The main method is the entry point of the program.
     * It retrieves a file URL, extracts the file name from the URL,
     * and uses it to read a list of VINs from an Amazon S3 bucket.
     * The VIN list is then printed to the console.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        String fileUrl = "https://s3-us-west-2.amazonaws.com/ota-poc/ota-vins/sampleVinUpload.txt";
        System.out.println("fileUrl = " + fileUrl);
        String[] parts = fileUrl.split("/");
        fileUrl = parts[parts.length - 1];
        System.out.println("fileUrl = " + fileUrl);
        HcpAmazonS3Client amazonS3Client = new HcpAmazonS3Client();
        List<String> vinList =
            amazonS3Client.readVinListFilesFromS3(HcpAmazonS3Client.VIN_LIST_DIR_NAME + "/" + fileUrl);
        System.out.println("vinList:" + vinList);

    }

    //public void loadAmazonS3Client() {}

    /**
     * Finds a file in the specified Amazon S3 bucket if it exists.
     *
     * @param bucketName The name of the Amazon S3 bucket.
     * @param fileName   The name of the file to search for.
     * @return An empty string if the file exists in the bucket, otherwise null.
     */
    public String findFileInS3IfExists(String bucketName, String fileName) {
        return "";

    }

    /**
     * Uploads a file to the specified Amazon S3 bucket.
     *
     * @param file       the file to be uploaded
     * @param bucketName the name of the Amazon S3 bucket
     * @param fileName   the name of the file in the bucket
     * @return a string representing the result of the upload process
     */
    public String uploadFile(File file, String bucketName, String fileName) {
        return "";
    }

    /**
     * Uploads a stream of data to the specified Amazon S3 bucket with the given file name.
     *
     * @param stream     The input stream containing the data to be uploaded.
     * @param headers    An array of headers to be included in the upload request.
     * @param bucketName The name of the Amazon S3 bucket to upload the data to.
     * @param fileName   The name of the file to be created in the Amazon S3 bucket.
     * @return A string representing the result of the upload operation.
     */
    public String uploadStream(InputStream stream, String[] headers, String bucketName, String fileName) {

        return null;
    }

    //private String uploadToS3(File file, InputStream stream, Header[] headers, String bucketName, String fileName) {
    //
    //bucketName = this.getBucketNameForEnv(bucketName);
    //try {
    //
    //if (!s3Client.doesBucketExist(bucketName)) {
    //
    //s3Client.createBucket(bucketName);
    //}
    //
    //if (file != null) {
    //
    //log.info("Uploading a new file [" + fileName + "] to S3 from a file");
    //s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)
    //.withCannedAcl(CannedAccessControlList.PublicRead));
    //} else if (stream != null) {
    //
    //log.info("Uploading a input stream to S3");
    //ObjectMetadata metaData = new ObjectMetadata();
    //if (headers != null && headers.length > 0) {
    //
    //for (Header header : headers) {
    //
    //metaData.setHeader(header.getName(), header.getValue());
    //}
    //}
    //s3Client.putObject(new PutObjectRequest(bucketName, fileName, stream, metaData)
    //.withCannedAcl(CannedAccessControlList.PublicRead));
    //}
    //} catch (AmazonServiceException ase) {
    //
    //log.error("Caught an AmazonServiceException, which means your request made it "
    //+ "to Amazon S3, but was rejected with an error response for some reason.", ase);
    //return null;
    //} catch (AmazonClientException ace) {
    //
    //log.error(
    //"Caught an AmazonClientException, which means the client encountered "
    //+ "a serious internal problem while trying to communicate with S3, "
    //+ "such as not being able to access the network.",
    //ace);
    //return null;
    //}
    //
    //return getFileLocation(bucketName, fileName);
    //}

    /**
     * Reads the VIN list files from Amazon S3.
     *
     * @param fileUrl The URL of the file to read.
     * @return A list of VIN list files.
     */
    public List<String> readVinListFilesFromS3(String fileUrl) {
        return null;
    }
    //
    //public String uploadToS3UsingTransferManager(String subFolderName, String filePath) throws
    // AmazonServiceException, AmazonClientException, InterruptedException{
    //    String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
    //    String keyName  = subFolderName+"/"+fileName;
    //    // TransferManager processes all transfers asynchronously,
    //    // so this call will return immediately.
    //    long start = System.currentTimeMillis();
    //PutObjectRequest putObjectRequestWithCannedAcl = new PutObjectRequest(otaBucket, keyName,new File(filePath))
    // .withCannedAcl(CannedAccessControlList.PublicRead);
    //    final Upload upload = transferManager.upload(putObjectRequestWithCannedAcl);
    //// Or you can block and wait for the upload to finish
    //upload.waitForCompletion();
    ////close the connection - if closed next file upload is failing , have one transferManager instance throught the
    // application
    ////transferManager.shutdownNow();
    //long end = System.currentTimeMillis();
    //log.info("time taken to upload "+fileName+" is "+(end - start)+" ms");
    //log.info("Upload complete.");
    //return getFileLocation(otaBucket, keyName);
    //}

    /**
     * Enum representing the S3 buckets used in the application.
     */
    @Resource(name = "hcpCampaignMgmtEnvConfig")


    public enum S3Buckets {

        OTA_BUCKET(HcpAmazonS3Client.OTA_BUCKET),
        CONFIG_BUCKET(HcpAmazonS3Client.CONFIG_BUCKET);

        private String bucketName;

        /**
         * Constructs a new S3Buckets enum with the specified bucket name.
         *
         * @param bucketName the name of the S3 bucket
         */
        private S3Buckets(String bucketName) {
            this.bucketName = bucketName;
        }

        /**
         * Returns the name of the S3 bucket.
         *
         * @return the name of the S3 bucket
         */
        public String getBucketName() {
            return bucketName;
        }
    }

}
