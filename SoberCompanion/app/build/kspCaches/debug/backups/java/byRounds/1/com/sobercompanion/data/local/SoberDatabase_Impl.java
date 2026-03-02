package com.sobercompanion.data.local;

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
import com.sobercompanion.data.local.dao.SobrietyDao;
import com.sobercompanion.data.local.dao.SobrietyDao_Impl;
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
public final class SoberDatabase_Impl extends SoberDatabase {
  private volatile SobrietyDao _sobrietyDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `sobriety_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startDate` TEXT NOT NULL, `endDate` TEXT, `isActive` INTEGER NOT NULL, `reason` TEXT NOT NULL, `note` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `mood` INTEGER NOT NULL, `cravingLevel` INTEGER NOT NULL, `didDrink` INTEGER NOT NULL, `drinkAmount` INTEGER NOT NULL, `note` TEXT NOT NULL, `createdAt` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `milestones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `targetDays` INTEGER NOT NULL, `achievedAt` TEXT, `isAchieved` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `motivational_quotes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quote` TEXT NOT NULL, `author` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'eb422f305cd5d55250fe631450ba96e0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `sobriety_records`");
        db.execSQL("DROP TABLE IF EXISTS `daily_logs`");
        db.execSQL("DROP TABLE IF EXISTS `milestones`");
        db.execSQL("DROP TABLE IF EXISTS `motivational_quotes`");
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
        final HashMap<String, TableInfo.Column> _columnsSobrietyRecords = new HashMap<String, TableInfo.Column>(6);
        _columnsSobrietyRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSobrietyRecords.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSobrietyRecords.put("endDate", new TableInfo.Column("endDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSobrietyRecords.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSobrietyRecords.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSobrietyRecords.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSobrietyRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSobrietyRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSobrietyRecords = new TableInfo("sobriety_records", _columnsSobrietyRecords, _foreignKeysSobrietyRecords, _indicesSobrietyRecords);
        final TableInfo _existingSobrietyRecords = TableInfo.read(db, "sobriety_records");
        if (!_infoSobrietyRecords.equals(_existingSobrietyRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "sobriety_records(com.sobercompanion.data.local.entity.SobrietyRecord).\n"
                  + " Expected:\n" + _infoSobrietyRecords + "\n"
                  + " Found:\n" + _existingSobrietyRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyLogs = new HashMap<String, TableInfo.Column>(8);
        _columnsDailyLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("mood", new TableInfo.Column("mood", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("cravingLevel", new TableInfo.Column("cravingLevel", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("didDrink", new TableInfo.Column("didDrink", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("drinkAmount", new TableInfo.Column("drinkAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLogs.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyLogs = new TableInfo("daily_logs", _columnsDailyLogs, _foreignKeysDailyLogs, _indicesDailyLogs);
        final TableInfo _existingDailyLogs = TableInfo.read(db, "daily_logs");
        if (!_infoDailyLogs.equals(_existingDailyLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_logs(com.sobercompanion.data.local.entity.DailyLog).\n"
                  + " Expected:\n" + _infoDailyLogs + "\n"
                  + " Found:\n" + _existingDailyLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsMilestones = new HashMap<String, TableInfo.Column>(6);
        _columnsMilestones.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("targetDays", new TableInfo.Column("targetDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("achievedAt", new TableInfo.Column("achievedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("isAchieved", new TableInfo.Column("isAchieved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMilestones = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMilestones = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMilestones = new TableInfo("milestones", _columnsMilestones, _foreignKeysMilestones, _indicesMilestones);
        final TableInfo _existingMilestones = TableInfo.read(db, "milestones");
        if (!_infoMilestones.equals(_existingMilestones)) {
          return new RoomOpenHelper.ValidationResult(false, "milestones(com.sobercompanion.data.local.entity.Milestone).\n"
                  + " Expected:\n" + _infoMilestones + "\n"
                  + " Found:\n" + _existingMilestones);
        }
        final HashMap<String, TableInfo.Column> _columnsMotivationalQuotes = new HashMap<String, TableInfo.Column>(3);
        _columnsMotivationalQuotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalQuotes.put("quote", new TableInfo.Column("quote", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalQuotes.put("author", new TableInfo.Column("author", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMotivationalQuotes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMotivationalQuotes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMotivationalQuotes = new TableInfo("motivational_quotes", _columnsMotivationalQuotes, _foreignKeysMotivationalQuotes, _indicesMotivationalQuotes);
        final TableInfo _existingMotivationalQuotes = TableInfo.read(db, "motivational_quotes");
        if (!_infoMotivationalQuotes.equals(_existingMotivationalQuotes)) {
          return new RoomOpenHelper.ValidationResult(false, "motivational_quotes(com.sobercompanion.data.local.entity.MotivationalQuote).\n"
                  + " Expected:\n" + _infoMotivationalQuotes + "\n"
                  + " Found:\n" + _existingMotivationalQuotes);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "eb422f305cd5d55250fe631450ba96e0", "7a9ba63d3ee94555755c53f5d0090a8e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "sobriety_records","daily_logs","milestones","motivational_quotes");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `sobriety_records`");
      _db.execSQL("DELETE FROM `daily_logs`");
      _db.execSQL("DELETE FROM `milestones`");
      _db.execSQL("DELETE FROM `motivational_quotes`");
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
    _typeConvertersMap.put(SobrietyDao.class, SobrietyDao_Impl.getRequiredConverters());
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
  public SobrietyDao sobrietyDao() {
    if (_sobrietyDao != null) {
      return _sobrietyDao;
    } else {
      synchronized(this) {
        if(_sobrietyDao == null) {
          _sobrietyDao = new SobrietyDao_Impl(this);
        }
        return _sobrietyDao;
      }
    }
  }
}
