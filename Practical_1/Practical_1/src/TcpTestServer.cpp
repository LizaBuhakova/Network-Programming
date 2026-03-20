#include "../include/TcpServer.h"
#include <winsock2.h>
#include <iostream>

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) { std::cerr << "WSAStartup failed\n"; return 1; }

    TcpServer server(5000);
    server.bindListen();
    SOCKET client = server.acceptClient();

    while (true) {
        std::string msg = server.recvMsg(client);
        if (msg.empty()) break;
        std::cout << "Client: " << msg << "\n";
        server.sendMsg(client, "Server got: " + msg);
    }

    WSACleanup();
    return 0;
}
