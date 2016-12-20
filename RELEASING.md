Releasing Guidelines
====================

In order to release new version of the library, we need to perform the following operations:
- create new release issue on GitHub
- prepare release notes and put them to the issue
- checkout to the `master` branch
- bump library version (`VERSION_NAME` and `VERSION_CODE`) in `gradle.properties` file
- commit and push the changes
- run command: `./gradlew uploadArchives`
- go to the https://oss.sonatype.org website
- log in to Sonatype
- go to "Staging Repositories" and sort by last "Updated" date and time
- close and release artifact
- copy `library/build/docs/javadoc` directory
- checkout to `gh-pages` branch
- remove old JavaDoc and paste new, generated JavaDoc there
- commit and push changes
- wait for the Maven Sync (up to 48 hours)
- when sync is done, checkout to the `master` branch
- update `CHANGELOG.md` file with new release version, current date and release notes
- bump library version in "Download" section in `README.md` file
- create new tagged GitHub release with name the same as `VERSION_NAME` from `gradle.properties` and release notes
