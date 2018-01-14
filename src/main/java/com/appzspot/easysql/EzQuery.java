package com.appzspot.easysql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.appzspot.easysql.annotations.EzTable;
import com.appzspot.easysql.config.ColumnConfigs;
import com.appzspot.easysql.config.Config;
import com.appzspot.easysql.config.ModelConfig;
import com.appzspot.easysql.ezdb.EzDbHelper;
import com.appzspot.easysql.util.UtilityMethods;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Muhammad Faisal Nadeem on 10/24/2017.
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

public class EzQuery {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "EzQuery";

   private static EzQuery instance;
   private EzDbHelper dbHelper;
   private Context mContext;
   private Class modelPointer;
   private String query;
   private boolean transactionSafe = true;

   private static final String SQL_SELECT = " SELECT %1$s FROM %2$s ";
   private static final String SQL_INSERT = " INSERT INTO `%1$s` %2$s VALUES %3$s";
   private static final String SQL_REMOVE = " DELETE FROM %1$s";
   private static final String SQL_UPDATE = " UPDATE %1$s SET %2$s";

   private static final String SQL_TRANSACTION_START = "BEGIN TRANSACTION";
   private static final String SQL_TRANSACTION_COMMIT = "COMMIT";
   private static final String SQL_TRANSACTION_ROLLBACK = "ROLLBACK";

   private static final String ERROR_EZ_TABLE_ANNOTATION_NOT_PRESENT = " The class : '%1$s' does not contains ez model annotation. ";

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * private constructor.
    */
   private EzQuery ( Context context ) {
      this.mContext = context;
      if ( dbHelper == null ) {
         Config configs = Config.getConfig ();
         dbHelper = new EzDbHelper ( this.mContext, configs.getDbName (), null, configs.getDbVersion () );
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Singleton instance getter method.
    *
    * @return instance.
    */
   public static EzQuery getQuery ( Context context ) {
      if ( instance == null )
         instance = new EzQuery ( context );
      return instance;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Select query.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Select statement starter method.
    *
    * @param ezClass
    *         the ez class.
    * @param cols
    *         the selection columns.
    *
    * @return EzQuery instance.
    *
    * @throws Exception
    */
   public EzQuery find ( Class ezClass, String... cols )
           throws Exception {
      query = "";
      if ( ezClass.isAnnotationPresent ( EzTable.class ) ) {
         modelPointer = ezClass;
         ModelConfig config = Config.getConfig ().getModelConfigForClass ( ezClass );

         // selection.
         String selection = "";
         if ( cols.length > 0 ) {
            selection += " ( ";
            ArrayList < String > colList = ( ArrayList < String > ) Arrays.asList ( cols );
            for ( String col : colList ) {
               selection += " col ";

               if ( colList.indexOf ( col ) < colList.size () ) {
                  selection += " , ";
               }
            }
            selection += " ) ";
         } else
            selection = " * ";

         // init select query.
         query += String.format ( SQL_SELECT, selection, config.getTableName () );
         return this;
      } else {
         throw new Exception ( String.format ( ERROR_EZ_TABLE_ANNOTATION_NOT_PRESENT, ezClass.getName () ) );
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Insert query.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Insert into database.
    *
    * @param ezClass
    *         the ez model.
    * @param colValMap
    *         the col value hash map.
    *
    * @return instance.
    */
   public EzQuery insert ( Class ezClass, HashMap colValMap )
           throws Exception {
      query += "";
      if ( ezClass.isAnnotationPresent ( EzTable.class ) ) {
         // get the table name.
         EzTable annotation = ( EzTable ) ezClass.getAnnotation ( EzTable.class );
         String tableName = annotation.value ();

         // get col value projection.
         String[] cols = new String[ colValMap.size () ];
         Object[] vals = new Object[ colValMap.size () ];
         Iterator iterator = colValMap.entrySet ().iterator ();
         int i = 0;

         while ( iterator.hasNext () ) {
            Map.Entry < String, Object > mapEntry = ( Map.Entry ) iterator.next ();
            cols[ i ] = mapEntry.getKey ();
            vals[ i ] = mapEntry.getValue ();
            i++;
         }

         // initialize selections.
         String selection = "(";
         for ( int j = 0 ; j < cols.length ; j++ ) {
            selection += " " + cols[ j ] + " ";
            if ( j < cols.length - 1 ) {
               selection += ",";
            }
         }
         selection += ")";

         // initialize values.
         String values = "(";
         for ( int k = 0 ; k < vals.length ; k++ ) {
            values += " " + new UtilityMethods ().getSqlValFromObject ( vals[ k ] ) + " ";
            if ( k < vals.length - 1 ) {
               values += ",";
            }
         }
         values += ")";

         query = String.format ( SQL_INSERT, tableName, selection, values );
         return this;
      } else {
         throw new Exception ( String.format ( ERROR_EZ_TABLE_ANNOTATION_NOT_PRESENT, ezClass.getName () ) );
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Remove query.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Delete a record from table.
    *
    * @param ezClass
    *         the table class.
    *
    * @return instance.
    *
    * @throws Exception
    */
   public EzQuery remove ( Class ezClass )
           throws Exception {
      query = "";
      if ( ezClass.isAnnotationPresent ( EzTable.class ) ) {
         modelPointer = ezClass;
         EzTable tableAnn = ( EzTable ) ezClass.getAnnotation ( EzTable.class );
         query += String.format ( SQL_REMOVE, tableAnn.value () );
         return this;
      } else
         throw new Exception ( String.format ( ERROR_EZ_TABLE_ANNOTATION_NOT_PRESENT, ezClass.getName () ) );
   }

   ///////////////////////////////////////////////////////////////////////////
   // Update query.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Update query.
    *
    * @param ezClass
    *         the model class.
    * @param updateVals
    *
    * @return
    *
    * @throws Exception
    */
   public EzQuery update ( Class ezClass, HashMap < String, Object > updateVals )
           throws Exception {
      query = "";
      if ( ezClass.isAnnotationPresent ( EzTable.class ) ) {
         modelPointer = ezClass;
         EzTable tableAnn = ( EzTable ) ezClass.getAnnotation ( EzTable.class );

         // values string.
         String colVals = "";
         Iterator it = updateVals.entrySet ().iterator ();
         while ( it.hasNext () ) {
            Map.Entry entry = ( Map.Entry ) it.next ();
            colVals += entry.getKey () + " = " + entry.getValue ();
            it.remove ();
            if ( updateVals.size () > 1 )
               colVals += " , ";
         }
         query += String.format ( SQL_UPDATE, tableAnn.value (), colVals );
      } else {
         throw new Exception ( String.format ( ERROR_EZ_TABLE_ANNOTATION_NOT_PRESENT, ezClass.getName () ) );
      }

      return this;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Query executors.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Execute the query.
    *
    * @param <RType>
    *         the return type generic value.
    *
    * @return the query result ( ArrayList<ModelObject> for select, long number of rows effected by
    * the query ).
    *
    * @throws IllegalAccessException+
    * @throws InvocationTargetException
    * @throws InstantiationException
    */
   public < RType > RType go ()
           throws IllegalAccessException, InvocationTargetException, InstantiationException {

      SQLiteDatabase db = dbHelper.getWritableDatabase ();

      // if the sql is select statement.
      if ( query.trim ().startsWith ( "SELECT" ) ) {
         Cursor cursor = db.rawQuery ( query, null );
         if ( Config.loggingEnabled )
            Log.d ( TAG, "go: executed query : " + query + " , cursor size : " + cursor.getCount () );
         return ( RType ) new UtilityMethods ().getListFromCursor ( cursor, modelPointer );
      }
      // if the sql is insert statement.
      else if ( query.trim ().startsWith ( "INSERT" ) ) {
         try {
            if ( transactionSafe )
               db.execSQL ( SQL_TRANSACTION_START );
            if ( Config.loggingEnabled )
               Log.d ( TAG, "go: executing query : " + query );
            db.execSQL ( query );
            if ( transactionSafe )
               db.execSQL ( SQL_TRANSACTION_COMMIT );
         } catch ( android.database.sqlite.SQLiteConstraintException ex ) {
            if ( Config.loggingEnabled ) {
               StringWriter stringWriter = new StringWriter ();
               PrintWriter printWriter = new PrintWriter ( stringWriter );
               ex.printStackTrace ( printWriter );
               Log.e ( TAG, "go: " + stringWriter );
            }
         } catch ( android.database.SQLException ex ) {
            if ( Config.loggingEnabled ) {
               StringWriter stringWriter = new StringWriter ();
               PrintWriter printWriter = new PrintWriter ( stringWriter );
               ex.printStackTrace ( printWriter );
               Log.e ( TAG, "go: " + stringWriter );
            }
         }
         return ( RType ) Long.valueOf ( checkChanges ( db ) );
      }
      // if the sql is update statement.
      else if ( query.trim ().startsWith ( "UPDATE" ) ) {
         if ( transactionSafe )
            db.execSQL ( SQL_TRANSACTION_START );
         db.execSQL ( query );
         if ( transactionSafe )
            db.execSQL ( SQL_TRANSACTION_COMMIT );
         if ( Config.loggingEnabled )
            Log.d ( TAG, "go: executing query : " + query );
         return ( RType ) Long.valueOf ( checkChanges ( db ) );
      }
      // if the sql is delete statement.
      else if ( query.trim ().startsWith ( "DELETE" ) ) {
         if ( transactionSafe )
            db.execSQL ( SQL_TRANSACTION_START );
         db.execSQL ( query );
         if ( transactionSafe )
            db.execSQL ( SQL_TRANSACTION_COMMIT );
         if ( Config.loggingEnabled )
            Log.d ( TAG, "go: executing query : " + query );
         return ( RType ) Long.valueOf ( checkChanges ( db ) );
      }
      return null;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Single object operations.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Save object to the database.
    *
    * @param objects
    *         the object to save.
    *
    * @return the number of rows effected.
    */
   public static long save ( Object... objects ) {
      long rows = 0;
      for ( Object object : objects ) {
         // get model config for object.
         ModelConfig modelConfig = Config.getConfig ().getModelConfigForClass ( object.getClass () );
         HashMap < String, Object > mapVals = new HashMap <> ();
         for ( ColumnConfigs columnConfig : modelConfig.getColConfigs () ) {
            Object val = null;
            try {
               String getterMethod = "get" + columnConfig.getFieldName ().substring ( 0, 1 ).toUpperCase ()
                       + columnConfig.getFieldName ().substring ( 1 );
               val = object.getClass ().getMethod ( getterMethod ).invoke ( object );
            } catch ( Exception ex ) {
               StringWriter stringWriter = new StringWriter ();
               PrintWriter printWriter = new PrintWriter ( stringWriter );
               ex.printStackTrace ( printWriter );

               if ( Config.loggingEnabled )
                  Log.e ( TAG, "save: Exception : " + stringWriter );
            }
            String col = columnConfig.getColumnName ();
            if ( val != null ) {
               mapVals.put ( col, val );
            } else {
               if ( Config.loggingEnabled )
                  Log.e ( TAG, "save: value was null. table : " + modelConfig.getTableName ()
                          + " , Col " + columnConfig.getColumnName ()
                  );
            }
         }

         try {
            long rs = EzQuery.getQuery ( Config.getConfig ().getContext () )
                    .insert ( object.getClass (), mapVals )
                    .go ();

            if ( rs <= 0 && Config.loggingEnabled ) {
               Log.e ( TAG, "save: insert operation for table '" + modelConfig.getTableName () + "' failed. rows effected : " + rs );
            }
            rows += rs;

         } catch ( Exception e ) {
            StringWriter stackTraceWriter = new StringWriter ();
            PrintWriter stackTracePrintWriter = new PrintWriter ( stackTraceWriter );
            e.printStackTrace ( stackTracePrintWriter );
            if ( Config.loggingEnabled )
               Log.d ( TAG, "Exception ( " + e.getMessage () + " ) :- \n" + stackTraceWriter );
         }
      }
      return rows;
   }

   /**
    * Remove the given objects from db.
    *
    * @param objects
    *         the objects to remove from database.
    *
    * @return the number of rows effected.
    */
   public static long delete ( Object... objects ) {
      int rows = 0;

      for ( Object object : objects ) {
         ModelConfig modelConfig = Config.getConfig ().getModelConfigForClass ( object.getClass () );

         String pkCol = modelConfig.getPrimaryKey ().getColumnName ();
         Object val = null;
         String getterMethod = "get" + pkCol.substring ( 0, 1 ).toUpperCase () + pkCol.substring ( 1 );

         try {
            val = object.getClass ().getMethod ( getterMethod ).invoke ( object );
         } catch ( Exception e ) {
            StringWriter stackTraceWriter = new StringWriter ();
            PrintWriter stackTracePrintWriter = new PrintWriter ( stackTraceWriter );
            e.printStackTrace ( stackTracePrintWriter );

            if ( Config.loggingEnabled )
               Log.d ( TAG, "Exception ( " + e.getMessage () + " ) :- \n" + stackTraceWriter );
         }
         try {
            String valStr = "";
            if ( val.getClass () == Integer.class || val.getClass () == Integer.TYPE
                    || val.getClass () == Short.class || val.getClass () == Short.TYPE ) {
               valStr = String.valueOf ( ( int ) val );
            }
            else if ( val.getClass () == Float.class || val.getClass () == Float.TYPE
                    || val.getClass () == Double.class || val.getClass () == Double.TYPE
                    || val.getClass () == Long.class || val.getClass () == Long.TYPE ) {
               valStr = String.valueOf ( (long) val );
            }
            else {
               valStr = "\""+String.valueOf ( val )+"\"";
            }

            long rowsDeleted = EzQuery.getQuery ( Config.getConfig ().getContext () )
                    .remove ( object.getClass () )
                    .colEq ( pkCol, valStr )
                    .go ();

            if ( rowsDeleted <= 0 && Config.loggingEnabled )
               Log.e ( TAG, "delete: error deleting rows for table : "+modelConfig.getTableName () );

            rows += rowsDeleted;

         } catch ( Exception e ) {
            StringWriter stackTraceWriter = new StringWriter ();
            PrintWriter stackTracePrintWriter = new PrintWriter ( stackTraceWriter );
            e.printStackTrace ( stackTracePrintWriter );

            if ( Config.loggingEnabled )
               Log.d ( TAG, "Exception ( " + e.getMessage () + " ) :- \n" + stackTraceWriter );
         }
      }
      return rows;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Raw sql.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Execute raw sql.
    *
    * @param sql
    *         the sql query string.
    * @param args
    *         optional arguments.
    *
    * @return cursor.
    */
   public Cursor rawSql ( String sql, String... args ) {
      return dbHelper.getWritableDatabase ().rawQuery ( sql, ( args != null ) ? args : null );
   }


   /**
    * Get the effected rows for sqlite db.
    *
    * @param db
    *         the database.
    *
    * @return number of rows effected.
    */
   private long checkChanges ( SQLiteDatabase db ) {
      SQLiteStatement statement = db.compileStatement ( "SELECT CHANGES()" );
      long changes = 0;
      try {
         changes = statement.simpleQueryForLong ();
      } finally {
         statement.close ();
         return changes;
      }
   }


   ///////////////////////////////////////////////////////////////////////////
   // Where clause helpers.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Column equal value comparison method.
    *
    * @param colName
    *         the column name.
    * @param value
    *         the column value.
    *
    * @return instance.
    */
   public EzQuery colEq ( String colName, String value ) {
      if ( !query.contains ( "WHERE" ) ) {
         query += " WHERE ";
      }
      query += colName + " = " + value;
      return this;
   }

   /**
    * Column greater value comparison method.
    *
    * @param colName
    *         the column name.
    * @param value
    *         the column value.
    *
    * @return instance.
    */
   public EzQuery colGt ( String colName, String value ) {
      if ( !query.contains ( "WHERE" ) ) {
         query += " WHERE ";
      }
      query += colName + " > " + value;
      return this;
   }

   /**
    * Column smaller value comparison method.
    *
    * @param colName
    *         the column name.
    * @param value
    *         the column value.
    *
    * @return instance.
    */
   public EzQuery colSm ( String colName, String value ) {
      if ( !query.contains ( "WHERE" ) ) {
         query += " WHERE ";
      }
      query += colName + " < " + value;
      return this;
   }

   /**
    * Column greater equals value comparison method.
    *
    * @param colName
    *         the column name.
    * @param value
    *         the column value.
    *
    * @return instance.
    */
   public EzQuery colGtEq ( String colName, String value ) {
      if ( !query.contains ( "WHERE" ) ) {
         query += " WHERE ";
      }
      query += colName + " >= " + value;
      return this;
   }

   /**
    * Column smaller equal value comparison method.
    *
    * @param colName
    *         the column name.
    * @param value
    *         the column value.
    *
    * @return instance.
    */
   public EzQuery colSmEq ( String colName, String value ) {
      if ( !query.contains ( "WHERE" ) ) {
         query += " WHERE ";
      }
      query += colName + " <= " + value;
      return this;
   }

   /**
    * Using the and logical operator.
    *
    * @return this.
    */
   public EzQuery and () {
      query += " AND ";
      return this;
   }

   /**
    * Using the or logical operator.
    *
    * @return this.
    */
   public EzQuery or () {
      query += " OR ";
      return this;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helper methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Set the query to be transaction safe.
    *
    * @param transactionSafe
    *         boolean value.
    *
    * @return instance of EzQuery.
    */
   public EzQuery setTransactionSafe ( boolean transactionSafe ) {
      this.transactionSafe = transactionSafe;
      return this;
   }


}
