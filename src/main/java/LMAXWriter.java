import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Sidath Weerasinghe and Udaka Manawadu
 * A class to control the disruptor engine. It initialize the disruptor,
 * submits messages to event producers and closes the disruptor.
 * */
public class LMAXWriter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Disruptor<WriteEvent> disruptor;
    private WriteEventProducer writeEventProducer;

    private int ringBufferSize;

    public void setRingBufferSize(int ringBufferSize) {
        this.ringBufferSize = ringBufferSize;
    }

    /**
     * Initialize the disruptor engine.
     * */
    public void init() {
        // create a thread pool executor to be used by disruptor
        Executor executor = Executors.newCachedThreadPool();

        // initialize our event factory
        WriteEventFactory factory = new WriteEventFactory();

        if (ringBufferSize == 0) {
            ringBufferSize = 1024;
        }
        // ring buffer size always has to be the power of 2.
        // so if it is not, make it equal to the nearest integer.

        double power = Math.log(ringBufferSize) / Math.log(2);
        if (power % 1 != 0) {
            power = Math.ceil(power);
            ringBufferSize = (int) Math.pow(2, power);
            logger.info("New ring buffer size = " + ringBufferSize);
        }

        // initialize two event handlers.
        WriteEventHandler handler = new WriteEventHandler(0,2);
        WriteEventHandler2 handler2 = new WriteEventHandler2(1,2);

        // initialize the disruptor
        disruptor = new Disruptor<WriteEvent>(factory, ringBufferSize, executor);
        disruptor.handleEventsWith(handler);
        disruptor.handleEventsWith(handler2);

        // set our custom exception handler
        ExceptionHandler exceptionHandler = new WriteExceptionHandler();
        disruptor.handleExceptionsFor(handler).with(exceptionHandler);
        disruptor.handleExceptionsFor(handler2).with(exceptionHandler);

        // start the disruptor and get the generated ring buffer instance
        disruptor.start();

        // initialize the event producer to submit messages
        writeEventProducer = new WriteEventProducer(disruptor);

        logger.info("Disruptor engine started successfully.");
    }

    public void close() {
        if (disruptor != null) {
            disruptor.halt();
            disruptor.shutdown();
        }
    }

    public void submitMessage(String message) {
        if (writeEventProducer != null ) {
            // publish the messages via event producer
            writeEventProducer.onData(message);
        }
    }
}
