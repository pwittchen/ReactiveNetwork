Releasing Guidelines
====================

In order to release new version of the library, we need to perform the following operations:
- create new release issue on GitHub
- prepare release notes and put them to the issue
- update javaDocs by running `./update_javadocs.sh` script
- verify, commit and push changes to `gh-pages` branch
- checkout to the appropriate branch (`RxJava1.x` or `RxJava2.x`)
- bump library version (`VERSION_NAME` and `VERSION_CODE`) in `gradle.properties` file
- commit and push the changes
- run command: `./release.sh`
- wait for the Maven Sync (up to 48 hours)
- when sync is done, checkout to the `RxJava1.x` or `RxJava2.x` branch
- update `CHANGELOG.md` file with new release version, current date and release notes
- bump library version in "Download" section in `README.md` file
- update website with docs by running `./update_docs.sh` script
- create new tagged GitHub release with name the same as `VERSION_NAME` from `gradle.properties` and release notes
