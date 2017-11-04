package com.appzspot.easysql.config;

import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Muhammad Faisal Nadeem on 10/23/2017.
 */
public class ColumnConfigs {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private String columnName;
   //  ( key: constraint name , value : constraint value )
   //private HashMap<String, Object> constraints;
   private String constraints;
   private String fkValue;
   private Class < ? > fieldType;
   private String fieldName;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   public ColumnConfigs () {}

   public ColumnConfigs ( String columnName, String constraints, @Nullable String fkValue,
                          Class < ? > fieldType, String fieldName ) {
      this.columnName = columnName;
      this.constraints = constraints;
      this.fieldType = fieldType;
      if ( fkValue != null )
         this.fkValue = fkValue;
      this.fieldName = fieldName;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getter and Setter methods.
   ///////////////////////////////////////////////////////////////////////////

   public String getFieldName () {
      return fieldName;
   }

   public void setFieldName ( String fieldName ) {
      this.fieldName = fieldName;
   }

   public String getFkValue () {
      return fkValue;
   }

   public void setFkValue ( String fkValue ) {
      this.fkValue = fkValue;
   }

   public String getColumnName () {
      return columnName;
   }

   public void setColumnName ( String columnName ) {
      this.columnName = columnName;
   }

   public String getConstraints () {
      return constraints;
   }

   public void setConstraints ( String constraints ) {
      this.constraints = constraints;
   }

   public Class < ? > getFieldType () {
      return fieldType;
   }

   public void setFieldType ( Class < ? > fieldType ) {
      this.fieldType = fieldType;
   }

}
