$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "compile-local.ps1")
if ($LASTEXITCODE -ne 0) {
    throw "Main compilation failed."
}

$testClasses = Join-Path $root "target\test-classes"
New-Item -ItemType Directory -Force -Path $testClasses | Out-Null

$sources = @(
    "src\main\java\com\smartexam\model\Exam.java",
    "src\main\java\com\smartexam\model\Question.java",
    "src\main\java\com\smartexam\model\Result.java",
    "src\main\java\com\smartexam\service\ExamService.java",
    "src\main\java\com\smartexam\service\ServiceException.java",
    "src\main\java\com\smartexam\dao\ExamDAO.java",
    "src\main\java\com\smartexam\dao\QuestionDAO.java",
    "src\main\java\com\smartexam\dao\ResultDAO.java",
    "src\main\java\com\smartexam\util\DatabaseUtil.java",
    "src\main\java\com\smartexam\util\ValidationUtil.java",
    "src\test\java\com\smartexam\service\ScoringSmokeTest.java"
) | ForEach-Object { Join-Path $root $_ }

javac -encoding UTF-8 --release 17 -d "$testClasses" $sources
if ($LASTEXITCODE -ne 0) {
    throw "Smoke test compilation failed."
}

java -cp "$testClasses" com.smartexam.service.ScoringSmokeTest
if ($LASTEXITCODE -ne 0) {
    throw "Smoke test failed."
}
