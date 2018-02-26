/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.is.migration.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.core.migrate.MigrationClient;
import org.wso2.carbon.is.migration.MigrationClientImpl;
import org.wso2.carbon.user.core.service.RealmService;


@Component(
        name = "org.wso2.carbon.is.migration.client",
        immediate = true
)
public class ISMigrationServiceComponent {

    private static final Log log = LogFactory.getLog(ISMigrationServiceComponent.class);

    /**
     * Method to activate bundle.
     *
     * @param context OSGi component context.
     */
    protected void activate(ComponentContext context) {
        try {
            ISMigrationServiceDataHolder.setIdentityOracleUser(System.getProperty("identityOracleUser"));
            ISMigrationServiceDataHolder.setUmOracleUser(System.getProperty("umOracleUser"));
            context.getBundleContext().registerService(MigrationClient.class, new MigrationClientImpl(), null);
            if(log.isDebugEnabled()) {
                log.debug("WSO2 IS migration bundle is activated");
            }
        } catch (Throwable e) {
            log.error("Error while initiating Config component", e);
        }

    }

    /**
     * Method to deactivate bundle.
     *
     * @param context OSGi component context.
     */
    protected void deactivate(ComponentContext context) {
        if(log.isDebugEnabled()) {
            log.debug("WSO2 IS migration bundle is deactivated");
        }
    }


    /**
     * Method to set realm service.
     *
     * @param realmService service to get tenant data.
     */
    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {
        if(log.isDebugEnabled()) {
            log.debug("Setting RealmService to WSO2 IS Config component");
        }
        ISMigrationServiceDataHolder.setRealmService(realmService);
    }

    /**
     * Method to unset realm service.
     *
     * @param realmService service to get tenant data.
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting RealmService from WSO2 IS Config component");
        }
        ISMigrationServiceDataHolder.setRealmService(null);
    }

    /**
     * Sets ApplicationManagement Service.
     *
     * @param applicationManagementService An instance of ApplicationManagementService
     */
    @Reference(
            name = "application.mgt.service",
            service = ApplicationManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationManagementService"
    )
    protected void setApplicationManagementService(ApplicationManagementService applicationManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting ApplicationManagement Service.");
        }
        ISMigrationServiceDataHolder.setApplicationManagementService(applicationManagementService);
    }

    /**
     * Unsets ApplicationManagement Service.
     *
     * @param applicationManagementService An instance of ApplicationManagementService
     */
    protected void unsetApplicationManagementService(ApplicationManagementService applicationManagementService) {

        if (log.isDebugEnabled()) {
            log.debug("Un setting ApplicationManagement Service.");
        }
        ISMigrationServiceDataHolder.setApplicationManagementService(null);
    }
}
