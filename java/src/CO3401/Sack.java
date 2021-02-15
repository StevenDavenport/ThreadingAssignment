package CO3401;

import java.util.concurrent.atomic.AtomicBoolean;

public class Sack
{
    int id; // identity of the sack
    Present[] accumulation; // stores the gifts in the sack
    private int size;   // number of gifts currently in the sack
    private int nextIn; // the index where the item next to be inserted

    // constructor
    public Sack(int id, int capacity)
    {
        accumulation = new Present[capacity];
        this.id = id;
        size = 0;
        nextIn = 0;
    }

    // returns the number of gifts currently in the sack
    public int getNumGifts()
    {
       return this.size;
    }

    // inserts a gift into the sack - critical region
    public synchronized void insert(Present p)
    {
        // insert the gift
        accumulation[nextIn] = p;
        size++;
        try
        {
            Thread.sleep((int) (Math.random() * 10));
        }
        catch (InterruptedException ex) {}
        nextIn++;

        // moves next in to the start if full, circular queue
        // however, this should never be the case in turms of the sack
        if (nextIn == accumulation.length)
        {
            nextIn = 0;
        }

        // when finished notify other threads
        notifyAll();
    }
}
