#include "../include/TcpServer.h"
#include <iostream>

TcpServer::TcpServer(int p) : port(p) {
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == INVALID_SOCKET) { std::cerr << "Socket error\n"; exit(1); }
}

void TcpServer::bindListen() {
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(port);

    if (bind(sockfd, (sockaddr*)&addr, sizeof(addr)) == SOCKET_ERROR) {
        std::cerr << "Bind error\n"; exit(1);
    }
    if (listen(sockfd, 5) == SOCKET_ERROR) {
        std::cerr << "Listen error\n"; exit(1);
    }
    std::cout << "TCP Server listening on port " << port << "\n";
}

SOCKET TcpServer::acceptClient() {
    sockaddr_in clientAddr;
    int len = sizeof(clientAddr);
    SOCKET clientSock = accept(sockfd, (sockaddr*)&clientAddr, &len);
    if (clientSock == INVALID_SOCKET) { std::cerr << "Accept error\n"; exit(1); }
    std::cout << "Client connected\n";
    return clientSock;
}

void TcpServer::sendMsg(SOCKET clientSock, const std::string& msg) {
    send(clientSock, msg.c_str(), static_cast<int>(msg.size()), 0);
}

std::string TcpServer::recvMsg(SOCKET clientSock) {
    char buffer[1024];
    int n = recv(clientSock, buffer, static_cast<int>(sizeof(buffer) - 1), 0);
    if (n <= 0) return "";
    buffer[n] = '\0';
    return std::string(buffer);
}

TcpServer::~TcpServer() {
    closesocket(sockfd);
}
