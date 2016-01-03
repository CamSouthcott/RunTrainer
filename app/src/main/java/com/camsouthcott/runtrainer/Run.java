package com.camsouthcott.runtrainer;

/**
 * Created by Cam Southcott on 12/23/2015.
 */
public class Run {
    private int runID, runTime, runInterval, walkInterval;
    private String runDate;

    public Run(int runID, int runTime, String runDate, int runInterval, int walkInterval){
        this.runID = runID;
        this.runTime = runTime;
        this.runDate = runDate;
        this.runInterval = runInterval;
        this.walkInterval = walkInterval;
    }

    public int getRunID(){
        return runID;
    }

    public int getRunTime(){
        return runTime;
    }

    public String getRunDate(){
        return runDate;
    }

    public int getRunInterval(){
        return runInterval;
    }

    public int getWalkInterval(){
        return walkInterval;
    }
}
