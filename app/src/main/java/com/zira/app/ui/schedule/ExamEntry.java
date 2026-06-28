package com.zira.app.ui.schedule;

/** User-entered exam used to request an AI study schedule. */
public class ExamEntry {

    public String subject;
    public String dateIso;

    public ExamEntry() {
    }

    public ExamEntry(String subject, String dateIso) {
        this.subject = subject;
        this.dateIso = dateIso;
    }

    public boolean isValid() {
        return subject != null && !subject.isEmpty()
                && dateIso != null && !dateIso.isEmpty();
    }
}
