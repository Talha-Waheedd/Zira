"""Shared helpers for route modules."""

from fastapi import HTTPException

from llm_service import is_llm_configured


def require_llm() -> None:
    """Raise 503 if no LLM API key (Gemini or Groq) is configured."""
    if not is_llm_configured():
        raise HTTPException(
            status_code=503,
            detail=(
                "No LLM API key configured. Set GOOGLE_API_KEY and/or GROQ_API_KEY "
                "in the environment and restart the server."
            ),
        )


# Backward-compatible alias
require_gemini = require_llm
