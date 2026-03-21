#include "icmp_ping.h"
#include <iostream>

int main() {
    std::string ip;

    std::cout << "Enter IP to ping: ";
    std::cin >> ip;

    ICMPPing pinger(ip);

    if (!pinger.sendPing()) {
        std::cout << "Ping failed.\n";
    }

    std::cout << "Press Enter to exit...";
    std::cin.ignore();
    std::cin.get();

    return 0;
}