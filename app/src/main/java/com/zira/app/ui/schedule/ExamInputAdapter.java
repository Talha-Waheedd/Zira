package com.zira.app.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.zira.app.R;
import com.zira.app.utils.Constants;
import com.zira.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/** Editable list of upcoming exams on the Schedule screen. */
public class ExamInputAdapter extends RecyclerView.Adapter<ExamInputAdapter.ExamViewHolder> {

    public interface ExamChangeListener {
        void onExamsChanged();
    }

    private final List<ExamEntry> exams = new ArrayList<>();
    private final ExamChangeListener listener;

    public ExamInputAdapter(ExamChangeListener listener) {
        this.listener = listener;
        exams.add(new ExamEntry());
    }

    public List<ExamEntry> getExams() {
        return exams;
    }

    public void addExam() {
        exams.add(new ExamEntry());
        notifyItemInserted(exams.size() - 1);
        if (listener != null) {
            listener.onExamsChanged();
        }
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_input, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        holder.bind(exams.get(position), position);
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    class ExamViewHolder extends RecyclerView.ViewHolder {
        private final AutoCompleteTextView actSubject;
        private final MaterialButton btnPickDate;
        private final MaterialButton btnRemove;

        ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            actSubject = itemView.findViewById(R.id.actSubject);
            btnPickDate = itemView.findViewById(R.id.btnPickDate);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        void bind(ExamEntry entry, int position) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    Constants.DEFAULT_SUBJECTS);
            actSubject.setAdapter(adapter);
            if (entry.subject != null) {
                actSubject.setText(entry.subject, false);
            }

            actSubject.setOnItemClickListener((parent, view, index, id) -> {
                entry.subject = Constants.DEFAULT_SUBJECTS[index];
                if (listener != null) {
                    listener.onExamsChanged();
                }
            });

            updateDateLabel(entry);
            btnPickDate.setOnClickListener(v -> showDatePicker(entry));

            btnRemove.setVisibility(exams.size() > 1 ? View.VISIBLE : View.GONE);
            btnRemove.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && exams.size() > 1) {
                    exams.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, exams.size() - pos);
                    if (listener != null) {
                        listener.onExamsChanged();
                    }
                }
            });
        }

        private void updateDateLabel(ExamEntry entry) {
            if (entry.dateIso != null && !entry.dateIso.isEmpty()) {
                btnPickDate.setText(entry.dateIso);
            } else {
                btnPickDate.setText(R.string.schedule_pick_date);
            }
        }

        private void showDatePicker(ExamEntry entry) {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.schedule_exam_date)
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                entry.dateIso = DateUtils.formatIsoDate(selection);
                updateDateLabel(entry);
                if (listener != null) {
                    listener.onExamsChanged();
                }
            });
            picker.show(
                    ((androidx.fragment.app.FragmentActivity) itemView.getContext())
                            .getSupportFragmentManager(),
                    "exam_date");
        }
    }
}
