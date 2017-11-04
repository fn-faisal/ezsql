package com.appzspot.easysql;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appzspot.easysql.annotations.EzTable;
import com.appzspot.easysql.config.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

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

public class ClassAccessor {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private static final String TAG = "ClassAccessor";

   private static final char PKG_SEPARATOR = '.';
   private static final char DIR_SEPARATOR = '/';
   private static final String CLASS_FILE_SUFFIX = ".class";
   private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

   ///////////////////////////////////////////////////////////////////////////
   // Methods.
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Ez classes from package.
    * Credit : https://stackoverflow.com/users/1225328/sp00m
    * @param pkg the package name.
    * @return the set of ez classes.
    */
   public static List < Class < ? > > getEzClassesFromPackage ( Context context, String pkg ) {
      List<Class<?>> modelClasses = new ArrayList <Class<?>> (  );
      try {
         DexFile dexFile = new DexFile ( context.getPackageCodePath () );
         for ( Enumeration<String> iter = dexFile.entries() ; iter.hasMoreElements();) {
            String s = iter.nextElement();
            if ( s.startsWith ( pkg ) ) {
               modelClasses.add ( Class.forName ( s ) );
            }
         }
      }
      catch ( IOException e ) {
         StringWriter stackTraceWriter = new StringWriter ();
         PrintWriter stackTracePrintWriter = new PrintWriter ( stackTraceWriter );
         e.printStackTrace ( stackTracePrintWriter );

         Log.e ( TAG, "Exception ( " + e.getMessage () + " ) :- \n" + stackTraceWriter );
      } catch ( ClassNotFoundException e ) {
         StringWriter stackTraceWriter = new StringWriter ();
         PrintWriter stackTracePrintWriter = new PrintWriter ( stackTraceWriter );
         e.printStackTrace ( stackTracePrintWriter );

         Log.e ( TAG, "Exception ( " + e.getMessage () + " ) :- \n" + stackTraceWriter );
      }
      return modelClasses;
   }


   public static Field[] getFieldsUpTo( Class<?> type, @Nullable Class<?> exclusiveParent) {
      Field[] result = type.getDeclaredFields();

      Class<?> parentClass = type.getSuperclass();
      if (parentClass != null && (exclusiveParent == null || !parentClass.equals(exclusiveParent))) {
         Field[] parentClassFields = getFieldsUpTo(parentClass, exclusiveParent);
         result = concat(result, parentClassFields, Field.class);
      }

      return result;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Methods from :
   // https://github.com/google/guava/blob/master/guava/src/com/google/common/collect/ObjectArrays.java
   ///////////////////////////////////////////////////////////////////////////
   
   /**
    * Returns a new array that contains the concatenated contents of two arrays.
    *
    * @param first the first array of elements to concatenate
    * @param second the second array of elements to concatenate
    * @param type the component type of the returned array
    */
   public static <T> T[] concat(T[] first, T[] second, Class<T> type) {
      T[] result = newArray(type, first.length + second.length);
      System.arraycopy(first, 0, result, 0, first.length);
      System.arraycopy(second, 0, result, first.length, second.length);
      return result;
   }

   public static <T> T[] newArray(Class<T> type, int length) {
      return (T[]) Array.newInstance(type, length);
   }
   
}
