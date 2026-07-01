# Deploying the Zira Backend to Render.com (Free)

This gives your backend a **permanent public HTTPS URL** (e.g.
`https://zira-backend.onrender.com`) so the Android app works from **any network** —
mobile data, Wi‑Fi, anywhere. No more LAN/IP issues.

> Time: ~10 minutes. Cost: free.

---

## Prerequisites

- A **Google Gemini API key** — get one free at https://aistudio.google.com/app/apikey
- A **GitHub account** (Render deploys from a Git repo)
- The `backend/` folder pushed to a GitHub repository

---

## Step 1 — Push the backend to GitHub

If your project isn't on GitHub yet:

```bash
cd D:\Zira
git add backend/ README.md
git commit -m "Add Zira FastAPI + Gemini backend"
# create a repo on github.com first, then:
git remote add origin https://github.com/<you>/zira.git   # skip if already set
git push -u origin main
```

> The `.env` file is git-ignored on purpose — never commit your API key.
> You'll set the key in Render's dashboard instead (Step 4).

---

## Step 2 — Create a Render account

1. Go to https://render.com and sign up (use "Sign in with GitHub" for the easiest setup).
2. Authorize Render to access your repositories.

---

## Step 3 — Create the Web Service

You have two options:

### Option A — Blueprint (uses the included `render.yaml`, recommended)

1. In the Render dashboard click **New +** → **Blueprint**.
2. Select your **zira** repository.
3. Render reads `backend/render.yaml` and pre-fills everything.
4. Click **Apply**.

> If Render asks for the blueprint location and your repo root isn't `backend/`,
> set the **Root Directory** to `backend`.

### Option B — Manual Web Service

1. **New +** → **Web Service** → pick your repo.
2. Fill in:
   - **Root Directory:** `backend`
   - **Runtime:** `Python 3`
   - **Build Command:** `pip install -r requirements.txt`
   - **Start Command:** `uvicorn main:app --host 0.0.0.0 --port $PORT`
   - **Instance Type:** `Free`
   - **Health Check Path:** `/api/health`

---

## Step 4 — Set the environment variable

In the service's **Environment** tab, add:

| Key | Value |
| --- | --- |
| `GOOGLE_API_KEY` | *your Gemini key* |
| `GEMINI_MODEL` | `gemini-2.5-flash` |
| `GROQ_API_KEY` | *(optional)* Groq fallback key |
| `ALLOWED_ORIGINS` | `*` (optional) |

Click **Save Changes** — Render redeploys automatically.

---

## Step 5 — Get your public URL

After the build finishes (watch the **Logs** tab for
`Uvicorn running on http://0.0.0.0:PORT`), Render shows your URL at the top, e.g.:

```
https://zira-backend.onrender.com
```

Test it in a browser:

```
https://zira-backend.onrender.com/api/health
```

Expected:

```json
{"status":"ok","service":"zira-backend","gemini_configured":true}
```

If `gemini_configured` is `false`, your `GOOGLE_API_KEY` isn't set correctly — recheck Step 4.

---

## Step 6 — Point the Android app at it

Edit `D:\Zira\local.properties` (note the trailing slash):

```properties
zira.api.base.url=https://zira-backend.onrender.com/
```

Then in Android Studio: **File → Sync Project with Gradle Files**, rebuild, and run.
Because the URL is HTTPS, **no cleartext/network-security changes are needed**.

---

## Free-tier note: cold starts

Render's free instances **sleep after ~15 minutes** of inactivity. The next request
"wakes" it and can take **30–60 seconds**. This is normal. Options:

- Just wait on the first request after idle (the app already has generous timeouts).
- Or hit `/api/health` from a free uptime pinger (e.g. UptimeRobot every 10 min) to keep it warm.

---

## Updating the backend later

Push to GitHub — Render auto-deploys:

```bash
git add backend/
git commit -m "Update backend"
git push
```

---

## Troubleshooting

| Symptom | Cause / Fix |
| --- | --- |
| `gemini_configured: false` | `GOOGLE_API_KEY` not set in Render env vars |
| 502 from `/api/quiz` etc. | Gemini error — check Render **Logs**; verify key/quota |
| First request very slow | Free-tier cold start (see above) |
| Build fails on Render | Confirm **Root Directory = backend** and `requirements.txt` present |
| App still shows network error | URL missing trailing `/`, or Gradle not re-synced |
