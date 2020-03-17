/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.identity.integration.test.rest.api.server.application.management.v1;

import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.wso2.identity.integration.test.rest.api.server.application.management.v1.Utils.assertNotBlank;
import static org.wso2.identity.integration.test.rest.api.server.application.management.v1.Utils.extractApplicationIdFromLocationHeader;

/**
 * Tests for claim configuration related operations an application.
 */
public class ApplicationManagementClaimConfigurationTest extends ApplicationManagementBaseTest {

    private String requestBodyPath;
    private String createdAppId;

    @Factory(dataProvider = "restAPIUserConfigProvider")
    public ApplicationManagementClaimConfigurationTest(TestUserMode userMode, String request) throws Exception {

        super(userMode);
        this.requestBodyPath = request;
    }

    @DataProvider(name = "restAPIUserConfigProvider")
    public static Object[][] restAPIUserConfigProvider() {

        return new Object[][]{
                {TestUserMode.SUPER_TENANT_ADMIN, "create-application-with-local-claims.json"},
                {TestUserMode.SUPER_TENANT_ADMIN, "create-application-with-custom-claims.json"},

                {TestUserMode.TENANT_ADMIN, "create-application-with-local-claims.json"},
                {TestUserMode.TENANT_ADMIN, "create-application-with-custom-claims.json"},
        };
    }

    @Test
    public void createApplication() throws Exception {

        String body = readResource(requestBodyPath);
        Response responseOfPost = getResponseOfPost(APPLICATION_MANAGEMENT_API_BASE_PATH, body);
        responseOfPost.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .header(HttpHeaders.LOCATION, notNullValue());

        String location = responseOfPost.getHeader(HttpHeaders.LOCATION);
        createdAppId = extractApplicationIdFromLocationHeader(location);
        assertNotBlank(createdAppId);
    }

    @Test(dependsOnMethods = {"createApplication"})
    public void testGetApplicationById() throws Exception {

        getResponseOfGet(APPLICATION_MANAGEMENT_API_BASE_PATH + "/" + createdAppId)
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        // TODO assert if the configured claims are returned
    }

    @Test(dependsOnMethods = {"testGetApplicationById"})
    public void testDeleteApplicationById() throws Exception {

        getResponseOfDelete(APPLICATION_MANAGEMENT_API_BASE_PATH + "/" + createdAppId)
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        // Verify that the application is not available.
        getResponseOfGet(APPLICATION_MANAGEMENT_API_BASE_PATH + "/" + createdAppId)
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
