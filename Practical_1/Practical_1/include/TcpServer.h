#pragma once
#include <string>
#include <winsock2.h>

class TcpServer {
    SOCKET sockfd;
    int port;

public:
    TcpServer(int p);
    void bindListen();
    SOCKET acceptClient();
    void sendMsg(SOCKET clientSock, const std::string& msg);
    std::string recvMsg(SOCKET clientSock);
    ~TcpServer();
};
