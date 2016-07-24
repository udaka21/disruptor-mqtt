import com.lmax.disruptor.EventHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sidath Weerasinghe and Udaka Manawadu
 * This class implements EventHandler interface.
 */
public class WriteEventHandler implements EventHandler<WriteEvent> {

    private final long ordinal;
    private final long numberOfConsumers;

    public WriteEventHandler(final long ordinal, final long numberOfConsumers)
    {
        this.ordinal = ordinal;
        this.numberOfConsumers = numberOfConsumers;
    }

 private Logger logger = LoggerFactory.getLogger(getClass());

    // Java temporary directory location
    private static final String JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");

    // The MQTT broker URL
    private static final String brokerURL = "tcp://localhost:1883";

    String subscriberClientId = "subscriber";
    String publisherClientId = "publisher";
    String topic = "simpleTopic1";
    boolean retained = false;


    public void onEvent(WriteEvent writeEvent, long sequence, boolean endOfBatch) throws Exception {

        // Creating mqtt publisher client
        MqttClient mqttPublisherClient = getNewMqttClient(publisherClientId);

       //This If statement responsible for consume one message for only one handler.
        if ((sequence % numberOfConsumers) == ordinal){

            logger.info("Running sample");
            byte[] payload = writeEvent.get().getBytes();
            logger.info(new String(payload) + " processed .");

            try {

                // Publishing to mqtt topic "simpleTopic"
                mqttPublisherClient.publish(topic, payload, QualityOfService.LEAST_ONCE.getValue(), retained);
                String s = new String(payload);
                logger.info(s);


            } catch (MqttException e) {
                logger.error("Error running the sample", e);
            }

        }

        //client disconnect method.
       mqttPublisherClient.disconnect();
       logger.info("Clients Disconnected!");

        }

    /**
     * Crate a new MQTT client and connect it to the server.
     *
     * @param clientId The unique mqtt client Id
     * @return Connected MQTT client
     * @throws MqttException
     */
    private static MqttClient getNewMqttClient(String clientId) throws MqttException {
        //Store messages until server fetches them
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(JAVA_TMP_DIR + "/" + clientId);

        MqttClient mqttClient = new MqttClient(brokerURL, clientId, dataStore);
        SimpleMQTTCallback callback = new SimpleMQTTCallback();
        mqttClient.setCallback(callback);

        MqttConnectOptions connectOptions = new MqttConnectOptions();

        connectOptions.setUserName("admin");
        connectOptions.setPassword("admin".toCharArray());
        connectOptions.setCleanSession(true);
        mqttClient.connect(connectOptions);

        return mqttClient;
    }
}

