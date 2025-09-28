@echo off
echo ========================================
echo Flipkart E-commerce Automation Test
echo ========================================
echo.

echo Checking Java installation...
java -version
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)
echo.

echo Compiling Java files...
javac -cp "libs/*" CompleteFlipkartAutomation.java
if errorlevel 1 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)
echo Compilation successful!
echo.

echo Starting Flipkart automation test...
echo This will open a Chrome browser and automate the complete user journey.
echo Please ensure Chrome browser is installed and up to date.
echo.
pause

echo Running automation...
java -cp "libs/*;." CompleteFlipkartAutomation

echo.
echo Automation test completed!
pause