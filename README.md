# JChat server and client

## Requirements

Building and running requires [JDK 13](https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html).

## How to run the server

- Windows : `./gradlew.bat :jchat-server:run`
- Linux : `./gradlew :jchat-server:run`

The server will look for a configuration file `config.json` in its running directory
(`./jchat-server` by default). If it can't find one it will generate a default one.

On shutdown, the server will save user and messages data into two files (`users.json` and `messages.json` respectively)
in its running directory.

## How to run the client

- Windows : `./gradlew.bat :jchat-client:run`
- Windows : `./gradlew :jchat-client:run`
