package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Response body for {@code POST /api/schedule}. */
public class ScheduleResponse {

    @SerializedName("schedule")
    private List<ScheduleItem> schedule;

    public List<ScheduleItem> getSchedule() {
        return schedule;
    }

    /** A single planned study task on a given date. */
    public static class ScheduleItem {

        @SerializedName("date")
        private String date;

        @SerializedName("subject")
        private String subject;

        @SerializedName("task")
        private String task;

        @SerializedName("durationMins")
        private int durationMins;

        public String getDate() {
            return date;
        }

        public String getSubject() {
            return subject;
        }

        public String getTask() {
            return task;
        }

        public int getDurationMins() {
            return durationMins;
        }
    }
}
