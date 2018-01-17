# JSGLR2

This project requires a Spoofax Build: http://www.metaborg.org/en/latest/source/dev/build.html.

Build project and run tests:

```
mvn clean verify
```

Build project and publish locally while skipping tests:

```
mvn clean install -Dmaven.test.skip=true
```

Test specific class:

```
mvn clean install -Dtest=TestClass
```
