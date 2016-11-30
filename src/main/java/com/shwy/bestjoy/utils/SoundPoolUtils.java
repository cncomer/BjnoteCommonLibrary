package com.shwy.bestjoy.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by bestjoy on 15/2/28.
 */
public class SoundPoolUtils {

    private Context mContext;
    //定义HASH表
    private HashMap<Integer, Integer> soundPoolMap;
    //音效的音量
    int mStreamVolume;
    //定义SoundPool 对象
    private SoundPool mSoundPool;

    private SoundPoolUtils(){}

    private static final SoundPoolUtils INSTANCE = new SoundPoolUtils();

    public static SoundPoolUtils getInstance() {
        return INSTANCE;
    }

    public SoundPool getSoundPool() {
        return mSoundPool;
    }

    public void setContext(Context context) {
        mContext = context;
        initSounds();
    }

    public void initSounds() {
        //初始化soundPool 对象,第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
        mSoundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);

        //初始化HASH表
        soundPoolMap = new HashMap<Integer, Integer>();

        //获得声音设备和设备音量
        AudioManager mgr = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mStreamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void loadSfx(int soundRawId, int priority) {
        //把资源中的音效加载到指定的ID(播放的时候就对应到这个ID播放就行了)
        soundPoolMap.put(soundRawId, mSoundPool.load(mContext, soundRawId, priority));
    }

    public int getSampleId(int soundId) {
        return soundPoolMap.get(soundId);
    }

    public boolean unload(int soundId) {
        return mSoundPool.unload(soundPoolMap.get(soundId));
    }

    public void resume(int streamId) {
         mSoundPool.resume(streamId);
    }

    public void pause(int streamId) {
        mSoundPool.pause(streamId);
    }

    /**
     *
     * @param soundId
     * @param uLoop loop mode (0 = no loop, -1 = loop forever)
     * @param rate
     * @return
     */
    public int play(int soundId, int uLoop, float rate) {

        return mSoundPool.play(soundPoolMap.get(soundId), mStreamVolume, mStreamVolume, 1, uLoop, rate);
    }

    /**
     * Stop a playback stream. Stop the stream specified by the streamID.
     * This is the value returned by the play() function. If the stream is playing, it will be stopped.
     * It also releases any native resources associated with this stream.
     * If the stream is not playing, it will have no effect.
     a streamID returned by the play() function
     * @param streamID a streamID returned by the play() function
     */
    public void stop(int streamID) {
        mSoundPool.stop(streamID);
    }
}
