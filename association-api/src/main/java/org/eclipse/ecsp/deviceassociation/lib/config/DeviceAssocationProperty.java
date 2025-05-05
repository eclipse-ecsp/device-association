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

package org.eclipse.ecsp.deviceassociation.lib.config;

import org.eclipse.ecsp.common.config.EnvConfigProperty;
import org.eclipse.ecsp.common.config.EnvConfigPropertyType;

/**
 * This enum represents the properties used in the device association configuration.
 * Each property corresponds to a specific configuration value that can be retrieved from the environment.
 * The enum values provide the name of the property in the configuration file, the default value (if any),
 * and the type of the property.
 */
public enum DeviceAssocationProperty implements EnvConfigProperty {

    /**
     *  hcp-deviceassociation-lib DEVELOPMENT.
     * # ###############################
     *
     * <p>service.auth.rest_url_base=http://localhost:4321/hcp-auth-webapp/
     * service.user.auth.rest_url_base=http://localhost:4321/hcp-userauth-
     * webapp/ service.deviceinfo.rest_url_base=http://localhost:4321/hcp-
     * deviceinfoquery-webapp/ service.notification.kafka.host=localhost:9092
     *
     * <p>service.deviceinfo.rest.device_info=/getSpecByID/
     * service.auth.rest.deactivate_device=/device/deactivate
     * service.auth.rest.set_ready_to_activate_device=/device/setReadyToActivate
     * service.user.auth.rest.login=/user/login
     * service.user.auth.username=device_association_sys_user
     * service.user.auth.password=******
     * service.notification.kafka.topicname=device_association
     *
     * <p>service.sdp.profile_url_base=http://sdpesbs4.ahanet.net:8281/
     *
     * <p>service.sdp.rest.profile=/sdp/1.1/user/profile
     * service.sdp.iss.baseurl=https://admin:admin@sdpiss4.ahanet.net:443/
     *
     * <p>service.sdp.iss.filter_by_username_url=wso2/scim/Users?filter=userNameeq?
     */
    SERVICE_AUTH_REST_URL_BASE("service_auth_rest_url_base"),

    /**
     * Endpoint for setting a device as ready to activate.
     */
    SERVICE_AUTH_REST_SET_READY_TO_ACTIVATE_DEVICE("service_auth_rest_set_ready_to_activate_device"),

    /**
     * Endpoint for deactivating a device.
     */
    SERVICE_AUTH_REST_DEACTIVATE_DEVICE("service_auth_rest_deactivate_device"),

    /**
     * Base URL for the user authentication service REST API.
     */
    SERVICE_USER_AUTH_REST_URL_BASE("service_user_auth_rest_url_base"),

    /**
     * Endpoint for user login.
     */
    SERVICE_USER_AUTH_REST_LOGIN("service_user_auth_rest_login"),

    /**
     * Username for user authentication.
     */
    SERVICE_USER_AUTH_USERNAME("service_user_auth_username"),

    /**
     * Password for user authentication.
     */
    SERVICE_USER_AUTH_PASSWORD("service_user_auth_password"),

    /**
     * Kafka host for notifications.
     */
    SERVICE_NOTIFICATION_KAFKA_HOST("service_notification_kafka_host"),

    /**
     * Kafka topic name for notifications.
     */
    SERVICE_NOTIFICATION_KAFKA_TOPICNAME("service_notification_kafka_topicname"),

    /**
     * Endpoint for updating device information state.
     */
    SERVICE_DEVICEINFO_REST_DEVICE_INFO_CHANGE("service_deviceinfo_rest_deviceinfo_stateupdate"),

    /**
     * Kafka topic for events.
     */
    KAFKA_EVENT_TOPIC("kafka_event_topic"),

    /**
     * Kafka topic for VIN-related events.
     */
    KAFKA_VIN_TOPIC("kafka_vin_topic"),

    /**
     * Kafka topic for asset activation events.
     */
    KAFKA_ASSET_ACTIVATION_TOPIC("kafka_asset_activation_topic"),

    /**
     * Flag to enable SSL for Kafka notifications.
     */
    SERVICE_NOTIFICATION_KAFKA_SSL_ENABLE("service_notification_kafka_ssl_enable"),

    /**
     * Flag to enable client authentication for Kafka SSL.
     */
    SERVICE_NOTIFICATION_KAFKA_SSL_CLIENT_AUTH("service_notification_kafka_ssl_client_auth"),

    /**
     * Path to the Kafka client keystore.
     */
    SERVICE_NOTIFICATION_KAFKA_KEYSTORE("service_notification_kafka_client_keystore"),

    /**
     * Password for the Kafka client keystore.
     */
    SERVICE_NOTIFICATION_KAFKA_KEYSTORE_PASSWORD("service_notification_kafka_client_keystore_password"),

    /**
     * Password for the Kafka client key.
     */
    SERVICE_NOTIFICATION_KAFKA_KEY_PASSWORD("service_notification_kafka_client_key_password"),

    /**
     * Path to the Kafka client truststore.
     */
    SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE("service_notification_kafka_client_truststore"),

    /**
     * Password for the Kafka client truststore.
     */
    SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE_PASSWORD("service_notification_kafka_client_truststore_password"),

    /**
     * Base URL for the device information service REST API.
     */
    SERVICE_DEVICEINFO_REST_URL_BASE("service_deviceinfo_rest_url_base"),

    /**
     * Endpoint for retrieving device information.
     */
    SERVICE_DEVICEINFO_REST_DEVICE_INFO("service_deviceinfo_rest_device_info"),

    /**
     * Security key for SSO.
     */
    SSO_SECURITY_KEY("sso_security_key"),

    /**
     * Base URL for the SDP profile service.
     */
    SERVICE_SDP_PROFILE_URL_BASE("service_sdp_profile_url_base"),

    /**
     * Endpoint for retrieving SDP user profiles.
     */
    SERVICE_SDP_REST_PROFILE("service_sdp_rest_profile"),

    /**
     * Default user for state change operations in SDP.
     */
    SERVICE_SDP_USER_FOR_STATE_CHANGE("service_sdp_user_for_state_change", "bob_argo"),

    /**
     * Base URL for the identity service.
     */
    SERVICE_IS_BASEURL("service_is_baseurl"),

    /**
     * Endpoint for filtering users by username in the identity service.
     */
    SERVICE_IS_FILTER_BY_USERNAME_URL("service_is_filter_by_username_url"),

    /**
     * Username for the identity service.
     */
    SERVICE_IS_USER_NAME("service_is_user_name"),

    /**
     * Password for the identity service.
     */
    SERVICE_IS_PASSWORD("service_is_password"),

    /**
     * Environment profile for the application.
     */
    ENV_PROFILE("env_profile"),

    /**
     * AWS region for Kinesis ECS.
     */
    KINESIS_ECS_REGION("kinesis_ecs_region"),

    /**
     * Name of the Kinesis stream.
     */
    KINESIS_STREAM_NAME("kinesis_stream_name"),

    /**
     * Base URL for OAuth2 authentication.
     */
    OAUTH2_BASE_URL("OAuth2_base_url"),

    /**
     * Basic authentication header for OAuth2.
     */
    OAUTH2_BASIC_AUTH_HEADER("OAuth2_basic_auth_header"),

    /**
     * Method for introspecting OAuth2 tokens.
     */
    OAUTH2_INTROPSECT_METHOD("OAuth2_intropsect_method"),

    /**
     * Endpoint for deactivating a device using version 2 of the API.
     */
    SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE("service_auth_rest_v2_deactivate_device"),

    /**
     * IP address of the Vault server.
     */
    VAULT_SERVER_IP_ADDRESS("vault_server_ip_address"),

    /**
     * Port of the Vault server.
     */
    VAULT_SERVER_PORT("vault_server_port"),

    /**
     * Environment for the Vault configuration.
     */
    VAULT_ENV("environment"),

    /**
     * Username for PostgreSQL.
     */
    POSTGRES_USERNAME("postgres_username"),

    /**
     * Password for PostgreSQL.
     */
    POSTGRES_PASSWORD("postgres_password"),

    /**
     * Flag to enable secrets vault.
     */
    SECRET_VAULT_ENABLE_FLG("secerets_vault_enable_flg"),

    /**
     * Flag to enable PostgreSQL vault integration.
     */
    POSTGRES_VAULT_ENABLE_FLG("postgres_vault_enable_flg"),

    /**
     * URL for PostgreSQL database.
     */
    POSTGRES_URL("postgres_url"),

    /**
     * Driver class name for PostgreSQL.
     */
    POSTGRES_DRIVER_CLASS_NAME("postgres_driver_class_name"),

    /**
     * Lease interval gap for PostgreSQL vault integration.
     */
    POSTGRES_VAULT_LEASE_INTERVAL_GAP("postgres_vault_leaseIntervalGap"),

    /**
     * Interval for checking PostgreSQL vault refresh.
     */
    POSTGRES_VAULT_REFRESH_CHECK_INTERVAL("postgres_vault_refreshCheckInterval"),

    /**
     * Flag to enable device messaging.
     */
    DEVICE_MESSAGE_ENABLED("device_message_enabled"),

    /**
     * Base URL for MQTT device messaging.
     */
    DEVICE_MQTT_MESSAGE_BASE_URL("device_mqtt_message_base_url"),

    /**
     * Version of the MQTT device messaging base URL.
     */
    DEVICE_MQTT_MESSAGE_BASE_URL_VERSION("device_mqtt_message_base_url_version"),

    /**
     * List of internal users to bypass user validation.
     */
    WHITE_LIST_USERS("white_list_users"),

    /**
     * Client ID for Spring authentication.
     */
    SPRING_AUTH_CLIENT_ID("client_id"),

    /**
     * Client secret for Spring authentication.
     */
    SPRING_AUTH_CLIENT_SECRET("client_secret"),

    /**
     * Service URL for Spring authentication.
     */
    SPRING_AUTH_SERVICE_URL("spring_auth_service_url"),

    /**
     * Base URL for Spring authentication.
     */
    SPRING_AUTH_BASE_URL("spring_auth_base_url"),

    /**
     * Base URL for the vehicle profile API.
     */
    VEHICLE_PROFILE_BASE_URL("vehicle_profile_base_url"),

    /**
     * Version of the vehicle profile API base URL.
     */
    VEHICLE_PROFILE_BASE_URL_VERSION("vehicle_profile_base_url_version"),

    /**
     * Version of the vehicle profile termination API base URL.
     */
    VEHICLE_PROFILE_TERMINATE_BASE_URL_VERSION("vehicle_profile_terminate_base_url_version"),

    /**
     * Endpoint for terminating a vehicle profile.
     */
    VEHICLE_PROFILE_TERMINATE("vehicle_profile_terminate"),

    /**
     * Endpoint for decoding a vehicle's VIN.
     */
    VEHICLE_PROFILE_VIN_DECODER("vehicle_profile_vin_decoder"),

    /**
     * List of allowed device types.
     */
    ALLOWED_DEVICE_TYPES("allowed_device_types"),

    /**
     * Base URL for the SaaS API.
     */
    SAAS_API_BASE_URL("saas_api_base_url"),

    /**
     * Version of the SaaS API base URL.
     */
    SAAS_API__BASE_URL_VERSION("saas_api_base_url_version"),

    /**
     * Base URL for the SWM service.
     */
    SWM_BASE_URL("swm_base_url"),

    /**
     * Login API URL for the SWM service.
     */
    SWM_LOGIN_API_URL("swm_login_api_url"),

    /**
     * Update API for the SWM service.
     */
    SWM_UPDATE_API("swm_update_api"),

    /**
     * Delete API for the SWM service.
     */
    SWM_DELETE_API("swm_delete_api"),

    /**
     * API for retrieving SWM vehicle models.
     */
    SWM_VEHICLE_MODELS_API("swm_vehicle_models"),

    /**
     * API for retrieving SWM vehicles.
     */
    SWM_VEHICLES_API("swm_vehicles"),

    /**
     * Password for the SWM service.
     */
    SWM_PASSWORD("swm_password"),

    /**
     * Username for the SWM service.
     */
    SWM_USERNAME("swm_username"),

    /**
     * Domain for the SWM service.
     */
    SWM_DOMAIN("swm_domain"),

    /**
     * Domain ID for the SWM service.
     */
    SWM_DOMAIN_ID("swm_domain_id"),

    /**
     * Vehicle model ID for the SWM service.
     */
    SWM_VEHICLE_MODEL_ID("swm_vehicle_model_id"),

    /**
     * Type of device creation.
     */
    DEVICE_CREATION_TYPE("device_creation_type"),

    /**
     * Flag to enable SWM integration.
     */
    SWM_INTEGRATION_ENABLED("swm_integration_enabled"),

    /**
     * Initial size of the connection pool.
     */
    INITIAL_POOL_SIZE("initial_pool_size"),

    /**
     * Minimum size of the connection pool.
     */
    MIN_POOL_SIZE("min_pool_size"),

    /**
     * Maximum size of the connection pool.
     */
    MAX_POOL_SIZE("max_pool_size"),

    /**
     * Maximum idle time for connections in the pool.
     */
    MAX_IDLE_TIME("max_idle_time"),

    /**
     * Increment size for acquiring new connections.
     */
    ACQUIRE_INCREMENT("acquire_increment"),

    /**
     * Period for testing idle connections.
     */
    IDLE_CONNECTION_TEST_PERIOD("idle_connection_test_period"),

    /**
     * Maximum size of supported device information requests.
     */
    SUPPORTED_DEVICE_INFO_REQUEST_SIZE("supported_device_info_request_size"),

    /**
     * Supported parameters for device information requests.
     */
    SUPPORTED_DEVICE_INFO_PARAMS("supported_device_info_params"),

    /**
     * Flag to enable API registry.
     */
    API_REGISTRY_ENABLED("api_registry_enabled"),

    /**
     * Version of the Spring application.
     */
    SPRING_APPLICATION_VERSION("spring_application_version"),

    /**
     * Paths to include in the OpenAPI documentation.
     */
    OPENAPI_PATH_INCLUDE("openapi_path_include"),

    /**
     * Port for the server.
     */
    SERVER_PORT("server_port"),

    /**
     * Name of the Spring application.
     */
    SPRING_APPLICATION_NAME("spring_application_name"),

    /**
     * Service name of the Spring application.
     */
    SPRING_APPLICATION_SERVICENAME("spring_application_servicename"),

    /**
     * Flag to enable API security.
     */
    API_SECURITY_ENABLED("api_security_enabled"),

    /**
     * Paths to exclude from the OpenAPI documentation.
     */
    OPENAPI_PATH_EXCLUDE("openapi_path_exclude"),

    /**
     * Context path for the API.
     */
    API_CONTEXT_PATH("api_context-path"),

    /**
     * Service name for the API registry.
     */
    API_REGISTRY_SERVICE_NAME("api_registry_service-name");

    private String nameInFile;
    private String defaultValue;
    private EnvConfigPropertyType type;

    /**
     * Constructs a DeviceAssocationProperty with the given name in the file.
     *
     * @param nameInFile the name of the property in the file
     */
    private DeviceAssocationProperty(String nameInFile) {
        this(nameInFile, null);
    }

    /**
     * Constructs a DeviceAssocationProperty with the given name in the file and default value.
     *
     * @param nameInFile the name of the property in the file
     * @param defaultValue the default value of the property
     */
    private DeviceAssocationProperty(String nameInFile, String defaultValue) {
        this(nameInFile, defaultValue, EnvConfigPropertyType.PUBLIC);
    }

    /**
     * Constructs a DeviceAssocationProperty with the given name in the file, default value, and type.
     *
     * @param nameInFile the name of the property in the file
     * @param defaultValue the default value of the property
     * @param type the type of the property
     */
    private DeviceAssocationProperty(String nameInFile, String defaultValue, EnvConfigPropertyType type) {
        this.nameInFile = nameInFile;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Returns the name of the property in the file.
     *
     * @return the name of the property in the file
     */
    @Override
    public String getNameInFile() {
        return nameInFile;
    }

    /**
     * Returns the default value of the property.
     *
     * @return the default value of the property
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the type of the property.
     *
     * @return the type of the property
     */
    @Override
    public EnvConfigPropertyType getType() {
        return type;
    }
}
