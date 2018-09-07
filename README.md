Lightweight WSO2 Identity Server for OAuth2
=========

This is a simple and light distribution focused on providing only the OAuth2/OIDC functionality.

// TODO list of supported features in the lightweight IS distribution


How to build from source?
-
1. Run ```'mvn clean install -Dmaven.test.skip=true'``` from the root directory. (We are skipping integration tests for now!)
2. You can find the distribution at modules/distribution/target directory

How to test the distribution?
-
1. Copy and extract the wso2is-5.7.x.zip from modules/distribution/target
2. Run the wso2server.sh or wso2server.bat file in the /bin directory
3. Create an oauth app using the below curl command

```
curl -k -v -X POST -H "Authorization: Basic YWRtaW46YWRtaW4=" -H"Content-Type: application/json" -d '{"redirect_uris": ["https://localhost/callback"],"client_name": "application_1","grant_types":["password"] }' "https://localhost:9443/api/identity/oauth2/dcr/v1.1/register"
```
You should get a response like,
```
{"client_name":"application_1","client_id":"BY_RlVNXzOLn_g_WIsLMNCinThEa","client_secret":"iwVMBylq6aI5Zu1lEVrfHIXiLP8a","redirect_uris":["https://localhost/callback"]}
```
4. Obtain an access token using the client_id and client_secret you obtained in step #3 using the below curl command
(replace client_id, client_secret with actual values),

```
curl -k -d "grant_type=password&username=admin&password=admin" --user client_id:client_secret https://localhost:9443/oauth2/token
```
Example:
```
curl -k -d "grant_type=password&username=admin&password=admin" --user BY_RlVNXzOLn_g_WIsLMNCinThEa:iwVMBylq6aI5Zu1lEVrfHIXiLP8a https://localhost:9443/oauth2/token -v
```
You should get the access_token and id_token in the response as below,
```
{"access_token":"20d87563-f1d7-311f-a3f6-cce54b8bc31e","refresh_token":"56375c71-6c53-33bb-86e0-db666119841d","token_type":"Bearer","expires_in":3600}
```