@echo off
echo Compiling Java files...
cd /d %~dp0

if not exist "bin" mkdir bin

echo Compiling...
javac -cp "lib\mysql-connector-j-9.6.0.jar" -d bin -sourcepath src src\Main.java src\view\*.java src\model\*.java src\dao\*.java src\controller\*.java

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo Running app...
    java -cp "bin;lib\mysql-connector-j-9.6.0.jar" Main
) else (
    echo Compilation failed!
    pause
    exit /b 1
)
