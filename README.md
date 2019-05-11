[![Build Status](https://travis-ci.org/LWJGLX/debug.svg?branch=master)](https://travis-ci.org/LWJGLX/debug)

# What

Java Agent for debugging LWJGL3 programs to prevent JVM crashes and resolve OpenGL errors.

# Why

Because some errors in user programs can cause the JVM to crash without a meaningful error message, since LWJGL 3 is tuned for extreme speed at the expense of robustness.

# How

1. Download the `lwjglx-debug-1.0.0.jar` file from [https://www.lwjgl.org/browse/addons/lwjglx-debug](https://www.lwjgl.org/browse/addons/lwjglx-debug) or build this Maven project via the instructions in the 'Build' section below
2. Copy the `lwjglx-debug-1.0.0.jar` to any directory (henceforth called `<cwd>`)
3. Start your LWJGL3 application with `-javaagent:<cwd>/lwjglx-debug-1.0.0.jar`
    1. when using the command line, it should look like: `java -javaagent:<cwd>/lwjglx-debug-1.0.0.jar -cp all/the/jars your.main.Class`
    2. when using Eclipse, right-click your class with the `main()` method, goto 'Run As > Run Configurations...' and on the 'Arguments' tab inside the 'VM Arguments:' field enter `-javaagent:<cwd>/lwjglx-debug-1.0.0.jar`

# Configuration

The following configuration properties are available to configure the library:
- `validate` - Perform argument validation and check for GL errors on each GL call (enabled by default, set via system property `-Dorg.lwjglx.VALIDATE` or via Agent argument `v`)
- `trace` - Generate a trace log (set via system property `-Dorg.lwjglx.TRACE` or via Agent argument `t`)
- `exclude` - Exclude trace outputs for called methods matching a given GLOB pattern (set via Agent argument `e`)
- `nothrow` - Do not throw a Java exception on any detected error but only log the error. Note that this may result in a JVM crash due to illegal arguments or GL errors. (set via system property `-Dorg.lwjglx.NO_THROW` or via Agent argument `n`)
- `debug` - Log additional information about classfile transformations (this can be used to debug the library itself). (set via system property `org.lwjglx.DEBUG` or via Agent argument `d`)
- `output` - Write LWJGL3 and LWJGLX debug and trace logging messages to a file (when this option is set, no output of LWJGL3 and LWJGLX is printed to stdout or stderr, but instead to the specified file). The file name is the value of this property. When the file name ends with `.zip` or `.gz` then a corresponding compressed archive file will be created to save storage space. In this case, the JVM must exit normally for the archive file to be finalized properly. (set via system property `-Dorg.lwjglx.OUTPUT` or via Agent argument `o`)
- `sleep` - Thread.sleep() before calling each intercepted method (useful when following a call trace). The number of milliseconds are specified as the value of this property. (set via system property `-Dorg.lwjglx.SLEEP` or via Agent argument `s`)
- `profile` - Profile various aspects about the application, such as frame time, number of GL calls, number of drawn vertices, allocated buffer object and texture memory. Once the application was started with profiling enabled, open a browser on [http://localhost:2992](http://localhost:2992). When `validate` was not enabled explicitly it will be disabled for ensuring more realistic timings when profiling the application. An optional argument `suspend`/`s` can be used to suspend the start of the application until the browser was opened. (set via system property `-Dorg.lwjglx.PROFILE` or via Agent argument `p`)

Examples:

* `java -javaagent:lwjglx-debug-1.0.0.jar=t ...` (generate a trace on stderr)
* `java -javaagent:lwjglx-debug-1.0.0.jar=t;o=trace.log` (generate a trace written to file `trace.log`)
* `java -javaagent:lwjglx-debug-1.0.0.jar=t;o=trace.log.zip` (generate a zip archive containing a single `trace.log` file)
* `java -javaagent:lwjglx-debug-1.0.0.jar=tn;o=trace.log` (generate a trace written to file `trace.log` and do not throw on GL errors)
* `java -javaagent:lwjglx-debug-1.0.0.jar=t;e=*GL20*,*GL11.glVertex3f` (generate a trace on stderr and exclude all methods from any class having `GL20` in its name, as well as exclude `glVertex3f` from any class ending with `GL11`)
* `java -javaagent:lwjglx-debug-1.0.0.jar=p ...` (profile the application and disable the default validation)
* `java -javaagent:lwjglx-debug-1.0.0.jar=p;v ...` (profile the application with validations enabled)
* `java -javaagent:lwjglx-debug-1.0.0.jar=p=s;v ...` (profile the application with validations enabled and suspend app start until the browser was opened on http://localhost:2992)

# Build

1. `./mvnw package`
2. see target/lwjglx-debug-1.0.0.jar
