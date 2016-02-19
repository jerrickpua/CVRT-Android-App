package com.blackbox.cvrt.core.service.recorder;

import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * @author Jerrick Pua
 */
public interface RecorderService {

    public static final String DEFAULT_RECORDINGS_FOLDER = "recordings";

    public static final int DEFAULT_SAMPLE_RATE = AudioTrack
            .getNativeOutputSampleRate( AudioManager.STREAM_SYSTEM );

    public static final int DEFAULT_INTERVAL = 300000;

    public void startRecord( int interval );

    public void stopRecord();

    public void cleanUp();


}
