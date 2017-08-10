package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class MovementHelper extends AbstractHelper {
	
	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";


	private volatile Message responseMessage;
	private volatile List<Message> responseMessageList = Collections.synchronizedList(new ArrayList<Message>());

	public CreateMovementRequest createMovementRequest(Asset testAsset, String mobileTerminalIdAsConnectId) throws IOException, ClientProtocolException,
			JsonProcessingException, JsonParseException, JsonMappingException {
		Date positionTime = getDate(2017, Calendar.DECEMBER, 24, 11, 45, 7, 980);
		return createMovementRequest(testAsset, -16.9, 32.6333333, 5, positionTime,mobileTerminalIdAsConnectId);
	}

	public CreateMovementRequest createMovementRequest(Asset testAsset, LatLong obs, String mobileTerminalIdAsConnectId) throws IOException,
			ClientProtocolException, JsonProcessingException, JsonParseException, JsonMappingException {
		return createMovementRequest(testAsset, obs.longitude, obs.latitude, 5, obs.positionTime,mobileTerminalIdAsConnectId);
	}

	/**
	 * 
	 * @param testAsset
	 * @param longitude
	 * @param latitude
	 * @param altitude
	 * @param positionTime
	 * @return CreateMovementRequest
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JsonProcessingException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public CreateMovementRequest createMovementRequest(Asset testAsset, double longitude, double latitude,
			double altitude, Date positionTime , String mobileTerminalIdAsConnectId) throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		final CreateMovementRequest createMovementRequest = new CreateMovementRequest();
		final MovementBaseType movementBaseType = new MovementBaseType();
		AssetId assetId = new AssetId();
		assetId.setAssetType(AssetType.VESSEL);
		assetId.setIdType(AssetIdType.GUID);
		assetId.setValue(testAsset.getAssetId().getGuid());
		movementBaseType.setAssetId(assetId);
		movementBaseType.setConnectId(mobileTerminalIdAsConnectId); // skall
																			// vara
																			// terminalens
																			// ID

		MovementActivityType movementActivityType = new MovementActivityType();
		movementBaseType.setActivity(movementActivityType);
		movementActivityType.setMessageId(UUID.randomUUID().toString());
		movementActivityType.setMessageType(MovementActivityTypeType.ANC);

		createMovementRequest.setMovement(movementBaseType);
		createMovementRequest.setMethod(MovementModuleMethod.CREATE);
		createMovementRequest.setUsername("vms_admin_com");

		MovementPoint movementPoint = new MovementPoint();
		movementPoint.setLongitude(longitude);
		movementPoint.setLatitude(latitude);
		movementPoint.setAltitude(altitude);

		movementBaseType.setPosition(movementPoint);
		movementBaseType.setPositionTime(positionTime);

		movementBaseType.setMovementType(MovementTypeType.POS);
		return createMovementRequest;

	}

	public List<LatLong> createRutt() {
		return createRutt(15 * 1000);
	}

	public List<LatLong> createRutt(int movementTimeDeltaInMillis) {

		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();
		rutt.add(new LatLong(57.42920, 11.58259, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42905, 11.58192, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42897, 11.58149, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42882, 11.58116, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42858, 11.58071, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42825, 11.57973, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42796, 11.57890, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42762, 11.57814, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42707, 11.57713, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42624, 11.57576, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42550, 11.57458, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42462, 11.57373, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42386, 11.57265, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42316, 11.57141, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42264, 11.56922, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42194, 11.56721, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42148, 11.56490, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42111, 11.56212, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42091, 11.55908, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42073, 11.55707, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42011, 11.55375, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41934, 11.55112, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41829, 11.54826, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41664, 11.54486, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41529, 11.54237, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41438, 11.54038, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41312, 11.53614, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41239, 11.53068, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41131, 11.52269, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41041, 11.51412, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40870, 11.50024, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40727, 11.48819, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40563, 11.48224, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40256, 11.47660, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.39744, 11.46579, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.39507, 11.46002, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.38956, 11.42624, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.37787, 11.40996, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.36099, 11.38318, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.34045, 11.25876, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.31126, 11.9727, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.27140, 10.46655, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.25455, 10.36438, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.28647, 10.35944, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.35723, 10.35944, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41104, 10.36603, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42216, 10.36026, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42711, 10.36263, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42794, 10.35769, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42862, 10.35563, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42945, 10.35521, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42946, 10.35416, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42928, 10.35400, getDate(ts += movementTimeDeltaInMillis)));
		return rutt;

	}

	private Date getDate(Long millis) {
		return new Date(millis);
	}

	/**
	 * Marshall.
	 *
	 * @param createMovementRequest
	 *            the create movement request
	 * @return the string
	 * @throws JAXBException
	 *             the JAXB exception
	 */

	public String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(CreateMovementRequest.class).createMarshaller().marshal(createMovementRequest, sw);
		return sw.toString();
	}

	/**
	 * Un marshall create movement response.
	 *
	 * @param response
	 *            the response
	 * @return the creates the movement response
	 * @throws Exception
	 *             the exception
	 */
	public CreateMovementResponse unMarshallCreateMovementResponse(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(CreateMovementResponse.class);
		return (CreateMovementResponse) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

	/**
	 * Check queue has elements.
	 * 
	 * @param connection
	 * @param queueName
	 * @return
	 * @throws Exception
	 */

	public boolean checkQueueHasElements(Connection connection, String queueName) throws Exception {
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(queueName);
		final QueueBrowser browser = session.createBrowser(queue);
		while (browser.getEnumeration().hasMoreElements()) {
			session.close();
			return true;
		}
		session.close();
		return false;
	}

	/**
	 * Send request to movement.
	 *
	 * @param ResponseQueueName
	 *            the response queue name
	 * @param createMovementRequest
	 *            the create movement request
	 * @throws JMSException
	 *             the JMS exception
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	public void sendRequest(Connection connection,  String ResponseQueueName,
			final CreateMovementRequest createMovementRequest) throws JMSException, JAXBException {
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue queue = session.createQueue(UVMS_MOVEMENT_REQUEST_QUEUE);

		final MessageProducer messageProducer = session.createProducer(queue);
		messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
		messageProducer.setTimeToLive(1000000000);
		String marshalled = marshall(createMovementRequest);
		TextMessage createTextMessage = session.createTextMessage(marshalled);
		final Queue responseQueue = session.createQueue(ResponseQueueName);
		createTextMessage.setJMSReplyTo(responseQueue);
		messageProducer.send(createTextMessage);
		session.close();
	}

	/**
	 * Sets the up response consumer.
	 *
	 * @param queueName
	 *            the new up response consumer
	 * @throws Exception
	 *             the exception
	 */

	public void setupResponseConsumer(ConnectionFactory connectionFactory, Connection connection, String queueName)
			throws Exception {
		Connection consumerConnection = connectionFactory.createConnection();
		consumerConnection.setClientID(UUID.randomUUID().toString());
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Queue responseQueue = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(responseQueue);
		consumer.setMessageListener(new ResponseQueueMessageListener());
		connection.start();

	}
	
	/**
	 * The listener interface for receiving responseQueueMessage events. The
	 * class that is interested in processing a responseQueueMessage event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addResponseQueueMessageListener<code> method. When the
	 * responseQueueMessage event occurs, that object's appropriate method is
	 * invoked.
	 *
	 * @see ResponseQueueMessageEvent
	 */
	public  class ResponseQueueMessageListener implements MessageListener {
		@Override
		public void onMessage(Message message) {
			responseMessage = message;
			responseMessageList.add(message);
		}
	}

	public Message getResponseMessage() {
		return responseMessage;
	}

	public List<Message> getResponseMessageList() {
		return responseMessageList;
	}

	public void setResponseMessage(Message responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void setResponseMessageList(List<Message> responseMessageList) {
		this.responseMessageList = responseMessageList;
	}
	
	public void clearResponseMessageList() {
		this.responseMessageList.clear();
	}
	
	public  CreateMovementRequest createMovement(ConnectionFactory connectionFactory, Connection connection) throws Exception{
		String ResponseQueueName = "createMovementRequestTest" + UUID.randomUUID().toString().replaceAll("-", "");
		setupResponseConsumer(connectionFactory, connection, ResponseQueueName);
		Asset testAsset = AssetTestHelper.createTestAsset();
		
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		String guid = mobileTerminalType.getMobileTerminalId().getGuid();
		
		final CreateMovementRequest createMovementRequest = createMovementRequest(testAsset,guid);
		sendRequest(connection,  ResponseQueueName, createMovementRequest);
		
		return createMovementRequest;
		
	}



}
