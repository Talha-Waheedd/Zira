package com.zira.app.data.remote;

import com.zira.app.data.remote.model.ExplanationRequest;
import com.zira.app.data.remote.model.ExplanationResponse;
import com.zira.app.data.remote.model.FlashcardRequest;
import com.zira.app.data.remote.model.FlashcardResponse;
import com.zira.app.data.remote.model.QuizRequest;
import com.zira.app.data.remote.model.QuizResponse;
import com.zira.app.data.remote.model.ScheduleRequest;
import com.zira.app.data.remote.model.ScheduleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit interface describing the Zira FastAPI backend.
 *
 * <p>All endpoints are POST requests with a JSON body and return a JSON payload parsed by Gson.
 */
public interface ApiService {

    @POST("api/explain")
    Call<ExplanationResponse> getExplanation(@Body ExplanationRequest request);

    @POST("api/quiz")
    Call<QuizResponse> getQuiz(@Body QuizRequest request);

    @POST("api/flashcards")
    Call<FlashcardResponse> getFlashcards(@Body FlashcardRequest request);

    @POST("api/schedule")
    Call<ScheduleResponse> getSchedule(@Body ScheduleRequest request);
}
