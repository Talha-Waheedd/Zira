package com.zira.app.data.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zira.app.data.model.ProgressData;
import com.zira.app.utils.Constants;
import com.zira.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure aggregation logic for {@link ProgressData} from Firestore session documents. */
final class ProgressAggregator {

    private ProgressAggregator() {
    }

    static ProgressData aggregate(List<DocumentSnapshot> studySessions,
                                  List<DocumentSnapshot> quizSessions,
                                  int streakCount,
                                  int totalXp) {
        ProgressData data = new ProgressData();
        data.currentStreak = streakCount;
        data.totalXp = totalXp;

        initDayLabels(data);

        Map<Integer, Float> weeklyBuckets = new HashMap<>();
        Map<Integer, Boolean> dayStudied = new HashMap<>();

        for (DocumentSnapshot doc : studySessions) {
            Timestamp ts = doc.getTimestamp(Constants.FIELD_TIMESTAMP);
            if (ts == null) {
                continue;
            }
            long millis = ts.toDate().getTime();
            Long duration = doc.getLong(Constants.FIELD_DURATION_MINS);
            float mins = duration != null ? duration.floatValue() : 0f;

            for (int day = 0; day < 7; day++) {
                if (DateUtils.isDaysAgo(millis, 6 - day)) {
                    weeklyBuckets.put(day, weeklyBuckets.getOrDefault(day, 0f) + mins);
                }
            }
            for (int day = 0; day < Constants.STREAK_CALENDAR_DAYS; day++) {
                if (DateUtils.isDaysAgo(millis, Constants.STREAK_CALENDAR_DAYS - 1 - day)) {
                    dayStudied.put(day, true);
                }
            }
        }

        for (int i = 0; i < 7; i++) {
            data.weeklyStudyMins[i] = weeklyBuckets.getOrDefault(i, 0f);
        }
        for (int i = 0; i < Constants.STREAK_CALENDAR_DAYS; i++) {
            data.streakDays[i] = Boolean.TRUE.equals(dayStudied.get(i));
        }

        aggregateQuizData(quizSessions, data);
        return data;
    }

    private static void initDayLabels(ProgressData data) {
        for (int i = 0; i < 7; i++) {
            long dayMillis = DateUtils.startOfDayDaysAgo(6 - i);
            data.dayLabels[i] = DateUtils.dayLabel(dayMillis);
        }
    }

    private static void aggregateQuizData(List<DocumentSnapshot> quizSessions,
                                          ProgressData data) {
        Map<String, int[]> subjectTotals = new LinkedHashMap<>();
        Map<String, Integer> topicMissCounts = new HashMap<>();

        for (DocumentSnapshot doc : quizSessions) {
            String subject = doc.getString(Constants.FIELD_SUBJECT);
            if (subject == null) {
                subject = Constants.SUBJECT_GENERAL;
            }
            Long score = doc.getLong(Constants.FIELD_SCORE);
            Long total = doc.getLong(Constants.FIELD_TOTAL_QUESTIONS);
            int s = score != null ? score.intValue() : 0;
            int t = total != null ? total.intValue() : 0;

            int[] acc = subjectTotals.get(subject);
            if (acc == null) {
                acc = new int[]{0, 0};
                subjectTotals.put(subject, acc);
            }
            acc[0] += s;
            acc[1] += t;

            @SuppressWarnings("unchecked")
            List<String> wrong = (List<String>) doc.get(Constants.FIELD_WRONG_TOPICS);
            if (wrong != null) {
                for (String topic : wrong) {
                    if (topic != null && !topic.isEmpty()) {
                        topicMissCounts.merge(topic, 1, Integer::sum);
                    }
                }
            }

            Timestamp ts = doc.getTimestamp(Constants.FIELD_TIMESTAMP);
            if (ts != null) {
                long millis = ts.toDate().getTime();
                for (int day = 0; day < Constants.STREAK_CALENDAR_DAYS; day++) {
                    if (DateUtils.isDaysAgo(millis, Constants.STREAK_CALENDAR_DAYS - 1 - day)) {
                        data.streakDays[day] = true;
                    }
                }
            }
        }

        for (Map.Entry<String, int[]> entry : subjectTotals.entrySet()) {
            int[] acc = entry.getValue();
            if (acc[1] > 0) {
                float pct = 100f * acc[0] / acc[1];
                data.subjectMastery.put(entry.getKey(), pct);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(topicMissCounts.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        for (int i = 0; i < Math.min(Constants.WEAK_TOPICS_LIMIT, sorted.size()); i++) {
            data.weakTopics.add(sorted.get(i).getKey());
        }
    }
}
