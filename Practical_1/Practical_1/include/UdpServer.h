#pragma once
#include <winsock2.h>
#include <vector>

class UdpServer {
    SOCKET sockfd;
    int port;

public:
    UdpServer(int p);
    void bindSocket();
    void sendData(const std::vector<int>& data, const sockaddr_in& clientAddr);
    std::vector<int> recvData(sockaddr_in& clientAddr);
    ~UdpServer();
};
