$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.10'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Set-Location 'c:\Users\Lenovo\Desktop\card_game\card\backend'
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.3\plugins\maven\lib\maven3\bin\mvn.cmd' compile --batch-mode -q
