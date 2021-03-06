#!/bin/bash
set -e

mvn install && mvn dockerfile:build -pl FROST-Server.HTTP,FROST-Server.MQTT,FROST-Server.MQTTP
mvn dockerfile:tag@tag-version -pl FROST-Server.HTTP,FROST-Server.MQTT,FROST-Server.MQTTP

if [ "${TRAVIS_BRANCH}" = "master" ]; then
  mvn dockerfile:push@push-latest -Ddockerfile.useMavenSettingsForAuth=true -pl FROST-Server.HTTP,FROST-Server.MQTT,FROST-Server.MQTTP --settings travis-settings.xml
fi

mvn dockerfile:push@push-version -Ddockerfile.useMavenSettingsForAuth=true -pl FROST-Server.HTTP,FROST-Server.MQTT,FROST-Server.MQTTP --settings travis-settings.xml