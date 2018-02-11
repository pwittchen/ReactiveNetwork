#!/usr/bin/env bash
git checkout gh-pages
git show RxJava1.x:README.md >docs/RxJava1.x/README.md
git show RxJava2.x:README.md >docs/RxJava2.x/README.md
git add -A
git commit -m "updating docs"
echo "docs updated, now you can push your changes"
