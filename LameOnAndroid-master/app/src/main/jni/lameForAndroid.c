#include <stdio.h>
#include "com_example_lameonandroid_activity_SongList.h"
#include "lame.h"
#include <android/log.h>
#define LOG_TAG "System.out.c"
#include <sys/stat.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

char* Jstring2CStr(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
                                        "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid,
                                                           strencode); // String .getByte("GB2312");
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1); //"\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
    return rtn;
}

JNIEXPORT jstring JNICALL Java_com_example_lameonandroid_activity_SongList_getLameVersion
        (JNIEnv * env , jobject obj ){
    //const char*  CDECL get_lame_version       ( void );
    const char* versionName = get_lame_version();
    return (*env) -> NewStringUTF(env, versionName); //jstring     (*NewStringUTF)(JNIEnv*, const char*);
}


JNIEXPORT void JNICALL Java_com_example_lameonandroid_activity_SongList_convert
(JNIEnv * env, jobject obj, jstring jwav, jstring jmp3){
    //init lame
    lame_global_flags* gfp = lame_init();
    lame_set_num_channels(gfp, 2);//设置声道数
    lame_set_in_samplerate(gfp, 48000);//设置采样率(这里很重要，必须跟音频源一致)
    lame_set_brate(gfp, 128);//设置比特率
    lame_set_mode(gfp,0);//* mode = 0,1,2,3 = stereo,jstereo,dualchannel(not supported),mono defaul
    lame_set_quality(gfp,2);   /* 2=high  5 = medium  7=low */
    // 3. 设置MP3的编码方式
    lame_set_VBR(gfp, vbr_max_indicator);
    LOGE("init lame finished ...");
    int initStatusCode = lame_init_params(gfp);
    if(initStatusCode >= 0){
    //将Java的字符串转成C的字符串
    char* cwav = Jstring2CStr(env, jwav);
    char* cmp3 = Jstring2CStr(env, jmp3);

    FILE* fwav = fopen(cwav, "rt");
    FILE* fmp3 = fopen(cmp3, "wb");
    //获取文件总长度
    int length = get_file_size(cwav);
    LOGE("length = %d\n ",length);
    //每次读取的数据长度
    const int WAV_SIZE = 8192*2;//在模拟信号中每秒取8192*4信号点
    const int MP3_SIZE = 8192*2;
    short int wav_buffer[WAV_SIZE*2];//这里乘以2是因为取双声道，同样也需要考虑到压缩率
    unsigned char mp3_buffer[MP3_SIZE];
    int read, write, total = 0;
    do{
        //从fwav中读取数据缓存到wav_buffer，每次读取sizeof(short int)*2，读8192次,
        // 取出数据长度sizeof(short int)*2*WAV_SIZE
        read = fread(wav_buffer,sizeof(short int)*2,WAV_SIZE,fwav);
        if(read != 0){
        total += read* sizeof(short int)*2;
        publishJavaProgress(env, obj, total);
        //第三个参数表示:每个通道取的数据长度
        write = lame_encode_buffer_interleaved(gfp,wav_buffer,WAV_SIZE,mp3_buffer,MP3_SIZE);
        LOGE("write=%d\n ",write);
    }else{
        //读到末尾
        write = lame_encode_flush(gfp,mp3_buffer,MP3_SIZE);
    }
        //将转换后的数据缓存mp3_buffer写到fmp3文件里
        fwrite(mp3_buffer,sizeof(unsigned char),write,fmp3);
    }while(read != 0);

    lame_close(gfp);
    fclose(fwav);
    fclose(fmp3);
    LOGE("convert completed");
    }

}
//获取文件长度，有限制，且是简介获取（读内存）
int file_size(FILE* fp)
{
    //FILE *fp=fopen(filename,"r");
    if(!fp) return -1;
    fseek(fp,0L,SEEK_END);
    int size=ftell(fp);
    fclose(fp);
    return size;
}
//获取文件长度
int get_file_size(char* filename)
{
    struct stat statbuf;
    stat(filename,&statbuf);
    int size=statbuf.st_size;
    return size;
}
jclass clazz = 0;
jmethodID methodid = 0;
void publishJavaProgress(JNIEnv * env, jobject obj, jint progress) {
    // 调用java代码 更新程序的进度条
    // 1.找到java的LameActivity的class
    if(clazz == 0){
        //注意这里初始化clazz会分配一块内存，不能重复初始化，否则易导致内存溢出
        clazz = (*env)->FindClass(env, "com/example/lameonandroid/activity/SongList");
    }
    if (clazz == 0) {
    LOGI("can't find clazz");
    }
    LOGI(" find clazz");
    //2 找到class 里面的方法定义
    //    jmethodID   (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
    if(methodid == 0){
        methodid = (*env)->GetMethodID(env, clazz, "updateProgress", "(I)V");
    }
    if (methodid == 0) {
    LOGI("can't find methodid");
    }
    LOGI(" find methodid");
    //  jclass      (*FindClass)(JNIEnv*, const char*);
    (*env)->CallVoidMethod(env, obj, methodid, progress);
}


