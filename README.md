# VelocityGlobalPing
Sends the ping response based on configurations defined in velocity.toml if `ping-passthrough = "all"` and the client is connecting from specified virtual host.

## Building
1. Clone the project
2. Run `./gradlew build`
3. Put the jar from `build/libs/VelocityGlobalPing-version.jar` into the Velocity server

## Commands
There is no command. You must restart the Velocity server to reload configuration.

## Configuration

`plugins/velocity-global-ping/config.yml`
```yml
servers:
- example.com
- virtual.host.example.com
```
