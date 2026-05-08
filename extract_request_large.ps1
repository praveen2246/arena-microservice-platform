$file = [System.IO.File]::ReadAllText('C:\Users\tbpra\.gemini\antigravity\brain\cf83e0e5-dcf1-40b0-a9e5-591ac822685a\.system_generated\logs\overview.txt')
$marker = "Only generate the 6 missing items below."
$index = $file.IndexOf($marker)
if ($index -ge 0) {
    $content = $file.Substring($index, [Math]::Min(10000, $file.Length - $index))
    $content | Out-File 'd:\Arena-dev\request_content_large.txt'
} else {
    "Marker not found" | Out-File 'd:\Arena-dev\request_content_large.txt'
}
