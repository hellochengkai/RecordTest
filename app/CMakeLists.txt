# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(JNIPATH src/main/cpp/)
include_directories(${JNIPATH} ${JNIPATH}include)

add_library(
             native-lib

             SHARED

             src/main/cpp/src/TDLameUtil.cpp )


set(LIBPATH ${PROJECT_SOURCE_DIR}/src/main/cpp/libs/)

add_library( mp3lame STATIC IMPORTED )
set_target_properties(mp3lame PROPERTIES IMPORTED_LOCATION ${LIBPATH}/libmp3lame.a )


find_library(  log-lib log )


target_link_libraries( # Specifies the target library.
                       native-lib mp3lame

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )