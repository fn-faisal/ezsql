# EzSql
An android sqlite ORM tool with easy querying of data, database ( Schema ) caching functionality,
and rollback functionality to shift the database to previous version. No need to include scripts or
update logic on database version changes, simple change the version number in the configuration.

## Getting started
### Configuring EzSql.
  1. Extend the Application Class in your android app.
  2. Call the EzSql Config Class's init method.

``` Java
  public class App extends Application {
    public void onCreate () {
      Config.init (
        this, // the application context.
        "DbName", // the name of your database.
        1, // the version of your database.
        "pckg.model" // the string package name of your model classes
      );
    }
  }
```

### Create the Model Classes which represent the database tables.
    The process of making the model classes is fairly simple. All you have to do
    is use Annotations Provided by EzSql, which are listed below:-
      1. EzTable ( Annotate your model classes with this in order to let EzSql know that this Class represents a Model ).
      2. EzColumn ( Annotate your Class properties with EzColumn to let EzSql know that this Property represents a Column ).
      3. EzPk ( Annotate your Class property with EzPk to let EzSql know that this Property represents the Primary key for the table ).
      4. EzFk ( Annotate your Class property with EzFk to let EzSql know that this Property represents a Foreign key for the table  ).
      
#### Example.
   Lets consider a table name user with columns id, name, and email, where the id column is the primary key.
 
   ``` Java
    @EzTable ( "user" )
    public class User {
       @EzPk ( "id" )
       private int id;
    
        @EzColumn ( "name" )
        private String name;

        @EzColumn ( "email" )
        private String email;

        public User () {}

        public int getId () { return id; }
        public void setId ( int id ) { this.id = id; }

        public String getName () { return name; }
        public void setName ( String name ) { this.name = name; }

        public String getEmail () { return email; }

        public void setEmail ( String email ) { this.email = email; }
    }
  ```
    
    For Foreign key, lets assume that each user has an Item. And the model class for this would look somthing like follows:-
    
   ``` Java
    @EzTable ( "item" )
    public class Item {
        @EzPk ( "id" )
        private int id;

        @EzColumn ( "name" )
        private String name;

        @EzFk ( name = "user_ref" , references = "user.id")
        private int userRef;

        public Item () {}

        public int getId () { return id; }
        public void setId ( int id ) { this.id = id; }

        public String getName () { return name; }
        public void setName ( String name ) { this.name = name; }

        public int getUserRef () { return userRef; }
        public void setUserRef ( int userRef ) { this.userRef = userRef; }
    }
  ```

## Queries.
### Insert Quries.
  EzSql consists of simple insert queries. These queries are ran by calling the **insert** 
  method of EzQuery Class from the Class instance. Supply this method with the Class
  Object of the Model class and a hashmap which represents the table column name as keys, and 
  column value as the value for the keys.
  #### Example
  ``` Java
        HashMap<String , Object> user = new HashMap < String, Object > (  );
        user.put 
          ( "id", // the name of the column.
            1 // the value of the column.
           );
        user.put ( "name", "James" );
        user.put ( "email", "james@gmail.com" );

        try {
           long rows = EzQuery.getQuery ( this ).setTransactionSafe ( true )
                 .insert ( User.class, user )
                 .go ();

  ```
  
### Search Quries.
  EzSql consists of simple search queries. These queries are ran by calling the 
  **find** method of EzQuery Class from the Class instance. Supply this method with
  the Model Class ( optionaly can include conditional statements ).
  #### Example
 ``` Java
  ArrayList<User> users = EzQuery.getQuery ( this )
                 .find ( User.class )
                 .colEq ( "name" , "james" )
                 .go ();
 ```
  The above query returns the user whose name is james. If you don't include the conditional methods 
  and call the **go** method after the find method, all the records will be returned.
  
### Update Quries ( Not yet finished ). 
  EzSql consists of simple update queries. These queries are ran by calling the 
  **update** method of EzQuery Class from the Class instance. Supply this method with
  the Model Class ( must include conditional method that identify the row to update ), and a HashMap
  with the name of the column/columns to update.
  
  #### Example
 ``` Java
    long rowsEffected = EzQuery.getQuery ( this )
    .update ( Model.class, HashMap )
    .colEq ( "name" , "james" )
    .go();
  ```
    The above query update the row where user's name is "James".
    
### Delete Quries ( Not yet finished ). 
  EzSql consists of simple delete queries. These queries are ran by calling the 
  **remove** method of EzQuery Class from the Class instance. Supply this method with
  the Model Class ( must include conditional method that identify the row to delete ).
 ``` Java
    long rowsEffected = EzQuery.getQuery ( this )
    .remove ( Model.class )
    .colEq ( "name" , "james" )
    .go();
  ```
    The above query deletes the row where user's name is James.

### Raw Sql Quries ( Not yet finished ).  
   When you need to run a raw sql command, simply use the **rawSql** method of the EzQuery Class.
   
   #### Example
 ``` Java
    Cursor c = EzQuery.getQuery ( this )
      .rawSql ( "Select * from user" )
      .go ();
 ```
  This type of query returns a cursor object.
