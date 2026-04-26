@echo off
echo =======================================================
echo     Starting LEARN-ED Online Quiz Platform
echo =======================================================
echo.
echo Cleaning old build and compiling the application...
call mvn clean install
echo.
echo Starting Tomcat 7 Server...
call mvn tomcat7:run
