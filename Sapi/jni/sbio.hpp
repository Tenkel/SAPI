#ifndef SBIO_HPP
#define SBIO_HPP

#include <iostream>
#include <fstream>
#include <cstdlib>
#include <string>

using std::ofstream;
using std::ifstream;

class SBIOException { };

template <typename T>
class sbwriter {
public:
    
    sbwriter(const std::string &filename, const int size) :
        _stream(filename.c_str(), std::ios::binary | std::ios::trunc)
    {
        _buffer = (T*) malloc(sizeof(T) * size);
        _current = 0;
        _size = size;
    }
    
    ~sbwriter()
    {
        if (_current != 0) {
            _stream.write((char*) _buffer, sizeof(T) * _current);
        }

        _stream.flush();
        _stream.close();        
        free(_buffer);
    }
    
    void put(const T &t)
    {
        _buffer[_current++] = t;
        
        if (_current == _size) {
            _stream.write((char*) _buffer, sizeof(T) * _current);
            _current = 0;
        }
    }
    
    bool good()
    {
        return _stream.good();
    }
    
private:
    
    ofstream _stream;
    
    T * _buffer;
    
    int _size;
    
    int _current;
    
};

template <typename T>
class sbreader {
public:
    
    sbreader(const std::string &filename, const int size) :
        _stream(filename.c_str(), std::ios::binary | std::ios::in)
    {
        _buffer = (T*) malloc(sizeof(T) * size);
        _content = 0;
        _current = 0;
        _size = size;
    }
    
    ~sbreader()
    {
        _stream.close();
        free(_buffer);
    }
    
    void get(T &t)
    {
        if (_current == _content) {
            _stream.read((char*) _buffer, sizeof(T) * _size);
            _content = _stream.gcount() / sizeof(T);
            _current = 0;
            
            if (_current == _content) 
                throw SBIOException();
        }
        
        t = _buffer[_current++];
    }
    
    bool good()
    {
        return _stream.good();
    }
    
private:
    
    ifstream _stream;
    
    T * _buffer;
    
    int _size;
    
    int _current;
    
    int _content;
    
};

#endif /* SBIO_HPP */

