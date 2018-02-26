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

package org.wso2.carbon.is.migration.service.v550.dao;

import org.wso2.carbon.is.migration.service.v540.bean.OAuthConsumerApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.is.migration.service.v550.SQLConstants.RETRIEVE_ALL_CONSUMER_APPS;

public class OAuthDAO {

    private static OAuthDAO instance = new OAuthDAO();

    private OAuthDAO() {

    }

    public static OAuthDAO getInstance() {

        return instance;
    }

    /**
     * Get all consumer apps.
     *
     * @param connection Database connection
     * @return List of consumer apps
     * @throws SQLException SQLException
     */
    public List<OAuthConsumerApp> getAllOAuthConsumerAppsOfTenant(Connection connection,
                                                                  int tenantId) throws SQLException {

        List<OAuthConsumerApp> consumerApps = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(RETRIEVE_ALL_CONSUMER_APPS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                consumerApps.add(new OAuthConsumerApp(resultSet.getString("CONSUMER_KEY"),
                        resultSet.getInt("TENANT_ID")));
            }
            connection.commit();
        }
        return consumerApps;
    }

}
