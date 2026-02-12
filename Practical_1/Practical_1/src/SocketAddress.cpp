#include "../include/SocketAddress.h"
#include <iostream>
#include <cstring>
#include <cstdio>

SocketAddress::SocketAddress(const std::string& hostname, int p) : host(hostname), port(p) {
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
}
bool SocketAddress::resolve() {
    addrinfo hints{}, * res;
    hints.ai_family = AF_INET;
    if (getaddrinfo(host.c_str(), nullptr, &hints, &res) != 0) {
        std::cerr << "Cannot resolve host: " << host << "\n";
        return false;
    }
    addr = *((sockaddr_in*)res->ai_addr);
    freeaddrinfo(res);
    return true;
}
std::string SocketAddress::getIp() const {
    char ip[16];
    if (addr.sin_addr.S_un.S_addr != 0) {
        sprintf_s(ip, "%u.%u.%u.%u",
            addr.sin_addr.S_un.S_un_b.s_b1,
            addr.sin_addr.S_un.S_un_b.s_b2,
            addr.sin_addr.S_un.S_un_b.s_b3,
            addr.sin_addr.S_un.S_un_b.s_b4);
        return std::string(ip);
    }
    return "";
}
sockaddr_in* SocketAddress::getAddr() { return &addr; }
bool stringToInAddr(const std::string& ipStr, in_addr& outAddr) {
    unsigned int b1, b2, b3, b4;
    if (sscanf_s(ipStr.c_str(), "%u.%u.%u.%u", &b1, &b2, &b3, &b4) == 4) {
        outAddr.S_un.S_un_b.s_b1 = (BYTE)b1;
        outAddr.S_un.S_un_b.s_b2 = (BYTE)b2;
        outAddr.S_un.S_un_b.s_b3 = (BYTE)b3;
        outAddr.S_un.S_un_b.s_b4 = (BYTE)b4;
        return true;
    }
    return false;
}
