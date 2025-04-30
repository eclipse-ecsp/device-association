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
    SERVICE_AUTH_REST_SET_READY_TO_ACTIVATE_DEVICE("service_auth_rest_set_ready_to_activate_device"),
    SERVICE_AUTH_REST_DEACTIVATE_DEVICE("service_auth_rest_deactivate_device"),
    SERVICE_USER_AUTH_REST_URL_BASE("service_user_auth_rest_url_base"),
    SERVICE_USER_AUTH_REST_LOGIN("service_user_auth_rest_login"),
    SERVICE_USER_AUTH_USERNAME("service_user_auth_username"),
    SERVICE_USER_AUTH_PASSWORD("service_user_auth_password"),
    SERVICE_NOTIFICATION_KAFKA_HOST("service_notification_kafka_host"),
    SERVICE_NOTIFICATION_KAFKA_TOPICNAME("service_notification_kafka_topicname"),
    SERVICE_DEVICEINFO_REST_DEVICE_INFO_CHANGE("service_deviceinfo_rest_deviceinfo_stateupdate"),
    KAFKA_EVENT_TOPIC("kafka_event_topic"),
    KAFKA_VIN_TOPIC("kafka_vin_topic"),
    KAFKA_ASSET_ACTIVATION_TOPIC("kafka_asset_activation_topic"),

    SERVICE_NOTIFICATION_KAFKA_SSL_ENABLE("service_notification_kafka_ssl_enable"),
    SERVICE_NOTIFICATION_KAFKA_SSL_CLIENT_AUTH("service_notification_kafka_ssl_client_auth"),
    SERVICE_NOTIFICATION_KAFKA_KEYSTORE("service_notification_kafka_client_keystore"),
    SERVICE_NOTIFICATION_KAFKA_KEYSTORE_PASSWORD("service_notification_kafka_client_keystore_password"),
    SERVICE_NOTIFICATION_KAFKA_KEY_PASSWORD("service_notification_kafka_client_key_password"),
    SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE("service_notification_kafka_client_truststore"),
    SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE_PASSWORD("service_notification_kafka_client_truststore_password"),

    SERVICE_DEVICEINFO_REST_URL_BASE("service_deviceinfo_rest_url_base"),
    SERVICE_DEVICEINFO_REST_DEVICE_INFO("service_deviceinfo_rest_device_info"),
    SSO_SECURITY_KEY("sso_security_key"),
    SERVICE_SDP_PROFILE_URL_BASE("service_sdp_profile_url_base"),
    SERVICE_SDP_REST_PROFILE("service_sdp_rest_profile"),
    SERVICE_SDP_USER_FOR_STATE_CHANGE("service_sdp_user_for_state_change", "bob_argo"),

    SERVICE_IS_BASEURL("service_is_baseurl"),
    SERVICE_IS_FILTER_BY_USERNAME_URL("service_is_filter_by_username_url"),
    SERVICE_IS_USER_NAME("service_is_user_name"),
    SERVICE_IS_PASSWORD("service_is_password"),

    // ENV_PROFILE("env_profile"),
    ENV_PROFILE("env_profile"),
    KINESIS_ECS_REGION("kinesis_ecs_region"),
    KINESIS_STREAM_NAME("kinesis_stream_name"),

    // Oauth related properties
    OAUTH2_BASE_URL("OAuth2_base_url"),
    OAUTH2_BASIC_AUTH_HEADER("OAuth2_basic_auth_header"),
    OAUTH2_INTROPSECT_METHOD("OAuth2_intropsect_method"),
    SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE("service_auth_rest_v2_deactivate_device"),

    // Vault related properties
    VAULT_SERVER_IP_ADDRESS("vault_server_ip_address"),
    VAULT_SERVER_PORT("vault_server_port"),
    VAULT_ENV("environment"),
    POSTGRES_USERNAME("postgres_username"),
    POSTGRES_PASSWORD("postgres_password"),
    SECRET_VAULT_ENABLE_FLG("secerets_vault_enable_flg"),
    POSTGRES_VAULT_ENABLE_FLG("postgres_vault_enable_flg"),
    POSTGRES_URL("postgres_url"),
    POSTGRES_DRIVER_CLASS_NAME("postgres_driver_class_name"),
    POSTGRES_VAULT_LEASE_INTERVAL_GAP("postgres_vault_leaseIntervalGap"),
    POSTGRES_VAULT_REFRESH_CHECK_INTERVAL("postgres_vault_refreshCheckInterval"),

    // Device message properties
    DEVICE_MESSAGE_ENABLED("device_message_enabled"),
    DEVICE_MQTT_MESSAGE_BASE_URL("device_mqtt_message_base_url"),
    DEVICE_MQTT_MESSAGE_BASE_URL_VERSION("device_mqtt_message_base_url_version"),

    // Internal users to by pass the user validation
    WHITE_LIST_USERS("white_list_users"),

    // SpringAuth related properties
    SPRING_AUTH_CLIENT_ID("client_id"),
    SPRING_AUTH_CLIENT_SECRET("client_secret"),
    SPRING_AUTH_SERVICE_URL("spring_auth_service_url"),
    SPRING_AUTH_BASE_URL("spring_auth_base_url"),

    //vehicle profile api endpoints and properties
    VEHICLE_PROFILE_BASE_URL("vehicle_profile_base_url"),
    VEHICLE_PROFILE_BASE_URL_VERSION("vehicle_profile_base_url_version"),
    VEHICLE_PROFILE_TERMINATE_BASE_URL_VERSION("vehicle_profile_terminate_base_url_version"),
    VEHICLE_PROFILE_TERMINATE("vehicle_profile_terminate"),
    VEHICLE_PROFILE_VIN_DECODER("vehicle_profile_vin_decoder"),
    ALLOWED_DEVICE_TYPES("allowed_device_types"),

    //saas-api endpoints
    SAAS_API_BASE_URL("saas_api_base_url"),
    SAAS_API__BASE_URL_VERSION("saas_api_base_url_version"),


    // SWM Vehicles
    SWM_BASE_URL("swm_base_url"),
    SWM_LOGIN_API_URL("swm_login_api_url"),
    SWM_UPDATE_API("swm_update_api"),
    SWM_DELETE_API("swm_delete_api"),
    SWM_VEHICLE_MODELS_API("swm_vehicle_models"),
    SWM_VEHICLES_API("swm_vehicles"),
    SWM_PASSWORD("swm_password"),
    SWM_USERNAME("swm_username"),
    SWM_DOMAIN("swm_domain"),
    SWM_DOMAIN_ID("swm_domain_id"),
    SWM_VEHICLE_MODEL_ID("swm_vehicle_model_id"),
    DEVICE_CREATION_TYPE("device_creation_type"),
    SWM_INTEGRATION_ENABLED("swm_integration_enabled"),

    INITIAL_POOL_SIZE("initial_pool_size"),
    MIN_POOL_SIZE("min_pool_size"),
    MAX_POOL_SIZE("max_pool_size"),
    MAX_IDLE_TIME("max_idle_time"),
    ACQUIRE_INCREMENT("acquire_increment"),
    IDLE_CONNECTION_TEST_PERIOD("idle_connection_test_period"),
    SUPPORTED_DEVICE_INFO_REQUEST_SIZE("supported_device_info_request_size"),
    SUPPORTED_DEVICE_INFO_PARAMS("supported_device_info_params"),
    API_REGISTRY_ENABLED("api_registry_enabled"),
    SPRING_APPLICATION_VERSION("spring_application_version"),
    OPENAPI_PATH_INCLUDE("openapi_path_include"),
    SERVER_PORT("server_port"),
    SPRING_APPLICATION_NAME("spring_application_name"),
    SPRING_APPLICATION_SERVICENAME("spring_application_servicename"),
    API_SECURITY_ENABLED("api_security_enabled"),
    OPENAPI_PATH_EXCLUDE("openapi_path_exclude"),
    API_CONTEXT_PATH("api_context-path"),
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
