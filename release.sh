#!/usr/bin/env bash
./gradlew clean build test uploadArchives closeAndReleaseRepository
