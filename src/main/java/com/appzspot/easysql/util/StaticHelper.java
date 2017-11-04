package com.appzspot.easysql.util;

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

public class StaticHelper {

   ///////////////////////////////////////////////////////////////////////////
   // Constraints.
   ///////////////////////////////////////////////////////////////////////////

   public static final String CONSTRAINT_PRIMARY_KEY = "pk";
   public static final String CONSTRAINT_UNIQUE = "uq";
   public static final String CONSTRAINT_CHECK = "chq";
   public static final String CONSTRAINT_FOREIGN_KEY = "fk";
   public static final String CONSTRAINT_ON_DELETE = "ondel";
   public static final String CONSTRAINT_DEFAULT = "default";
   public static final String CONSTRAINT_NOT_NULL = "notnull";


   ///////////////////////////////////////////////////////////////////////////
   // Meta data fields.
   ///////////////////////////////////////////////////////////////////////////

   public static final String META_DB_NAME = "ezsql_db_name";
   public static final String META_DB_VERSION = "ezsql_db_version";
   public static final String META_MODEL_PKG = "ezsql_model";
   public static final String META_MAX_VERSION_CACHE = "ezsql_max_version_cache";

   public static final String META_DEFAULT_TEXT = "ez_default_text";
   public static final String META_DEFAULT_INT = "ez_default_int";
   public static final String META_DEFAULT_REAL = "ez_default_real";

   ///////////////////////////////////////////////////////////////////////////
   // SQL data types.
   ///////////////////////////////////////////////////////////////////////////

   public static final String SQLITE_TYPE_INTEGER = "INTEGER";
   public static final String SQLITE_TYPE_TEXT = "TEXT";
   public static final String SQLITE_TYPE_BLOB = "BLOB";
   public static final String SQLITE_TYPE_REAL = "REAL";
   public static final String SQLITE_TYPE_NUMERIC = "NUMERIC";

   ///////////////////////////////////////////////////////////////////////////
   // SQLite constraints values.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * PK constraint.
    * arg 1 : column name.
    */
   public static final String SQLITE_CONSTRAINT_PK = "PRIMARY KEY (`%1$s`)";
   /**
    * UQ constraint.
    */
   public static final String SQLITE_CONSTRAINT_UQ = "UNIQUE";
   /**
    * CHK constraint.
    * arg 1 : condition.
    */
   public static final String SQLITE_CONSTRAINT_CHK = "CHECK (%1$s)";
   /**
    * FK constraint.
    * arg 1 : fk col.
    * arg 2 : fk table.
    * arg 3 : fk col in fk table.
    */
   public static final String SQLITE_CONSTRAINT_FK = "FOREIGN KEY ( `%1$s` ) REFERENCES `%2$s`(`%3$s`)";
   /**
    * ONDEL constraint.
    * arg 1 : on delete action.
    */
   public static final String SQLITE_CONSTRAINT_ONDEL = "ON DELETE %1$s";
   /**
    * DEFAULT constraint.
    * arg 1 : default value.
    */
   public static final String SQLITE_CONSTRAINT_DEFAULT = "DEFAULT %1$s";
   /**
    * Not null constraint.
    */
   public static final String SQLITE_CONSTRAINT_NOT_NULL = "NOT NULL";

}
