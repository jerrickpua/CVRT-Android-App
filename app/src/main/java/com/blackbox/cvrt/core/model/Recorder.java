package com.blackbox.cvrt.core.model;

/**
 * @author Jerrick Pua
 */
@Deprecated
public interface Recorder {

    public Record stop();

    public void start();

    public boolean isRecording();

    public void cleanUp();

}
