#!/bin/bash
clear
echo "start..."
mvn clean compile assembly:single
java -jar target/jar/JavaAugmentation.jar > target/output.txt
echo "...end"
