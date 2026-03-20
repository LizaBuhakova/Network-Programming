#pragma once
#include <string>
#include <winsock2.h>

class TcpClient {
    SOCKET sockfd;

public:
    TcpClient(const std::string& ip, int port);
    void sendMsg(const std::string& msg);
    std::string recvMsg();
    ~TcpClient();
};
