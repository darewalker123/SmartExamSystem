$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root "target"
$classes = Join-Path $target "classes"
$staging = Join-Path $target "SmartExamSystem"
$war = Join-Path $target "SmartExamSystem.war"
$mysqlJar = Join-Path $root ".deps\mysql-connector-j-9.5.0.jar"

& (Join-Path $PSScriptRoot "compile-local.ps1")
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

Remove-Item -Recurse -Force $staging -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $staging | Out-Null
Copy-Item -Path (Join-Path $root "src\main\webapp\*") -Destination $staging -Recurse -Force

New-Item -ItemType Directory -Force -Path (Join-Path $staging "WEB-INF\classes"), (Join-Path $staging "WEB-INF\lib") | Out-Null
Copy-Item -Path (Join-Path $classes "*") -Destination (Join-Path $staging "WEB-INF\classes") -Recurse -Force
Copy-Item -Path $mysqlJar -Destination (Join-Path $staging "WEB-INF\lib\mysql-connector-j-9.5.0.jar") -Force

Remove-Item -Force $war -ErrorAction SilentlyContinue
Push-Location $staging
try {
    jar --create --file $war -C $staging .
    if ($LASTEXITCODE -ne 0) {
        throw "WAR packaging failed."
    }
} finally {
    Pop-Location
}

Write-Host "WAR created: $war"
