#ifndef JNIUTILS_HPP
#define JNIUTILS_HPP

#define LOG_TAG "Native"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);

class IntArray {
public:

    IntArray(JNIEnv *env, jintArray array) 
    {
        _env      = env;
        _array    = array;
        _intArray = env->GetIntArrayElements(array, NULL);
    }
    
    ~IntArray()
    {
        _env->ReleaseIntArrayElements(_array, _intArray, 0);
    }
    
    int operator[](const int index) const
    {
        return _intArray[index];
    }
    
private:
    
    JNIEnv * _env;
    
    jintArray _array;
    
    int * _intArray;
    
};

#endif /* JNIUTILS_HPP */

