package com.sobercompanion.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sobercompanion.data.local.Converters;
import com.sobercompanion.data.local.entity.DailyLog;
import com.sobercompanion.data.local.entity.Milestone;
import com.sobercompanion.data.local.entity.MotivationalQuote;
import com.sobercompanion.data.local.entity.SobrietyRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SobrietyDao_Impl implements SobrietyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SobrietyRecord> __insertionAdapterOfSobrietyRecord;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<DailyLog> __insertionAdapterOfDailyLog;

  private final EntityInsertionAdapter<Milestone> __insertionAdapterOfMilestone;

  private final EntityInsertionAdapter<MotivationalQuote> __insertionAdapterOfMotivationalQuote;

  private final EntityDeletionOrUpdateAdapter<SobrietyRecord> __deletionAdapterOfSobrietyRecord;

  private final EntityDeletionOrUpdateAdapter<DailyLog> __deletionAdapterOfDailyLog;

  private final EntityDeletionOrUpdateAdapter<SobrietyRecord> __updateAdapterOfSobrietyRecord;

  private final EntityDeletionOrUpdateAdapter<DailyLog> __updateAdapterOfDailyLog;

  private final EntityDeletionOrUpdateAdapter<Milestone> __updateAdapterOfMilestone;

  private final SharedSQLiteStatement __preparedStmtOfEndCurrentSobriety;

  public SobrietyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSobrietyRecord = new EntityInsertionAdapter<SobrietyRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sobriety_records` (`id`,`startDate`,`endDate`,`isActive`,`reason`,`note`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SobrietyRecord entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromLocalDateTime(entity.getStartDate());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getEndDate());
        if (_tmp_1 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_1);
        }
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp_2);
        statement.bindString(5, entity.getReason());
        statement.bindString(6, entity.getNote());
      }
    };
    this.__insertionAdapterOfDailyLog = new EntityInsertionAdapter<DailyLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_logs` (`id`,`date`,`mood`,`cravingLevel`,`didDrink`,`drinkAmount`,`note`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyLog entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromLocalDate(entity.getDate());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        statement.bindLong(3, entity.getMood());
        statement.bindLong(4, entity.getCravingLevel());
        final int _tmp_1 = entity.getDidDrink() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindLong(6, entity.getDrinkAmount());
        statement.bindString(7, entity.getNote());
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
      }
    };
    this.__insertionAdapterOfMilestone = new EntityInsertionAdapter<Milestone>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `milestones` (`id`,`title`,`description`,`targetDays`,`achievedAt`,`isAchieved`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Milestone entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getTargetDays());
        final String _tmp = __converters.fromLocalDateTime(entity.getAchievedAt());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.isAchieved() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__insertionAdapterOfMotivationalQuote = new EntityInsertionAdapter<MotivationalQuote>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `motivational_quotes` (`id`,`quote`,`author`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MotivationalQuote entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getQuote());
        statement.bindString(3, entity.getAuthor());
      }
    };
    this.__deletionAdapterOfSobrietyRecord = new EntityDeletionOrUpdateAdapter<SobrietyRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sobriety_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SobrietyRecord entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfDailyLog = new EntityDeletionOrUpdateAdapter<DailyLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `daily_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyLog entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSobrietyRecord = new EntityDeletionOrUpdateAdapter<SobrietyRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sobriety_records` SET `id` = ?,`startDate` = ?,`endDate` = ?,`isActive` = ?,`reason` = ?,`note` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SobrietyRecord entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromLocalDateTime(entity.getStartDate());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getEndDate());
        if (_tmp_1 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_1);
        }
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp_2);
        statement.bindString(5, entity.getReason());
        statement.bindString(6, entity.getNote());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__updateAdapterOfDailyLog = new EntityDeletionOrUpdateAdapter<DailyLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `daily_logs` SET `id` = ?,`date` = ?,`mood` = ?,`cravingLevel` = ?,`didDrink` = ?,`drinkAmount` = ?,`note` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyLog entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromLocalDate(entity.getDate());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        statement.bindLong(3, entity.getMood());
        statement.bindLong(4, entity.getCravingLevel());
        final int _tmp_1 = entity.getDidDrink() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindLong(6, entity.getDrinkAmount());
        statement.bindString(7, entity.getNote());
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__updateAdapterOfMilestone = new EntityDeletionOrUpdateAdapter<Milestone>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `milestones` SET `id` = ?,`title` = ?,`description` = ?,`targetDays` = ?,`achievedAt` = ?,`isAchieved` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Milestone entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getTargetDays());
        final String _tmp = __converters.fromLocalDateTime(entity.getAchievedAt());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.isAchieved() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfEndCurrentSobriety = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sobriety_records SET isActive = 0, endDate = ? WHERE isActive = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insertSobrietyRecord(final SobrietyRecord record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSobrietyRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDailyLog(final DailyLog log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDailyLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMilestone(final Milestone milestone,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMilestone.insertAndReturnId(milestone);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMilestones(final List<Milestone> milestones,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMilestone.insert(milestones);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuote(final MotivationalQuote quote,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMotivationalQuote.insertAndReturnId(quote);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuotes(final List<MotivationalQuote> quotes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMotivationalQuote.insert(quotes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSobrietyRecord(final SobrietyRecord record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSobrietyRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDailyLog(final DailyLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDailyLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSobrietyRecord(final SobrietyRecord record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSobrietyRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDailyLog(final DailyLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDailyLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMilestone(final Milestone milestone,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMilestone.handle(milestone);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object endCurrentSobriety(final LocalDateTime endDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEndCurrentSobriety.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromLocalDateTime(endDate);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfEndCurrentSobriety.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<SobrietyRecord> getActiveSobrietyRecord() {
    final String _sql = "SELECT * FROM sobriety_records WHERE isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sobriety_records"}, new Callable<SobrietyRecord>() {
      @Override
      @Nullable
      public SobrietyRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final SobrietyRecord _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDateTime _tmpStartDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartDate);
            }
            final LocalDateTime _tmp_1 = __converters.toLocalDateTime(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpStartDate = _tmp_1;
            }
            final LocalDateTime _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            _tmpEndDate = __converters.toLocalDateTime(_tmp_2);
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new SobrietyRecord(_tmpId,_tmpStartDate,_tmpEndDate,_tmpIsActive,_tmpReason,_tmpNote);
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

  @Override
  public Flow<List<SobrietyRecord>> getAllSobrietyRecords() {
    final String _sql = "SELECT * FROM sobriety_records ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sobriety_records"}, new Callable<List<SobrietyRecord>>() {
      @Override
      @NonNull
      public List<SobrietyRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SobrietyRecord> _result = new ArrayList<SobrietyRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SobrietyRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDateTime _tmpStartDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartDate);
            }
            final LocalDateTime _tmp_1 = __converters.toLocalDateTime(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpStartDate = _tmp_1;
            }
            final LocalDateTime _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            _tmpEndDate = __converters.toLocalDateTime(_tmp_2);
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new SobrietyRecord(_tmpId,_tmpStartDate,_tmpEndDate,_tmpIsActive,_tmpReason,_tmpNote);
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
  public Object getDailyLogByDate(final LocalDate date,
      final Continuation<? super DailyLog> $completion) {
    final String _sql = "SELECT * FROM daily_logs WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyLog>() {
      @Override
      @Nullable
      public DailyLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingLevel");
          final int _cursorIndexOfDidDrink = CursorUtil.getColumnIndexOrThrow(_cursor, "didDrink");
          final int _cursorIndexOfDrinkAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "drinkAmount");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DailyLog _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDate _tmpDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_2 = __converters.toLocalDate(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_2;
            }
            final int _tmpMood;
            _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            final int _tmpCravingLevel;
            _tmpCravingLevel = _cursor.getInt(_cursorIndexOfCravingLevel);
            final boolean _tmpDidDrink;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfDidDrink);
            _tmpDidDrink = _tmp_3 != 0;
            final int _tmpDrinkAmount;
            _tmpDrinkAmount = _cursor.getInt(_cursorIndexOfDrinkAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final LocalDateTime _tmp_5 = __converters.toLocalDateTime(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_5;
            }
            _result = new DailyLog(_tmpId,_tmpDate,_tmpMood,_tmpCravingLevel,_tmpDidDrink,_tmpDrinkAmount,_tmpNote,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyLog>> getAllDailyLogs() {
    final String _sql = "SELECT * FROM daily_logs ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_logs"}, new Callable<List<DailyLog>>() {
      @Override
      @NonNull
      public List<DailyLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingLevel");
          final int _cursorIndexOfDidDrink = CursorUtil.getColumnIndexOrThrow(_cursor, "didDrink");
          final int _cursorIndexOfDrinkAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "drinkAmount");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<DailyLog> _result = new ArrayList<DailyLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDate _tmpDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_1 = __converters.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_1;
            }
            final int _tmpMood;
            _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            final int _tmpCravingLevel;
            _tmpCravingLevel = _cursor.getInt(_cursorIndexOfCravingLevel);
            final boolean _tmpDidDrink;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDidDrink);
            _tmpDidDrink = _tmp_2 != 0;
            final int _tmpDrinkAmount;
            _tmpDrinkAmount = _cursor.getInt(_cursorIndexOfDrinkAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final LocalDateTime _tmp_4 = __converters.toLocalDateTime(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_4;
            }
            _item = new DailyLog(_tmpId,_tmpDate,_tmpMood,_tmpCravingLevel,_tmpDidDrink,_tmpDrinkAmount,_tmpNote,_tmpCreatedAt);
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
  public Flow<List<DailyLog>> getDailyLogsBetween(final LocalDate startDate,
      final LocalDate endDate) {
    final String _sql = "SELECT * FROM daily_logs WHERE date BETWEEN ? AND ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromLocalDate(startDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __converters.fromLocalDate(endDate);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_logs"}, new Callable<List<DailyLog>>() {
      @Override
      @NonNull
      public List<DailyLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingLevel");
          final int _cursorIndexOfDidDrink = CursorUtil.getColumnIndexOrThrow(_cursor, "didDrink");
          final int _cursorIndexOfDrinkAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "drinkAmount");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<DailyLog> _result = new ArrayList<DailyLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDate _tmpDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_3 = __converters.toLocalDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_3;
            }
            final int _tmpMood;
            _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            final int _tmpCravingLevel;
            _tmpCravingLevel = _cursor.getInt(_cursorIndexOfCravingLevel);
            final boolean _tmpDidDrink;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfDidDrink);
            _tmpDidDrink = _tmp_4 != 0;
            final int _tmpDrinkAmount;
            _tmpDrinkAmount = _cursor.getInt(_cursorIndexOfDrinkAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final LocalDateTime _tmp_6 = __converters.toLocalDateTime(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_6;
            }
            _item = new DailyLog(_tmpId,_tmpDate,_tmpMood,_tmpCravingLevel,_tmpDidDrink,_tmpDrinkAmount,_tmpNote,_tmpCreatedAt);
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
  public Flow<List<DailyLog>> getRecentDailyLogs(final int limit) {
    final String _sql = "SELECT * FROM daily_logs ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_logs"}, new Callable<List<DailyLog>>() {
      @Override
      @NonNull
      public List<DailyLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfCravingLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "cravingLevel");
          final int _cursorIndexOfDidDrink = CursorUtil.getColumnIndexOrThrow(_cursor, "didDrink");
          final int _cursorIndexOfDrinkAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "drinkAmount");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<DailyLog> _result = new ArrayList<DailyLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final LocalDate _tmpDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_1 = __converters.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_1;
            }
            final int _tmpMood;
            _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            final int _tmpCravingLevel;
            _tmpCravingLevel = _cursor.getInt(_cursorIndexOfCravingLevel);
            final boolean _tmpDidDrink;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDidDrink);
            _tmpDidDrink = _tmp_2 != 0;
            final int _tmpDrinkAmount;
            _tmpDrinkAmount = _cursor.getInt(_cursorIndexOfDrinkAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final LocalDateTime _tmp_4 = __converters.toLocalDateTime(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_4;
            }
            _item = new DailyLog(_tmpId,_tmpDate,_tmpMood,_tmpCravingLevel,_tmpDidDrink,_tmpDrinkAmount,_tmpNote,_tmpCreatedAt);
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
  public Flow<List<Milestone>> getAllMilestones() {
    final String _sql = "SELECT * FROM milestones ORDER BY targetDays ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"milestones"}, new Callable<List<Milestone>>() {
      @Override
      @NonNull
      public List<Milestone> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetDays = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDays");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final int _cursorIndexOfIsAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "isAchieved");
          final List<Milestone> _result = new ArrayList<Milestone>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Milestone _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpTargetDays;
            _tmpTargetDays = _cursor.getInt(_cursorIndexOfTargetDays);
            final LocalDateTime _tmpAchievedAt;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAchievedAt);
            }
            _tmpAchievedAt = __converters.toLocalDateTime(_tmp);
            final boolean _tmpIsAchieved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsAchieved);
            _tmpIsAchieved = _tmp_1 != 0;
            _item = new Milestone(_tmpId,_tmpTitle,_tmpDescription,_tmpTargetDays,_tmpAchievedAt,_tmpIsAchieved);
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
  public Flow<List<Milestone>> getUnachievedMilestones() {
    final String _sql = "SELECT * FROM milestones WHERE isAchieved = 0 ORDER BY targetDays ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"milestones"}, new Callable<List<Milestone>>() {
      @Override
      @NonNull
      public List<Milestone> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetDays = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDays");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final int _cursorIndexOfIsAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "isAchieved");
          final List<Milestone> _result = new ArrayList<Milestone>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Milestone _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpTargetDays;
            _tmpTargetDays = _cursor.getInt(_cursorIndexOfTargetDays);
            final LocalDateTime _tmpAchievedAt;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAchievedAt);
            }
            _tmpAchievedAt = __converters.toLocalDateTime(_tmp);
            final boolean _tmpIsAchieved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsAchieved);
            _tmpIsAchieved = _tmp_1 != 0;
            _item = new Milestone(_tmpId,_tmpTitle,_tmpDescription,_tmpTargetDays,_tmpAchievedAt,_tmpIsAchieved);
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
  public Flow<List<Milestone>> getAchievedMilestones() {
    final String _sql = "SELECT * FROM milestones WHERE isAchieved = 1 ORDER BY achievedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"milestones"}, new Callable<List<Milestone>>() {
      @Override
      @NonNull
      public List<Milestone> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetDays = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDays");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final int _cursorIndexOfIsAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "isAchieved");
          final List<Milestone> _result = new ArrayList<Milestone>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Milestone _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpTargetDays;
            _tmpTargetDays = _cursor.getInt(_cursorIndexOfTargetDays);
            final LocalDateTime _tmpAchievedAt;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAchievedAt);
            }
            _tmpAchievedAt = __converters.toLocalDateTime(_tmp);
            final boolean _tmpIsAchieved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsAchieved);
            _tmpIsAchieved = _tmp_1 != 0;
            _item = new Milestone(_tmpId,_tmpTitle,_tmpDescription,_tmpTargetDays,_tmpAchievedAt,_tmpIsAchieved);
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
  public Object getMilestonesToAchieve(final int days,
      final Continuation<? super List<Milestone>> $completion) {
    final String _sql = "SELECT * FROM milestones WHERE targetDays <= ? AND isAchieved = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Milestone>>() {
      @Override
      @NonNull
      public List<Milestone> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTargetDays = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDays");
          final int _cursorIndexOfAchievedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedAt");
          final int _cursorIndexOfIsAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "isAchieved");
          final List<Milestone> _result = new ArrayList<Milestone>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Milestone _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpTargetDays;
            _tmpTargetDays = _cursor.getInt(_cursorIndexOfTargetDays);
            final LocalDateTime _tmpAchievedAt;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAchievedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAchievedAt);
            }
            _tmpAchievedAt = __converters.toLocalDateTime(_tmp);
            final boolean _tmpIsAchieved;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsAchieved);
            _tmpIsAchieved = _tmp_1 != 0;
            _item = new Milestone(_tmpId,_tmpTitle,_tmpDescription,_tmpTargetDays,_tmpAchievedAt,_tmpIsAchieved);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRandomQuote(final Continuation<? super MotivationalQuote> $completion) {
    final String _sql = "SELECT * FROM motivational_quotes ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MotivationalQuote>() {
      @Override
      @Nullable
      public MotivationalQuote call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuote = CursorUtil.getColumnIndexOrThrow(_cursor, "quote");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final MotivationalQuote _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpQuote;
            _tmpQuote = _cursor.getString(_cursorIndexOfQuote);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            _result = new MotivationalQuote(_tmpId,_tmpQuote,_tmpAuthor);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getQuoteCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM motivational_quotes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
