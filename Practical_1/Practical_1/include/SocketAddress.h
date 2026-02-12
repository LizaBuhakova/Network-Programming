#pragma once
#include <string>
#include <winsock2.h>
#include <ws2tcpip.h>

class SocketAddress {
    std::string host;
    int port;
    sockaddr_in addr;

public:
    SocketAddress(const std::string& hostname, int p);
    bool resolve();
    std::string getIp() const;
    sockaddr_in* getAddr();
};
bool stringToInAddr(const std::string& ipStr, in_addr& outAddr);
