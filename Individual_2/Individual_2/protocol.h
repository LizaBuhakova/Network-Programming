#ifndef PROTOCOL_H
#define PROTOCOL_H

#define MAX_STRING_SIZE 256

struct ClientRequest {
    char text[MAX_STRING_SIZE];
};

struct ServerResponse {
    int vowels;
    int consonants;
    int length;
    char message[128];
    bool isError;
};

#endif