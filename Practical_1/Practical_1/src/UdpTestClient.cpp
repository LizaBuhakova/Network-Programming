#include "../include/UdpClient.h"
#include <winsock2.h>
#include <iostream>
#include <vector>

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) { std::cerr << "WSAStartup failed\n"; return 1; }

    UdpClient client("127.0.0.1", 6000);

    while (true) {
        std::vector<int> nums;
        int x;
        std::cout << "Enter numbers (0 to send): ";
        while (std::cin >> x && x != 0) nums.push_back(x);

        if (nums.empty()) break;

        client.sendData(nums);
        auto reply = client.recvData();
        std::cout << "Server replied: ";
        for (int n : reply) std::cout << n << " ";
        std::cout << "\n";

        std::cin.ignore();
    }

    WSACleanup();
    return 0;
}
