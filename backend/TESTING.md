# Testing the Zira Backend

## 1. Verify the deployed backend

**Browser** — open:

```
https://zira-backend.onrender.com/api/health
```

Expected:

```json
{"status":"ok","service":"zira-backend","gemini_configured":true}
```

**Swagger UI** — `https://zira-backend.onrender.com/docs` lets you try every endpoint
interactively (click an endpoint → **Try it out** → **Execute**).

**curl / PowerShell** — exercise the AI endpoints:

```powershell
# Explain
Invoke-RestMethod -Method POST -Uri https://zira-backend.onrender.com/api/explain `
  -ContentType "application/json" `
  -Body '{"question":"Explain recursion","userId":"test","subject":"Computer Science"}'

# Quiz
Invoke-RestMethod -Method POST -Uri https://zira-backend.onrender.com/api/quiz `
  -ContentType "application/json" `
  -Body '{"subject":"Physics","difficulty":"easy","count":3,"userId":"test"}'

# Flashcards
Invoke-RestMethod -Method POST -Uri https://zira-backend.onrender.com/api/flashcards `
  -ContentType "application/json" `
  -Body '{"topic":"Newton''s Laws","count":5,"userId":"test"}'

# Schedule
Invoke-RestMethod -Method POST -Uri https://zira-backend.onrender.com/api/schedule `
  -ContentType "application/json" `
  -Body '{"exams":[{"subject":"Physics","date":"2026-07-15"}],"dailyMins":60,"userId":"test"}'
```

A JSON response with the expected fields means the backend + Gemini are working.

> First call after idle may take 30–60s (free-tier cold start). Retry if it times out.

## 2. Test from the Android app

1. Set `local.properties` → `zira.api.base.url=https://zira-backend.onrender.com/`
2. **File → Sync Project with Gradle Files**, then Run on your device.
3. Try each feature:
   - **Ask Zira** → type a question → expect a streamed/typed explanation + follow-up chips.
   - **Quiz** → pick subject/difficulty → Start → questions load.
   - **Flashcards** → Generate New Deck → "X cards added".
   - **Schedule** → add an exam + date → Generate → task list appears.

Because the URL is HTTPS and public, this works on **mobile data or any Wi‑Fi**.

## 3. Check logs for errors

**Render logs:** Dashboard → your service → **Logs** tab. You'll see each request, e.g.
`POST /api/quiz 200`. A `502` line includes the Gemini error message.

**Android logs (Logcat):** filter by the ViewModel tags added for debugging:

- `AskViewModel`, `QuizViewModel`, `FlashcardViewModel`, `ScheduleViewModel`

These print the HTTP status / error body on failure. You can also filter by `okhttp`
to see the exact request URL the app is calling — confirm it's your Render URL.

## 4. Local testing (optional)

```powershell
cd D:\Zira\backend
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

Then use `http://localhost:8000/...` in the curl commands above. From the Android
emulator use `http://10.0.2.2:8000/`; from a physical device on the **same Wi‑Fi**,
use your PC's LAN IP.
