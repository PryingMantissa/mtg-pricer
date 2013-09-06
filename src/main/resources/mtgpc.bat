@ECHO OFF
REM  QBFC Project Options Begin
REM  HasVersionInfo: No
REM  Companyname: 
REM  Productname: 
REM  Filedescription: 
REM  Copyrights: 
REM  Trademarks: 
REM  Originalname: 
REM  Comments: 
REM  Productversion:  0. 0. 0. 0
REM  Fileversion:  0. 0. 0. 0
REM  Internalname: 
REM  Appicon: ..\icon.ico
REM  AdministratorManifest: No
REM  QBFC Project Options End
ECHO ON
@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

mode con: cols=130 lines=40

if [%info_shown%]==[] (
    echo DeckPricer - Simple tool for evaluation of the price of an MtG deck 
    echo based on prices from http://www.cernyrytir.cz/
    echo v0.1 - beta
    echo SRSK 2013
    set info_shown=1
)


if "%1"=="" (
    echo.
    echo The deck file has to specified.
    set /P input=Enter file name: 
    DeckPricer !input!
    if %errorlevel% neq 0 PAUSE
) else (
    echo.
    java -cp ./bin -jar bin/mtg-pricer.jar %~dpnx1
    pause
)


