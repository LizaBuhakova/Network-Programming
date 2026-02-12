#include "../include/TcpClient.h"
#include <winsock2.h>
#include <iostream>
#include <string>

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) { std::cerr << "WSAStartup failed\n"; return 1; }

    TcpClient client("127.0.0.1", 5000);

    std::string msg;
    while (true) {
        std::cout << "Enter message: ";
        std::getline(std::cin, msg);
        if (msg == "exit") break;
        client.sendMsg(msg);
        std::cout << "Server: " << client.recvMsg() << "\n";
    }

    WSACleanup();
    return 0;
}
