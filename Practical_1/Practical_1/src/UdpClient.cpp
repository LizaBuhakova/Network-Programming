#include "../include/UdpClient.h"
#include "../include/SocketAddress.h"
#include <iostream>

UdpClient::UdpClient(const std::string& ip, int port) {
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd == INVALID_SOCKET) { std::cerr << "UDP socket error\n"; exit(1); }

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    if (!stringToInAddr(ip, serverAddr.sin_addr)) {
        std::cerr << "Invalid IP address\n"; exit(1);
    }
}

void UdpClient::sendData(const std::vector<int>& data) {
    sendto(sockfd, reinterpret_cast<const char*>(data.data()), data.size() * sizeof(int), 0,
        (sockaddr*)&serverAddr, sizeof(serverAddr));
}

std::vector<int> UdpClient::recvData() {
    sockaddr_in fromAddr;
    char buffer[1024];
    int len = sizeof(fromAddr);
    int n = recvfrom(sockfd, buffer, sizeof(buffer), 0, (sockaddr*)&fromAddr, &len);
    if (n <= 0) return {};
    int count = n / sizeof(int);
    std::vector<int> data(count);
    memcpy(data.data(), buffer, n);
    return data;
}

UdpClient::~UdpClient() { closesocket(sockfd); }
