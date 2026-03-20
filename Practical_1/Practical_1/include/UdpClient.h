#pragma once
#include <winsock2.h>
#include <vector>
#include <string>

class UdpClient {
    SOCKET sockfd;
    sockaddr_in serverAddr;

public:
    UdpClient(const std::string& ip, int port);
    void sendData(const std::vector<int>& data);
    std::vector<int> recvData();
    ~UdpClient();
};
