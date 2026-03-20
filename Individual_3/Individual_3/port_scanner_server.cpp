#include "thread_wrappers.h"
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <thread>
#include <mutex>
#ifdef _WIN32
#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <winsock2.h>
#include <ws2tcpip.h>
#pragma comment(lib,"ws2_32.lib")
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#define SOCKET int
#define INVALID_SOCKET -1
#define closesocket close
#endif
std::mutex consoleMutex;
bool scanPort(const std::string& ip, int port) {
    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) return false;
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
#ifdef _WIN32
    InetPton(AF_INET, ip.c_str(), &addr.sin_addr);
#else
    inet_pton(AF_INET, ip.c_str(), &addr.sin_addr);
#endif
    bool ok = (connect(sock, (sockaddr*)&addr, sizeof(addr)) == 0);
    closesocket(sock);
    return ok;}
void clientHandler(SOCKET clientSock) {
    char buffer[1024];
    while (true) {
        int bytes = static_cast<int>(recv(clientSock, buffer, sizeof(buffer) - 1, 0));
        if (bytes <= 0) break;
        buffer[bytes] = '\0';
        std::string data(buffer);
        if (data == "exit") break;
        std::istringstream iss(data);
        std::string ip;
        int startPort, endPort;
        iss >> ip >> startPort >> endPort;        {
            std::lock_guard<std::mutex> lock(consoleMutex);
            std::cout << "=== Client connected: " << ip << " | Ports: " << startPort << "-" << endPort << " ===\n";}
        std::string result;
        for (int p = startPort; p <= endPort; ++p)
            result += (scanPort(ip, p) ? "[OPEN] " : "[CLOSED] ") + std::string("Port ") + std::to_string(p) + "\n";
        send(clientSock, result.c_str(), static_cast<int>(result.size()), 0);{
            std::lock_guard<std::mutex> lock(consoleMutex);
            std::cout << "=== Scan completed for " << ip << " ===\n";        }    }
    closesocket(clientSock);   {
        std::lock_guard<std::mutex> lock(consoleMutex);
        std::cout << "--- Client disconnected ---\n";    }}
int main() {
#ifdef _WIN32
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) { std::cerr << "WSAStartup failed\n"; return 1; }
#endif
    SOCKET serverSock = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSock == INVALID_SOCKET) return 1;
    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(5555);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    bind(serverSock, (sockaddr*)&serverAddr, sizeof(serverAddr));
    listen(serverSock, 5);    {
        std::lock_guard<std::mutex> lock(consoleMutex);
        std::cout << "=== TCP Port Scanner Server Started on port 5555 ===\n";}
    std::vector<std::thread> clients;
    while (true) {
        SOCKET client = accept(serverSock, nullptr, nullptr);
        if (client != INVALID_SOCKET) clients.emplace_back(clientHandler, client);}
    for (auto& t : clients) t.join();
    closesocket(serverSock);
#ifdef _WIN32
    WSACleanup();
#endif
    return 0;}