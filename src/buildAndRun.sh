#!/bin/bash

javac -cp ../lib/json-20230227.jar Whisper.java ChatGPT.java newGuiGPT.java
java -cp ../lib/json-20230227.jar:. newGuiGPT 