cmake_minimum_required(VERSION 3.10)
project(EchoServerClient)

set(CMAKE_CXX_STANDARD 11)

if(WIN32)
    add_definitions(-D_WIN32_WINNT=0x0601)
    set(SOCK_LIB ws2_32)
endif()

add_executable(echo_server echo_server.cpp)
add_executable(echo_client echo_client.cpp)

target_link_libraries(echo_server PRIVATE ${SOCK_LIB})
target_link_libraries(echo_client PRIVATE ${SOCK_LIB})