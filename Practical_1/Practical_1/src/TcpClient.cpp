#include "../include/TcpClient.h"
#include "../include/SocketAddress.h"
#include <iostream>

TcpClient::TcpClient(const std::string& ip, int port) {
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == INVALID_SOCKET) { std::cerr << "Socket error\n"; exit(1); }

    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);

    if (!stringToInAddr(ip, serverAddr.sin_addr)) {
        std::cerr << "Invalid IP address\n";
        exit(1);
    }

    if (connect(sockfd, (sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        std::cerr << "Connect error\n"; exit(1);
    }
}

void TcpClient::sendMsg(const std::string& msg) {
    send(sockfd, msg.c_str(), static_cast<int>(msg.size()), 0);
}

std::string TcpClient::recvMsg() {
    char buffer[1024];
    int n = recv(sockfd, buffer, static_cast<int>(sizeof(buffer) - 1), 0);
    if (n <= 0) return "";
    buffer[n] = '\0';
    return std::string(buffer);
}

TcpClient::~TcpClient() { closesocket(sockfd); }
