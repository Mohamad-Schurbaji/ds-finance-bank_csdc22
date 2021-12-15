echo off

goto(){
# linux
uname -o
}
goto $@
exit

:(){
rem windows script here
cd C:\bankApp\clientJar
CLS
java -jar ./ds-finance-bank-client-1.0-SNAPSHOT-jar-with-dependencies.jar
pause
exit