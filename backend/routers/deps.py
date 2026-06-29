"""Shared helpers for route modules."""

from fastapi import HTTPException

from gemini_service import is_gemini_configured


def require_gemini() -> None:
    """Raise 503 if the Gemini API key is not configured."""
    if not is_gemini_configured():
        raise HTTPException(
            status_code=503,
            detail=(
                "Gemini API is not configured. Set GOOGLE_API_KEY in the environment "
                "(.env locally, or Render dashboard env var) and restart the server."
            ),
        )
