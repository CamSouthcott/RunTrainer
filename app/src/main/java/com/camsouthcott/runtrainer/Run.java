package com.camsouthcott.runtrainer;

/**
 * Created by Cam Southcott on 12/23/2015.
 */
public class Run {
    private int runID, runTime, runInterval, walkInterval;
    private long runDate;
    private Integer globalRunID;

    public Run(int runID, Integer globalRunID, int runTime, long runDate, int runInterval, int walkInterval){
        this.runID = runID;
        this.globalRunID = globalRunID;
        this.runTime = runTime;
        this.runDate = runDate;
        this.runInterval = runInterval;
        this.walkInterval = walkInterval;
    }

    public int getRunID(){
        return runID;
    }

    public Integer getGlobalRunID(){
        return globalRunID;
    }

    public int getRunTime(){
        return runTime;
    }

    public long getRunDate(){
        return runDate;
    }

    public int getRunInterval(){
        return runInterval;
    }

    public int getWalkInterval(){
        return walkInterval;
    }
}
