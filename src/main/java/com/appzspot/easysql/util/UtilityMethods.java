package com.appzspot.easysql.util;

import android.database.Cursor;
import android.util.Log;

import com.appzspot.easysql.annotations.EzFk;
import com.appzspot.easysql.annotations.EzTable;
import com.appzspot.easysql.config.ColumnConfigs;
import com.appzspot.easysql.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Muhammad Faisal Nadeem on 10/29/2017.
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

public class UtilityMethods {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "UtilityMethods";

   private static final String SQL_ERROR_COL_VAL_LENGTH_DIFFER = "Column length differs from values length. [ COL_LENGTH : %1$s , VAL_LENGTH : %2$s ]";
   private static final String SQL_ERROR_COL_VAL_LENGTH_ZERO = "Column length is zero.";

   ///////////////////////////////////////////////////////////////////////////
   // Methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Get sql value from object.
    *
    * @param obj
    *         the object to fetch the sql value for.
    *
    * @return string sql value.
    */
   public String getSqlValFromObject ( Object obj ) {

      if ( obj instanceof Integer )
         return ( ( Integer ) obj ) + "";
      else if ( obj instanceof Float )
         return ( ( Float ) obj ) + "";
      else if ( obj instanceof Double )
         return ( ( Double ) obj ) + "";
      else if ( obj instanceof Long )
         return ( ( Long ) obj ) + "";
      else if ( obj instanceof Short )
         return ( ( Short ) obj ) + "";
      else if ( obj instanceof String )
         return "\"" + ( ( String ) obj ) + "\"";

      return "";
   }

   /**
    * Get an array list of cursor for the model instance.
    *
    * @param cursor
    *         the cursor.
    * @param model
    *         the model class.
    *
    * @return a list of model class instances from cursor.
    *
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws InvocationTargetException
    */
   public ArrayList getListFromCursor ( Cursor cursor, Class < ? > model )
           throws
           IllegalAccessException,
           InstantiationException,
           InvocationTargetException {
      ArrayList < Object > result = new ArrayList < Object > ();
      ArrayList < ColumnConfigs > colConfigs = Config.getConfig ().getModelConfigForClass ( model ).getColConfigs ();
      if ( cursor.moveToFirst () ) {
         do {
            Object modelInstance = model.newInstance ();
            Method[] modelMethods = model.getMethods ();

            // parse each column in cursor.
            for ( int i = 0 ; i < cursor.getColumnCount () ; i++ ) {
               ColumnConfigs columnConfig = colConfigs.get ( i );
               Object value = getValueFromCursor ( cursor, cursor.getColumnIndex ( columnConfig.getColumnName () ), columnConfig.getFieldType () );

               String fieldName = columnConfig.getFieldName ();
               String setterMethod = "set" + fieldName.substring ( 0, 1 ).toUpperCase () + fieldName.substring ( 1 );
               boolean setterFound = false;

               for ( Method modelMethod : modelMethods ) {
                  if ( modelMethod.getName ().equals ( setterMethod ) ) {
                     setterFound = true;
                     // call the setter method.
                     modelMethod.invoke ( modelInstance, value );
                  }
               }
               if ( !setterFound && Config.loggingEnabled )
                  Log.e ( TAG, "getListFromCursor: Error invoking setter method for field : " + fieldName );
            }
            // add the model instance to list.
            result.add ( modelInstance );
         } while ( cursor.moveToNext () );
      }

      return result;
   }

   /**
    * Get the object value for cursor for a given index.
    *
    * @param cursor
    *         the cursor.
    * @param indx
    *         the index for the value.
    * @param type
    *         the type of value to look for.
    *
    * @return the object value.
    */
   public Object getValueFromCursor ( Cursor cursor, int indx, Class < ? > type ) {
      // if cursor is of type integer.
      if ( type.equals ( Integer.class ) || type.equals ( Integer.TYPE ) )
         return cursor.getInt ( indx );
         // if cursor is of type float.
      else if ( type.equals ( Float.class ) || type.equals ( Float.TYPE ) )
         return cursor.getFloat ( indx );
         // if cursor is of type double.
      else if ( type.equals ( Double.class ) || type.equals ( Double.TYPE ) )
         return cursor.getDouble ( indx );
         // if cursor is of type long.
      else if ( type.equals ( Long.class ) || type.equals ( Long.TYPE ) )
         return cursor.getLong ( indx );
         // if cursor is of type short.
      else if ( type.equals ( Short.class ) || type.equals ( Short.TYPE ) )
         return cursor.getShort ( indx );
         // if cursor is of type string.
      else if ( type.equals ( String.class ) )
         return cursor.getString ( indx );
      else if ( type.isAnnotationPresent ( EzFk.class ) ) {

      }

      return null;
   }

}
