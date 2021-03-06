package nl.ellipsis.tpjms.core.message;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.*;

import org.junit.*;

import nl.ellipsis.tpjms.core.connection.TPJMSConnectionFactory;
import nl.ellipsis.tpjms.core.message.TPJMSObjectMessage;
import nl.ellipsis.tpjms.provider.vm.VmProvider;

public class TPJMSObjectMessageTest {
	private TPJMSObjectMessage message;

	@Before
	public void setUp() throws Exception {
		TPJMSConnectionFactory factory = new TPJMSConnectionFactory();
		TopicConnection connection = factory.createTopicConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		message = new TPJMSObjectMessage(session);
	}

	@After
	public void tearDown() {
		message = null;
	}

	@Test
	public void testNullSerialization() throws JMSException {
		message.setObject(null);
		assertNull(message.getObject());
	}

	@Test
	public void testObjectSerialization() throws JMSException {
		message.setObject("TEST");
		assertEquals("TEST", message.getObject());
	}

	@Test
	public void testBody() throws JMSException {
		message.setObject("TEST");
		byte[] data = message.getBody();

		message.setObject("TEST1");
		message.setBody(data);
		assertEquals("TEST", message.getObject());
	}

	@Test(expected = MessageNotWriteableException.class)
	public void testReadOnlySerialization() throws JMSException {
		message.setReadOnly(true);
		message.setObject("TEST");
	}

	@Test(expected = MessageFormatException.class)
	public void testInvalidSerialization() throws JMSException {
		message.setObject(new AtomicReference<Object>(new Object()));
	}

	@Test(expected = MessageFormatException.class)
	public void testInvalidDeserialization() throws Exception {
		Field data = message.getClass().getDeclaredField("body");
		data.setAccessible(true);
		data.set(message, new byte[] {});

		message.getObject();
	}

	@Test
	public void testClearBody() throws JMSException {
		message.setReadOnly(false);
		message.setObject("TEST");
		message.clearBody();
		assertNull(message.getObject());
	}

	@Test
	public void testClearBodyReadOnly() throws JMSException {
		message.setReadOnly(true);
		message.clearBody();
		message.setObject("TEST");
		assertEquals("TEST", message.getObject());
	}
}
