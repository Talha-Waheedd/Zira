"""POST /api/flashcards — spaced-repetition flashcard generation."""

import logging

from fastapi import APIRouter, HTTPException

from gemini_service import SYSTEM_BASE, ask_gemini_json
from models import FlashcardRequest, FlashcardResponse
from routers.deps import require_llm

router = APIRouter()
logger = logging.getLogger("zira.flashcards")


@router.post("/api/flashcards", response_model=FlashcardResponse)
def flashcards(request: FlashcardRequest) -> FlashcardResponse:
    require_llm()

    count = max(1, min(request.count, 20))
    system = (
        f"{SYSTEM_BASE}\n\n"
        "Return JSON with exactly this shape:\n"
        '{"cards": [{"front": "question or term", "back": "answer or definition", '
        '"hint": "short hint"}]}\n'
        f"Generate exactly {count} flashcards. Front = concise question/term, "
        "back = clear answer, hint = one short clue without giving away the answer."
    )
    user = (
        f"Topic: {request.topic}\n"
        f"Number of cards: {count}\n"
        f"User ID: {request.userId}"
    )

    try:
        data = ask_gemini_json(system, user, max_tokens=4096)
        response = FlashcardResponse.model_validate(data)
        if not response.cards:
            raise ValueError("Gemini returned no flashcards.")
        return response
    except Exception as exc:  # noqa: BLE001
        logger.exception("flashcards failed")
        raise HTTPException(status_code=502, detail=str(exc)) from exc
