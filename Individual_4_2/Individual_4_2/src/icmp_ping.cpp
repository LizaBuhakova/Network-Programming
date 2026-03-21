#include "icmp_ping.h"
#include <windows.h>
#include <Iphlpapi.h>
#include <icmpapi.h>
#include <iostream>
#pragma comment(lib, "Iphlpapi.lib")
#pragma comment(lib, "Ws2_32.lib")

ICMPPing::ICMPPing(const std::string& ip) : ipAddress(ip) {}
bool ICMPPing::sendPing() {
    HANDLE hIcmp = IcmpCreateFile();
    if (hIcmp == INVALID_HANDLE_VALUE) {
        std::cerr << "IcmpCreateFile failed\n";
        return false;}
    IPAddr ipAddr = inet_addr(ipAddress.c_str());
    BYTE replyBuffer[sizeof(ICMP_ECHO_REPLY) + 32] = { 0 };
    DWORD replySize = sizeof(replyBuffer);
    DWORD dwRetVal = IcmpSendEcho(
        hIcmp,        
        ipAddr,     
        nullptr,      
        0,          
        nullptr,       
        replyBuffer,    
        replySize,    
        1000 );
    if (dwRetVal != 0) {
        ICMP_ECHO_REPLY* echoReply = (ICMP_ECHO_REPLY*)replyBuffer;
        std::cout << "Reply from " << ipAddress
            << " in " << echoReply->RoundTripTime << " ms" << std::endl;
        IcmpCloseHandle(hIcmp);
        return true;
    }
    else {
        std::cout << "No reply from " << ipAddress << std::endl;
        IcmpCloseHandle(hIcmp);
        return false;
    }
}