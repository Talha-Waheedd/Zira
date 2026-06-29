"""Google Gemini API helper — structured JSON generation for Zira endpoints."""

from __future__ import annotations

import json
import os
import re
from typing import Any

import google.generativeai as genai

DEFAULT_MODEL = "gemini-1.5-flash"

SYSTEM_BASE = (
    "You are Zira, an AI study companion for university students in Pakistan. "
    "Subjects include Mathematics, Physics, Computer Science, Chemistry, Biology, "
    "and Economics. Use clear, step-by-step explanations appropriate for "
    "undergraduate level. Always respond with valid JSON only — no markdown, "
    "no code fences, no extra text."
)

_configured = False


def is_gemini_configured() -> bool:
    return bool(os.getenv("GOOGLE_API_KEY", "").strip())


def get_model_name() -> str:
    return os.getenv("GEMINI_MODEL", DEFAULT_MODEL).strip() or DEFAULT_MODEL


def _ensure_configured() -> None:
    global _configured
    api_key = os.getenv("GOOGLE_API_KEY", "").strip()
    if not api_key:
        raise RuntimeError(
            "GOOGLE_API_KEY is not set. Copy backend/.env.example to backend/.env "
            "and add your Google AI Studio API key."
        )
    if not _configured:
        genai.configure(api_key=api_key)
        _configured = True


def _extract_json(text: str) -> dict[str, Any]:
    """Parse JSON from the model response, stripping optional markdown fences."""
    cleaned = text.strip()
    fence_match = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", cleaned)
    if fence_match:
        cleaned = fence_match.group(1).strip()
    return json.loads(cleaned)


def ask_gemini_json(system: str, user: str, max_tokens: int = 4096) -> dict[str, Any]:
    """Call Gemini and return a parsed JSON object."""
    _ensure_configured()

    model = genai.GenerativeModel(
        model_name=get_model_name(),
        system_instruction=system,
        generation_config={
            "max_output_tokens": max_tokens,
            "temperature": 0.7,
            "response_mime_type": "application/json",
        },
    )

    try:
        response = model.generate_content(user)
    except Exception as exc:  # noqa: BLE001 - surface as a clean runtime error
        raise RuntimeError(f"Gemini API error: {exc}") from exc

    text = (response.text or "").strip()
    if not text:
        raise RuntimeError("Gemini returned an empty response.")

    try:
        return _extract_json(text)
    except json.JSONDecodeError as exc:
        raise RuntimeError(f"Gemini returned invalid JSON: {exc}") from exc
