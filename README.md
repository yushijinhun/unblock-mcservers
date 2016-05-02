# unblock-mcservers
Unblock the blacklisted Minecraft servers. (see [Mojang is blocking certain servers as of 1.9.3 r2. - Reddit](https://www.reddit.com/r/Minecraft/comments/4h3c6u/mojang_is_blocking_certain_servers_as_of_193_r2/))

# Compile
```
gradle build
```
Then copy out `build/libs/unblock-mcservers.jar`.

# Usage
Add the following parameter into the JVM arguments.
```
-javaagent:</path/to/unblock-mcservers.jar>
```

