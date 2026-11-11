#include <jni.h>
#include <oboe/Oboe.h>
#include <android/log.h>

#define TAG "VoiceEngineNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

class VoiceEngine : public oboe::AudioStreamCallback {
public:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        int32_t channelCount = oboeStream->getChannelCount();
        int16_t *pcmData = static_cast<int16_t *>(audioData);

        for (int i = 0; i < numFrames * channelCount; ++i) {
            int sample = pcmData[i] * 1.15f; 
            if (sample > 32767) sample = 32767;
            if (sample < -32768) sample = -32768;
            pcmData[i] = static_cast<int16_t>(sample);
        }
        return oboe::DataCallbackResult::Continue;
    }
};

static VoiceEngine g_engine;
static std::shared_ptr<oboe::AudioStream> g_stream;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_app_voicechanger_audio_VoiceModulator_startEngine(JNIEnv *env, jobject thiz) {
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Input);
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);
    builder.setFormat(oboe::AudioFormat::I16);
    builder.setChannelCount(oboe::ChannelCount::Mono);
    builder.setCallback(&g_engine);

    oboe::Result result = builder.openStream(g_stream);
    if (result != oboe::Result::OK) {
        LOGI("Failed to open audio stream: %s", oboe::convertToText(result));
        return JNI_FALSE;
    }
    
    result = g_stream->requestStart();
    return (result == oboe::Result::OK) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_app_voicechanger_audio_VoiceModulator_stopEngine(JNIEnv *env, jobject thiz) {
    if (g_stream) {
        g_stream->requestStop();
        g_stream->close();
        g_stream.reset();
    }
}
