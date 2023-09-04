# distributed-tracing

Once you cloned the project into your local, and you enter into *distributed-tracing* directory, you can run it using the following command:
```
./gradlew run --args='/absolute/path/to/your/file.txt'
```
where */absolute/path/to/your/file.txt* is the absolute path to your text file which contains the Graph setup.

Example:
```
./gradlew run --args='/home/hernan/distributed-tracing/src/test/resources/graph-test.txt'
```
In case you are on Windows platform, you have to use the gradlew.bat
