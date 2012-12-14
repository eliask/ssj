find_package(JNI REQUIRED)

set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)
set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)

include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})

add_library(ssjutil SHARED Chrono.c)

# Not included because we have the binaries in the resources directory, and this
# is difficult to compile on Windows.
# add_library(randvar SHARED RandUnuran.c)