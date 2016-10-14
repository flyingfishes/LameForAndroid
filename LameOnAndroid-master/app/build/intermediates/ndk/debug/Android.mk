LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := lame
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \
	-lz \
	-lm \

LOCAL_SRC_FILES := \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\bitstream.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\encoder.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\fft.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\gain_analysis.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\id3tag.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\lame.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\lameForAndroid.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\mpglib_interface.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\newmdct.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\presets.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\psymodel.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\quantize.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\quantize_pvt.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\reservoir.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\set_get.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\tables.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\takehiro.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\util.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\vbrquantize.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\VbrTag.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\vector\xmm_quantize_sub.c \
	E:\Hi3798\LameOnAndroid-master\app\src\main\jni\version.c \

LOCAL_C_INCLUDES += E:\Hi3798\LameOnAndroid-master\app\src\main\jni
LOCAL_C_INCLUDES += E:\Hi3798\LameOnAndroid-master\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
