@echo on

set SONICFRAMEWORK_VERSION=

set TMP_PARAM1=%1
set TMP_PARAM2=%2

set BACKUP_POM=y

if  "%TMP_PARAM1%" NEQ "" (
	set SONICFRAMEWORK_VERSION=%1
	goto afterInput
) else (
	goto inputVersion
)

:inputVersion
set /p SONICFRAMEWORK_VERSION=please input version:

:afterInput
if "%SONICFRAMEWORK_VERSION%"=="" goto inputVersion

echo set version %SONICFRAMEWORK_VERSION% begin
echo mvn versions:set -DnewVersion=%SONICFRAMEWORK_VERSION%
call mvn versions:set -DnewVersion=%SONICFRAMEWORK_VERSION%
echo set version %SONICFRAMEWORK_VERSION% finished

if /i "%TMP_PARAM2%" EQU "commit" ( 
	set BACKUP_POM=n
	goto commit
)
set /p BACKUP_POM=need backup pom?(y/n) (default y):	


:commit
if "%BACKUP_POM%" == "n" (
	echo mvn versions:commit
	call mvn versions:commit
)

pause

