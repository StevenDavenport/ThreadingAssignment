package CO3401;

import java.util.concurrent.atomic.AtomicBoolean;

public class Hopper extends Thread
{
    private int id; // identity of this hopper
    private Conveyor belt; // the belt that this hopper produces to
    private int speed; // speed of this hopper
    private int size; // number of items currently in the hopper
    private Present[] collection; // array storing the gifts in the hopper
    private int startSize; // the number of items in the hoppers at the start if the simulation
    private final AtomicBoolean stopRequested; // flag indicating if the thread has been asked to stop

    // constructor
    public Hopper(int id, Conveyor con, int capacity, int speed)
    {
        collection = new Present[capacity];
        this.id = id;
        belt = con;
        this.speed = speed;
        size = 0;
        startSize = 0;
        stopRequested = new AtomicBoolean(false);
    }

    // returns the number of gifts deposited by this hopper
    public int getDeposited()
    {
        return startSize - size;
    }

    // returns the capacity of this hopper
    public int getCapacity()
    {
        return collection.length;
    }

    // returns the identity of this hopper
    public int getHopperId()
    {
        return id;
    }

    // returns the number of gifts currently in the hopper
    public int getNumGifts()
    {
        return size;
    }

    // requests the thread to stop
    public void requestStop()
    {
        stopRequested.set(true);
    }

    // fills the hopper with presents
    public void fill(Present p)
    {
        collection[size] = p;
        size++;
        startSize++;
    }

    // inserts a gift on the belt
    public void placeOnBelt(Present p)
    {
        belt.insert(p);
        size--;
    }

    // driver function
    public void run()
    {
        // loop through the the number of gifts in the hopper
        // sleep until there is a space on the belt, then insert to the belt
        // if stop has been requested, break from the loop
        for (int i = 0; i < startSize; ++i)
        {
            if (stopRequested.get())
            {
                break;
            }
            try
            {
                sleep((int) (Math.random() * 5));
            }
            catch (InterruptedException ex){}
            if (size > 0)
            {
                placeOnBelt(collection[i]);
            }
        }
    }
}
