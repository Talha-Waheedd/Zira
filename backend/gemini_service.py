"""Google Gemini API helper — delegates to llm_service for backward compatibility."""

from llm_service import (  # noqa: F401
    SYSTEM_BASE,
    ask_llm_json as ask_gemini_json,
    get_configured_providers,
    is_gemini_configured,
    is_groq_configured,
    is_llm_configured,
)

DEFAULT_MODEL = "gemini-2.5-flash"


def get_model_name() -> str:
    import os
    from llm_service import DEFAULT_GEMINI_MODELS

    override = os.getenv("GEMINI_MODEL", "").strip()
    deprecated = {
        "gemini-2.0-flash",
        "gemini-2.0-flash-exp",
        "gemini-1.5-flash",
        "gemini-1.5-pro",
    }
    if override and override not in deprecated:
        return override
    return DEFAULT_GEMINI_MODELS[0]
