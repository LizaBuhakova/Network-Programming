cmake_minimum_required(VERSION 3.10)
project(PortScanner)

set(CMAKE_CXX_STANDARD 11)

# Додаємо серверний файл та заголовок
add_executable(server
    port_scanner_server.cpp
    thread_wrappers.h
)

# Додаємо клієнтський файл та заголовок
add_executable(client
    port_scanner_client.cpp
    thread_wrappers.h
)

# Лінкування pthread для Linux/UNIX
if(UNIX)
    target_link_libraries(server pthread)
    target_link_libraries(client pthread)
endif()