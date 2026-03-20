#include <iostream>
#ifdef _WIN32
#include <winsock2.h>
#pragma comment(lib,"ws2_32.lib")
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#endif

int main() {
#ifdef _WIN32
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
#endif
    int port = 54000;
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket < 0) {
        std::cerr << "Socket creation failed\n";
        return 1;
    }
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;
    if (bind(serverSocket, (sockaddr*)&addr, sizeof(addr)) < 0) {
        std::cerr << "Bind failed\n";
        return 1;
    }
    if (listen(serverSocket, 1) < 0) {
        std::cerr << "Listen failed\n";
        return 1;
    }
    std::cout << "Server listening on port " << port << "...\n";
    int clientSock = accept(serverSocket, nullptr, nullptr);
    if (clientSock >= 0) {
        std::cout << "Client connected!\n";
#ifdef _WIN32
        closesocket(clientSock);
        closesocket(serverSocket);
#else
        close(clientSock);
        close(serverSocket);
#endif
    }

#ifdef _WIN32
    WSACleanup();
#endif
    return 0;
}

