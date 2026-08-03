@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
set "PATH=%JAVA_HOME%\bin;%PATH%"
"C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" -f "c:\Users\Lenovo\Desktop\card_game\card\backend\pom.xml" compile --batch-mode
