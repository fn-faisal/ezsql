package com.appzspot.easysql.annotations;

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
public enum OnDeleteAction
{
   /**
    * Configuring "NO ACTION" means just that: when a parent key is modified
    * or deleted from the database, no special action is taken.
    * credit : https://www.w3resource.com/sqlite/sqlite-constraint.php#ON DELETE
    */
   NO_ACTION,
   /**
    * The "RESTRICT" action means that the application is prohibited from deleting
    * (for ON DELETE RESTRICT) or modifying (for ON UPDATE RESTRICT) a parent key
    * when there exist one or more child keys mapped to it.
    * credit : https://www.w3resource.com/sqlite/sqlite-constraint.php#ON DELETE
    */
   RESTRICT,
   /**
    * If the configured action is "SET NULL", then when a parent key is deleted
    * (for ON DELETE SET NULL) or modified (for ON UPDATE SET NULL), the child
    * key columns of all rows in the child table that mapped to the parent key
    * are set to contain SQL NULL values.
    * credit : https://www.w3resource.com/sqlite/sqlite-constraint.php#ON DELETE
    */
   SET_NULL,
   /**
    * The "SET DEFAULT" actions are similar to "SET NULL", except that each of
    * the child key columns is set to contain the columns default value instead of NULL.
    * credit : https://www.w3resource.com/sqlite/sqlite-constraint.php#ON DELETE
    */
   SET_DEFAULT,
   /**
    * A "CASCADE" action propagates the delete or update operation on the parent
    * key to each dependent child key. For an "ON DELETE CASCADE" action,
    * this means that each row in the child table that was associated with the deleted
    * parent row is also deleted. For an "ON UPDATE CASCADE" action, it means that the
    * values stored in each dependent child key are modified to match the new parent key values.
    * credit : https://www.w3resource.com/sqlite/sqlite-constraint.php#ON DELETE
    */
   CASCADE
}
