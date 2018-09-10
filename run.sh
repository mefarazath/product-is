#!/usr/bin/env bash
IS_SOURCE_HOME=/home/farazath/IS/product-is
IS_VERSION=wso2is-5.7.0

mvn clean install -Dmaven.test.skip=true
cd /tmp
rm -rf $IS_VERSION.

unzip $IS_SOURCE_HOME/modules/distribution/target/$IS_VERSION.zip
cd $IS_VERSION
cd bin
sh wso2server.sh -DosgiConsole