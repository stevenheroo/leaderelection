package webservers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class Server {

    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";

    //store listening port
    private final int port;
    private HttpServer server;

    public Server(int port) {
        this.port = port;
    }

    //start server
    public void startServer() throws IOException {
        //backlog size specified system default
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        //define endpoint
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheck);
        taskContext.setHandler(this::handleTaskRequest);

        //allocate threads to the server
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")){
            exchange.close();
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")){
            String testResponse = "123::::success\n";
            sendResponseMsg(testResponse.getBytes(), exchange);
            return;
        }
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")){
            isDebugMode = true;
        }
        long startTime = System.nanoTime();
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = CalculateResponse(requestBytes);

        long endTime = System.nanoTime();

        if (isDebugMode){
            String debugMsg = String.format("Operation took %d ns\n", endTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMsg));
        }

        sendResponseMsg(responseBytes, exchange);
    }

    private byte[] CalculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String [] stringNumbers = bodyString.split(",");

        BigInteger result = BigInteger.ONE;
        for (String number : stringNumbers){
            BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }
        return String.format("Result of multiplying is %s\n", result).getBytes();
    }

    //check status of server
    private void handleStatusCheck(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")){
            exchange.close();
            return;
        }
        String responseMessage = "Server is alive\n";
        sendResponseMsg(responseMessage.getBytes(), exchange);
    }

    private void sendResponseMsg(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream ops = exchange.getResponseBody();
        ops.write(responseBytes);
        ops.close();
        ops.flush();
    }


}
