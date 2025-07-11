import java.util.*;

public class B1 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the size of the buffer: ");
        int size = sc.nextInt();

        System.out.println("Enter number of elements to produce: ");
        int toProduce = sc.nextInt();

        Buffer buffer = new Buffer(size);

        Producer producer = new Producer(buffer, toProduce);

        producer.start();

        System.out.flush();
        System.out.println("Enter number of elements to consume: ");
        System.out.flush();
        int toConsume = sc.nextInt();

        if (toConsume > toProduce) {
            System.out.println("Not Sufficient Elements to Consume");
            System.out.println(toProduce + ": Elements are Consuming...>!");
            toConsume = toProduce;
        }

        Consumer consumer = new Consumer(buffer, toConsume);

        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Production and consumption completed.");
    }
}

// class Buffer {

// int[] arr;
// int size;
// int index = 0;

// Seamaphore fullSlots;
// Seamaphore emptySlots;

// Buffer(int size) {
// this.size = size;
// this.arr = new int[size];
// this.fullSlots = new Seamaphore(0); // initially 0 full
// this.emptySlots = new Seamaphore(size); // initially all empty
// }

// public void produce(int val) {
// emptySlots.waits(); // wait for an empty slot

// synchronized (this) {
// arr[index] = val;
// System.out.println("Produced: " + val);
// index++;
// }

// fullSlots.signal(); // signal that a full slot is now available
// }

// public void consume() {
// fullSlots.waits(); // wait for full slot

// int val;
// synchronized (this) {
// index--;
// val = arr[index];
// arr[index] = 0;
// }

// System.out.println("Consumed: " + val);
// emptySlots.signal(); // signal that an empty slot is now available
// }
// }

class Buffer {
    int[] arr;
    int size;
    int in = 0;
    int out = 0;

    Seamaphore fullSlots;
    Seamaphore emptySlots;

    Buffer(int size) {
        this.size = size;
        this.arr = new int[size];
        this.fullSlots = new Seamaphore(0);
        this.emptySlots = new Seamaphore(size);
    }

    public void produce(int val) {
        emptySlots.waits(); // wait for an empty slot

        synchronized (this) {
            arr[in] = val;
            System.out.println("Produced: " + val);
            in = (in + 1) % size; // FIFO write pointer
        }

        fullSlots.signal(); // signal that a full slot is now available
    }

    public void consume() {
        fullSlots.waits(); // wait for full slot

        int val;
        synchronized (this) {
            val = arr[out];
            arr[out] = 0;
            out = (out + 1) % size; // FIFO read pointer
        }

        System.out.println("Consumed: " + val);
        emptySlots.signal(); // signal that an empty slot is now available
    }
}

class Producer extends Thread {
    Buffer buffer;
    int n;

    public Producer(Buffer buffer, int n) {
        this.buffer = buffer;
        this.n = n;
    }

    public void run() {
        try {
            for (int i = 0; i < n; i++) {
                buffer.produce((int) (Math.random() * 100));
                Thread.sleep(100); // simulate delay
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer extends Thread {
    Buffer buffer;
    int n;

    public Consumer(Buffer buffer, int n) {
        this.buffer = buffer;
        this.n = n;
    }

    public void run() {
        try {
            for (int i = 0; i < n; i++) {
                buffer.consume();
                Thread.sleep(100); // simulate delay
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Custom semaphore implementation
class Seamaphore {
    private int value;

    public Seamaphore(int initial) {
        this.value = initial;
    }

    public synchronized void waits() {
        while (value <= 0) {
            try {
                wait(); // wait if no permits
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        value--;
    }

    public synchronized void signal() {
        value++;
        notify(); // wake one waiting thread
    }
}


//####################################################################################################################
//=============== Alternate Code (Showining Automation) ==============================================================
//####################################################################################################################


import java.util.Random;

public class B1 {
    public static void main(String[] args) {
        Random rand = new Random();

        int size = rand.nextInt(6) + 5; // Buffer size between 5 to 10
        System.out.println("Buffer Size: " + size);

        Buffer buffer = new Buffer(size);

        for (int i = 1; i <= 10; i++) { // run for 10 cycles
            System.out.println("\n[Round " + i + "]");

            int choice = rand.nextInt(2) + 1; // 1 - Produce, 2 - Consume

            if (choice == 1) {
                int maxProduce = buffer.availableEmptySlots();
                if (maxProduce == 0) {
                    System.out.println("No space to produce. Skipping produce.");
                    continue;
                }

                int toProduce = rand.nextInt(maxProduce) + 1;
                System.out.println("Automatically decided to PRODUCE " + toProduce + " elements.");

                Producer producer = new Producer(buffer, toProduce);
                producer.start();
                try {
                    producer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                int maxConsume = buffer.availableFullSlots();
                if (maxConsume == 0) {
                    System.out.println("No items to consume. Skipping consume.");
                    continue;
                }

                int toConsume = rand.nextInt(maxConsume) + 1;
                System.out.println("Automatically decided to CONSUME " + toConsume + " elements.");

                Consumer consumer = new Consumer(buffer, toConsume);
                consumer.start();
                try {
                    consumer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\nAutomatic simulation completed.");
    }
}

class Buffer {
    int[] arr;
    int size;
    int in = 0;
    int out = 0;

    Seamaphore fullSlots;
    Seamaphore emptySlots;

    Buffer(int size) {
        this.size = size;
        this.arr = new int[size];
        this.fullSlots = new Seamaphore(0);
        this.emptySlots = new Seamaphore(size);
    }

    public void produce(int val) {
        emptySlots.waits();
        synchronized (this) {
            arr[in] = val;
            System.out.println("Produced: " + val);
            in = (in + 1) % size;
        }
        fullSlots.signal();
    }

    public void consume() {
        fullSlots.waits();
        int val;
        synchronized (this) {
            val = arr[out];
            arr[out] = 0;
            out = (out + 1) % size;
        }
        System.out.println("Consumed: " + val);
        emptySlots.signal();
    }

    public int availableEmptySlots() {
        return emptySlots.getValue();
    }

    public int availableFullSlots() {
        return fullSlots.getValue();
    }
}

class Producer extends Thread {
    Buffer buffer;
    int n;

    public Producer(Buffer buffer, int n) {
        this.buffer = buffer;
        this.n = n;
    }

    public void run() {
        try {
            for (int i = 0; i < n; i++) {
                buffer.produce((int) (Math.random() * 100));
                Thread.sleep(100); // simulate delay
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer extends Thread {
    Buffer buffer;
    int n;

    public Consumer(Buffer buffer, int n) {
        this.buffer = buffer;
        this.n = n;
    }

    public void run() {
        try {
            for (int i = 0; i < n; i++) {
                buffer.consume();
                Thread.sleep(100); // simulate delay
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Seamaphore {
    private int value;

    public Seamaphore(int initial) {
        this.value = initial;
    }

    public synchronized void waits() {
        while (value <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        value--;
    }

    public synchronized void signal() {
        value++;
        notify();
    }

    public synchronized int getValue() {
        return value;
    }
}




