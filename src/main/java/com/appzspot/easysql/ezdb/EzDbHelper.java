package com.appzspot.easysql.ezdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.appzspot.easysql.config.ColumnConfigs;
import com.appzspot.easysql.config.Config;
import com.appzspot.easysql.config.ModelConfig;
import com.appzspot.easysql.ezdb.ezdto.EzTables;
import com.appzspot.easysql.util.StaticHelper;
import com.appzspot.easysql.util.UtilityMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Muhammad Faisal Nadeem on 10/23/2017.
 * Copyright © 2017 by Muhammad Faisal Nadeem
 * <p>
 * All information contained herein is, and remains
 * the property of Muhammad Faisal Nadeem. No part of this document
 * may be reproduced, distributed, or transmitted in any form or by any means
 * without the prior written permission of the publisher.
 * <p>
 * For permission request write to :-
 * <p>
 * Muhammad Faisal Nadeem.
 * mfaisalnadeem@hotmail.com
 */

public class EzDbHelper extends SQLiteOpenHelper {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "EzDbHelper";

   private static final String EZ_TABLE_CACHE = "ez_cache";

   private static final String EZ_COL_ID = "id";
   private static final String EZ_COL_SQL = "sql";
   private static final String EZ_COL_VERSION = "version";

   private static final String EZ_SQL_INSERT = "INSERT INTO %1$s %2$s VALUES (%3#s)";

   private static final String EZ_DEBUG_TABLE_CREATE_START = "creating table '%1$s'.";
   private static final String EZ_DEBUG_TABLE_CREATE_SUCCESS = "table '%1$s' successfully created.";
   private static final String EZ_ERROR_TABLE_CREATE_FAIL = "could not create table '%1$s'.";

   private String dbName = "";
   private int version = 1;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * @param context
    * @param name
    * @param factory
    * @param version
    */
   public EzDbHelper ( Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
      super ( context, name, factory, version );
      this.dbName = name;
      this.version = version;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Open helper methods.
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onCreate ( SQLiteDatabase db ) {
      // init ez cache table.
      initConfigTable ( db );

      // create tables from configs.
      createUserTables ( db );
   }

   @Override
   public void onUpgrade ( SQLiteDatabase db, int oldV, int newV ) {

      // TODO: 10/25/2017 finish the upgrade method.

      // 1. load old version.
      int lastVersion = loadLastVersionFromDb ( db );

      // 2. save old data into tables.
      boolean saveOldData = Config.getConfig ().isSaveDataOnUpgradeToggle ();
      if ( saveOldData ) {
         String query = "SELECT * FROM sqlite_master WHERE type = 'table' and name != 'sqlite_sequence'";
         Cursor cursor = db.rawQuery ( query, null );

         if ( cursor.moveToFirst () ) {
            do {
               String tableName = cursor.getString ( cursor.getColumnIndex ( "tbl_name" ) );

               // 2.1 create temp tables for each user defined table.
               String tempQuery = "CREATE TEMP TABLE TEMP_" + tableName + " AS SELECT * FROM " + tableName;
               db.execSQL ( tempQuery );

               // 2.2 remove old tables.
               String dropTableQuery = "DROP TABLE " + tableName;

               // 2.3 create user tables
               for ( ModelConfig modelConfig : Config.getConfig ().getModelConfigs () ) {

                  // 2.3.1 get the table configs.
                  String modelTableName = modelConfig.getTableName ();
                  ArrayList < ColumnConfigs > columnConfigs = modelConfig.getColConfigs ();
                  Collections.reverse ( columnConfigs );

                  // 2.3.2 create new table.
                  String createTableQuery = "CREATE TABLE " + modelTableName + " ( ";
                  for ( ColumnConfigs columnConfig : columnConfigs ) {
                     createTableQuery += " " + columnConfig.getColumnName () + " " + getSQLTypeForClass ( columnConfig.getFieldType () );
                     createTableQuery += " " + columnConfig.getConstraints ();

                     if ( ( columnConfigs.indexOf ( columnConfig ) < ( columnConfigs.size () - 1 ) ) )
                        createTableQuery += " , ";

                  }

                  // set the primary key.
                  if ( modelConfig.getPrimaryKey () != null ) {
                     createTableQuery += " , "
                             + String.format ( StaticHelper.SQLITE_CONSTRAINT_PK, modelConfig.getPrimaryKey ().getColumnName () );
                  }

                  // set the foreign key.
                  if ( modelConfig.getForeignKey () != null ) {
                     String fkValStr = modelConfig.getForeignKey ().getFkValue ();
                     String fkVal[] = fkValStr.split ( "\\." );
                     String fkTable = fkVal[ 0 ];
                     String fkCol = fkVal[ 1 ];

                     query += String.format ( StaticHelper.SQLITE_CONSTRAINT_FK
                             , modelConfig.getForeignKey ().getColumnName (), fkTable, fkCol );
                  }

                  createTableQuery += " ) ";

                  if ( Config.loggingEnabled )
                     Log.d ( TAG, "createUserTables: Query for table '" + modelConfig.getTableName () + "' is : " + createTableQuery );

                  // create table and add to model.
                  db.execSQL ( createTableQuery );

                  if ( checkTable ( db, modelConfig.getTableName () ) ) {
                     String ez_query = "insert into `" + EZ_TABLE_CACHE + "` VALUES ( null, \"" + createTableQuery + "\", " + newV + " )";
                     db.execSQL ( ez_query );
                  } else {
                     if ( Config.loggingEnabled )
                        Log.e ( TAG, "createUserTables: Error inserting table data into ez cache. " );
                  }

                  if ( saveOldData ) {
                     // copy data from temp table into new table.
                     String tempTableQuery = "SELECT * FROM TEMP_"+tableName;
                     UtilityMethods utilityMethods = new UtilityMethods ();
                     ArrayList<String> insertQueryList = new ArrayList <String> (  );
                     Cursor tempDataCursor = db.rawQuery ( tempTableQuery , null );

                     if ( tempDataCursor.moveToFirst () ) {
                        do {

                           String cols = "";
                           String vals = "";

                           for ( ColumnConfigs columnConfig : columnConfigs ) {
                              int colIndex = cursor.getColumnIndex ( columnConfig.getColumnName () );

                              cols += " "+columnConfig.getColumnName ()+" ";

                              if ( colIndex == -1 ) {
                                 vals +=
                                         utilityMethods.getSqlValFromObject (
                                                 utilityMethods.getValueFromCursor ( cursor , colIndex , columnConfig.getFieldType () )
                                         );
                              }
                              else {
                                 Config.Defaults defaults = Config.getConfig ().getDefaults ();
                                 if ( columnConfig.getFieldType () == String.class ) {
                                    vals += defaults.getDefaultValText ();
                                 }
                                 else if ( columnConfig.getFieldType () == Integer.class
                                         || columnConfig.getFieldType () == Integer.TYPE
                                         || columnConfig.getFieldType () == Short.class
                                         || columnConfig.getFieldType () == Short.TYPE ) {
                                    vals += String.valueOf ( defaults.getDefaultValInt () );
                                 }
                                 else if ( columnConfig.getFieldType () == Float.class
                                         || columnConfig.getFieldType () == Float.TYPE
                                         || columnConfig.getFieldType () == Double.class
                                         || columnConfig.getFieldType () == Double.TYPE
                                         || columnConfig.getFieldType () == Long.class
                                         || columnConfig.getFieldType () == Long.TYPE ) {
                                    vals += String.valueOf ( defaults.getDefaultValReal () );
                                 }
                              }

                              if ( columnConfigs.indexOf ( columnConfig ) < columnConfigs.size () - 1 ) {
                                 cols += ",";
                                 vals += ",";
                              }
                           }

                           insertQueryList.add ( String.format ( EZ_SQL_INSERT , tableName , cols, vals ) );
                        } while ( tempDataCursor.moveToNext () );
                     }

                     // execute insert list
                     for ( String insertQuery : insertQueryList ) {
                        db.execSQL ( insertQuery );
                        SQLiteStatement statement = db.compileStatement ( "SELECT CHANGES()" );
                        long changes = 0;
                        try {
                           changes = statement.simpleQueryForLong ();
                        } finally {
                           statement.close ();
                        }
                        if ( changes == 0 && Config.loggingEnabled )
                           Log.e ( TAG, "onUpgrade: Insert query faild. q : "+insertQuery  );
                     }

                  }
               }
            } while ( cursor.moveToNext () );
         }
      }

      // clear out old versions.
      clearOldEzTableVersions ( db );
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helper methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Load the latest version from the database.
    *
    * @param db
    *         the sqlite database.
    *
    * @return the latest version.
    */
   private int loadLastVersionFromDb ( SQLiteDatabase db ) {
      String query = "SELECT * FROM " + EZ_TABLE_CACHE + " ORDER BY " + EZ_COL_VERSION + " DESC";
      Cursor cursor = db.rawQuery ( query, null );
      if ( cursor.getCount () <= 0 ) {
         if ( Config.loggingEnabled )
            Log.e ( TAG, "loadLastVersionFromDb: Cursor didn't have any data." );
         return 0;
      }
      cursor.moveToFirst ();
      return cursor.getInt ( cursor.getColumnIndex ( EZ_COL_VERSION ) );
   }

   /**
    * Create user tables.
    *
    * @param db
    *         the database.
    */
   private void createUserTables ( SQLiteDatabase db ) {
      // 1. create the user table in database.
      for ( ModelConfig model : Config.getConfig ().getModelConfigs () ) {

         // get the table name.
         String tableName = model.getTableName ();

         // get the column names.
         ArrayList < ColumnConfigs > columnConfigs = model.getColConfigs ();
         Collections.reverse ( columnConfigs );

         // initialize query.
         String query = "CREATE TABLE `" + tableName + "` " +
                 "(";

         for ( ColumnConfigs configs : columnConfigs ) {
            // set the column name and type.
            query += " " + configs.getColumnName () + " " + getSQLTypeForClass ( configs.getFieldType () );

            // set the constraints.
            query += " " + configs.getConstraints ();

            // TODO: 10/24/2017 Use the hash map constraint system

            // HashMap < String, Object > cmap = configs.getConstraints ();

            // set the NOT NULL constraint.
//            if ( cmap.containsKey ( StaticHelper.CONSTRAINT_NOT_NULL ) )
//               query += " "+StaticHelper.SQLITE_CONSTRAINT_NOT_NULL;
//
//            // set the UQ constraint.
//            if ( cmap.con )

            if ( ( columnConfigs.indexOf ( configs ) < ( columnConfigs.size () - 1 ) ) )
               query += " , ";

         }
         // set the primary key.
         if ( model.getPrimaryKey () != null ) {
            query += " , "
                    + String.format ( StaticHelper.SQLITE_CONSTRAINT_PK, model.getPrimaryKey ().getColumnName () );
         }
         if ( model.getForeignKey () != null ) {
            String fkValStr = model.getForeignKey ().getFkValue ();
            String fkVal[] = fkValStr.split ( "\\." );
            String fkTable = fkVal[ 0 ];
            String fkCol = fkVal[ 1 ];

            query += String.format ( StaticHelper.SQLITE_CONSTRAINT_FK
                    , model.getForeignKey ().getColumnName (), fkTable, fkCol );
         }

         query += ")";

         if ( Config.loggingEnabled )
            Log.d ( TAG, "createUserTables: Query for table '" + model.getTableName () + "' is : " + query );

         // create table and add to model.
         db.execSQL ( query );

         if ( checkTable ( db, model.getTableName () ) ) {
            String ez_query = "insert into `" + EZ_TABLE_CACHE + "` VALUES ( null, \"" + query + "\", " + db.getVersion () + " )";
            db.execSQL ( ez_query );
         } else {
            if ( Config.loggingEnabled )
               Log.e ( TAG, "createUserTables: Error inserting table data into ez cache. " );
         }

      }

      // 2. create the table record in ez tables.

      // 3. clean out old ez_table versions.

   }

   /**
    * Get the SQL data type for the given class.
    *
    * @param type
    *         the class.
    *
    * @return the sql type
    */
   private String getSQLTypeForClass ( Class < ? > type ) {

      // if field is integer.
      if ( type.equals ( Integer.class ) || type.equals ( Integer.TYPE ) )
         return StaticHelper.SQLITE_TYPE_INTEGER;

         // if field is byte.
      else if ( type.equals ( Byte.class ) || type.equals ( Byte.TYPE ) )
         return StaticHelper.SQLITE_TYPE_INTEGER;

         // if field is short.
      else if ( type.equals ( Short.class ) || type.equals ( Short.TYPE ) )
         return StaticHelper.SQLITE_TYPE_INTEGER;

         // if field is floating point
      else if ( type.equals ( Float.class ) || type.equals ( Float.TYPE ) )
         return StaticHelper.SQLITE_TYPE_REAL;

         // if field is long.
      else if ( type.equals ( Long.class ) || type.equals ( Long.TYPE ) )
         return StaticHelper.SQLITE_TYPE_NUMERIC;

         // if field is type double.
      else if ( type.equals ( Double.class ) || type.equals ( Double.TYPE ) )
         return StaticHelper.SQLITE_TYPE_NUMERIC;

         // if field is type number.
      else if ( type.equals ( Number.class ) )
         return StaticHelper.SQLITE_TYPE_NUMERIC;

         // if field is type string.
      else if ( type.equals ( String.class ) )
         return StaticHelper.SQLITE_TYPE_TEXT;

         // if type is char sequence.
      else if ( type.equals ( CharSequence.class ) )
         return StaticHelper.SQLITE_TYPE_TEXT;


      return "";
   }

   /**
    * Initialize the ez_table table.
    *
    * @param db
    *         the sqlite database.
    */
   private void initConfigTable ( SQLiteDatabase db ) {
      // table does not exists in local db.
      if ( !checkTable ( db, EZ_TABLE_CACHE ) ) {
         if ( Config.loggingEnabled )
            Log.e ( TAG, "checkTablesTable: " + String.format ( EZ_DEBUG_TABLE_CREATE_START, EZ_TABLE_CACHE ) );

         // create ez cache table.
         db.execSQL ( "CREATE TABLE `" + EZ_TABLE_CACHE + "` (" +
                 "`" + EZ_COL_ID + "` INTEGER NOT NULL," +
                 "`" + EZ_COL_SQL + "` TEXT NOT NULL," +
                 "`" + EZ_COL_VERSION + "` INT," +
                 "PRIMARY KEY(`" + EZ_COL_ID + "`)" +
                 ")" );

         // if tables table created successfully.
         if ( checkTable ( db, EZ_TABLE_CACHE ) ) {
            if ( Config.loggingEnabled )
               Log.d ( TAG, "initTablesTable: " + String.format ( EZ_DEBUG_TABLE_CREATE_SUCCESS, EZ_TABLE_CACHE ) );
         }
         // if tables table could not be created successfully.
         else {
            if ( Config.loggingEnabled )
               Log.e ( TAG, "initTablesTable: " + String.format ( EZ_ERROR_TABLE_CREATE_FAIL, EZ_TABLE_CACHE ) );
         }
      }

      // clear old version
      clearOldEzTableVersions ( db );
   }

   /**
    * Remove old version's tables from cache.
    *
    * @param db
    *         the sqlite db.
    */
   private void clearOldEzTableVersions ( SQLiteDatabase db ) {
      // clear out dated data.
      ArrayList < EzTables > tables = new ArrayList < EzTables > ();
      Cursor tablesCursor = db.rawQuery ( " select * from " + EZ_TABLE_CACHE, null );

      int dbVersionMax = 1;
      int totalVersions = 1;

      if ( tablesCursor.moveToFirst () ) {
         do {
            EzTables table = new EzTables ();

            // the id of the table.
            table.setId ( tablesCursor.getInt ( 0 ) );

            // the name of the table.
            table.setName ( tablesCursor.getString ( 1 ) );

            // the version of the table.
            int version = tablesCursor.getInt ( 2 );
            table.setVersion ( version );

            if ( version > dbVersionMax ) {
               dbVersionMax = version;
               totalVersions++;
            }
            // add to tables list.
            tables.add ( table );

         } while ( tablesCursor.moveToNext () );
      }

      // get the max cache size.
      int maxCacheAllowed = Config.getConfig ().getVersionCache ();

      // clear needed.
      if ( maxCacheAllowed > 1 && totalVersions > maxCacheAllowed ) {
         ArrayList < Integer > toKeep = new ArrayList < Integer > ();
         int delFrom = dbVersionMax - maxCacheAllowed;

         for ( int i = dbVersionMax ; i >= 1 ; i-- ) {
            if ( i > delFrom )
               toKeep.add ( i );
         }

         for ( EzTables table : tables ) {
            // remove old versions.
            if ( !toKeep.contains ( table.getVersion () ) ) {
               db.execSQL ( "DELETE FROM " + EZ_TABLE_CACHE + " WHERE " + EZ_COL_ID + " = " + table.getId () );
            }
         }
      }
   }

   /**
    * Check if a table exists in database.
    *
    * @param db
    *         the db.
    * @param tableName
    *         the table name to look for.
    *
    * @return true if table exists.
    */
   private boolean checkTable ( SQLiteDatabase db, String tableName ) {
      Cursor cursor = db.rawQuery ( "SELECT name FROM sqlite_master WHERE type = ? AND name = ?",
              new String[]{ "table" , tableName } );
      return ( cursor != null && ( cursor.getCount () == 1 ) );
   }

}
