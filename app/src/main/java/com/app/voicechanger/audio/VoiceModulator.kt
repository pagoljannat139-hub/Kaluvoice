package com.app.voicechanger.audio

class VoiceModulator {
    companion object {
        init {
            System.loadLibrary("voice_engine")
        }
    }

    external fun startEngine(): Boolean
    external fun stopEngine()
}
