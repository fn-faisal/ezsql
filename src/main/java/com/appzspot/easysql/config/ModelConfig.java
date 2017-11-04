package com.appzspot.easysql.config;

import android.content.Context;
import android.util.Log;

import com.appzspot.easysql.ClassAccessor;
import com.appzspot.easysql.annotations.EzColumn;
import com.appzspot.easysql.annotations.EzConstraints;
import com.appzspot.easysql.annotations.EzFk;
import com.appzspot.easysql.annotations.EzPk;
import com.appzspot.easysql.annotations.EzTable;
import com.appzspot.easysql.annotations.OnDeleteAction;
import com.appzspot.easysql.util.StaticHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class ModelConfig {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "ModelConfig";

   private ArrayList < ColumnConfigs > colConfigs;
   private String className;
   private String tableName;

   private ColumnConfigs primaryKey;
   private ColumnConfigs foreignKey;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   public ModelConfig () {
   }

   public ModelConfig ( String className, String tableName
           , ArrayList < ColumnConfigs > colConfigs ) {
      this.className = className;
      this.tableName = tableName;
      this.colConfigs = colConfigs;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getters and Setters.
   ///////////////////////////////////////////////////////////////////////////

   public ColumnConfigs getPrimaryKey () {
      return primaryKey;
   }

   public void setPrimaryKey ( ColumnConfigs primaryKey ) {
      this.primaryKey = primaryKey;
   }

   public ColumnConfigs getForeignKey () {
      return foreignKey;
   }

   public void setForeignKey ( ColumnConfigs foreignKey ) {
      this.foreignKey = foreignKey;
   }

   public ArrayList < ColumnConfigs > getColConfigs () {
      return colConfigs;
   }

   public void setColConfigs ( ArrayList < ColumnConfigs > colConfigs ) {
      this.colConfigs = colConfigs;
   }

   public String getClassName () {
      return className;
   }

   public void setClassName ( String className ) {
      this.className = className;
   }

   public String getTableName () {
      return tableName;
   }

   public void setTableName ( String tableName ) {
      this.tableName = tableName;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Get model config list from package.
    *
    * @param pkg
    *         the package name.
    *
    * @return a list of model config.
    */
   public ArrayList < ModelConfig > getListForPkg ( Context context , String pkg ) {
      ArrayList < ModelConfig > modelConfigs = new ArrayList < ModelConfig > ();
      List < Class < ? > > classes = ClassAccessor.getEzClassesFromPackage ( context , pkg );

      for ( Class ezClass : classes ) {
         ModelConfig model = new ModelConfig ();
         EzTable tableAnnotation = ( EzTable ) ezClass.getAnnotation ( EzTable.class );
         if ( tableAnnotation != null ) {
            // set the table name.
            // if table name not given, use class name.
            if ( tableAnnotation.value ().equals ( "" ) )
               model.setTableName ( ezClass.getSimpleName () );
               // if table name given.
            else
               model.setTableName ( tableAnnotation.value () );

            // set column configs.
            model.setColConfigs ( getColumnListForClass ( ezClass ) );

            // set pk constraint.
            ColumnConfigs pk = getPrimaryKeyCol ( ezClass );

            if ( pk != null ) {
               model.setPrimaryKey ( pk );
            }

            // set fk constraint.
            ColumnConfigs fk = getForeignKeyCol ( ezClass );
            if ( fk != null ) {
               model.setForeignKey ( fk );
            }

            modelConfigs.add ( model );
         }
         if ( Config.loggingEnabled )
            Log.e ( TAG, "getListForPkg: table annotation is null for class : " + ezClass.getName () );

      }

      return modelConfigs;
   }

   private ColumnConfigs getForeignKeyCol ( Class ezClass ) {
      for ( Field field : ezClass.getDeclaredFields () ) {
         if ( field.isAnnotationPresent ( EzFk.class ) ) {
            EzFk fkAnn = field.getAnnotation ( EzFk.class );
            ColumnConfigs configs = new ColumnConfigs ();
            configs.setColumnName ( fkAnn.name () );
            configs.setFieldName ( field.getName () );
            configs.setFieldType ( field.getType () );
            configs.setFkValue ( fkAnn.references () );
            if ( field.isAnnotationPresent ( EzConstraints.class ) ) {
               EzConstraints constraints = field.getAnnotation ( EzConstraints.class );
               configs.setConstraints ( constraints.value () );
            }
            return configs;
         }
      }
      return null;
   }

   /**
    * Get the primary key column for class.
    * @param ezClass the ez class.
    * @return column configs.
    */
   private ColumnConfigs getPrimaryKeyCol ( Class ezClass ) {
      for ( Field field : ezClass.getDeclaredFields () ) {
         if ( field.isAnnotationPresent ( EzPk.class ) ) {
            EzPk pkAnn = field.getAnnotation ( EzPk.class );

            ColumnConfigs configs = new ColumnConfigs (  );
            configs.setColumnName ( pkAnn.value () );
            configs.setFieldType ( field.getType () );

            return configs;
         }
      }

      return null;
   }

   /**
    * Get a list of columns for a given class.
    *
    * @param ezClass
    *         the ez class to extract the columns from.
    *
    * @return a list of column configs.
    */
   private ArrayList < ColumnConfigs > getColumnListForClass ( Class ezClass ) {
      ArrayList < ColumnConfigs > columnConfigList = new ArrayList < ColumnConfigs > ();

      // parse fields.
      for ( Field field : ClassAccessor.getFieldsUpTo (  ezClass , null ) ) {
         // if the field is annotated.
         if ( field.isAnnotationPresent ( EzColumn.class ) ) {
            ColumnConfigs columnConfigs = new ColumnConfigs ();
            EzColumn columnAnnotation = field.getAnnotation ( EzColumn.class );
            // set the column name.
            if ( columnAnnotation.value ().equals ( "" ) )
               columnConfigs.setColumnName ( field.getName () );
            else
               columnConfigs.setColumnName ( columnAnnotation.value () );

            // set field name.
            columnConfigs.setFieldName ( field.getName () );

            // set field type.
            columnConfigs.setFieldType ( field.getType () );

            // check the constraints.
            if ( field.isAnnotationPresent ( EzConstraints.class ) ) {
//               HashMap < String, Object > constraintMap =
//                       new HashMap < String, Object > ();
               EzConstraints constraintsAnnotation = field
                       .getAnnotation ( EzConstraints.class );

               columnConfigs.setConstraints ( constraintsAnnotation.value () );

               // TODO: 10/24/2017 implement hash map constraint system.
               // add pk constraint to cmap.
//               if ( constraintsAnnotation.primaryKey () )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_PRIMARY_KEY, true );
//               else if ( field.isAnnotationPresent ( EzPk.class ) )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_PRIMARY_KEY , true );

               // add fk constraint to cmap.
//               if ( !constraintsAnnotation.foreignKey ().equals ( "" ) )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_FOREIGN_KEY, constraintsAnnotation.foreignKey () );
//               else if ( field.isAnnotationPresent ( EzFk.class ) ) {
//                  EzFk fkAnnotation = field.getAnnotation ( EzFk.class );
//                  constraintMap.put ( StaticHelper.CONSTRAINT_FOREIGN_KEY, fkAnnotation.references () );
//               }

               // add uq constraint to cmap.
//               if ( !constraintsAnnotation.unique () )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_UNIQUE, true );

               // add chk constraint to cmap.
//               if ( !constraintsAnnotation.check ().equals ( "" ) )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_CHECK, constraintsAnnotation.check () );

               // add ondel constraint to cmap.
//               if ( constraintsAnnotation.onDelete () != OnDeleteAction.NO_ACTION )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_ON_DELETE, constraintsAnnotation.onDelete () );

               // add not null constraint to cmap.
//               if ( constraintsAnnotation.notNull () )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_NOT_NULL, true );

               // add default constraint to cmap.
//               if ( !constraintsAnnotation.defaultVal ().equals ( "" ) )
//                  constraintMap.put ( StaticHelper.CONSTRAINT_DEFAULT, constraintsAnnotation.defaultVal () );

//               columnConfigs.setConstraints ( constraintMap );
            }

            columnConfigList.add ( columnConfigs );
         }
         else if ( field.isAnnotationPresent ( EzPk.class ) ) {
            ColumnConfigs columnConfigs = new ColumnConfigs ();
            EzPk pkAnnotation = field.getAnnotation ( EzPk.class );
            // set the column name.
            if ( pkAnnotation.value ().equals ( "" ) )
               columnConfigs.setColumnName ( field.getName () );
            else
               columnConfigs.setColumnName ( pkAnnotation.value () );

            // set field name.
            columnConfigs.setFieldName ( field.getName () );

            // set field type.
            columnConfigs.setFieldType ( field.getType () );

            if ( field.isAnnotationPresent ( EzConstraints.class ) ) {
               EzConstraints constraintsAnnotation = field
                       .getAnnotation ( EzConstraints.class );

               columnConfigs.setConstraints ( constraintsAnnotation.value () );
            }

            columnConfigList.add ( columnConfigs );
         }
         else if ( field.isAnnotationPresent ( EzFk.class ) ) {
            ColumnConfigs columnConfigs = new ColumnConfigs ();
            EzFk fkAnnotation = field.getAnnotation ( EzFk.class );
            // set the column name.
            if ( fkAnnotation.name ().equals ( "" ) )
               columnConfigs.setColumnName ( field.getName () );
            else
               columnConfigs.setColumnName ( fkAnnotation.name () );

            // set field name.
            columnConfigs.setFieldName ( field.getName () );

            // set fk val.
            columnConfigs.setFkValue ( fkAnnotation.references () );

            // set field type.
            columnConfigs.setFieldType ( field.getType () );

            if ( field.isAnnotationPresent ( EzConstraints.class ) ) {

               EzConstraints constraintsAnnotation = field
                       .getAnnotation ( EzConstraints.class );

               columnConfigs.setConstraints ( constraintsAnnotation.value () );
            }

            columnConfigList.add ( columnConfigs );
         }
      }
      return columnConfigList;
   }

}
