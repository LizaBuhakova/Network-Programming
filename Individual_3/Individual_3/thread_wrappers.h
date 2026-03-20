#pragma once
#include <thread>
#include <mutex>

class Mutex {
private: std::mutex mtx;
public:
    void Lock() { mtx.lock(); }
    void Unlock() { mtx.unlock(); }
};

class Thread {
private: std::thread t;
public:
    virtual void Run() = 0;
    void Start() { t = std::thread([this] { Run(); }); }
    void Join() { if (t.joinable()) t.join(); }
};