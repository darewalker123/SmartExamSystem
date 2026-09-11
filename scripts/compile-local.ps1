$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$deps = Join-Path $root ".deps"
$classes = Join-Path $root "target\classes"
$servletJar = Join-Path $deps "javax.servlet-api-4.0.1.jar"
$mysqlJar = Join-Path $deps "mysql-connector-j-9.5.0.jar"

New-Item -ItemType Directory -Force -Path $deps, $classes | Out-Null

function Get-Dependency($url, $out) {
    if (Test-Path $out) {
        return
    }
    $script = "const https=require('https'),fs=require('fs');const url=process.argv[1],out=process.argv[2];https.get(url,r=>{if(r.statusCode!==200){console.error('HTTP '+r.statusCode);process.exit(1)};const f=fs.createWriteStream(out);r.pipe(f);f.on('finish',()=>f.close());}).on('error',e=>{console.error(e.message);process.exit(1)});"
    node -e $script $url $out
    if ($LASTEXITCODE -eq 0) {
        return
    }
    curl.exe --ssl-no-revoke -L $url -o $out
    if ($LASTEXITCODE -ne 0) {
        throw "Could not download dependency: $url. Install Maven or place the jar manually in .deps."
    }
}

Get-Dependency "https://repo.maven.apache.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar" $servletJar
Get-Dependency "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar" $mysqlJar

$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$classpath = "$servletJar;$mysqlJar"
javac -encoding UTF-8 --release 17 -cp "$classpath" -d "$classes" $sources
if ($LASTEXITCODE -ne 0) {
    throw "javac compilation failed."
}
Copy-Item -Path (Join-Path $root "src\main\resources\*") -Destination $classes -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "Compilation successful: $classes"
