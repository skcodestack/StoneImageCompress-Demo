

cmake_minimum_required(VERSION 3.4.1)

set(distribution_DIR ../../../../libs )
set(SOURCE_FILES src/main/cpp/compressImage.cpp)
set(INC_DIR src/main/cpp/include)

include_directories(src/main/cpp/include)

find_library(   log-lib
                log )
find_library(graphics jnigraphics)

add_library(    libjpeg
                SHARED
                 IMPORTED )


set_target_properties(  libjpeg
                        PROPERTIES IMPORTED_LOCATION
                        ${distribution_DIR}/${ANDROID_ABI}/libjpeg.so)


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")




add_library(    compressImage
                SHARED
                ${SOURCE_FILES} )



target_link_libraries(  compressImage
                        libjpeg
                        ${log-lib}
                        ${graphics})
