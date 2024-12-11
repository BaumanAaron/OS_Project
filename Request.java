package os_project4;

class Request implements Comparable<Request> {
    private int lamportClock; // Lamport clock for priority
    private int port;         // Port number as an integer
    private int operation;    // Operation type: 0 for read, 1 for write
    private String client;       // To know which client (for server later)

    public Request(int lamportClock, int port, int operation, String client) {
        this.lamportClock = lamportClock;
        this.port = port;
        this.operation = operation;
        this.client = client;
    }

    public int getLamportClock() {
        return lamportClock;
    }

    public int getPort() {
        return port;
    }

    public int getOperation() {
        return operation;
    }
    
    public String getClient() {
        return client;
    }

    @Override
    public int compareTo(Request other) {
        // Compare based on Lamport clock first
        if (this.lamportClock != other.lamportClock) {
            return Integer.compare(this.lamportClock, other.lamportClock);
        }
        // If Lamport clocks are the same, compare based on port number
        return Integer.compare(this.port, other.port);
    }

    @Override
    public String toString() {
        return "Request{" +
                "lamportClock=" + lamportClock +
                ", port=" + port +
                ", operation=" + (operation == 0 ? "Read" : "Write") +
                ", client=" + client +
                '}';
    }
}