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

    // Ждемо, щоб консоль не закривалась
    std::cout << "Press Enter to exit...";
    std::cin.ignore(); // пропускаємо залишок буфера після std::cin >> ip
    std::cin.get();    // чекаємо Enter

    return 0;
}