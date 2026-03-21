cmake_minimum_required(VERSION 3.10)
project(ICMP_Ping_Windows)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

include_directories(src)

set(SOURCES
    src/main.cpp
    src/icmp_ping.cpp
)

add_executable(icmp_ping_win ${SOURCES})

target_link_libraries(icmp_ping_win
    Iphlpapi
    ws2_32
)