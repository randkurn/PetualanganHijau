# ============================================
# Build EXE dengan Launch4j - Petualangan Hijau
# ============================================

Write-Host "Building Windows EXE with Launch4j..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$LAUNCH4J_VERSION = "3.50"
$LAUNCH4J_URL = "https://sourceforge.net/projects/launch4j/files/launch4j-3/$LAUNCH4J_VERSION/launch4j-$LAUNCH4J_VERSION-win32.zip/download"
$LAUNCH4J_DIR = "tools\launch4j"
$LAUNCH4J_EXE = "$LAUNCH4J_DIR\launch4jc.exe"

$APP_NAME = "PetualanganHijau"
$JAR_PATH = "build\PetualangHijau_New.jar"
$OUTPUT_EXE = "build\exe\PetualanganHijau.exe"
$MAIN_CLASS = "controller.App"

# Check JAR
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "[ERROR] JAR not found: $JAR_PATH" -ForegroundColor Red
    Write-Host "[INFO] Run build.ps1 first!" -ForegroundColor Yellow
    exit 1
}

# Download Launch4j if not exists
if (-not (Test-Path $LAUNCH4J_EXE)) {
    Write-Host "[1/4] Downloading Launch4j..." -ForegroundColor Green
    
    New-Item -ItemType Directory -Path "tools" -Force | Out-Null
    $zipPath = "tools\launch4j.zip"
    
    try {
        Invoke-WebRequest -Uri $LAUNCH4J_URL -OutFile $zipPath -UseBasicParsing
        Write-Host "[INFO] Extracting Launch4j..." -ForegroundColor Yellow
        Expand-Archive -Path $zipPath -DestinationPath "tools" -Force
        Remove-Item $zipPath
    }
    catch {
        Write-Host "[ERROR] Failed to download Launch4j!" -ForegroundColor Red
        Write-Host "[INFO] Please download manually from: https://sourceforge.net/projects/launch4j/" -ForegroundColor Yellow
        exit 1
    }
}
else {
    Write-Host "[1/4] Launch4j found!" -ForegroundColor Green
}

# Create output directory
Write-Host "[2/4] Preparing output directory..." -ForegroundColor Green
if (-not (Test-Path "build\exe")) {
    New-Item -ItemType Directory -Path "build\exe" -Force | Out-Null
}

# Create Launch4j config
Write-Host "[3/4] Creating Launch4j configuration..." -ForegroundColor Green

$configXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>gui</headerType>
  <jar>../../$JAR_PATH</jar>
  <outfile>../../$OUTPUT_EXE</outfile>
  <errTitle>Petualangan Hijau - Error</errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>https://adoptium.net/</downloadUrl>
  <supportUrl>https://github.com/your-repo</supportUrl>
  <stayAlive>false</stayAlive>
  <restartOnCrash>false</restartOnCrash>
  <manifest></manifest>
  <icon></icon>
  <classPath>
    <mainClass>$MAIN_CLASS</mainClass>
  </classPath>
  <jre>
    <path></path>
    <bundledJre64Bit>false</bundledJre64Bit>
    <bundledJreAsFallback>false</bundledJreAsFallback>
    <minVersion>21</minVersion>
    <maxVersion></maxVersion>
    <jdkPreference>preferJre</jdkPreference>
    <runtimeBits>64</runtimeBits>
    <initialHeapSize>256</initialHeapSize>
    <maxHeapSize>1024</maxHeapSize>
  </jre>
  <versionInfo>
    <fileVersion>1.0.0.0</fileVersion>
    <txtFileVersion>1.0.0</txtFileVersion>
    <fileDescription>$APP_NAME - Game Edukasi Lingkungan</fileDescription>
    <copyright>CMPT276 Group 7</copyright>
    <productVersion>1.0.0.0</productVersion>
    <txtProductVersion>1.0.0</txtProductVersion>
    <productName>$APP_NAME</productName>
    <companyName>CMPT276 Group 7</companyName>
    <internalName>$APP_NAME</internalName>
    <originalFilename>PetualanganHijau.exe</originalFilename>
  </versionInfo>
</launch4jConfig>
"@

$configPath = "$LAUNCH4J_DIR\config.xml"
$configXml | Out-File -FilePath $configPath -Encoding UTF8

# Run Launch4j
Write-Host "[4/4] Building EXE..." -ForegroundColor Green

try {
    & $LAUNCH4J_EXE $configPath
    
    if (Test-Path $OUTPUT_EXE) {
        $exeInfo = Get-Item $OUTPUT_EXE
        Write-Host "" -ForegroundColor Green
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "SUCCESS! EXE Created!" -ForegroundColor Green
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "Location: $($exeInfo.FullName)" -ForegroundColor Yellow
        Write-Host "Size: $([math]::Round($exeInfo.Length / 1KB, 2)) KB" -ForegroundColor Yellow
        Write-Host "" -ForegroundColor Green
        Write-Host "You can now run PetualanganHijau.exe!" -ForegroundColor Cyan
        Write-Host "(Requires Java 21 or newer installed)" -ForegroundColor DarkGray
    }
    else {
        Write-Host "[ERROR] EXE creation failed!" -ForegroundColor Red
    }
    
}
catch {
    Write-Host "[ERROR] Launch4j execution failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
