# Zira Backend (FastAPI + Google Gemini / Groq)

The AI backend for the Zira Android app. Generates explanations, quizzes, flashcards,
and study schedules using **Google Gemini** (primary) with optional **Groq** fallback.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `GET`  | `/api/health` | Health check + LLM provider status |
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
├── llm_service.py      # Gemini (primary) + Groq (fallback) + model chain
├── gemini_service.py   # Thin compatibility wrapper
├── routers/
│   ├── deps.py         # require_llm() guard
│   ├── health.py       # GET  /api/health
│   ├── explain.py      # POST /api/explain
│   ├── quiz.py         # POST /api/quiz
│   ├── flashcards.py   # POST /api/flashcards
│   └── schedule.py     # POST /api/schedule
├── requirements.txt
├── runtime.txt
├── render.yaml
└── .env.example
```

## Run locally

```powershell
cd D:\Zira\backend
pip install -r requirements.txt

# 1. Get a FREE Gemini key: https://aistudio.google.com/app/apikey
# 2. (Optional) Groq fallback key: https://console.groq.com/keys
copy .env.example .env
#    edit .env — set GOOGLE_API_KEY=...

uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

Verify: http://localhost:8000/api/health

```json
{
  "status": "ok",
  "service": "zira-backend",
  "gemini_configured": true,
  "groq_configured": false,
  "llm_configured": true,
  "providers": ["gemini"],
  "default_gemini_model": "gemini-2.5-flash"
}
```

## Environment variables

| Variable | Required | Default | Notes |
| --- | --- | --- | --- |
| `GOOGLE_API_KEY` | Yes* | — | Google AI Studio key |
| `GROQ_API_KEY` | No | — | Optional fallback when Gemini fails |
| `GEMINI_MODEL` | No | auto chain | Do **not** use `gemini-2.0-flash` (shut down 2026) |
| `GROQ_MODEL` | No | `llama-3.3-70b-versatile` | Groq model when fallback is used |
| `ALLOWED_ORIGINS` | No | `*` | CORS origins |
| `PORT` | No | `8000` | Set by Render automatically |

\*At least one of `GOOGLE_API_KEY` or `GROQ_API_KEY` is required.

### Gemini model chain (when `GEMINI_MODEL` is unset)

1. `gemini-2.5-flash`
2. `gemini-2.5-flash-lite`
3. `gemini-3-flash-preview`

Deprecated models (`gemini-2.0-flash`, `gemini-1.5-flash`, etc.) are **ignored**
if set via `GEMINI_MODEL`.

## Deploy

See **[DEPLOYMENT.md](DEPLOYMENT.md)** for Render.com (free tier).

After deploy, set in Render **Environment**:

- `GOOGLE_API_KEY` = your Gemini key
- `GEMINI_MODEL` = `gemini-2.5-flash` (recommended)
- `GROQ_API_KEY` = optional Groq fallback key
