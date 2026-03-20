#include "thread_wrappers.h"
#include <stdio.h>
#include <string>
#include <iostream>
#ifdef _WIN32
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")
#else
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#endif
#define PORT 5553
#define SERVER_IP "127.0.0.1"
void closeSocket(SOCKET s) {
#ifdef _WIN32
    closesocket(s);
#else
    close(s);
#endif
}
int main() {
#ifdef _WIN32
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
#endif
    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        printf("Socket creation failed\n");
        return -1;}
    sockaddr_in serverAddr = {};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = inet_addr(SERVER_IP);
    if (connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr)) != 0) {
        printf("Connection failed\n");
        closeSocket(sock);
        return -1;}
    printf("Connected! Type text (type 'exit' to quit)\n");
    char buffer[1024];
    while (true) {
        std::string input;
        std::cout << "You> ";
        std::getline(std::cin, input);
        if (input == "exit") break;
        if (input.empty()) continue;
        send(sock, input.c_str(), (int)input.length(), 0);
        int bytes = recv(sock, buffer, sizeof(buffer) - 1, 0);
        if (bytes <= 0) break;
        buffer[bytes] = '\0';
        printf("%s\n", buffer);}
    closeSocket(sock);
#ifdef _WIN32
    WSACleanup();
#endif
    return 0;}