package com.appzspot.easysql.ezdb.ezdto;

import com.appzspot.easysql.annotations.EzTable;

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

public class EzColumns {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private int id;
   private String name;
   private String constraint;

   private EzTable ezTable;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   public EzColumns () {}

   public EzColumns ( int id, String name, String constraint, EzTable ezTable ) {
      this.id = id;
      this.name = name;
      this.constraint = constraint;
      this.ezTable = ezTable;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getters and Setters.
   ///////////////////////////////////////////////////////////////////////////

   public int getId () {
      return id;
   }

   public void setId ( int id ) {
      this.id = id;
   }

   public String getName () {
      return name;
   }

   public void setName ( String name ) {
      this.name = name;
   }

   public String getConstraint () {
      return constraint;
   }

   public void setConstraint ( String constraint ) {
      this.constraint = constraint;
   }

   public EzTable getEzTable () {
      return ezTable;
   }

   public void setEzTable ( EzTable ezTable ) {
      this.ezTable = ezTable;
   }
}
