"""POST /api/quiz — multiple-choice quiz generation."""

import logging

from fastapi import APIRouter, HTTPException

from gemini_service import SYSTEM_BASE, ask_gemini_json
from models import QuizRequest, QuizResponse
from routers.deps import require_gemini

router = APIRouter()
logger = logging.getLogger("zira.quiz")


@router.post("/api/quiz", response_model=QuizResponse)
def quiz(request: QuizRequest) -> QuizResponse:
    require_gemini()

    count = max(1, min(request.count, 10))
    system = (
        f"{SYSTEM_BASE}\n\n"
        "Return JSON with exactly this shape:\n"
        '{"questions": [{"question": "string", "options": ["A","B","C","D"], '
        '"correctIndex": 0, "explanation": "string"}]}\n'
        f"Generate exactly {count} questions. Each question must have exactly 4 options. "
        f"correctIndex is 0-based (0-3). Match difficulty: {request.difficulty}."
    )
    user = (
        f"Subject: {request.subject}\n"
        f"Difficulty: {request.difficulty}\n"
        f"Number of questions: {count}\n"
        f"User ID: {request.userId}"
    )

    try:
        data = ask_gemini_json(system, user, max_tokens=4096)
        response = QuizResponse.model_validate(data)
        if not response.questions:
            raise ValueError("Gemini returned no questions.")
        return response
    except Exception as exc:  # noqa: BLE001
        logger.exception("quiz failed")
        raise HTTPException(status_code=502, detail=str(exc)) from exc
