package it.albertus.mqtt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.albertus.util.NewLine;
import it.albertus.util.logging.LoggerFactory;

public class MqttPayloadTest {

	private static final Logger logger = LoggerFactory.getLogger(MqttPayloadTest.class);

	private static final String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." + NewLine.CRLF + "Mauris pretium eget ligula vehicula tempus. Proin accumsan varius sem non blandit." + NewLine.LF + "Nam consectetur magna eu cursus condimentum. Nunc volutpat tellus velit. Nullam elementum vel nisi at euismod." + NewLine.CRLF + "Pellentesque et ante nibh. Donec et leo varius, volutpat libero sit amet, vulputate sem." + NewLine.CR
			+ "Vivamus tempus mi est, malesuada tincidunt mi consequat id. Nunc neque ligula, interdum ut feugiat eget, imperdiet sed orci." + NewLine.CR;

	private static MqttPayloadDecoder decoder;

	@BeforeClass
	public static void init() {
		decoder = new MqttPayloadDecoder();
	}

	public void log(final byte[] payload) {
		System.out.println(Thread.currentThread().getStackTrace()[2]);
		final List<byte[]> split = MqttPayload.split(payload);
		System.out.println("tokens: " + split.size());

		for (final byte[] ba : split) {
			System.out.print(ba.length + ">");
			System.out.println(new String(ba, Charset.forName("UTF-8")));
		}
	}

	@Test
	public void testEncodeDecodeEmptyPayload() throws IOException {
		Assert.assertArrayEquals(new byte[] {}, decoder.decode(new MqttPayloadEncoder().encode("".getBytes(), false).toByteArray()));
	}

	@Test
	public void testDecodeWrongHeader() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append("Content-Encoding:x").append(NewLine.CRLF);
		payload.append("Content-Length: ").append(text.length()).append(NewLine.CRLF);
		payload.append(NewLine.CRLF);
		payload.append(text);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		Assert.assertArrayEquals(text.getBytes(Charset.forName("UTF-8")), decoder.decode(bytes));
	}

	@Test
	public void testDecodeHeadersBody() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append("Content-Encoding: identity").append(NewLine.CRLF);
		payload.append("Content-length: ").append(text.length()).append(NewLine.CRLF);
		payload.append(NewLine.CRLF);
		payload.append(text);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		Assert.assertArrayEquals(text.getBytes(Charset.forName("UTF-8")), decoder.decode(bytes));
	}

	@Test
	public void testDecodeNoHeadersWithCRLF() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append(NewLine.CRLF);
		payload.append(NewLine.CRLF);
		payload.append(text);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		Assert.assertArrayEquals(text.getBytes(Charset.forName("UTF-8")), decoder.decode(bytes));
	}

	@Test
	public void testDecodeWrongLength() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append("Content-Encoding: identity").append(NewLine.CRLF);
		payload.append("Content-Length: ").append(text.length() + 1 + new Random().nextInt(Byte.MAX_VALUE)).append(NewLine.CRLF);
		payload.append(NewLine.CRLF);
		payload.append(text);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		try {
			decoder.decode(bytes);
			Assert.assertFalse(true);
		}
		catch (final IOException e) {
			logger.log(Level.INFO, e.toString(), e);
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testDecodeMissingCRLF() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append("Content-Encoding: identity").append(NewLine.CRLF);
		payload.append("Content-Length: ").append(text.length() + 1 + new Random().nextInt(Byte.MAX_VALUE)).append(NewLine.CRLF);
		payload.append(text);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		try {
			decoder.decode(bytes);
			Assert.assertFalse(true);
		}
		catch (final IOException e) {
			logger.log(Level.INFO, e.toString(), e);
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testDecodeEmptyBody() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append("Content-Encoding: identity").append(NewLine.CRLF);
		payload.append("Content-Length: ").append(0).append(NewLine.CRLF);
		payload.append(NewLine.CRLF);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		Assert.assertArrayEquals(new byte[0], decoder.decode(bytes));
	}

	@Test
	public void testDecodeNoHeadersEmptyBody() throws IOException {
		final StringBuilder payload = new StringBuilder();
		payload.append(NewLine.CRLF);
		payload.append(NewLine.CRLF);

		final byte[] bytes = payload.toString().getBytes(Charset.forName("UTF-8"));
		log(bytes);
		Assert.assertArrayEquals(new byte[0], decoder.decode(bytes));
	}

}
