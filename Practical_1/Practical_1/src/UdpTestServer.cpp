#include "../include/UdpServer.h"
#include <winsock2.h>
#include <iostream>

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) { std::cerr << "WSAStartup failed\n"; return 1; }

    UdpServer server(6000);
    server.bindSocket();

    sockaddr_in clientAddr{};
    while (true) {
        auto data = server.recvData(clientAddr);
        if (data.empty()) continue;

        std::cout << "Received: ";
        for (int n : data) std::cout << n << " ";
        std::cout << "\n";

        server.sendData(data, clientAddr);
    }

    WSACleanup();
    return 0;
}
