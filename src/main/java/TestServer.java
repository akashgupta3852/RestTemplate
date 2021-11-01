///****************************************************************************************************************
// *
//
// *  Copyright (c) 2015 OCLC, Inc. All Rights Reserved.
// *
// *  OCLC proprietary information: the enclosed materials contain
// *  proprietary information of OCLC, Inc. and shall not be disclosed in whole or in
// *  any part to any third party or used by any person for any purpose, without written
// *  consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
// *
// ******************************************************************************************************************/
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Base64;
//import java.io.ByteArrayOutputStream;
//import java.io.ObjectOutputStream;
//import java.nio.channels.CompletionHandler;
//import java.io.ByteArrayInputStream;
//
//import com.beanit.jasn1.ber.types.BerBoolean;
//import com.beanit.jasn1.ber.types.BerInteger;
//import com.beanit.jasn1.ber.types.BerOctetString;
//import com.beanit.jasn1.ber.types.string.BerVisibleString;
//
//import org.glassfish.grizzly.filterchain.BaseFilter;
//import org.glassfish.grizzly.filterchain.FilterChainBuilder;
//import org.glassfish.grizzly.filterchain.FilterChainContext;
//import org.glassfish.grizzly.filterchain.NextAction;
//import org.glassfish.grizzly.filterchain.TransportFilter;
//import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
//import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
//import org.oclc.Z39.tcpserver.util.InitializeResponseUtil;
//import org.oclc.Z39.tcpserver.util.PropertiesUtil;
//import org.oclc.Z39.tcpserver.util.RestCallerUtil;
//import org.oclc.Z39.tcpserver.util.SearchResponseUtil;
//import org.oclc.Z39.tcpserver.validator.InitRequestValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import z3950.AttributeSetId;
//import z3950.CloseReason;
//import z3950.DefaultDiagFormat;
//import z3950.InternationalString;
//import z3950.Options;
//import z3950.PresentStatus;
//import z3950.Records;
//
///**
// * The actual test server
// */
//public class TestServer { // implements AutoCloseable {
//	/** The port to listen on */
//	private final int port;
//
//	/** The actual network transport */
//	private final TCPNIOTransport transport;
//
//	/**
//	 * Set up the test server
//	 * 
//	 * @param port The port to listen on
//	 */
//
//	RestCallerUtil restUtil;
//
//	public TestServer(int port) {
//		this.port = port;
//
//		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
//		filterChainBuilder.add(new TransportFilter());
//
//		filterChainBuilder.add(new TCPBufferByteFilter());
//
//		try {
//			PropertiesUtil.setInitFailureCount(0);
//			PropertiesUtil.setInitSuccessStatus(false);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		filterChainBuilder.add(new BaseFilter() {
//			@Value("S{my.greetings")
//			String message;
//
//			/**
//			 * {@inheritDoc}
//			 *
//			 * @param ctx
//			 */
//			@Override
//			public NextAction handleRead(FilterChainContext ctx) throws IOException {
//
//				z3950.PDU pdu_in = new z3950.PDU();
//				boolean requestCharLimitExceeded = false;
//
//				byte[] bytes = ctx.getMessage();
//
//				System.out.println("\nAddress of sender " + ctx.getAddress().toString());
//				System.out.println("The length of input request: " + bytes.length);
//				System.out.println(new String(bytes));
//				System.out.println(message);
//
//				pdu_in.decode(new ByteArrayInputStream(bytes));
//
//				z3950.PDU pdu_out = new z3950.PDU();
//
//				if (pdu_in.getInitRequest() != null) {
//					System.out.println("Inside Initialize block");
//
//					if (PropertiesUtil.getInitSuccessStatus()) {
//						System.out.println("The connection has been established already.");
//						z3950.Close closeReq = new z3950.Close();
//						closeReq.setCloseReason(new CloseReason(8));
//						pdu_out.setClose(closeReq);
//						pdu_out.encodeAndSave(1000);
//						ctx.write(pdu_out.code);
//						return ctx.getForkAction();
//					}
//
//					z3950.InitializeRequest ir_in = pdu_in.getInitRequest();
//
//					z3950.InitializeResponse ir_out = new z3950.InitializeResponse();
//
//					InitializeResponseUtil respUtil = new InitializeResponseUtil();
//
//					// Validate IntializeRequest here
//					ir_out = respUtil.generateInitializeResponse(ir_in);
//
//					InitRequestValidator initRequestValidator = new InitRequestValidator();
//					if (initRequestValidator.isMaxLimitReached()) {
//						z3950.Close closeReq = new z3950.Close();
//						closeReq.setCloseReason(new CloseReason(5));
//						pdu_out.setClose(closeReq);
//						System.out.println("The connection has been closed.");
//						PropertiesUtil.setInitFailureCount(0);
//					}
//
//					// Authenticate the client with the WS key and Secret supplied by the
//					// InitializeRequest
//
//					// Server will provide its supported options
//					ir_out.setOptions(setUpOptions());
//
//					pdu_out.setInitResponse(ir_out);
//					System.out.println("Going out of Initialize block");
//
//				} else if (pdu_in.getSearchRequest() != null) {
//					System.out.println("Inside Search block");
//
//					z3950.SearchRequest sr_in = pdu_in.getSearchRequest();
//					z3950.SearchResponse sr_out = new z3950.SearchResponse();
//					// Validation of incoming character limit
//
//					String dbName = sr_in.getDatabaseNames().getDatabaseName().get(0).toString();
//					System.out.println("Database Name is: " + dbName);
//					// Validate the dbName
//
//					// populate SearchResponseUtil with mock data
//					SearchResponseUtil respUtil = new SearchResponseUtil();
//					List<String> authDbNames = new ArrayList<>();
//					authDbNames.add("z3950Test");
//					respUtil.populateAuthorizedDatabaseNames(authDbNames);
//					List<String> ResultsetNames = new ArrayList<>();
//					ResultsetNames.add("myResults");
//					respUtil.populateAlreadyUsedResultsetNames(ResultsetNames);
//					// Now Validate the incoming Search Request
//					if (respUtil.searchValidationChecks(sr_in, bytes) != null) {
//						System.out.println("The SearchRequest has  validation Failures::-");
//						sr_out = respUtil.searchValidationChecks(sr_in, bytes);
//					} else {
//						// Query the database connector/test connector
//						System.out
//								.println("The SearchRequest has passed validation and now querying the Test Connector");
//						// Call Rest Template class
//
//						// Validation of Resultset name
//						// validation if Replace-Indicator is Off and resultset Existing
//						System.out.println(sr_in.getQuery().toString());
//						AttributeSetId aid = sr_in.getQuery().getType1().getAttributeSet();
//
//						// System.out.println("Passed AttributeSet
//						// is:sr_in.getQuery().getType1().getAttributeSet());
//						System.out.println(sr_in.getQuery().getType1().getRpn());
//						System.out.println(
//								"Input Attribute type is: " + sr_in.getQuery().getType1().getRpn().getOp().getAttrTerm()
//										.getAttributes().getAttributeElement().get(0).getAttributeType().intValue());
//						// System.out.println(sr_in.getQuery().getType1().getRpn().
//						// getOp().getAttrTerm().getAttributes().getAttributeElement().get(0).getAttributeType());
//
//						int attributeVal = sr_in.getQuery().getType1().getRpn().getOp().getAttrTerm().getAttributes()
//								.getAttributeElement().get(0).getAttributeValue().getNumeric().intValue();
//						System.out.println("input attribute value is: "
//								+ sr_in.getQuery().getType1().getRpn().getOp().getAttrTerm().getAttributes()
//										.getAttributeElement().get(0).getAttributeValue().getNumeric().intValue());
//						System.out.println(
//								sr_in.getQuery().getType1().getRpn().getOp().getAttrTerm().getTerm().getGeneral());
//						BerOctetString thisBString = sr_in.getQuery().getType1().getRpn().getOp().getAttrTerm()
//								.getTerm().getGeneral();
//						String termVal = new String(thisBString.value);
//						System.out.println("Input term value is : " + new String(thisBString.value));
//						// sr_in.
//
//						// delete-- z3950.SearchResponse sr_out = new z3950.SearchResponse();
//						RestCallerUtil restUtil = new RestCallerUtil();
//						Integer rowCount = restUtil.makeRestCallAndRetireveRecords(attributeVal, termVal);
//						System.out.println("RestTemplate returned rowCount: " + rowCount);
//						// do work
//
//						sr_out.setResultSetStatus(new BerInteger(1));
//						sr_out.setSearchStatus(new BerBoolean(true));
//						sr_out.setNextResultSetPosition(new BerInteger(1));
//						sr_out.setNumberOfRecordsReturned(new BerInteger(rowCount));
//						sr_out.setReferenceId(sr_in.getReferenceId());
//						sr_out.setPresentStatus(new PresentStatus(0));
//						// sr_out.setRecords(new Records());
//						sr_out.setResultCount(new BerInteger(rowCount));
//
//						// Set up the Records for SearchResponse
//						/*
//						 * DefaultDiagFormat.Addinfo addInfo = new DefaultDiagFormat.Addinfo();
//						 * addInfo.setV2Addinfo(new BerVisibleString(dbName));
//						 * 
//						 * DefaultDiagFormat diagFormat = new DefaultDiagFormat();
//						 * diagFormat.setDiagnosticSetId(aid); diagFormat.setCondition(new
//						 * BerInteger(109)); diagFormat.setAddinfo(addInfo);
//						 * 
//						 * Records records = new Records();
//						 * records.setNonSurrogateDiagnostic(diagFormat); sr_out.setRecords(records);
//						 */
//					}
//					pdu_out.setSearchResponse(sr_out);
//					System.out.println("Getting out of Search block");
//
//				} else if (pdu_in.getPresentRequest() != null) {
//					System.out.println("Inside Present block");
//
//					z3950.PresentRequest pr_in = pdu_in.getPresentRequest();
//
//					z3950.PresentResponse pr_out = new z3950.PresentResponse();
//
//					// do work
//
//					pdu_out.setPresentResponse(pr_out);
//
//				}
//
//				pdu_out.encodeAndSave(1000);
//				System.out.println("encodeAndSave done");
//
//				ctx.write(ctx.getAddress(), pdu_out.code, ctx.getTransportContext().getCompletionHandler());
//				// ctx.write(pdu_out.code,true);
//				System.out.println("Write done No of bytes: " + pdu_out.code.length);
//				ctx.flush(ctx.getTransportContext().getCompletionHandler());
//				System.out.println("Flush done");
//
//				return ctx.getForkAction();
//
//			}
//
//			public Options setUpOptions() {
//				// This project's setup
//				Options opt = new Options(new boolean[] { true, true, false, false, false, false, false, false, true,
//						false, false, false, false, false, true });
//				return opt;
//
//			}
//
//		});
//
//		transport = TCPNIOTransportBuilder.newInstance().build();
//		transport.setProcessor(filterChainBuilder.build());
//	}
//
//	/**
//	 * Start the server
//	 * 
//	 * @throws IOException if an error occurs
//	 */
//	public void start() throws IOException {
//		transport.bind(port);
//		transport.start();
//	}
//
//	/**
//	 * Stop the server
//	 * 
//	 * @throws IOException if an error occurs
//	 */
//	// @Override
//	public void close() throws IOException {
//		transport.shutdownNow();
//	}
//
//	/**
//	 * Clear the recorded messages store and start recording messages.
//	 */
//	// public void startRecording() {
//	// synchronized (this) {
//	// recordedMessages = new ArrayList<>();
//	// }
//	// }
//
//	/**
//	 * Record a request item message
//	 * 
//	 * @param message the message to record
//	 */
//	// private void recordMessage(Message message) {
//	// synchronized (this) {
//	// if (recordedMessages != null) {
//	// recordedMessages.add(message);
//	// }
//	// }
//	// }
//
//	/**
//	 * Stop recording messages and clear the store
//	 * 
//	 * @return the recorded messages
//	 */
//	// public List<Message> stopRecording() {
//	// List<Message> result;
//	// synchronized (this) {
//	// result = recordedMessages;
//	// recordedMessages = null;
//	// }
//	// return result;
//	// }
//}
