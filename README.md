# EzSql
an android sqlite ORM tool

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

  
