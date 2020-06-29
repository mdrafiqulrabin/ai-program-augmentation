#!/bin/bash
clear
echo "...START..."
cd JavaAugmentation/
# jar -> target/jar/JavaAugmentation.jar
mvn clean compile assembly:single
cd ../
echo "...END..."
