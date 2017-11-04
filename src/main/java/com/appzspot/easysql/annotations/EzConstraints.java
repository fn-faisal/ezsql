package com.appzspot.easysql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target ( ElementType.FIELD )
@Retention ( RetentionPolicy.RUNTIME )
public @interface EzConstraints {

   String value ();

//   boolean primaryKey () default false;
//   boolean unique () default false;
//   String check () default "";
//   String foreignKey () default "";
//   OnDeleteAction onDelete () default OnDeleteAction.NO_ACTION;
//   boolean notNull () default false;
//   String defaultVal () default "";

}
