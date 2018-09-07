#!/usr/bin/env bash
mvn clean install
cd /tmp
rm -rf wso2is-5.7.0-beta

unzip /home/farazath/IS/product-is/modules/distribution/target/wso2is-5.7.0-beta.zip
cd wso2is-5.7.0-beta
cd bin
sh wso2server.sh -DosgiConsole