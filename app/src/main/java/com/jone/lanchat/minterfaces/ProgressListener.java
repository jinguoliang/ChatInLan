package com.jone.lanchat.minterfaces;

/**
 * Created by xanarry on 2016/5/23.
 */
public interface ProgressListener {
    void updateProgress(int filePositon, long hasGot, long totalSize, int speed);
}