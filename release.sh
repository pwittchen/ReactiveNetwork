#!/usr/bin/env bash

echo "### STAGE 1: updating docs"
sh ./update_docs.sh
git push
git checkout RxJava2.x

echo "### STAGE 2: updating javadocs"
sh ./update_javadocs.sh
git push
git checkout RxJava2.x

echo "### STAGE 3: uploading archives"
./gradlew uploadArchives

echo "### STAGE 4: closing and releasing repository"
./gradlew closeAndReleaseRepository

echo "### RELEASE DONE"
