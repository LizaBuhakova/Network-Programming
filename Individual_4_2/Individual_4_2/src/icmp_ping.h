#pragma once
#include <string>

class ICMPPing {
public:
    ICMPPing(const std::string& ip);
    bool sendPing();
private:
    std::string ipAddress;
};