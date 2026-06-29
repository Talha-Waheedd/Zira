"""
Zira FastAPI backend — AI study companion API for the Android client.

Powered by Google Gemini (free tier). Run locally:

    cd D:\\Zira\\backend
    pip install -r requirements.txt
    copy .env.example .env          # then add your GOOGLE_API_KEY
    uvicorn main:app --host 0.0.0.0 --port 8000 --reload

Docs:   http://localhost:8000/docs
Health: http://localhost:8000/api/health
"""

from __future__ import annotations

import logging
import os

from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from routers import explain, flashcards, health, quiz, schedule

load_dotenv()

logging.basicConfig(level=logging.INFO)

app = FastAPI(
    title="Zira API",
    description="AI-powered study companion backend for the Zira Android app.",
    version="1.1.0",
)

# CORS — allow the Android app and browsers to connect from anywhere.
# ALLOWED_ORIGINS can be set to a comma-separated list to restrict in production.
_origins_env = os.getenv("ALLOWED_ORIGINS", "*").strip()
allow_origins = ["*"] if _origins_env in ("", "*") else [
    o.strip() for o in _origins_env.split(",") if o.strip()
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allow_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router, tags=["health"])
app.include_router(explain.router, tags=["explain"])
app.include_router(quiz.router, tags=["quiz"])
app.include_router(flashcards.router, tags=["flashcards"])
app.include_router(schedule.router, tags=["schedule"])


@app.get("/", tags=["root"])
def root() -> dict[str, str]:
    return {"service": "zira-backend", "docs": "/docs", "health": "/api/health"}


if __name__ == "__main__":
    import uvicorn

    port = int(os.getenv("PORT", "8000"))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=True)
