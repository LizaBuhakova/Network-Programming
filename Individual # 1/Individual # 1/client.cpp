#include <iostream>
#include <string>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        std::cerr << "Failed to initialize Winsock.\n";
        return 1;
    }

    std::string ip;
    int startPort, endPort;

    std::cout << "Enter IP address: ";
    std::cin >> ip;
    std::cout << "Enter start port: ";
    std::cin >> startPort;
    std::cout << "Enter end port: ";
    std::cin >> endPort;

    std::cout << "Scanning " << ip << "...\n";

    for (int port = startPort; port <= endPort; port++) {
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) continue;

        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons(port);
        addr.sin_addr.s_addr = inet_addr(ip.c_str());

        int result = connect(sock, (sockaddr*)&addr, sizeof(addr));
        if (result != SOCKET_ERROR) {
            std::cout << "[OPEN] Port " << port << "\n";
        }

        closesocket(sock);
    }
    WSACleanup();
    std::cout << "Scan complete.\n";
    return 0;
}
