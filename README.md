# Most Active Cookie Finder

This program finds the most active cookies from a log file for a given date.

## Problem Description

Given a CSV file containing cookie activity logs in the following format:

```
cookie,timestamp
AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00
SAZuXPGUrfbcn5UA,2018-12-09T10:13:00+00:00
```

You are to implement a command-line program that returns the most active cookie(s) for a specified date.  
If multiple cookies meet that criterion, return all of them on separate lines.

## Requirements

- Kotlin 1.9+
- JDK 17+
- [Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/) (this project uses Maven)

## How to Run

Build and run from IntelliJ, or via command line:

```bash
kotlin Main.kt -f cookie_log.csv -d 2018-12-09
```

## Running Tests

Tests are written using **JUnit 5**.

To run tests from IntelliJ:
- Right-click `CookieCommandTest.kt` and select `Run`.

To run from command line (if using Maven):

```bash
./mvnw test
```

## Sample Output

```bash
$ kotlin Main.kt -f cookie_log.csv -d 2018-12-09

AtY0laUfhglK3lC7
```

## Author

Maciej Wrzesień