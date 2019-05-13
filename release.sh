#!/usr/bin/env bash
./gradlew clean build test check uploadArchives closeAndReleaseRepository
