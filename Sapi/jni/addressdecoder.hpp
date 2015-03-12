#ifndef __ADDRESSDECODER
#define __ADDRESSDECODER

#include <iostream>
#include <cstring>
#include <cmath>

using namespace std;

template <typename Key>
class AddressDecoder {

public:

    AddressDecoder(const int inputs, const int bits) : 
            _inputs(inputs), _bits(bits)
    {
        _numRams = (int) ceil(float(inputs) / float(bits));
        _output = new Key[_numRams];
        _shuffling = randperm(inputs);
    }
    
    AddressDecoder(sbreader<int> &reader)
    {
        int tmp;
        reader.get(tmp);
        if (tmp != -1) throw SBIOException();
        
        reader.get(_numRams);
        reader.get(_inputs);
        reader.get(_bits);
        
        _output = new Key[_numRams];
        _shuffling = new int[_inputs];
        
        try {
            for (int i=0;i<_inputs;++i) {
                reader.get(_shuffling[i]);
            }
        } catch (SBIOException e) {
            delete [] _shuffling;
            delete [] _output;
            throw e;
        }
        
        reader.get(tmp);
        if (tmp != -1) throw SBIOException();
    }
    
    ~AddressDecoder()
    {
        delete [] _shuffling;
        delete [] _output;
    }

    void exportTo(sbwriter<int> &writer) const 
    {
        int tmp = -1;
        writer.put(tmp);
        
        writer.put(_numRams);
        writer.put(_inputs);
        writer.put(_bits);
        
        for (int i=0;i<_inputs;++i)
            writer.put(_shuffling[i]);
        
        writer.put(tmp);
    }
    
    const Key * const read(const IntArray &pattern)
    {	
        Key address;
        int k = 0;

        for (int r=0;r<_numRams;++r) {
            address = 0;

            const int top = _inputs < (k+_bits) ? _inputs : (k+_bits);
            for(;k<top;++k) {
                const int bit = pattern[_shuffling[k]] == 0 ? 0 : 1;
                address = (address << 1) + bit;
            }
            
            _output[r] = address;
        }

        return _output;
    }

    int numRams() const
    {
        return _numRams;
    }

    int bits() const
    {
        return _bits;
    }
    
    int inputs() const
    {
        return _inputs;
    }

private:
    
    int * randperm(const int n) const
    {
        int * indexes = new int[n];
        int r, t;
        
        for (int i=0;i<n;++i)
            indexes[i] = i;

        for (int i=0;i<n;++i) {
            r = rand() % (n - i);
            t = indexes[r];
            indexes[r] = indexes[n-i-1];
            indexes[n-i-1] = t;
        }

        return indexes;
    }

private:

    int _inputs;

    int _numRams;

    int _bits;

    int * _shuffling;

    Key * _output;

};

#endif /* __ADDRESSDECODER */

