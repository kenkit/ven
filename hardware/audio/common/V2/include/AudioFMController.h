#ifndef ANDROID_AUDIO_FM_CONTROLLER_H
#define ANDROID_AUDIO_FM_CONTROLLER_H

#include <utils/threads.h>

#include "AudioType.h"

namespace android
{

// AudioMTKHardware.cpp & FMAudioPlayer.cpp need this structure!!
typedef struct _AUDIO_DEVICE_CHANGE_CALLBACK_STRUCT
{
    void (*callback)(void *data);
} AUDIO_DEVICE_CHANGE_CALLBACK_STRUCT;

class AudioFMResourceManager;
class AudioMTKStreamManagerInterface;

class AudioFMController
{
    public:
        virtual ~AudioFMController();

        static AudioFMController *GetInstance();

        virtual bool     GetFmEnable();
        virtual status_t SetFmEnable(const bool enable,bool bForceControl = false, bool bForce2DirectConn = false, bool bNeedSyncVolume = false, bool bSkipHwLock = false);

        virtual uint32_t GetFmUplinkSamplingRate() const;
        virtual uint32_t GetFmDownlinkSamplingRate() const;

        virtual status_t ChangeDevice(const audio_devices_t new_device);

        virtual status_t SetFmVolume(const float fm_volume);

        virtual status_t SetFmChipInitialization();

        virtual bool     GetFmChipPowerInfo();
        virtual void     SetFmDeviceCallback(const AUDIO_DEVICE_CHANGE_CALLBACK_STRUCT *callback_data);

        virtual void     setUseFmDirectConnectionMode(const bool use_direct_mode) { mUseFmDirectConnectionMode = use_direct_mode; }
        virtual bool     CheckFmNeedUseDirectConnectionMode();
    protected:
        AudioFMController();

        virtual status_t SetFmDirectConnection(const bool enable, const bool bforce);

        void (*mFmDeviceCallback)(void *data);
        virtual status_t DoDeviceChangeCallback();
        Mutex mLock;

        AudioFMResourceManager         *mAudioFMResourceManager;
        AudioMTKStreamManagerInterface *mAudioMTKStreamManager;

        bool mFmEnable;
        bool mIsFmDirectConnectionMode;
        bool mUseFmDirectConnectionMode;
        
        float mFmVolume;

    private:
        static AudioFMController *mAudioFMController; // singleton

};

} // end namespace android

#endif // end of ANDROID_AUDIO_FM_CONTROLLER_H
