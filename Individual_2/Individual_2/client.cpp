#include <winsock2.h>
#include <windows.h>
#include <stdio.h>
#include <string.h>
#include "Protocol.h"

#pragma comment(lib, "Ws2_32.lib")

int main() {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);

    SOCKET s = socket(AF_INET, SOCK_STREAM, 0);
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(5555);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    if (connect(s, (sockaddr*)&serverAddr, sizeof(serverAddr)) != 0) {
        printf("Cannot connect to server.\n");
        return 1;
    }
    printf("Connected to server.\n");
    while (true) {
        ClientRequest req;
        ServerResponse res;
        printf("Enter text (or EXIT to quit): ");
        fgets(req.text, MAX_STRING_SIZE, stdin);
        req.text[strcspn(req.text, "\n")] = 0;
        if (_stricmp(req.text, "EXIT") == 0) break;
        send(s, (char*)&req, sizeof(req), 0);
        int ret = recv(s, (char*)&res, sizeof(res), 0);
        if (ret <= 0) break;
        if (!res.isError) {
            printf("Server response: Vowels=%d, Consonants=%d, Length=%d\n",
                res.vowels, res.consonants, res.length);}
        else {
            printf("Error from server: %s\n", res.message);}}
    closesocket(s);
    WSACleanup();
    return 0;
}