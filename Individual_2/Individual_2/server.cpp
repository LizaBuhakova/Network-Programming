#include <winsock2.h>
#include <windows.h>
#include <stdio.h>
#include <string.h>
#include "Protocol.h"
#pragma comment(lib, "Ws2_32.lib")
CRITICAL_SECTION consoleLock;
bool isVowel(char c) {
    c = tolower(c);
    return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
}
DWORD WINAPI ClientHandler(LPVOID lpParam) {
    SOCKET clientSocket = (SOCKET)lpParam;
    ClientRequest req;
    ServerResponse res;
    while (true) {
        int ret = recv(clientSocket, (char*)&req, sizeof(req), 0);
        if (ret <= 0) break;
        memset(&res, 0, sizeof(res));
        int vowels = 0, consonants = 0;
        int len = strlen(req.text);
        for (int i = 0; i < len; i++) {
            char c = req.text[i];
            if (isalpha(c)) {
                if (isVowel(c)) vowels++;
                else consonants++;           } }
        res.vowels = vowels;
        res.consonants = consonants;
        res.length = len;
        res.isError = false;
        strcpy(res.message, "Processed successfully");
        send(clientSocket, (char*)&res, sizeof(res), 0);
        EnterCriticalSection(&consoleLock);
        printf("[Thread %lu] Processed: \"%s\" -> Vowels:%d Consonants:%d Length:%d\n",
            GetCurrentThreadId(), req.text, vowels, consonants, len);
        LeaveCriticalSection(&consoleLock); }
    closesocket(clientSocket);
    EnterCriticalSection(&consoleLock);
    printf("[Thread %lu] Client disconnected.\n", GetCurrentThreadId());
    LeaveCriticalSection(&consoleLock);
    return 0;}
int main() {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    InitializeCriticalSection(&consoleLock);
    SOCKET serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(5555);
    bind(serverSocket, (sockaddr*)&serverAddr, sizeof(serverAddr));
    listen(serverSocket, 5);
    printf("Server started on port 5555...\n");
    while (true) {
        sockaddr_in clientAddr;
        int addrLen = sizeof(clientAddr);
        SOCKET clientSocket = accept(serverSocket, (sockaddr*)&clientAddr, &addrLen);
        if (clientSocket == INVALID_SOCKET) continue;
        HANDLE hThread = CreateThread(NULL, 0, ClientHandler, (LPVOID)clientSocket, 0, NULL);
        if (hThread) CloseHandle(hThread);}
    DeleteCriticalSection(&consoleLock);
    closesocket(serverSocket);
    WSACleanup();
    return 0;}