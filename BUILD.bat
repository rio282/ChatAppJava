@echo off
cls
mvn clean compile assembly:single -e
exit