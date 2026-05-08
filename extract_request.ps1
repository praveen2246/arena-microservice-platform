$file = Get-Content 'C:\Users\tbpra\.gemini\antigravity\brain\cf83e0e5-dcf1-40b0-a9e5-591ac822685a\.system_generated\logs\overview.txt'
$line = $file[446]
$line | Out-File 'd:\Arena-dev\model_response_1123.txt'
