#!/usr/bin/env bash

# update javadocs for RxJava2.x
git checkout RxJava2.x
./gradlew clean androidJavadocs
git checkout gh-pages
rm -rf javadoc/RxJava2.x/*
cp -avr library/build/docs/javadoc/* ./javadoc/RxJava2.x
git add -A
git commit -m "updating JavaDoc for RxJava2.x"
rm -rf library/build/docs
echo "javadocs for RxJava2.x updated"

echo "javadocs for both RxJava1.x and RxJava2.x updated - now you can push your changes"
