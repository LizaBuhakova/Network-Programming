#include <iostream>
#include <string>
#include <sstream>
#ifdef _WIN32
#include <winsock2.h>
#pragma comment(lib,"ws2_32.lib")
#else
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#define SOCKET int
#define closesocket close
#define INVALID_SOCKET -1
#endif

int main() {
#ifdef _WIN32
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
#endif
    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) return 1;
    std::string serverIP = "127.0.0.1";
    int serverPort = 5555;
    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(serverPort);
    serverAddr.sin_addr.s_addr = inet_addr(serverIP.c_str());
    if (connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) { std::cout << "Cannot connect to server\n"; return 1; }
    std::cout << "=== Connected to server. Type 'exit' to quit ===\n";
    while (true) {
        std::string ip;
        int startPort, endPort;
        std::cout << "\nEnter IP to scan: ";
        std::cin >> ip;
        if (ip == "exit") { send(sock, ip.c_str(), static_cast<int>(ip.size()), 0); break; }
        std::cout << "Enter start port: "; std::cin >> startPort;
        std::cout << "Enter end port: "; std::cin >> endPort;
        std::ostringstream oss;
        oss << ip << " " << startPort << " " << endPort;
        std::string request = oss.str();
        send(sock, request.c_str(), static_cast<int>(request.size()), 0);
        char buffer[4096];
        int bytes = static_cast<int>(recv(sock, buffer, sizeof(buffer) - 1, 0));
        if (bytes > 0) { buffer[bytes] = '\0'; std::cout << "\n=== Scan result for " << ip << " ===\n" << buffer; }   }
    closesocket(sock);
#ifdef _WIN32
    WSACleanup();
#endif
    return 0;
}