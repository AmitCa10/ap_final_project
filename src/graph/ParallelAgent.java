package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private static final char SEP = '\u0000';

    private final Agent               agent;
    private final BlockingQueue<Message> queue;
    private final Thread              worker;

    public ParallelAgent(Agent agent, int capacity) {
        this.agent  = agent;
        this.queue  = new ArrayBlockingQueue<>(capacity);
        this.worker = new Thread(this::runWorker, agent.getName() + "-worker");
        this.worker.start();
    }

    @Override
    public String getName() {return agent.getName();}

    @Override
    public void reset() {agent.reset();}

    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new Message(topic + SEP + msg.asText));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        worker.interrupt();
        try { worker.join(); } catch (InterruptedException ignored) {}
        agent.close();
    }

    /* ---------------------------------------------------- */

    private void runWorker() {
        while (true) {
            try {
                Message packed = queue.take();
                int idx = packed.asText.indexOf(SEP);
                if (idx < 0) continue; // malformed – skip
                String topic  = packed.asText.substring(0, idx);
                String body   = packed.asText.substring(idx + 1);
                agent.callback(topic, new Message(body));
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
