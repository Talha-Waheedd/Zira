"""POST /api/explain — step-by-step explanation with follow-ups and key concepts."""

import logging

from fastapi import APIRouter, HTTPException

from gemini_service import SYSTEM_BASE, ask_gemini_json
from models import ExplanationRequest, ExplanationResponse
from routers.deps import require_llm

router = APIRouter()
logger = logging.getLogger("zira.explain")


@router.post("/api/explain", response_model=ExplanationResponse)
def explain(request: ExplanationRequest) -> ExplanationResponse:
    require_llm()

    system = (
        f"{SYSTEM_BASE}\n\n"
        "Return JSON with exactly these keys:\n"
        '{"explanation": "string (step-by-step answer)", '
        '"followUpQuestions": ["string", ...], "keyConcepts": ["string", ...]}\n'
        "Provide 2-4 followUpQuestions and 2-5 keyConcepts."
    )
    user = (
        f"Subject: {request.subject}\n"
        f"Student question: {request.question}\n"
        f"User ID: {request.userId}"
    )

    try:
        data = ask_gemini_json(system, user, max_tokens=2048)
        return ExplanationResponse.model_validate(data)
    except Exception as exc:  # noqa: BLE001
        logger.exception("explain failed")
        raise HTTPException(status_code=502, detail=str(exc)) from exc
