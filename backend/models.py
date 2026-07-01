"""Pydantic models matching the Zira Android Retrofit POJOs (camelCase JSON)."""

from pydantic import BaseModel, Field


# ---------------------------------------------------------------------------
# /api/explain
# ---------------------------------------------------------------------------

class ExplanationRequest(BaseModel):
    question: str
    userId: str
    subject: str


class ExplanationResponse(BaseModel):
    explanation: str
    followUpQuestions: list[str] = Field(default_factory=list)
    keyConcepts: list[str] = Field(default_factory=list)


# ---------------------------------------------------------------------------
# /api/quiz
# ---------------------------------------------------------------------------

class QuizRequest(BaseModel):
    subject: str
    difficulty: str
    count: int
    userId: str


class QuizQuestion(BaseModel):
    question: str
    options: list[str]
    correctIndex: int
    explanation: str


class QuizResponse(BaseModel):
    questions: list[QuizQuestion]


# ---------------------------------------------------------------------------
# /api/flashcards
# ---------------------------------------------------------------------------

class FlashcardRequest(BaseModel):
    topic: str
    count: int
    userId: str


class FlashcardCard(BaseModel):
    front: str
    back: str
    hint: str


class FlashcardResponse(BaseModel):
    cards: list[FlashcardCard]


# ---------------------------------------------------------------------------
# /api/schedule
# ---------------------------------------------------------------------------

class ScheduleExam(BaseModel):
    subject: str
    date: str  # ISO yyyy-MM-dd


class ScheduleRequest(BaseModel):
    exams: list[ScheduleExam]
    dailyMins: int
    userId: str


class ScheduleItem(BaseModel):
    date: str
    subject: str
    task: str
    durationMins: int


class ScheduleResponse(BaseModel):
    schedule: list[ScheduleItem]


# ---------------------------------------------------------------------------
# /api/health
# ---------------------------------------------------------------------------

class HealthResponse(BaseModel):
    status: str
    service: str
    gemini_configured: bool
    groq_configured: bool = False
    llm_configured: bool = False
    providers: list[str] = Field(default_factory=list)
    default_gemini_model: str = ""
