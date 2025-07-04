# playback.ps1
# 用法：.\playback.ps1 -GameProgramPath "你的游戏程序路径" -CommandFile "命令文件路径"

param(
    [string]$GameProgramPath = "java -jar unnamed.jar",  # 替换为实际启动游戏的命令
    [string]$CommandFile = "test3.txt"                     # 替换为你的命令文件
)

# 检查文件是否存在
if (-not (Test-Path $CommandFile)) {
    Write-Host "错误：命令文件 $CommandFile 不存在！"
    exit 1
}

# 启动游戏进程并重定向输入输出
$processInfo = New-Object System.Diagnostics.ProcessStartInfo
$processInfo.FileName = "cmd.exe"                          # 通过 cmd 启动程序
$processInfo.Arguments = "/c $GameProgramPath"             # 传递游戏启动命令
$processInfo.UseShellExecute = $false
$processInfo.RedirectStandardInput = $true
$processInfo.RedirectStandardOutput = $true
$processInfo.CreateNoWindow = $true

$process = New-Object System.Diagnostics.Process
$process.StartInfo = $processInfo
$process.Start() | Out-Null

# 异步读取游戏输出并实时显示
$outputBuffer = New-Object System.Text.StringBuilder
$outputStream = $process.StandardOutput
$outputTask = $outputStream.BaseStream.BeginRead(
    $outputBuffer,
    0,
    $outputBuffer.Capacity,
    $null,
    $null
)

# 逐行读取命令文件并发送到游戏进程
Get-Content $CommandFile | ForEach-Object {
    $command = $_
    # 发送命令到游戏
    $process.StandardInput.WriteLine($command)
    # 显示发送的命令（可选）
    Write-Host "> $command" -ForegroundColor Cyan
    # 等待1秒
    Start-Sleep -Seconds 1
}

# 等待游戏进程结束
while (-not $process.HasExited) {
    # 实时显示输出
    if ($outputStream.Peek() -gt 0) {
        $output = $outputStream.ReadToEnd()
        Write-Host $output -NoNewline
    }
    Start-Sleep -Milliseconds 100
}

# 关闭进程
$process.WaitForExit()
$exitCode = $process.ExitCode
$process.Close()

Write-Host "游戏进程已退出，代码: $exitCode"