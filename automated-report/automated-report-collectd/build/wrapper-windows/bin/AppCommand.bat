@echo off
setlocal

rem
rem Copyright (c) 1999, 2015 Tanuki Software, Ltd.
rem http://www.tanukisoftware.com
rem All rights reserved.
rem
rem This software is the proprietary information of Tanuki Software.
rem You shall use it only in accordance with the terms of the
rem license agreement you entered into with Tanuki Software.
rem http://wrapper.tanukisoftware.com/doc/english/licenseOverview.html
rem
rem Java Service Wrapper command based script.
rem

rem -----------------------------------------------------------------------------
rem These settings can be modified to fit the needs of your application
rem Optimized for use with version 3.5.27 of the Wrapper.

rem The base name for the Wrapper binary.
set _WRAPPER_BASE=wrapper

rem The directory where the Wrapper binary (.exe) file is located. It can be
rem  either an absolute or a relative path. If the path contains any special 
rem  characters, please make sure to quote the variable. 
set _WRAPPER_DIR=

rem The name and location of the Wrapper configuration file. This will be used
rem  if the user does not specify a configuration file as the first parameter to
rem  this script.
set _WRAPPER_CONF="../conf/%_WRAPPER_BASE%.conf"

rem _FIXED_COMMAND tells the script to use a hard coded command rather than
rem  expecting the first parameter of the command line to be the command.
rem  By default the command will will be expected to be the first parameter.
rem set _FIXED_COMMAND=console

rem _PASS_THROUGH tells the script to pass all parameters through to the JVM
rem  as is.  If _FIXED_COMMAND is specified then all parameters will be passed.
rem  If not set then all parameters starting with the second will be passed.
set _PASS_THROUGH=true

rem Do not modify anything beyond this point
rem -----------------------------------------------------------------------------

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
rem Find the application home.
rem if no path path specified do the default action
IF not DEFINED _WRAPPER_DIR goto dir_undefined
set _WRAPPER_DIR_QUOTED="%_WRAPPER_DIR:"=%"
if not "%_WRAPPER_DIR:~-2,1%" == "\" set _WRAPPER_DIR_QUOTED="%_WRAPPER_DIR_QUOTED:"=%\"
rem check if absolute path
if "%_WRAPPER_DIR_QUOTED:~2,1%" == ":" goto absolute_path
if "%_WRAPPER_DIR_QUOTED:~1,1%" == "\" goto absolute_path
rem everythig else means relative path
set _REALPATH="%~dp0%_WRAPPER_DIR_QUOTED:"=%"
goto pathfound

:dir_undefined
rem Use a relative path to the wrapper %~dp0 is location of current script under NT
set _REALPATH="%~dp0"
goto pathfound
:absolute_path
rem Use an absolute path to the wrapper
set _REALPATH="%_WRAPPER_DIR_QUOTED:"=%"

:pathfound
rem
rem Decide on the specific Wrapper binary to use (See delta-pack)
rem
if "%PROCESSOR_ARCHITEW6432%"=="AMD64" goto amd64
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto amd64
if "%PROCESSOR_ARCHITECTURE%"=="IA64" goto ia64
set _WRAPPER_L_EXE="%_REALPATH:"=%%_WRAPPER_BASE%-windows-x86-32.exe"
goto search
:amd64
set _WRAPPER_L_EXE="%_REALPATH:"=%%_WRAPPER_BASE%-windows-x86-64.exe"
goto search
:ia64
set _WRAPPER_L_EXE="%_REALPATH:"=%%_WRAPPER_BASE%-windows-ia-64.exe"
goto search
:search
set _WRAPPER_EXE="%_WRAPPER_L_EXE:"=%"
if exist %_WRAPPER_EXE% goto conf
set _WRAPPER_EXE="%_REALPATH:"=%%_WRAPPER_BASE%.exe"
if exist %_WRAPPER_EXE% goto conf
echo Unable to locate a Wrapper executable using any of the following names:
echo %_WRAPPER_L_EXE%
echo %_WRAPPER_EXE%
pause
goto :eof

:conf
if not [%_FIXED_COMMAND%]==[] (
    set _COMMAND=%_FIXED_COMMAND%
) else (
    set _COMMAND=%1
    shift
)

rem Collect all parameters
:parameters
set _PARAMETERS=%_PARAMETERS% %1
shift
if not [%1]==[] goto parameters

:callcommand
rem
rem Run the application.
rem At runtime, the current directory will be that of wrapper.exe
rem
set _MATCHED=true
if [%_COMMAND%]==[console] (
    if [%_PASS_THROUGH%]==[] (
        %_WRAPPER_EXE% -c "%_WRAPPER_CONF%" %_PARAMETERS%
    ) else (
        %_WRAPPER_EXE% -c "%_WRAPPER_CONF%" -- %_PARAMETERS%
    )
) else if [%_COMMAND%]==[setup] (
    call :setup
) else if [%_COMMAND%]==[start] (
    call :start
) else if [%_COMMAND%]==[stop] (
    call :stop
) else if [%_COMMAND%]==[install] (
    if [%_PASS_THROUGH%]==[] (
        %_WRAPPER_EXE% -i "%_WRAPPER_CONF%" %_PARAMETERS%
    ) else (
        %_WRAPPER_EXE% -i "%_WRAPPER_CONF%" -- %_PARAMETERS%
    )
) else if [%_COMMAND%]==[pause] (
    %_WRAPPER_EXE% -a "%_WRAPPER_CONF%"
) else if [%_COMMAND%]==[resume] (
    %_WRAPPER_EXE% -e "%_WRAPPER_CONF%"
) else if [%_COMMAND%]==[status] (
    %_WRAPPER_EXE% -q "%_WRAPPER_CONF%"
) else if [%_COMMAND%]==[remove] (
    %_WRAPPER_EXE% -r "%_WRAPPER_CONF%"
) else if [%_COMMAND%]==[restart] (
   call :stop
   call :start
) else (
   set _MATCHED=
   goto showusage
)

if errorlevel 1 (
    if [%_MATCHED%]==[] goto showusage
)
goto :eof

:showusage
rem A command was not specified, or it was now known.
if not [%_COMMAND%]==[] (
    echo Unknown command: %_COMMAND%
    echo.
)
if [%_PASS_THROUGH%]==[] ( 
    echo Usage: %0 [ console : start : pause : resume : stop : restart : install : remove : status ]
) else (
    echo Usage: %0 [ console {JavaAppArgs} : start : pause : resume : stop : restart : install {JavaAppArgs} : remove : status ]
)
pause
goto :eof


:setup
    %_WRAPPER_EXE% -su "%_WRAPPER_CONF%"
    goto :eof
:start
    %_WRAPPER_EXE% -t "%_WRAPPER_CONF%"
    goto :eof
:stop
    %_WRAPPER_EXE% -p "%_WRAPPER_CONF%"
    goto :eof
