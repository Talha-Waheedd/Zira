"""POST /api/schedule — personalized study schedule from exam dates."""

import json
import logging
from datetime import date

from fastapi import APIRouter, HTTPException

from gemini_service import SYSTEM_BASE, ask_gemini_json
from models import ScheduleRequest, ScheduleResponse
from routers.deps import require_gemini

router = APIRouter()
logger = logging.getLogger("zira.schedule")


@router.post("/api/schedule", response_model=ScheduleResponse)
def schedule(request: ScheduleRequest) -> ScheduleResponse:
    require_gemini()

    if not request.exams:
        raise HTTPException(status_code=400, detail="At least one exam is required.")

    today = date.today().isoformat()
    exams_json = json.dumps(
        [{"subject": e.subject, "date": e.date} for e in request.exams]
    )

    system = (
        f"{SYSTEM_BASE}\n\n"
        "Return JSON with exactly this shape:\n"
        '{"schedule": [{"date": "yyyy-MM-dd", "subject": "string", '
        '"task": "specific study task", "durationMins": 30}]}\n'
        "Create a realistic day-by-day plan from today until each exam. "
        "Each day's total durationMins should not exceed the daily goal. "
        "Include revision days and weaker-topic review before each exam. "
        "Use ISO dates (yyyy-MM-dd)."
    )
    user = (
        f"Today: {today}\n"
        f"Daily study goal: {request.dailyMins} minutes\n"
        f"Exams: {exams_json}\n"
        f"User ID: {request.userId}"
    )

    try:
        data = ask_gemini_json(system, user, max_tokens=4096)
        response = ScheduleResponse.model_validate(data)
        if not response.schedule:
            raise ValueError("Gemini returned an empty schedule.")
        return response
    except HTTPException:
        raise
    except Exception as exc:  # noqa: BLE001
        logger.exception("schedule failed")
        raise HTTPException(status_code=502, detail=str(exc)) from exc
