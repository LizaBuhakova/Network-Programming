#include "../include/UdpServer.h"
#include <iostream>

UdpServer::UdpServer(int p) : port(p) {
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd == INVALID_SOCKET) { std::cerr << "UDP socket error\n"; exit(1); }
}

void UdpServer::bindSocket() {
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(port);

    if (bind(sockfd, (sockaddr*)&addr, sizeof(addr)) == SOCKET_ERROR) {
        std::cerr << "UDP bind error\n"; exit(1);
    }
    std::cout << "UDP Server listening on port " << port << "\n";
}

void UdpServer::sendData(const std::vector<int>& data, const sockaddr_in& clientAddr) {
    sendto(sockfd, reinterpret_cast<const char*>(data.data()), data.size() * sizeof(int), 0,
        (sockaddr*)&clientAddr, sizeof(clientAddr));
}

std::vector<int> UdpServer::recvData(sockaddr_in& clientAddr) {
    char buffer[1024];
    int len = sizeof(clientAddr);
    int n = recvfrom(sockfd, buffer, sizeof(buffer), 0, (sockaddr*)&clientAddr, &len);
    if (n <= 0) return {};
    int count = n / sizeof(int);
    std::vector<int> data(count);
    memcpy(data.data(), buffer, n);
    return data;
}

UdpServer::~UdpServer() {
    closesocket(sockfd);
}
