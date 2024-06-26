@echo off
:: Copyright 1999-2024 Original Holding Ltd.
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
::
::      http://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.

if not exist "%JAVA_HOME%\bin\jps.exe" (
    echo Please set the JAVA_HOME variable in your environment to the correct JDK directory.
    echo JDK8 or later is recommended!
    EXIT /B 1
)

setlocal

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Killing Automated admin...

:: Find and kill Automated admin process
set "JAVA_RUNNING=false"
for /f "tokens=1" %%i in ('jps -m ^| find "automated-admin"') do (
    set "JAVA_RUNNING=true"
    taskkill /F /PID %%i
)

if "%JAVA_RUNNING%"=="true" (
    echo Automated admin stopped.
) else (
    echo Automated admin is not running.
)

echo Done!
