import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sidath Weerasinghe and Udaka Manawadu
 */
public class WriteExceptionHandler implements ExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void handleEventException(Throwable throwable, long sequence, Object obj) {
        logger.error("Exception while handling event.", throwable);
    }

    public void handleOnStartException(Throwable throwable) {
        logger.error("Exception while starting up.", throwable);
    }

    public void handleOnShutdownException(Throwable throwable) {
        logger.error("Exception while shutting down.", throwable);
    }
}
