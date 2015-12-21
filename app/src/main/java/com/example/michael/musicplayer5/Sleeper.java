package com.example.michael.musicplayer5;

/**
 * Created by michael on 12/20/15.
 */
public class Sleeper {

    public void sleep(int ms){

        //do something

        //sleep for 3000ms (approx)
        long timeToSleep = ms;
        long start, end, slept;
        boolean interrupted=false;

        while(timeToSleep > 0){
            start=System.currentTimeMillis();
            try{
                Thread.currentThread().sleep(timeToSleep);
                break;
            }
            catch(InterruptedException e){

                //work out how much more time to sleep for
                end=System.currentTimeMillis();
                slept=end-start;
                timeToSleep-=slept;
                interrupted=true;
            }
        }

        if(interrupted){
            //restore interruption before exit
            Thread.currentThread().interrupt();
        }
    }
}
