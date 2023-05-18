#!/bin/bash

rm -rf ./server/
javac -d bin -cp lib/org.jar src/*.java
java -cp bin:lib/org.jar server.src.JavaServer