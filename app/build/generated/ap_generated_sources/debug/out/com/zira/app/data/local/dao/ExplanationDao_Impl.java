package com.zira.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.zira.app.data.local.Converters;
import com.zira.app.data.local.entity.ExplanationEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExplanationDao_Impl implements ExplanationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExplanationEntity> __insertionAdapterOfExplanationEntity;

  public ExplanationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExplanationEntity = new EntityInsertionAdapter<ExplanationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `explanations` (`id`,`userId`,`question`,`explanation`,`subject`,`keyConcepts`,`followUpQuestions`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ExplanationEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.userId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.userId);
        }
        if (entity.question == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.question);
        }
        if (entity.explanation == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.explanation);
        }
        if (entity.subject == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.subject);
        }
        final String _tmp = Converters.fromStringList(entity.keyConcepts);
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        final String _tmp_1 = Converters.fromStringList(entity.followUpQuestions);
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        statement.bindLong(8, entity.timestamp);
      }
    };
  }

  @Override
  public long insert(final ExplanationEntity explanation) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfExplanationEntity.insertAndReturnId(explanation);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<ExplanationEntity>> getRecent(final String userId, final int limit) {
    final String _sql = "SELECT * FROM explanations WHERE userId = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return __db.getInvalidationTracker().createLiveData(new String[] {"explanations"}, false, new Callable<List<ExplanationEntity>>() {
      @Override
      @Nullable
      public List<ExplanationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfKeyConcepts = CursorUtil.getColumnIndexOrThrow(_cursor, "keyConcepts");
          final int _cursorIndexOfFollowUpQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpQuestions");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ExplanationEntity> _result = new ArrayList<ExplanationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExplanationEntity _item;
            _item = new ExplanationEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _item.userId = null;
            } else {
              _item.userId = _cursor.getString(_cursorIndexOfUserId);
            }
            if (_cursor.isNull(_cursorIndexOfQuestion)) {
              _item.question = null;
            } else {
              _item.question = _cursor.getString(_cursorIndexOfQuestion);
            }
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _item.explanation = null;
            } else {
              _item.explanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfKeyConcepts)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfKeyConcepts);
            }
            _item.keyConcepts = Converters.toStringList(_tmp);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfFollowUpQuestions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfFollowUpQuestions);
            }
            _item.followUpQuestions = Converters.toStringList(_tmp_1);
            _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<ExplanationEntity>> getAll(final String userId) {
    final String _sql = "SELECT * FROM explanations WHERE userId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"explanations"}, false, new Callable<List<ExplanationEntity>>() {
      @Override
      @Nullable
      public List<ExplanationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfKeyConcepts = CursorUtil.getColumnIndexOrThrow(_cursor, "keyConcepts");
          final int _cursorIndexOfFollowUpQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpQuestions");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ExplanationEntity> _result = new ArrayList<ExplanationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExplanationEntity _item;
            _item = new ExplanationEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _item.userId = null;
            } else {
              _item.userId = _cursor.getString(_cursorIndexOfUserId);
            }
            if (_cursor.isNull(_cursorIndexOfQuestion)) {
              _item.question = null;
            } else {
              _item.question = _cursor.getString(_cursorIndexOfQuestion);
            }
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _item.explanation = null;
            } else {
              _item.explanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfKeyConcepts)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfKeyConcepts);
            }
            _item.keyConcepts = Converters.toStringList(_tmp);
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfFollowUpQuestions)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfFollowUpQuestions);
            }
            _item.followUpQuestions = Converters.toStringList(_tmp_1);
            _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
