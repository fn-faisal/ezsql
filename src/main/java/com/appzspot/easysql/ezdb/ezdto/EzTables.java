package com.appzspot.easysql.ezdb.ezdto;

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

public class EzTables {

   ///////////////////////////////////////////////////////////////////////////
   // Fields.
   ///////////////////////////////////////////////////////////////////////////

   private int id;
   private String name;
   private int version;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors.
   ///////////////////////////////////////////////////////////////////////////

   public EzTables () {}

   public EzTables ( int id, String name, int version ) {
      this.id = id;
      this.name = name;
      this.version = version;
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

   public int getVersion () {
      return version;
   }

   public void setVersion ( int version ) {
      this.version = version;
   }

}

