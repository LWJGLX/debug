[![Build Status](https://travis-ci.org/LWJGLX/debug.svg?branch=master)](https://travis-ci.org/LWJGLX/debug)

# What

Java Agent for debugging LWJGL3 programs to prevent JVM crashes and resolve OpenGL errors.

## Validation

In order to enable full debugging and validation for your OpenGL application, LWJGLX/debug does the following:

1. Enable all [LWJGL3 debug properties](https://github.com/LWJGL/lwjgl3-wiki/wiki/2.5.-Troubleshooting)
2. When creating a GLFW window, set the window hint `GLFW_OPENGL_DEBUG_CONTEXT`
3. Configuring an OpenGL debug message callback right after `GL.createCapabilities()` that outputs any message from the GL driver and throws synchronously on GL error
4. Instrumenting every GL call in the following way:
    1. Before performing the GL call, call `GL.getCapabilities()` to check whether the current thread has `GLCapabilities` set
    2. Before performing the GL call, check the `GLCapabilities` field of the called GL function to see if it is exposed by the driver (i.e. available in the current context)
    3. Performing the GL call
    4. When debug message callbacks are unavailable, calling `glGetError()` to throw an exception if it returns not `0`

## Tracing

LWJGLX/debug also allows to capture a full OpenGL call trace, recording the called GL method, the actual arguments and the return value.
Method arguments that are of type GLboolean, GLenum or GLbitfield will be correctly decoded to their symbolic name, such as `GL_DEPTH_BUFFER_BIT`, `GL_TEXTURE0` or `GL_TRUE`.

By default, the GL call trace is written to stderr and can be enabled via the system property `org.lwjglx.TRACE`. Using the system property `org.lwjglx.OUTPUT` the LWJGLX and LWJGL3 log outputs can be written to a file instead whose name is the argument of that system property. The file will be (re)-created.

When using `org.lwjglx.TRACE` and `org.lwjglx.OUTPUT` and generating traces over a relatively long period of time, the generated text file can become very large (hundreds of Megabytes to well over a Gigabyte). In order to reduce the size of the generated file on the filesystem, it is possible to generate a .zip or .gz file. Simply let the argument to `org.lwjglx.OUTPUT` end with `.zip` or `.gz`. Please note that in this case the JVM must terminate correctly/normally or be killed gracefully, so it must not be killed via SIGTERM, for LWJGLX/debug to finalize the written zip/gz file.

## Exclude methods

By default, LWJGLX/debug will intercept all public static methods on all classes in (sub)packages of `org.lwjgl.*`, except for `org.lwjgl.system.*`. Sometimes it may be desirable to exclude more calls, which occur often and are not necessary for analyzing an issue, such as `glVertex3f`. To exclude those, use a GLOB pattern matching the fully-qualified class name in the form `org/some/pack/age/Class.method`. So, in order to exclude `glVertex3f` you could use `*glVertex3f`, which will match `org/lwjgl/opengl/GL11.glVertex3f`. A comma-separated list of such GLOB patterns can be specified as the Java Agent argument on the `-javaagent` VM argument, like so:

`-javaagent:debug.jar=*glVertex3f,*glVertex2f,*GL30.*`

# Why

Because some errors in user programs can cause the JVM to crash without a meaningful error message, since LWJGL 3 is tuned for extreme speed at the expense of robustness.

# How

1. Copy the prebuilt/debug.jar to any directory (henceforth called `<prebuilt>`).
2. Start your LWJGL3 application with `-javaagent:<prebuilt>/debug.jar`
    1. when using the command line, it should look like: `java -javaagent:<prebuilt>/debug.jar -cp all/the/jars your.main.Class`
    2. when using Eclipse, right-click your class with the `main()` method, goto 'Run As > Run Configurations...' and on the 'Arguments' tab inside the 'VM Arguments:' field enter `-javaagent:<prebuilt>/debug.jar`
3. Additionally, when tracing GL calls, add `-Dorg.lwjglx.TRACE` to the VM arguments

# Configuration

The following system properties are available to configure the library:
- `org.lwjglx.TRACE` - Generate a trace log.
- `org.lwjglx.NO_THROW` - Do not throw a Java exception on any detected error but only log the error. Note that this may result in a JVM crash due to illegal arguments or GL errors.
- `org.lwjglx.DEBUG` - Log additional information about classfile transformations (this can be used to debug the library itself).
- `org.lwjglx.OUTPUT` - Write LWJGL3 and LWJGLX debug and trace logging messages to a file (when this option is set, no output of LWJGL3 and LWJGLX is printed to stdout or stderr, but instead to the specified file). The file name is the value of this property.
- `org.lwjglx.DELAY` - Thread.sleep() before calling each intercepted method (useful when following a call trace). The number of milliseconds are specified as the value of this property.

# Develop

`./mvnw generate-sources`

# Build

1. `./mvnw package`
2. see target/debug.jar
