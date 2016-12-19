Contributing guidelines
=======================

Looking for help?
-----------------

You have the following options:
- Check out documentation in `README.md` file and read it carefully. It covers almost everything what is important.
- Browse JavaDoc at http://pwittchen.github.io/ReactiveNetwork/
- [Ask the question on StackOverlow](http://stackoverflow.com/questions/ask?tags=reactivenetwork).
- Provide detailed information about your problem and environment and then ask the question in the new GitHub issue here.

Found a bug?
------------

Provide detailed steps to reproduce and make sure this bug is not related to your custom project or environment.
Ideally, create Pull Request with failing unit test (or more tests).

Want a few feature or improvement?
----------------------------------

This is tiny library, so I would avoid overcomplicating it, but if you think a new feature
would be useful and make this project better, then create a new issue.
After that, we can discuss it and work on a Pull Request.

Want to create a Pull Request?
------------------------------

Before creating new Pull Request, please create a new issue and discuss the problem.
If we agree that PR will be reasonable solution, then fork repository, create a separate branch
and work on a feature or bug-fix on this branch. When you're done, make sure that project passes
static code analysis verification with `./gradlew check` command. Moreover, format your code according to
[SquareAndroid](https://github.com/square/java-code-styles) Java Code Styles.
When you performed more commits than one, squash them into one within a single PR (you can use http://rebaseandsqua.sh/ website).
Make sure that your commit message is descriptive enough. If not, then use `git commit --amend` command and change it.
