#pragma once
#include <string>

class UdpScanner {
public:
    UdpScanner();
    ~UdpScanner();

    bool scanPort(const std::string& ip, int port);

private:
    void initWinsock();
    void cleanupWinsock();
};