$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$taskDirs = @('Task01','Task02','Task03','Task04','Task05','Task06','Task07')
foreach ($taskDir in $taskDirs) {
    $dirPath = Join-Path $root $taskDir
    if (-not (Test-Path $dirPath)) {
        continue
    }
    $javaFiles = Get-ChildItem -Path $dirPath -Filter *.java | Sort-Object Name
    if ($javaFiles.Count -eq 0) {
        continue
    }
    $paths = @($javaFiles | ForEach-Object { $_.FullName })
    Write-Host "Compiling $taskDir"
    javac @paths
}
Write-Host 'All Java task folders compiled successfully.'
