cmake_minimum_required(VERSION 3.10)

project(UDPPortScanner)

set(CMAKE_CXX_STANDARD 17)

add_executable(UDPPortScanner
    src/main.cpp
    src/UdpScanner.cpp
)

target_link_libraries(UDPPortScanner ws2_32)