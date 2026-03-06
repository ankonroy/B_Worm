#!/bin/sh

echo "===== DEBUG: Current directory contents ====="
pwd
ls -la
echo "===== DEBUG: Looking for pom.xml specifically ====="
find /app -name "pom.xml" -type f | head -5
echo "===== DEBUG: End of debug ====="

# Background process to watch for changes and compile
while inotifywait -r -e modify /app/src/main/; do
  echo "Change detected, recompiling..."
  mvn compile -o -DskipTests
done >/dev/null 2>&1 &

# Use the FULLY QUALIFIED plugin name to run Spring Boot
echo "Starting Spring Boot application..."
mvn -f /app/pom.xml org.springframework.boot:spring-boot-maven-plugin:4.0.3:run