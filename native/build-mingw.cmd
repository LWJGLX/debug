echo "Building for x86-64..."
if not exist "build" mkdir build
gcc -c -fPIC -m64 -O3 -I"ffmpeg" -I"%FFMPEG_HOME%" -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -o build\JNI.o src\JNI.c
gcc -shared -L"ffmpeg" -O3 -o build\lwjglxdebug.dll -fPIC -m64 build\JNI.o -lavformat -lavcodec -lavutil -lm -lx264 -lz -liconv
del build\*.o
strip -x -s build\lwjglxdebug.dll
