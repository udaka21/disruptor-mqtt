import com.lmax.disruptor.EventFactory;

/**
 * @author Sidath Weerasinghe and Udaka Manawadu
 */
public class WriteEventFactory implements EventFactory<WriteEvent> {

    public WriteEvent newInstance() {
        return new WriteEvent();
    }
}
