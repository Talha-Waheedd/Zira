"""Health check endpoint."""

from fastapi import APIRouter

from gemini_service import is_gemini_configured
from models import HealthResponse

router = APIRouter()


@router.get("/api/health", response_model=HealthResponse)
def health() -> HealthResponse:
    """Verify the server is running and whether Gemini is configured."""
    return HealthResponse(
        status="ok",
        service="zira-backend",
        gemini_configured=is_gemini_configured(),
    )
