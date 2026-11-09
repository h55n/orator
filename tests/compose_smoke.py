"""Exercise a disposable Compose backend without making AI provider requests."""
import json
import secrets
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BASE = "http://127.0.0.1:8080"


def request(path, data=None, token=None, expected=200):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    req = Request(BASE + path, data=None if data is None else json.dumps(data).encode(), headers=headers)
    try:
        with urlopen(req, timeout=10) as response:
            status, body = response.status, response.read()
    except HTTPError as error:
        status, body = error.code, error.read()
    assert status == expected, f"{path}: expected {expected}, received {status}"
    return json.loads(body) if body else None


for attempt in range(90):
    try:
        request("/api/auth/login", {"email": "missing@example.invalid", "password": "not-a-user"}, expected=401)
        break
    except (URLError, TimeoutError, AssertionError):
        time.sleep(2)
else:
    raise AssertionError("Backend did not become ready through published port 8080")

email = "compose-" + secrets.token_hex(8) + "@example.invalid"
password = secrets.token_urlsafe(24)
registered = request("/api/auth/register", {"name": "Compose Test", "email": email, "password": password}, expected=201)
assert registered["user"]["email"] == email
assert registered["user"]["id"] > 0
assert registered["token"]
logged_in = request("/api/auth/login", {"email": email, "password": password})
assert logged_in["user"]["id"] == registered["user"]["id"]
profile = request("/api/auth/me", token=logged_in["token"])
assert profile["email"] == email
history = request("/api/questions/history", token=logged_in["token"])
assert history["content"] == [] and history["totalElements"] == 0
request("/api/auth/login", {"email": email, "password": "wrong-password"}, expected=401)
print("Compose smoke passed: startup, registration, login, profile, empty history, rejected password")
