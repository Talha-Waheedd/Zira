package com.zira.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.zira.app.data.local.entity.DeckSummary;
import com.zira.app.data.local.entity.FlashcardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class FlashcardDao_Impl implements FlashcardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FlashcardEntity> __insertionAdapterOfFlashcardEntity;

  private final EntityDeletionOrUpdateAdapter<FlashcardEntity> __updateAdapterOfFlashcardEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReviewDate;

  public FlashcardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlashcardEntity = new EntityInsertionAdapter<FlashcardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `flashcards` (`id`,`front`,`back`,`hint`,`subject`,`difficulty`,`nextReviewDate`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final FlashcardEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.front == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.front);
        }
        if (entity.back == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.back);
        }
        if (entity.hint == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.hint);
        }
        if (entity.subject == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.subject);
        }
        statement.bindLong(6, entity.difficulty);
        statement.bindLong(7, entity.nextReviewDate);
        statement.bindLong(8, entity.createdAt);
      }
    };
    this.__updateAdapterOfFlashcardEntity = new EntityDeletionOrUpdateAdapter<FlashcardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flashcards` SET `id` = ?,`front` = ?,`back` = ?,`hint` = ?,`subject` = ?,`difficulty` = ?,`nextReviewDate` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final FlashcardEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.front == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.front);
        }
        if (entity.back == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.back);
        }
        if (entity.hint == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.hint);
        }
        if (entity.subject == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.subject);
        }
        statement.bindLong(6, entity.difficulty);
        statement.bindLong(7, entity.nextReviewDate);
        statement.bindLong(8, entity.createdAt);
        statement.bindLong(9, entity.id);
      }
    };
    this.__preparedStmtOfUpdateReviewDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE flashcards SET nextReviewDate = ?, difficulty = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<FlashcardEntity> cards) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfFlashcardEntity.insert(cards);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final FlashcardEntity card) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfFlashcardEntity.handle(card);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateReviewDate(final int id, final long nextReviewDate, final int difficulty) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReviewDate.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, nextReviewDate);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, difficulty);
    _argIndex = 3;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateReviewDate.release(_stmt);
    }
  }

  @Override
  public LiveData<List<FlashcardEntity>> getBySubject(final String subject) {
    final String _sql = "SELECT * FROM flashcards WHERE subject = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (subject == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, subject);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"flashcards"}, false, new Callable<List<FlashcardEntity>>() {
      @Override
      @Nullable
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfHint = CursorUtil.getColumnIndexOrThrow(_cursor, "hint");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            _item = new FlashcardEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _item.front = null;
            } else {
              _item.front = _cursor.getString(_cursorIndexOfFront);
            }
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _item.back = null;
            } else {
              _item.back = _cursor.getString(_cursorIndexOfBack);
            }
            if (_cursor.isNull(_cursorIndexOfHint)) {
              _item.hint = null;
            } else {
              _item.hint = _cursor.getString(_cursorIndexOfHint);
            }
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            _item.difficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            _item.nextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
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
  public LiveData<List<FlashcardEntity>> getDueToday(final long endOfToday) {
    final String _sql = "SELECT * FROM flashcards WHERE nextReviewDate <= ? ORDER BY nextReviewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, endOfToday);
    return __db.getInvalidationTracker().createLiveData(new String[] {"flashcards"}, false, new Callable<List<FlashcardEntity>>() {
      @Override
      @Nullable
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfHint = CursorUtil.getColumnIndexOrThrow(_cursor, "hint");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            _item = new FlashcardEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _item.front = null;
            } else {
              _item.front = _cursor.getString(_cursorIndexOfFront);
            }
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _item.back = null;
            } else {
              _item.back = _cursor.getString(_cursorIndexOfBack);
            }
            if (_cursor.isNull(_cursorIndexOfHint)) {
              _item.hint = null;
            } else {
              _item.hint = _cursor.getString(_cursorIndexOfHint);
            }
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            _item.difficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            _item.nextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
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
  public LiveData<List<FlashcardEntity>> getDueBySubject(final String subject,
      final long endOfToday) {
    final String _sql = "SELECT * FROM flashcards WHERE subject = ? AND nextReviewDate <= ? ORDER BY nextReviewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (subject == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, subject);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfToday);
    return __db.getInvalidationTracker().createLiveData(new String[] {"flashcards"}, false, new Callable<List<FlashcardEntity>>() {
      @Override
      @Nullable
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFront = CursorUtil.getColumnIndexOrThrow(_cursor, "front");
          final int _cursorIndexOfBack = CursorUtil.getColumnIndexOrThrow(_cursor, "back");
          final int _cursorIndexOfHint = CursorUtil.getColumnIndexOrThrow(_cursor, "hint");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            _item = new FlashcardEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfFront)) {
              _item.front = null;
            } else {
              _item.front = _cursor.getString(_cursorIndexOfFront);
            }
            if (_cursor.isNull(_cursorIndexOfBack)) {
              _item.back = null;
            } else {
              _item.back = _cursor.getString(_cursorIndexOfBack);
            }
            if (_cursor.isNull(_cursorIndexOfHint)) {
              _item.hint = null;
            } else {
              _item.hint = _cursor.getString(_cursorIndexOfHint);
            }
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            _item.difficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            _item.nextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
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
  public LiveData<List<DeckSummary>> getDeckSummaries(final long endOfToday) {
    final String _sql = "SELECT subject AS subject, COUNT(*) AS totalCount, SUM(CASE WHEN nextReviewDate <= ? THEN 1 ELSE 0 END) AS dueCount FROM flashcards GROUP BY subject ORDER BY subject ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, endOfToday);
    return __db.getInvalidationTracker().createLiveData(new String[] {"flashcards"}, false, new Callable<List<DeckSummary>>() {
      @Override
      @Nullable
      public List<DeckSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSubject = 0;
          final int _cursorIndexOfTotalCount = 1;
          final int _cursorIndexOfDueCount = 2;
          final List<DeckSummary> _result = new ArrayList<DeckSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeckSummary _item;
            _item = new DeckSummary();
            if (_cursor.isNull(_cursorIndexOfSubject)) {
              _item.subject = null;
            } else {
              _item.subject = _cursor.getString(_cursorIndexOfSubject);
            }
            _item.totalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            _item.dueCount = _cursor.getInt(_cursorIndexOfDueCount);
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
  public LiveData<Integer> getTotalDueCount(final long endOfToday) {
    final String _sql = "SELECT COUNT(*) FROM flashcards WHERE nextReviewDate <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, endOfToday);
    return __db.getInvalidationTracker().createLiveData(new String[] {"flashcards"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
