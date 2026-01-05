$ErrorActionPreference = "Stop"

Write-Host "===================================="
Write-Host "Building Petualang Hijau Game (PS)"
Write-Host "===================================="

$buildDir = "build"
$classesDir = "$buildDir\classes"
$libDir = "lib"

if (-not (Test-Path $buildDir)) { New-Item -ItemType Directory -Path $buildDir }
if (-not (Test-Path $classesDir)) { New-Item -ItemType Directory -Path $classesDir }
if (-not (Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir }

Write-Host "[1/4] Collecting Java source files..."
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
$filesPath = "$buildDir\sources.txt"
$javaFiles | Out-File -FilePath $filesPath -Encoding ascii

Write-Host "[2/4] Compiling Java source files..."
# Use the existing fat JAR as a dependency provider
$fatJar = "Artifacts\UntitledFarmGame.jar"
$classpath = "$libDir\*;$fatJar"

try {
    javac -d $classesDir -cp $classpath "@$filesPath"
}
catch {
    Write-Host "`nERROR: Compilation failed!" -ForegroundColor Red
    Write-Host "Please check the errors above."
    exit 1
}

Write-Host "[3/4] Copying resources..."
if (Test-Path "src\main\resources") {
    Copy-Item -Path "src\main\resources\*" -Destination $classesDir -Recurse -Force
}

Write-Host "[4/4] Creating executable JAR..."
$jarName = "PetualangHijau_New.jar"
$manifest = "MANIFEST.MF"
if (-not (Test-Path $manifest)) {
    @"
Manifest-Version: 1.0
Main-Class: controller.App
"@ | Out-File -FilePath $manifest -Encoding ascii
}

# We want to create a fat JAR if possible, or at least one that works.
# For now, let's just make the JAR with the new classes.
jar cfm "$buildDir\$jarName" $manifest -C $classesDir .

Write-Host "`n===================================="
Write-Host "Build Complete!"
Write-Host "===================================="
Write-Host "JAR file created: $buildDir\$jarName"
Write-Host "`nNote: This JAR uses $fatJar for dependencies."
Write-Host "To run the game:"
Write-Host "java -cp `"$buildDir\$jarName;$fatJar`" controller.App"
Write-Host "===================================="
