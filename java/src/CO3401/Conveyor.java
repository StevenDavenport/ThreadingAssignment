package CO3401;

import java.util.HashSet;

public class Conveyor {
    private int id; // Identity of this conveyer belt
    private Present[] presents; // The requirements say this must be a fixed size array
    public HashSet<Integer> destinations = new HashSet(); // hashmap storing destinations
    private int nextIn; // index of where the next item to be inserted will be placed
    private int nextOut; // index of where next item to be extracted will be taken from
    private int available; // available slots

    // constructor
    public Conveyor(int id, int size)
    {
        this.id = id;
        presents = new Present[size];
        available = 0;
        nextIn = 0;
        nextOut = 0;
    }

    // ads a destination to the hashmap
    public void addDestination(int sackID)
    {
        destinations.add(sackID);
    }

    public int getSize()
    {
        return available;
    }

    // used to insert a present on the belt, only one thread can access this at a time
    // critical region
    public synchronized void insert(Present p)
    {
        // sleep while there is no space to insert
        while (available == presents.length)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ex) {}
        }

        // insert the present
        presents[nextIn] = p;
        available++;
        try
        {
            Thread.sleep((int) (Math.random() * 10));
        }
        catch (InterruptedException ex) {}
        nextIn++;

        // move next in to the start, circular queue
        if (nextIn == presents.length)
        {
            nextIn = 0;
        }

        // when complete, notify other threads
        notifyAll();
    }

    // used to extract a present from the belt, only one thread can access this at a time
    // critical region
    public synchronized Present extract()
    {
        // Gift to be extracted
        Present p = null;

        // sleep until there is a gift available
        while (available == 0)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ex) {}
        }

        // assign the gift
        p = presents[nextOut];
        try
        {
            Thread.sleep((int) (Math.random() * 10));
        }
        catch (InterruptedException ex) {}

        available--;
        nextOut++;

        // move next out back to the start, circular queue
        if (nextOut == presents.length)
        {
            nextOut = 0;
        }

        // when complete notify other threads
        notifyAll();

        // extract gift
        return p;
    }
}