# EzSql
an android sqlite ORM tool

## Getting started
### Configuring EzSql.
  1. Extend the Application Class in your android app.
  2. Call the EzSql Config Class's init method.

``` Java
  public class App extends Application {
    public void onCreate ( Context context ) {
      Config.init (
        this, // the application context.
        "DbName", // the name of your database.
        1, // the version of your database.
        "pckg.model" // the string package name of your model classes
      );
    }
  }
```
