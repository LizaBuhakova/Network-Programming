#include "UdpScanner.h"
#include <winsock2.h>
#include <ws2tcpip.h>
#include <iostream>

#pragma comment(lib, "ws2_32.lib")

#define TIMEOUT 1000

UdpScanner::UdpScanner() {
    initWinsock();
}

UdpScanner::~UdpScanner() {
    cleanupWinsock();
}

void UdpScanner::initWinsock() {
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        std::cerr << "WSAStartup failed\n";
    }
}

void UdpScanner::cleanupWinsock() {
    WSACleanup();
}

bool UdpScanner::scanPort(const std::string& ip, int port) {
    SOCKET sock;
    sockaddr_in addr;
    char buffer[1024];

    sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sock == INVALID_SOCKET) {
        return false;
    }

    DWORD timeout = TIMEOUT;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (const char*)&timeout, sizeof(timeout));

    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    inet_pton(AF_INET, ip.c_str(), &addr.sin_addr);

    const char* message = "ping";

    sendto(sock, message, strlen(message), 0, (sockaddr*)&addr, sizeof(addr));

    int addrLen = sizeof(addr);
    int result = recvfrom(sock, buffer, sizeof(buffer), 0, (sockaddr*)&addr, &addrLen);

    closesocket(sock);

    if (result > 0) {
        return true;
    }

    return false;
}