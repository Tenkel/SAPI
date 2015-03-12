
extern "C" JNIEXPORT jlong JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1create
    (JNIEnv *env, jobject self, jint inputBits, jint numRams)
{
    return (jlong) new ZWisard((int) inputBits, (int) numRams);
}

extern "C" JNIEXPORT void JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1learn
    (JNIEnv *env, jobject self, jlong ptr, jintArray pattern, jint target)
{
    ((ZWisard*) ptr)->learn(IntArray(env, pattern), target);
}

extern "C" JNIEXPORT int JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1read
    (JNIEnv *env, jobject self, jlong ptr, jintArray pattern)
{
	//return ((ZWisard*) ptr)->readCounts(IntArray(env, pattern));
    return ((ZWisard*) ptr)->readBleaching(IntArray(env, pattern), 1, 0.005);
    //return ((ZWisard*) ptr)->readBinaryBleaching(IntArray(env, pattern));
}

extern "C" JNIEXPORT jfloat JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1getConfidence
    (JNIEnv *env, jobject self, jlong ptr)
{
    return ((ZWisard*) ptr)->getConfidence();
}

extern "C" JNIEXPORT jfloat JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1getActivation
    (JNIEnv *env, jobject self, jlong ptr, jint target)
{
    return ((ZWisard*) ptr)->getExcitation(target);
}

extern "C" JNIEXPORT void JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1destroy
    (JNIEnv *env, jobject self, jlong ptr)
{
    delete (ZWisard*) ptr;
}

extern "C" JNIEXPORT void JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1exportTo
    (JNIEnv *env, jobject self, jlong ptr, jstring filename)
{
    const char *nativeString = env->GetStringUTFChars(filename, NULL);
    sbwriter<int> writer(nativeString, 1024*1024);
    
    if (writer.good()) {
        try {
            ((ZWisard*) ptr)->exportTo(writer);
            LOGE("Export succeeded");
        } catch (SBIOException e) {
            LOGE("Could not create output file \'%s\'", nativeString);
        }
    } else {
        LOGE("Could not create output file \'%s\'", nativeString);
    }
    
    env->ReleaseStringUTFChars(filename, nativeString);
}

extern "C" JNIEXPORT jlong JNICALL
Java_br_ufrj_cos_labia_aips_ips_whips_Wisard_native_1importFrom
    (JNIEnv *env, jobject self, jstring filename)
{
    const char *nativeString = env->GetStringUTFChars(filename, NULL);
    sbreader<int> reader(nativeString, 1024*1024);
    
    if (reader.good()) {
        try {
            ZWisard *w = new ZWisard(reader);
            env->ReleaseStringUTFChars(filename, nativeString);
            LOGE("Import succeeded");
            return (jlong) w;
        } catch (SBIOException e) {
            LOGE("Could not import ZWisard from \'%s\'", nativeString);
        }
    } else {
        LOGE("Could not import ZWisard from \'%s\'", nativeString);
    }
    
    return (jlong) NULL;
}

