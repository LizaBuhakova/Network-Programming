#include <iostream>
#include "UdpScanner.h"

int main() {
    UdpScanner scanner;

    std::string ip;
    int startPort, endPort;

    std::cout << "Enter IP: ";
    std::cin >> ip;

    std::cout << "Start port: ";
    std::cin >> startPort;

    std::cout << "End port: ";
    std::cin >> endPort;

    for (int port = startPort; port <= endPort; port++) {
        if (scanner.scanPort(ip, port)) {
            std::cout << "Port " << port << " OPEN\n";
        }
        else {
            std::cout << "Port " << port << " closed or filtered\n";
        }
    }

    return 0;
}