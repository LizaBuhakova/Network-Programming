#include "thread_wrappers.h"
#include <stdio.h>
#include <string>
#include <vector>
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
Mutex consoleMutex;
void closeSocket(SOCKET s) {
#ifdef _WIN32
    closesocket(s);
#else
    close(s);
#endif}
class ClientWorker : public Thread {
private:
    SOCKET m_socket;
    sockaddr_in m_addr;
public:
    ClientWorker(SOCKET sock, sockaddr_in addr) : m_socket(sock), m_addr(addr) {}
    void Run() override {
        char* ip = inet_ntoa(m_addr.sin_addr);
        consoleMutex.Lock();
        printf("[Server] Client connected: %s\n", ip);
        consoleMutex.Unlock();
        char buffer[1024];
        while (true) {
            int ret = recv(m_socket, buffer, sizeof(buffer) - 1, 0);
            if (ret <= 0) break;
            buffer[ret] = '\0';
            consoleMutex.Lock();
            printf("[%s] Says: %s\n", ip, buffer);
            consoleMutex.Unlock();
            std::string reply = "Echo: " + std::string(buffer);
            send(m_socket, reply.c_str(), (int)reply.length(), 0);}
        closeSocket(m_socket);
        consoleMutex.Lock();
        printf("[Server] Client disconnected: %s\n", ip);
        consoleMutex.Unlock();
        delete this;}};
int main() {
#ifdef _WIN32
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
#endif
    SOCKET listenSock = socket(AF_INET, SOCK_STREAM, 0);
    if (listenSock == INVALID_SOCKET) {
        printf("Socket creation failed\n");
        return -1; }
    sockaddr_in serverAddr = {};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    if (bind(listenSock, (sockaddr*)&serverAddr, sizeof(serverAddr)) != 0) {
        printf("Bind failed\n");
        return -1;}
    if (listen(listenSock, 10) != 0) {
        printf("Listen failed\n");
        return -1;}
    printf("Server listening on port %d...\n", PORT);
    while (true) {
        sockaddr_in clientAddr;
#ifdef _WIN32
        int len = sizeof(clientAddr);
#else
        socklen_t len = sizeof(clientAddr);
#endif
        SOCKET clientSock = accept(listenSock, (sockaddr*)&clientAddr, &len);
        if (clientSock != INVALID_SOCKET) {
            ClientWorker* worker = new ClientWorker(clientSock, clientAddr);
            if (worker->Start()) worker->Detach();
            else {
                printf("Failed to create thread\n");
                delete worker;
                closeSocket(clientSock);}}}
    closeSocket(listenSock);
#ifdef _WIN32
    WSACleanup();
#endif
    return 0;}