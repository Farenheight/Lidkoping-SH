# Lidköpings Stenhuggeri
Android application for Lidköpings Stenhuggeri AB. Software Engineering Project course (DAT255), Chalmers.

## Install and run
Locate the `Lidkoping-SH.apk` file in the `/dist/` directory. Every release on github has an attached .apk file as well. 
### Login
Username (email): test  
Password: test

## Documentation
All documentation can be found in the `/docs/` folder.

## Build instructions

### Client
Clone the project on github and import it in eclipse. All external libraries are bundled with the project in the /libs/ folder. Two of them needs to be imported as library projects. Those are Google Play Services and Google Maps Utilities. To be able use the MapView you also need to use the custom keystore named debug.keystore in the repository’s root folder which includes an accepted API key to Google Maps. 

### Server
The web server REST API is running on a PHP web server. Publish the source files (folder api) in the root folder, exactly as they are on Git. A MySQL database should then be created and then populated by the SQL file in /api/setup/ folder.

The PHP application uses a config file, where the MySQL host address, username, password, and database name must be specified. That’s also where some other secret settings is stored. This file is named db_config.php and is located in the root of /api/. In the Git repo is an example.

The folder /web\_gui/ contains the web interface source code. This can be published to the same web site as the API, or another by using a separate db\_config.php file to setup database configuration.

In index.php in api folder, where requiring a HTTPS connection for secure transportation. Modify it to suit your needs.

## Links
### Stacktrace
Log of all exception thrown by all clients: http://simonbengtsson.se/lsh/stacktrace/

### Orderformulär
Form for creating new orders on the server: http://lidkopingsh.kimkling.net/web_gui/form.php
