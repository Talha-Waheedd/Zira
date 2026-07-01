"""
Unified LLM service for Zira — Google Gemini (primary) with optional Groq fallback.

Gemini 2.0 / 1.5 models were shut down in 2026; this module tries current free-tier
models in order, then falls back to Groq when GROQ_API_KEY is set.
"""

from __future__ import annotations

import json
import logging
import os
import re
from typing import Any

logger = logging.getLogger("zira.llm")

# Shut down June 2026 — do not use.
_DEPRECATED_MODELS = frozenset({
    "gemini-2.0-flash",
    "gemini-2.0-flash-exp",
    "gemini-2.0-flash-001",
    "gemini-1.5-flash",
    "gemini-1.5-pro",
})

# Tried in order until one succeeds (override with GEMINI_MODEL for a single model).
DEFAULT_GEMINI_MODELS = (
    "gemini-2.5-flash",
    "gemini-2.5-flash-lite",
    "gemini-3-flash-preview",
)

DEFAULT_GROQ_MODEL = "llama-3.3-70b-versatile"

SYSTEM_BASE = (
    "You are Zira, an AI study companion for university students in Pakistan. "
    "Subjects include Mathematics, Physics, Computer Science, Chemistry, Biology, "
    "and Economics. Use clear, step-by-step explanations appropriate for "
    "undergraduate level. Always respond with valid JSON only — no markdown, "
    "no code fences, no extra text."
)


def is_gemini_configured() -> bool:
    return bool(os.getenv("GOOGLE_API_KEY", "").strip())


def is_groq_configured() -> bool:
    return bool(os.getenv("GROQ_API_KEY", "").strip())


def is_llm_configured() -> bool:
    return is_gemini_configured() or is_groq_configured()


def get_configured_providers() -> list[str]:
    providers: list[str] = []
    if is_gemini_configured():
        providers.append("gemini")
    if is_groq_configured():
        providers.append("groq")
    return providers


def _gemini_model_candidates() -> list[str]:
    override = os.getenv("GEMINI_MODEL", "").strip()
    if override:
        if override in _DEPRECATED_MODELS:
            logger.warning(
                "GEMINI_MODEL=%s is deprecated/shut down; using current defaults instead.",
                override,
            )
            return list(DEFAULT_GEMINI_MODELS)
        return [override]
    return list(DEFAULT_GEMINI_MODELS)


def _extract_json(text: str) -> dict[str, Any]:
    cleaned = text.strip()
    fence_match = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", cleaned)
    if fence_match:
        cleaned = fence_match.group(1).strip()
    return json.loads(cleaned)


def _ask_gemini_json(system: str, user: str, max_tokens: int) -> dict[str, Any]:
    from google import genai
    from google.genai import types

    api_key = os.getenv("GOOGLE_API_KEY", "").strip()
    if not api_key:
        raise RuntimeError("GOOGLE_API_KEY is not set.")

    client = genai.Client(api_key=api_key)
    config = types.GenerateContentConfig(
        system_instruction=system,
        response_mime_type="application/json",
        temperature=0.7,
        max_output_tokens=max_tokens,
    )

    last_error: Exception | None = None
    for model_name in _gemini_model_candidates():
        try:
            logger.info("Calling Gemini model: %s", model_name)
            response = client.models.generate_content(
                model=model_name,
                contents=user,
                config=config,
            )
            text = (response.text or "").strip()
            if not text:
                raise RuntimeError("Gemini returned an empty response.")
            return _extract_json(text)
        except Exception as exc:  # noqa: BLE001
            last_error = exc
            logger.warning("Gemini model %s failed: %s", model_name, exc)

    raise RuntimeError(
        f"All Gemini models failed. Last error: {last_error}"
    ) from last_error


def _ask_groq_json(system: str, user: str, max_tokens: int) -> dict[str, Any]:
    from groq import Groq

    api_key = os.getenv("GROQ_API_KEY", "").strip()
    if not api_key:
        raise RuntimeError("GROQ_API_KEY is not set.")

    model = os.getenv("GROQ_MODEL", DEFAULT_GROQ_MODEL).strip() or DEFAULT_GROQ_MODEL
    client = Groq(api_key=api_key)

    logger.info("Calling Groq model: %s", model)
    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": system},
            {"role": "user", "content": user},
        ],
        max_tokens=max_tokens,
        temperature=0.7,
        response_format={"type": "json_object"},
    )

    text = (response.choices[0].message.content or "").strip()
    if not text:
        raise RuntimeError("Groq returned an empty response.")
    return _extract_json(text)


def ask_llm_json(system: str, user: str, max_tokens: int = 4096) -> dict[str, Any]:
    """Generate structured JSON using Gemini, with optional Groq fallback."""
    errors: list[str] = []

    if is_gemini_configured():
        try:
            return _ask_gemini_json(system, user, max_tokens)
        except Exception as exc:  # noqa: BLE001
            errors.append(f"Gemini: {exc}")
            logger.error("Gemini failed: %s", exc)

    if is_groq_configured():
        try:
            return _ask_groq_json(system, user, max_tokens)
        except Exception as exc:  # noqa: BLE001
            errors.append(f"Groq: {exc}")
            logger.error("Groq failed: %s", exc)

    if not errors:
        raise RuntimeError(
            "No LLM API key configured. Set GOOGLE_API_KEY and/or GROQ_API_KEY."
        )

    raise RuntimeError(" | ".join(errors))
