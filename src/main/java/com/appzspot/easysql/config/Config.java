package com.appzspot.easysql.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.appzspot.easysql.annotations.EzFk;
import com.appzspot.easysql.annotations.EzTable;
import com.appzspot.easysql.ezdb.EzDbHelper;
import com.appzspot.easysql.util.StaticHelper;

import java.util.ArrayList;

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

public class Config {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "Config";

   private static Config instance;

   private Context mContext;
   private ArrayList < ModelConfig > mModelConfigs;
   private String dbName;
   private int dbVersion;
   private String pkgModels;
   private int versionCache = 2;
   private EzDbHelper mEzDbHelper;
   private Defaults mDefaults;

   private boolean saveDataOnUpgradeToggle = false;

   public static boolean loggingEnabled = false;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   private Config ( Context context,
                    ArrayList < ModelConfig > modelConfigs,
                    String dbName, int dbVersion, String pkgModels, Defaults defaults ) {
      this.mContext = context;
      this.mModelConfigs = modelConfigs;
      this.dbName = dbName;
      this.dbVersion = dbVersion;
      this.pkgModels = pkgModels;
      this.mEzDbHelper = new EzDbHelper ( mContext, dbName, null, dbVersion );
      this.mDefaults = defaults;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Get the configuration instance.
    *
    * @param context
    *         the application context.
    * @param dbName
    *         the database name.
    * @param dbVersion
    *         the database version.
    * @param pkgModels
    *         the package that contains the models.
    *
    * @return the configuration object.
    */
   public static Config getAndInitConfig (
           Context context, String dbName, int dbVersion, String pkgModels
   ) {
      if ( instance == null ) {
         instance = new Config ( context, new ModelConfig ().getListForPkg ( context, pkgModels ), dbName, dbVersion, pkgModels, new Defaults () );
      }
      return instance;
   }

   /**
    * Get the instance of the configuration object.
    *
    * @return the config object.
    */
   public static Config getConfig () {
      return instance;
   }

   /**
    * Initialize the config files.
    *
    * @param context
    *         the application context.
    * @param dbName
    *         the database name.
    * @param dbVersion
    *         the database version.
    * @param pkgModels
    *         the package that contains the models.
    */
   public static void init ( Context context,
                             String dbName,
                             int dbVersion,
                             String pkgModels
   ) {
      instance = new Config ( context, new ModelConfig ().getListForPkg ( context, pkgModels ), dbName, dbVersion, pkgModels, new Defaults () );
   }

   /**
    * Initialize the config files.
    *
    * @param context
    *         the application context.
    * @param dbName
    *         the database name.
    * @param dbVersion
    *         the database version.
    * @param pkgModels
    *         the package that contains the models.
    * @param versionCache
    *         the max numbers of version ( note: not version number but version count, eg. save 2
    *         versions : version 4 and version 5 ) to store in cache.
    */
   public static void init ( Context context,
                             String dbName,
                             int dbVersion,
                             String pkgModels,
                             int versionCache
   ) {
      instance = new Config ( context, new ModelConfig ().getListForPkg ( context, pkgModels ), dbName, dbVersion, pkgModels, new Defaults () );
      instance.setVersionCache ( versionCache );
   }

   public static void init ( Context context,
                             String dbName,
                             int dbVersion,
                             String pkgModels,
                             int versionCache,
                             boolean saveDataOnUpgradeToggle
   ) {
      instance = new Config ( context, new ModelConfig ().getListForPkg ( context, pkgModels ), dbName, dbVersion, pkgModels, new Defaults () );
      instance.setVersionCache ( versionCache );
      instance.setSaveDataOnUpgradeToggle ( saveDataOnUpgradeToggle );
   }

   public static void init ( Context context,
                             String dbName,
                             int dbVersion,
                             String pkgModels,
                             int versionCache,
                             boolean saveDataOnUpgradeToggle,
                             Defaults defaults
   ) {
      instance = new Config ( context, new ModelConfig ().getListForPkg ( context, pkgModels ), dbName, dbVersion, pkgModels, defaults );
      instance.setVersionCache ( versionCache );
      instance.setSaveDataOnUpgradeToggle ( saveDataOnUpgradeToggle );
   }

   /**
    * Initialize EzSql using android metadata.
    *
    * @param context
    *         the application context.
    */
   public static void init ( Context context ) {
      try {
         ApplicationInfo appInfo = context.getPackageManager ().getApplicationInfo (
                 context.getPackageName (),
                 PackageManager.GET_META_DATA
         );
         Bundle bundle = appInfo.metaData;

         // set db name from meta data.
         String dbName = bundle.getString ( StaticHelper.META_DB_NAME );

         // set db version from meta data.
         int dbVersion = bundle.getInt ( StaticHelper.META_DB_VERSION );

         // set db package from meta data.
         String dbPackage = bundle.getString ( StaticHelper.META_MODEL_PKG );

         // set defaults from meta data.
         Defaults defaults = new Defaults ();
         if ( bundle.containsKey ( StaticHelper.META_DEFAULT_TEXT ) )
            defaults.setDefaultValText ( bundle.getString ( StaticHelper.META_DEFAULT_TEXT ) );
         if ( bundle.containsKey ( StaticHelper.META_DEFAULT_INT ) )
            defaults.setDefaultValInt ( bundle.getInt ( StaticHelper.META_DEFAULT_INT ) );
         if ( bundle.containsKey ( StaticHelper.META_DEFAULT_REAL ) )
            defaults.setDefaultValReal ( bundle.getLong ( StaticHelper.META_DEFAULT_REAL ) );

         // initialize the config object.
         instance = new Config ( context, new ModelConfig ().getListForPkg ( context, dbPackage ),
                 dbName, dbVersion, dbPackage, defaults );

         // set the max version caching.
         if ( bundle.containsKey ( StaticHelper.META_MAX_VERSION_CACHE ) )
            instance.setVersionCache ( bundle.getInt ( StaticHelper.META_MAX_VERSION_CACHE ) );

      } catch ( PackageManager.NameNotFoundException e ) {
         if ( loggingEnabled )
            Log.e ( TAG, "init: error : " + e.getMessage () );
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getters and Setters.
   ///////////////////////////////////////////////////////////////////////////

   public Defaults getDefaults () {
      return mDefaults;
   }

   public void setDefaults ( Defaults defaults ) {
      mDefaults = defaults;
   }

   public boolean isSaveDataOnUpgradeToggle () {
      return saveDataOnUpgradeToggle;
   }

   public void setSaveDataOnUpgradeToggle ( boolean saveDataOnUpgradeToggle ) {
      this.saveDataOnUpgradeToggle = saveDataOnUpgradeToggle;
   }

   public EzDbHelper getEzDbHelper () {
      return mEzDbHelper;
   }

   public void setEzDbHelper ( EzDbHelper ezDbHelper ) {
      mEzDbHelper = ezDbHelper;
   }

   public int getVersionCache () {
      return versionCache;
   }

   public void setVersionCache ( int versionCache ) {
      this.versionCache = versionCache;
   }

   public static Config getInstance () {
      return instance;
   }

   public static void setInstance ( Config instance ) {
      Config.instance = instance;
   }

   public Context getContext () {
      return mContext;
   }

   public void setContext ( Context context ) {
      mContext = context;
   }

   public ArrayList < ModelConfig > getModelConfigs () {
      return mModelConfigs;
   }

   public void setModelConfigs ( ArrayList < ModelConfig > modelConfigs ) {
      mModelConfigs = modelConfigs;
   }

   public String getDbName () {
      return dbName;
   }

   public void setDbName ( String dbName ) {
      this.dbName = dbName;
   }

   public int getDbVersion () {
      return dbVersion;
   }

   public void setDbVersion ( int dbVersion ) {
      this.dbVersion = dbVersion;
   }

   public String getPkgModels () {
      return pkgModels;
   }

   public void setPkgModels ( String pkgModels ) {
      this.pkgModels = pkgModels;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helper methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Get the model configurations for a given table.
    *
    * @param ezModel
    *         the ez model.
    *
    * @return model configs.
    */
   public ModelConfig getModelConfigForClass ( Class ezModel ) {

      EzTable tableAnn = ( EzTable ) ezModel.getAnnotation ( EzTable.class );
      String tableName = tableAnn.value ();

      for ( ModelConfig model : mModelConfigs ) {
         if ( model.getTableName ().equals ( tableName ) )
            return model;
      }

      return null;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Classes.
   ///////////////////////////////////////////////////////////////////////////

   public static class Defaults {

      private String defaultValText = "\"\"";
      private int defaultValInt = 0;
      private long defaultValReal = ( long ) 0.0;

      public String getDefaultValText () {
         return defaultValText;
      }

      public void setDefaultValText ( String defaultValText ) {
         this.defaultValText = defaultValText;
      }

      public int getDefaultValInt () {
         return defaultValInt;
      }

      public void setDefaultValInt ( int defaultValInt ) {
         this.defaultValInt = defaultValInt;
      }

      public long getDefaultValReal () {
         return defaultValReal;
      }

      public void setDefaultValReal ( long defaultValReal ) {
         this.defaultValReal = defaultValReal;
      }

   }

}
