package ru.nsu.amazyar;

// this is not thread safe
public class NonblockingCircularByteBuffer {
    private final int capacity;
    private final byte[] data;
    private int readSequence;
    private int writeSequence;
    private volatile int writeLimit;

    public NonblockingCircularByteBuffer(int capacity) {
        this.capacity = capacity;
        this.data = new byte[capacity];
        this.readSequence = 0;
        this.writeSequence = 0;
        this.writeLimit = capacity;
    }

    public boolean offer(byte new_byte) {
        if(writeLimit < 1){
            return false;
        }
        
        data[writeSequence] = new_byte;
        writeSequence = (writeSequence + 1) % capacity;
        writeLimit--;
        
        return true;
    }

    public boolean offer(byte[] new_bytes) {
        if(writeLimit < new_bytes.length){
            return false;
        }

        int continious = capacity - writeSequence;

        // if write is doable without cycling to the start
        if(continious >= new_bytes.length){
            System.arraycopy(new_bytes, 0, data, writeSequence, new_bytes.length);
        }
        else{
            int extra_bytes = new_bytes.length - continious;
            System.arraycopy(new_bytes, 0, data, writeLimit, continious); // copy until buffer's end
            System.arraycopy(new_bytes, continious, data, 0, extra_bytes); // copy to buffer's beginning
        }
        writeSequence = (writeSequence + new_bytes.length) % capacity;
        writeLimit -= new_bytes.length;
        
        return true;
    }

    public boolean poll(byte[] out) {
        if(getReadLimit() < out.length){
            return false;
        }
        
        int continious = capacity - readSequence;

        // if read is doable without cycling to the start
        if(continious >= out.length){
            System.arraycopy(data, readSequence, out, 0, out.length);
        }
        else{
            int extra_bytes = out.length - continious;
            System.arraycopy(data, readSequence, out, 0, continious); // copy until buffer's end
            System.arraycopy(data, 0, out, continious, extra_bytes); // copy to buffer's beginning
        }
        readSequence = (readSequence + out.length) % capacity;
        writeLimit += out.length;

        return true;
    }

    public int getWriteLimit(){
        return writeLimit;
    }

    public int getReadLimit(){
        return capacity - writeLimit;
    }
}
