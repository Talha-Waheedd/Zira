# Zira Backend (FastAPI + Google Gemini)

The AI backend for the Zira Android app. Generates explanations, quizzes, flashcards,
and study schedules using **Google Gemini** (free tier).

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `GET`  | `/api/health` | Health check + whether Gemini is configured |
| `POST` | `/api/explain` | Step-by-step explanation |
| `POST` | `/api/quiz` | Multiple-choice quiz |
| `POST` | `/api/flashcards` | Flashcard deck |
| `POST` | `/api/schedule` | Study schedule from exam dates |

Interactive docs (Swagger UI): `/docs`

## Project layout

```
backend/
├── main.py             # FastAPI app + CORS + router registration
├── models.py           # Pydantic models (match the Android JSON exactly)
├── gemini_service.py   # Google Gemini integration + JSON parsing
├── routers/
│   ├── deps.py         # shared require_gemini() guard
│   ├── health.py       # GET  /api/health
│   ├── explain.py      # POST /api/explain
│   ├── quiz.py         # POST /api/quiz
│   ├── flashcards.py   # POST /api/flashcards
│   └── schedule.py     # POST /api/schedule
├── requirements.txt
├── runtime.txt         # Python version for Render
├── render.yaml         # Render blueprint (one-click deploy)
├── Procfile            # process command (Render/Heroku-style hosts)
└── .env.example        # copy to .env and add your key
```

## Run locally

```powershell
cd D:\Zira\backend
pip install -r requirements.txt

# 1. Get a FREE Gemini API key: https://aistudio.google.com/app/apikey
# 2. Create your .env:
copy .env.example .env
#    then edit .env and set GOOGLE_API_KEY=...

# 3. Start the server
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

Verify:

- Health: http://localhost:8000/api/health → `{"status":"ok",...,"gemini_configured":true}`
- Docs:   http://localhost:8000/docs

## Environment variables

| Variable | Required | Default | Notes |
| --- | --- | --- | --- |
| `GOOGLE_API_KEY` | Yes | — | From Google AI Studio |
| `GEMINI_MODEL` | No | `gemini-1.5-flash` | Any Gemini model id |
| `ALLOWED_ORIGINS` | No | `*` | Comma-separated origins, or `*` for all |
| `PORT` | No | `8000` | Set automatically by Render |

## Deploy

See **[DEPLOYMENT.md](DEPLOYMENT.md)** for a free Render.com walkthrough that gives the
backend a permanent public HTTPS URL.
