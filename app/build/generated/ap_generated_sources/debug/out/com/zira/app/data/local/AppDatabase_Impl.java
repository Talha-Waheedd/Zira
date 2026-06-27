package com.zira.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.zira.app.data.local.dao.ExplanationDao;
import com.zira.app.data.local.dao.ExplanationDao_Impl;
import com.zira.app.data.local.dao.FlashcardDao;
import com.zira.app.data.local.dao.FlashcardDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ExplanationDao _explanationDao;

  private volatile FlashcardDao _flashcardDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `explanations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT, `question` TEXT, `explanation` TEXT, `subject` TEXT, `keyConcepts` TEXT, `followUpQuestions` TEXT, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `flashcards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `front` TEXT, `back` TEXT, `hint` TEXT, `subject` TEXT, `difficulty` INTEGER NOT NULL, `nextReviewDate` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8bb9544a0483488a2eaed074b3d39a56')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `explanations`");
        db.execSQL("DROP TABLE IF EXISTS `flashcards`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsExplanations = new HashMap<String, TableInfo.Column>(8);
        _columnsExplanations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("userId", new TableInfo.Column("userId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("question", new TableInfo.Column("question", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("explanation", new TableInfo.Column("explanation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("subject", new TableInfo.Column("subject", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("keyConcepts", new TableInfo.Column("keyConcepts", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("followUpQuestions", new TableInfo.Column("followUpQuestions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExplanations.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExplanations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExplanations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExplanations = new TableInfo("explanations", _columnsExplanations, _foreignKeysExplanations, _indicesExplanations);
        final TableInfo _existingExplanations = TableInfo.read(db, "explanations");
        if (!_infoExplanations.equals(_existingExplanations)) {
          return new RoomOpenHelper.ValidationResult(false, "explanations(com.zira.app.data.local.entity.ExplanationEntity).\n"
                  + " Expected:\n" + _infoExplanations + "\n"
                  + " Found:\n" + _existingExplanations);
        }
        final HashMap<String, TableInfo.Column> _columnsFlashcards = new HashMap<String, TableInfo.Column>(8);
        _columnsFlashcards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("front", new TableInfo.Column("front", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("back", new TableInfo.Column("back", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("hint", new TableInfo.Column("hint", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("subject", new TableInfo.Column("subject", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("difficulty", new TableInfo.Column("difficulty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("nextReviewDate", new TableInfo.Column("nextReviewDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlashcards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFlashcards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFlashcards = new TableInfo("flashcards", _columnsFlashcards, _foreignKeysFlashcards, _indicesFlashcards);
        final TableInfo _existingFlashcards = TableInfo.read(db, "flashcards");
        if (!_infoFlashcards.equals(_existingFlashcards)) {
          return new RoomOpenHelper.ValidationResult(false, "flashcards(com.zira.app.data.local.entity.FlashcardEntity).\n"
                  + " Expected:\n" + _infoFlashcards + "\n"
                  + " Found:\n" + _existingFlashcards);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8bb9544a0483488a2eaed074b3d39a56", "c0500c95186a0656c9a13a42f88a3fa0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "explanations","flashcards");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `explanations`");
      _db.execSQL("DELETE FROM `flashcards`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ExplanationDao.class, ExplanationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FlashcardDao.class, FlashcardDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ExplanationDao explanationDao() {
    if (_explanationDao != null) {
      return _explanationDao;
    } else {
      synchronized(this) {
        if(_explanationDao == null) {
          _explanationDao = new ExplanationDao_Impl(this);
        }
        return _explanationDao;
      }
    }
  }

  @Override
  public FlashcardDao flashcardDao() {
    if (_flashcardDao != null) {
      return _flashcardDao;
    } else {
      synchronized(this) {
        if(_flashcardDao == null) {
          _flashcardDao = new FlashcardDao_Impl(this);
        }
        return _flashcardDao;
      }
    }
  }
}
