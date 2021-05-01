# SSHCommandExecutor

SSHCommandExecutor is a Java based Android application. Where, On clicking the button connect to the SSH server with “host”, “port”, “username” and “password”. And then execute the “command” in the connected ssh server.

## Application UI

![Optional Text](../master/screenshot/app_screen.png)


## Installation

### Clone the Repository

As usual, you get started by cloning the project to your local machine:

```bash
git clone https://github.com/maksudur-rahman/AndroidCodingTaskOceanizeInc.git
```
### Open and Run Project in Android Studio
* Open Android Studio and select `File->Open...` or from the Android Launcher select `Import project (Eclipse ADT, Gradle, etc.)` and navigate to the root directory of your project.
* Select the directory or drill in and select the file `build.gradle` in the cloned repo.
* Click 'OK' to open the the project in Android Studio.
* A Gradle sync should start, but you can force a sync and build the 'app' module as needed.

### Gradle (command line)

* Build the APK: `./gradlew build`


## Usage

```java


JSch jsch = new JSch();
            Session session = jsch.getSession(item.getUsername(), item.getHost(), item.getPort());
            session.setPassword(item.getPassword());

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.setTimeout(10000);
            session.connect();
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[JU © Maksudur Rahman.](../LICENSE)
