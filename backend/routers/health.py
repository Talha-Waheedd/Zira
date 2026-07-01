"""Health check endpoint."""

import os

from fastapi import APIRouter

from llm_service import (
    DEFAULT_GEMINI_MODELS,
    get_configured_providers,
    is_gemini_configured,
    is_groq_configured,
    is_llm_configured,
)
from models import HealthResponse

router = APIRouter()


@router.get("/api/health", response_model=HealthResponse)
def health() -> HealthResponse:
    """Verify the server is running and whether an LLM provider is configured."""
    override = os.getenv("GEMINI_MODEL", "").strip()
    default_model = override if override else DEFAULT_GEMINI_MODELS[0]
    return HealthResponse(
        status="ok",
        service="zira-backend",
        gemini_configured=is_gemini_configured(),
        groq_configured=is_groq_configured(),
        llm_configured=is_llm_configured(),
        providers=get_configured_providers(),
        default_gemini_model=default_model,
    )
