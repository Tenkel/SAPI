#ifndef ZWISARD_HPP
#define ZWISARD_HPP

#include "sbio.hpp"
#include "addressdecoder.hpp"

typedef std::map<int, int> innermap;
typedef std::map<int, innermap> outtermap;

class ZWisard {
public:
    
    ZWisard(const int inputBits, const int ramBits) : 
        _decoder(inputBits, ramBits), _confidence(1.0), 
        _memory(NULL), _maxBleaching(1), _activations(NULL), 
        _numDiscriminators(8)
    {
        _activations = new int[_numDiscriminators]();
        _memory = new outtermap[_decoder.numRams()]();
    }
    
    ZWisard(sbreader<int> &reader) : _decoder(reader), _confidence(1.0), 
        _memory(NULL), _maxBleaching(1), _activations(NULL), 
        _numDiscriminators(0)
    {
        // Número de verificação
        int tmp = 0;
        reader.get(tmp);
        if (tmp != -1) throw SBIOException();
        
        // Atributos simples
        reader.get(_maxBleaching);
        reader.get(_numDiscriminators);
        
        try {
            
            int key, length, target, hits;
                
            // Aloca os vetores internos
            _activations = new int[_numDiscriminators]();
            _memory = new outtermap[_decoder.numRams()]();
            
            // Para cada ram
            for (int r=0;r<_decoder.numRams();++r) {
                int keys;
                reader.get(keys);
                outtermap &omap = _memory[r];
                //LOGE("I: Ram %d has %d STARTED", r, keys);
            
                // Para cada chave nesta ram
                for (int k=0;k<keys;++k) {
                    reader.get(key);
                    reader.get(length);
                    
                    //LOGE("I: Ram %d Key %d", r, key);
                    innermap &imap = omap[key];
                    for (int t=0;t<length;++t) {
                        reader.get(target);
                        reader.get(hits);
                        imap[target] = hits;
                    }
                }
                
                //LOGE("I: Ram %d has %d ENDED", r, keys);
            }
            
            // Se algo der errado limpe os vetores
        } catch (SBIOException e) {
            delete [] _memory;
            delete [] _activations;
            throw e;
        }
        
        // Número de verificação no final
        tmp = 0;
        reader.get(tmp);
        if (tmp != -1) throw SBIOException();
    }

    ~ZWisard()
    {
        delete [] _memory;
        delete [] _activations;
    }

    void exportTo(sbwriter<int> &writer)
    {
        _decoder.exportTo(writer);

        // Número de verificação inicial        
        int tmp = -1;
        writer.put(tmp);
        
        // Atributos simples
        writer.put(_maxBleaching);
        writer.put(_numDiscriminators);
        
        // Para cada ram
        for (int r=0;r<_decoder.numRams();++r) {
            outtermap &omap = _memory[r];
            
            int keys = omap.size();
            writer.put(keys);
            //LOGE("E: Ram %d has %d", r, _memory[r].size());
            
            // Para cada posição nela endereçada
            outtermap::const_iterator it;
            for (it=omap.begin(); it!=omap.end();++it) {
                const innermap &imap = it->second;
                
                writer.put(it->first);
                writer.put(imap.size());
                
                //LOGE("E: Ram %d Key %lld", r, it->first);
                
                // Salva as caixas
                innermap::const_iterator it2;
                for (it2=imap.begin(); it2 != imap.end();++it2) {
                    writer.put(it2->first);
                    writer.put(it2->second);
                }
            }
        }
        
        // Número de verificação final
        writer.put(tmp);
    }
    
    void learn(const IntArray &pattern, const int target) 
    {
        // Decodifica o pattern
        const int * const addresses = _decoder.read(pattern);
        
        // Redimensiona o tamanho dos vetores de classes
		if (target >= _numDiscriminators) {
			_numDiscriminators = target * 2;
			delete [] _activations;
			_activations = new int[_numDiscriminators];
		}
		
		// Para cada RAM
		for (int r=0;r<_decoder.numRams();++r) {
			outtermap &omap = _memory[r];
			innermap &imap = omap[addresses[r]];
			
			// Se a posição endereçada não foi alocada
			if (imap.find(target) == imap.end())
			    imap[target] = 1;
			else
			    imap[target] = imap[target] + 1;
			
			// Incrementa maxBleaching se necessario
			if (imap[target] > _maxBleaching)
			    _maxBleaching = imap[target];
		}
    }

	int readCounts(const IntArray &pattern)
	{
		// Decodifica o pattern
		const int * const addresses = _decoder.read(pattern);

		// Limpa o vetor de ativações
		for (int i=0;i<_numDiscriminators;++i)
			_activations[i] = 0;

		// Para cada RAM
		for (int r=0;r<_decoder.numRams();++r) {
			outtermap &omap = _memory[r];

			// Se não contém o endereço mapeado continua
			if (omap.find(addresses[r]) == omap.end())
				continue;

			// Do contrario incrementa as ativações
			innermap::const_iterator it;
			innermap &imap = omap[addresses[r]];
			for (it=imap.begin(); it!= imap.end();++it)
				_activations[it->first] += it->second;
		}

		// Retorna o discriminador mais ativado, calculando a confiança
		return indexOfMax(_activations, _numDiscriminators);
	}

    int readBinary(const IntArray &pattern) 
    {
        return readThreshold(_decoder.read(pattern), 1);
    }
    
    int readBleaching(const IntArray &pattern, const int step, 
        const float minConfidence) 
    {
        // Decodifica o pattern
        const int * const addresses = _decoder.read(pattern);
        
		int bestPredicted    = 0;
		float bestConfidence = 0;

		// Aplica o bleaching
		for (int t=1;t<=_maxBleaching;t+=step) {
			const int predicted    = readThreshold(addresses, t);
			const float confidence = getConfidence();
			
			// Se atingiu a confiança mínima retorne a resposta
			if (confidence > minConfidence)  {
				return predicted;
			}
			
			// Se for a de maior confiança até agora guarde-a
			if (confidence > bestConfidence) {
				bestConfidence = confidence;
				bestPredicted  = predicted;
			}
		}
		
		// Se nenhum deles atingiu a confiança mínima, 
		// retorne o melhor encontrado
	    //LOGI("Predicted %d", bestPredicted);
	    if (bestPredicted == -1) 
	        return 0;
        else
    		return bestPredicted;
    }

    int readBinaryBleaching(const IntArray &pattern) 
    {
        // Decodifica o pattern
        const int * const addresses = _decoder.read(pattern);
        
        if (_maxBleaching == 1)
            return readThreshold(addresses, 1);
        
        int begin = 1;
        int end = _maxBleaching;
        int current = _nextBinaryStep(begin, end);
        
        double weightBegin = _activations[readThreshold(addresses, begin)];
        double weightCurrent = _activations[readThreshold(addresses, current)];
        
        double weightOne = weightBegin;
        
        while(begin != end - 1) {
            if (weightCurrent == weightOne) {
                begin = current;
                weightBegin = weightCurrent;
            } else {
                end = current;
            }
            
            current = _nextBinaryStep(begin, end);
            weightCurrent = _activations[readThreshold(addresses, current)];
        }
        
        return getFirstBestPrediction();
    }
    
    float getConfidence() const
    {
        return _confidence;
    }
    
    float getExcitation(const int target) const
    {
        if (target >= _numDiscriminators)
            return 0;
        else
            return _activations[target] / float(_decoder.numRams());
    }
    
    int getFirstBestPrediction() const
    {
        return _k1;
    }
    
    int getSecondBestPrediction() const
    {
        return _k2;
    }
    
    int getThirdBestPrediction() const
    {
        return _k3;
    }
    
private:
    
    int _nextBinaryStep(const int begin, const int end) const 
    {
        const int r = (int) round(sqrt((float) (begin * end)));
        if (r == begin)  return begin + 1;
        if (r == end) return end - 1;
        return r;
    }
    
    int readThreshold(const int * const addresses, const int threshold) 
    {
		// Limpa o vetor de ativações
		for (int i=0;i<_numDiscriminators;++i)
			_activations[i] = 0;
		
		// Para cada RAM
		for (int r=0;r<_decoder.numRams();++r) {
			outtermap &omap = _memory[r];
			
			// Se não contém o endereço mapeado continua
			if (omap.find(addresses[r]) == omap.end()) 
			    continue;
		    
		    // Do contrario incrementa as ativações
			innermap::const_iterator it;
			innermap &imap = omap[addresses[r]];
			for (it=imap.begin(); it!= imap.end();++it)
			    if (it->second >= threshold)
			        ++_activations[it->first];
		}
		
		// Retorna o discriminador mais ativado, calculando a confiança 
		return indexOfMax(_activations, _numDiscriminators);
    }
    
    int indexOfMax(const int * const array, const int length)
    {
		_k1 = -1;
		_k2 = -1;
		_k3 = -1;
		
		for (int i=0;i<length;++i)
			if (_k1 == -1 || array[i] > array[_k1] )
				_k1 =  i;
		
		for (int i=0;i<length;++i)
		    if (i != _k1 && (_k2 == -1 || array[i] > array[_k2]))
		        _k2 = i;
        
		for (int i=0;i<length;++i)
		    if (i != _k1 && i != _k2 && (_k3 == -1 || array[i] > array[_k3]))
		        _k3 = i;
        
		if (_k1 == -1 || _k2 == -1) 
			_confidence = 1;
		else 
			_confidence = (float) (array[_k1] - array[_k2]) / (array[_k1]);
		
		return _k1;
	}
    
private:

    AddressDecoder<int> _decoder;
    
    float _confidence;
    
    outtermap *_memory;
    
    int _maxBleaching;
    
    int *_activations;
    
    int _numDiscriminators;
    
    int _k1;
    
    int _k2;
    
    int _k3;
    
};

#endif /* ZWISARD_HPP */

