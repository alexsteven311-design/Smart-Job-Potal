import uuid, urllib.request
from pathlib import Path
boundary = '----WebKitFormBoundary' + uuid.uuid4().hex
file_path = Path(r'C:\Users\Alex Steven\OneDrive\Apps\Smart job\Server\sample-resume.txt')
file_bytes = file_path.read_bytes()
body = (
    f'--{boundary}\r\n'.encode() +
    b'Content-Disposition: form-data; name="file"; filename="sample-resume.txt"\r\n' +
    b'Content-Type: text/plain\r\n\r\n' +
    file_bytes + b'\r\n' +
    f'--{boundary}\r\n'.encode() +
    b'Content-Disposition: form-data; name="jobTitle"\r\n\r\n' +
    b'Java Developer\r\n' +
    f'--{boundary}--\r\n'.encode()
)
req = urllib.request.Request('http://localhost:8081/api/candidates/resume-analyze', data=body, method='POST')
req.add_header('Content-Type', f'multipart/form-data; boundary={boundary}')
try:
    with urllib.request.urlopen(req, timeout=20) as resp:
        print(resp.status)
        print(resp.read().decode())
except Exception:
    import traceback; traceback.print_exc()
