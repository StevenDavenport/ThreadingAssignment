package CO3401;

import java.util.HashMap;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Turntable extends Thread
{
    String id;  // identity of this table
    Present p = null;   // gift on the turntable
    private int pollIndex;  // index of which port is ready to have a gift extracted
    private final AtomicBoolean stopRequested;  // flag indicating if the thread has been requested to stop

    // variables true to all threads, static
    static int N = 0;
    static int E = 1;
    static int S = 2;
    static int W = 3;

    // connections that the turntable has with its surroundings
    Connection[] connections = new Connection[4];

    // global lookup: age-range -> SackID
    static HashMap<String, Integer> destinations = new HashMap<>();

    // this individual table's lookup: SackID -> output port
    HashMap<Integer, Integer> outputMap = new HashMap<>();

    // orientation of the turntable "belt"
    int[] portDirection;

    // constructor
    public Turntable (String ID)
    {
        id = ID;
        portDirection = new int[2];
        portDirection[0] = N;
        portDirection[1] = S;
        pollIndex = 0;
        stopRequested = new AtomicBoolean(false);
    }

    // Nick's code, unreadable and bloated... ;)
    public void addConnection(int port, Connection conn)
    {
        connections[port] = conn;

        if(conn != null)
        {
            if(conn.connType == ConnectionType.OutputBelt)
            {
                for (Integer destination : conn.belt.destinations)
                {
                    outputMap.put(destination, port);
                }
            }
            else if(conn.connType == ConnectionType.OutputSack)
            {
                outputMap.put(conn.sack.id, port);
            }
        }
    }

    // requests the thread to stop
    public void requestStop()
    {
        stopRequested.set(true);
    }

    // find a present ready to be extracted at an input port
    private void poll()
    {
        while (true)
        {
            if (pollIndex < 3)
            {
                pollIndex++;
            }
            else
            {
                pollIndex = 0;
            }
            if (connections[pollIndex] != null && connections[pollIndex].connType == ConnectionType.InputBelt)
            {
                if (connections[pollIndex].belt.getSize() > 0)
                {
                    return;
                }
            }
        }

    }

    // returns true if an input belt is connected and can accept present
    private boolean correctOrientationChecker(int index)
    {
        for (int i = 0; i < portDirection.length; ++i)
        {
            if (portDirection[i] == index)
            {
                return true;
            }
        }
        return false;
    }

    // turns the turntable
    private void turnTheTable()
    {
        // swap the orientations
        if (portDirection[0] == N && portDirection[1] == S)
        {
            portDirection[0] = E;
            portDirection[1] = W;
        }
        else
        {
            portDirection[0] = N;
            portDirection[1] = S;
        }

        // Sleep to imitate movement
        tableSleep(500);
    }

    // returns false if the table needs to be turned to release the present
    private boolean checkDestination(int i, int j)
    {
        return i + j % 2 == 0;
    }

    // release the present to its destination
    private void releasePresent(int outputPort)
    {
        if (connections[outputPort].connType == ConnectionType.OutputBelt)
        {
            // if output destination is a belt
            connections[outputPort].belt.insert(p);
        }
        else
        {
            // if output destination is  a sack
            connections[outputPort].sack.insert(p);
        }

        // Sleep to imitate movement
        tableSleep(750);
    }

    // Takes the present from the available input belt
    private void takePresent(int index)
    {
        // take the present
        p = connections[index].belt.extract();

        // Wait to imitate movement
        tableSleep(750);
    }

    // Sleep function used to imitate movement
    private void tableSleep(int milisecs)
    {
        try
        {
            sleep(milisecs);
        }
        catch (InterruptedException e) {}
    }

    private void resolve()
    {
        // While there are no available presents keep looping in poll
        poll();

        // Does the table need to turn to accept the present
        if (!correctOrientationChecker(pollIndex))
        {
            // if so turn the table
            turnTheTable();
        }

        // Extract from inputBelt
        takePresent(pollIndex);

        // Variable used to store the index of the destination
        int outputIndex = outputMap.get(destinations.get(p.readDestination()));

        // Does the output port connect to the required destination
        if (!checkDestination(pollIndex, outputIndex))
        {
            // if not, turn the table
            turnTheTable();
        }

        // Give the output port the present
        releasePresent(outputIndex);
    }

    // thread driver function
    public void run()
    {
        // repeatedly try and insert/extract gifts, sleeps if cant
        // runs until this thread has been requested to stop
        while (!stopRequested.get())
        {
            try
            {
                sleep((int)(Math.random() * 5));
            }
            catch (InterruptedException ex) {}

            // perform the action of taking and giving the present
            resolve();
        }
    }
}
