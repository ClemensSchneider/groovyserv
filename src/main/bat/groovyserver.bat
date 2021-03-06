@if "%DEBUG%" == "" @echo off

@rem -----------------------------------------------------------------------
@rem Copyright 2009-2013 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem     http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem -----------------------------------------------------------------------

setlocal
:begin

@rem ----------------------------------------
@rem Save script path
@rem ----------------------------------------

set SCRIPT_PATH=%~f0
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.\

@rem ----------------------------------------
@rem Parse arguments
@rem ----------------------------------------

set OPT_QUIET=NO
:loop_args
    if "%1" == "" (
        goto break_loop_args
    ) else if "%1" == "-q" (
        set OPT_QUIET=YES
    ) else if "%1" == "--quiet" (
        set OPT_QUIET=YES
    ) else if "%1" == "--help" (
        goto usage
        goto end
    ) else if "%1" == "-h" (
        goto usage
        goto end
    )
    shift
goto loop_args
:break_loop_args

@rem ----------------------------------------
@rem Support for Cygwin
@rem ----------------------------------------

call :expand_path JAVA_HOME "%JAVA_HOME%"
call :expand_path GROOVY_HOME "%GROOVY_HOME%"
call :expand_path GROOVYSERV_HOME "%GROOVYSERV_HOME%"
call :expand_path CLASSPATH "%CLASSPATH%"

@rem ----------------------------------------
@rem Find groovy command
@rem ----------------------------------------

if defined GROOVY_HOME (
    call :setup_GROOVY_CMD_from_GROOVY_HOME
    if errorlevel 1 goto end
) else (
    call :setup_GROOVY_CMD_from_PATH
    if errorlevel 1 goto end
)

@rem ----------------------------------------
@rem Resolve GROOVYSERV_HOME
@rem ----------------------------------------

if not defined GROOVYSERV_HOME (
    set GROOVYSERV_HOME=%DIRNAME%..
)
if not exist "%GROOVYSERV_HOME%\lib\groovyserv-*.jar" (
    echo ERROR: invalid GROOVYSERV_HOME: "%GROOVYSERV_HOME%" >&2
    goto end
)
call :info_log GroovyServ home directory: "%GROOVYSERV_HOME%"

@rem ----------------------------------------
@rem GroovyServ's work directory
@rem ----------------------------------------

set GROOVYSERV_WORK_DIR=%USERPROFILE%\.groovy\groovyserv
if not exist "%GROOVYSERV_WORK_DIR%" (
    mkdir "%GROOVYSERV_WORK_DIR%"
)
call :info_log GroovyServ work directory: "%GROOVYSERV_WORK_DIR%"

@rem ----------------------------------------
@rem Setup classpath
@rem ----------------------------------------

if defined CLASSPATH (
    call :info_log Original classpath: %CLASSPATH%
    set CLASSPATH=%CLASSPATH%;%GROOVYSERV_HOME%\lib\*
) else (
    call :info_log Original classpath: ^(none^)
    set CLASSPATH=%GROOVYSERV_HOME%\lib\*
)
call :info_log GroovyServ default classpath: "%CLASSPATH%"

@rem ----------------------------------------
@rem Setup other variables
@rem ----------------------------------------

@rem -server: for performance (experimental)
@rem -Djava.awt.headless=true: without this, annoying to switch an active process to it when new process is created as daemon
set JAVA_OPTS=%JAVA_OPTS% -server -Djava.awt.headless=true

@rem -------------------------------------------
@rem Invoke server
@rem -------------------------------------------

%GROOVY_CMD% %GROOVYSERV_OPTS% -e org.jggug.kobo.groovyserv.ui.ServerCLI.main(args) -- "%SCRIPT_PATH%" %*

@rem -------------------------------------------
@rem Endpoint
@rem -------------------------------------------

:end
endlocal
exit /B %ERRORLEVEL%

@rem -------------------------------------------
@rem Common function
@rem -------------------------------------------

:info_log
setlocal
    if not "%OPT_QUIET%" == "YES" echo %*
endlocal
exit /B

@rem GROOVY_CMD will be modified
:setup_GROOVY_CMD_from_GROOVY_HOME
    set GROOVY_CMD=%GROOVY_HOME%\bin\groovy.bat
    if not exist "%GROOVY_CMD%" (
        echo ERROR: invalid GROOVY_HOME: "%GROOVY_HOME%" >&2
        exit /B 1
    )
    call :info_log Groovy home directory: "%GROOVY_HOME%"
    call :info_log Groovy command path: "%GROOVY_CMD%" ^(found at GROOVY_HOME^)
exit /B

@rem GROOVY_CMD will be modified
:setup_GROOVY_CMD_from_PATH
    call :find_groovy_from_path_and_setup_GROOVY_CMD groovy.bat
    if not defined GROOVY_CMD (
        echo ERROR: groovy command not found >&2
        echo Hint:  Requires either PATH having groovy command or GROOVY_HOME. >&2
        exit /B 1
    )
    call :info_log Groovy home directory: ^(none^)
    call :info_log Groovy command path: "%GROOVY_CMD%" ^(found at PATH^)
exit /B

@rem GROOVY_CMD will be modified
:find_groovy_from_path_and_setup_GROOVY_CMD
    @rem Replace long name to short name for start command
    set GROOVY_CMD=%~s$PATH:1
exit /B

@rem ERRORLEVEL will be modified
:reset_errorlevel
exit /B 0

@rem environment variable which name is the first argument will be modified
@rem or when it's a valid windows' long name, it will be converted to the short name.
:expand_path
    set gs_tmp_value=%~s2
    @rem TODO checking a first char need to apply each entry of CLASSPATH
    if "%gs_tmp_value:~0,1%" == "/" (
        for /f "delims=" %%z in ('cygpath.exe --windows --path "%~2"') do (
            set %1=%%z
            call :info_log Expand path:
            call :info_log   -  %1="%gs_tmp_value%"
            call :info_log   +  %1="%%z"
        )
    ) else (
        @rem Replace long name to short name for start command
        set %1=%gs_tmp_value%
    )
exit /B 0

@rem usage
:usage
echo usage: groovyserver.bat [options]
echo options:
echo   -h,--help                     show this usage
echo   -k,--kill                     kill the running groovyserver
echo   -p,--port ^<port^>              specify the port to listen
echo   -q,--quiet                    suppress output to console except error message
echo   -r,--restart                  restart the running groovyserver
echo   -v,--verbose                  verbose output to a log file
echo      --allow-from ^<addresses^>   specify optional acceptable client addresses ^(delimiter: comma^)
echo      --authtoken ^<authtoken^>    specify authtoken ^(which is automatically generated if not specified^)
exit /B 0
