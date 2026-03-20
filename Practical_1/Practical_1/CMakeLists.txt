cmake_minimum_required(VERSION 3.10)
project(Practical_1)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(${PROJECT_SOURCE_DIR}/include)

# TCP Server
add_executable(tcptestserver
    src/SocketAddress.cpp
    src/TcpServer.cpp
    src/TcpTestServer.cpp
)
target_link_libraries(tcptestserver ws2_32)

# TCP Client
add_executable(tcptestclient
    src/SocketAddress.cpp
    src/TcpClient.cpp
    src/TcpTestClient.cpp
)
target_link_libraries(tcptestclient ws2_32)

# UDP Server
add_executable(udptestserver
    src/SocketAddress.cpp
    src/UdpServer.cpp
    src/UdpTestServer.cpp
)
target_link_libraries(udptestserver ws2_32)

# UDP Client
add_executable(udptestclient
    src/SocketAddress.cpp
    src/UdpClient.cpp
    src/UdpTestClient.cpp
)
target_link_libraries(udptestclient ws2_32)
