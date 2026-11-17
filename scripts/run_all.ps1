$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$tasks = @(
    @{ Name = 'Task01'; Class = 'Main'; Path = Join-Path $root 'Task01' },
    @{ Name = 'Task02'; Class = 'Main'; Path = Join-Path $root 'Task02' },
    @{ Name = 'Task03'; Class = 'Attendance'; Path = Join-Path $root 'Task03' },
    @{ Name = 'Task04'; Class = 'Main'; Path = Join-Path $root 'Task04' },
    @{ Name = 'Task05'; Class = 'Main'; Path = Join-Path $root 'Task05' },
    @{ Name = 'Task06'; Class = 'Main'; Path = Join-Path $root 'Task06' },
    @{ Name = 'Task07'; Class = 'Main'; Path = Join-Path $root 'Task07' }
)
foreach ($task in $tasks) {
    Write-Host "Running $($task.Name)"
    Push-Location $task.Path
    try {
        java -cp $task.Path $task.Class
    } finally {
        Pop-Location
    }
    Write-Host ''
}
Write-Host 'Finished all task runs.'
