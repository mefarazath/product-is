/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.is.migration.service.v550.migrator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.core.migrate.MigrationClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.is.migration.internal.ISMigrationServiceDataHolder;
import org.wso2.carbon.is.migration.service.Migrator;
import org.wso2.carbon.is.migration.service.v530.ISMigrationException;
import org.wso2.carbon.is.migration.service.v540.bean.OAuthConsumerApp;
import org.wso2.carbon.is.migration.service.v550.dao.OAuthDAO;
import org.wso2.carbon.is.migration.util.Constant;
import org.wso2.carbon.is.migration.util.Utility;
import org.wso2.carbon.user.api.Tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ApplicationCertificateMigrator extends Migrator {

    private static final Log log = LogFactory.getLog(ApplicationCertificateMigrator.class);

    @Override
    public void migrate() throws MigrationClientException {

        try {
            log.info(Constant.MIGRATION_LOG + "Migration starting on Service Provider Application Certs.");
            migrateApplicationCertificateOfOAuthApps();
        } catch (Exception e) {
            String message = "Error occurred while migrating Application Certificate of Service Providers.";
            if (isContinueOnError()) {
                log.error(message, e);
            } else {
                throw new MigrationClientException(message, e);
            }
        }
    }

    private void migrateApplicationCertificateOfOAuthApps() throws Exception {

        // Get all active tenants
        List<Tenant> tenants = Utility.getAllTenantsIncludingSuperTenant();
        for (Tenant tenant : tenants) {
            String tenantDomain = tenant.getDomain();
            if (isMigrationNotApplicableForTenant(tenant)) {
                log.info("Skipping Application Certificate Migration for Inactive tenant : " + tenantDomain);
                continue;
            }
            handleApplicationCertMigration(tenant);
        }
    }

    private void handleApplicationCertMigration(Tenant tenant) throws Exception {

        // Get all OAuth apps belonging to the tenant
        int tenantId = tenant.getId();
        String tenantDomain = tenant.getDomain();
        List<OAuthConsumerApp> consumerAppsOfTenant;
        try (Connection connection = getDataSource().getConnection()) {
            consumerAppsOfTenant = OAuthDAO.getInstance().getAllOAuthConsumerAppsOfTenant(connection, tenantId);
        }

        for (OAuthConsumerApp oauthApp : consumerAppsOfTenant) {
            ServiceProvider sp = OAuth2Util.getServiceProvider(oauthApp.getConsumerKey(), tenantDomain);
            if (StringUtils.isNotBlank(sp.getCertificateContent())) {
                // LOG AND SKIP
                String msg = "Service Provider: %s of %s tenant already has an application certificate " +
                        "configured. Skipping Application Certificate Migration.";
                log.info(String.format(msg, sp.getApplicationName(), tenantDomain));
            } else {
                handleApplicationCertMigration(sp, oauthApp.getConsumerKey(), tenantDomain);
            }
        }
    }

    private void handleApplicationCertMigration(ServiceProvider serviceProvider,
                                                String publicCertAlias,
                                                String spTenantDomain) {

        // Load the tenant keystore

        String msg = "Application Certificate Migration completed for Service Provider: %s of tenantDomain: %s";
        log.info(String.format(msg, serviceProvider.getApplicationName(), spTenantDomain));
        // Check whether the cert exists

        // Update the Service Provider With Cert
    }

    public static ServiceProvider getServiceProvider(String clientId,
                                                     String tenantDomain) throws Exception {

        ApplicationManagementService applicationMgtService = ISMigrationServiceDataHolder.getApplicationManagementService();

        try {
            return applicationMgtService.getServiceProviderByClientId(clientId, "oauth2", tenantDomain);
        } catch (IdentityApplicationManagementException ex) {
            throw new ISMigrationException("Error while obtaining the service provider for client_id: " + clientId +
                    " of tenantDomain: " + tenantDomain, ex);
        }
    }

    private boolean isMigrationNotApplicableForTenant(Tenant tenant) {
        // Skipping migration for inactive tenants is enabled and the tenant is inactive.
        return isIgnoreForInactiveTenants() && !tenant.isActive();
    }
}
