/**
 * FILENAME: JavaServer.java
 * 
 * this file handles the routing and thread 
 * startup for the http server as well as 
 * declarting the vvirtual mappings 
 * 
 * -Vincent Sgherzi
 * 
 * TODO:
 * add login
 * add accoutnt creation
 */

package server.src;

//imports
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class JavaServer {

    // initialize server port and hostname
    private static final int SERVER_PORT = 3000;
    private static final String SERVER_HOSTNAME = "localhost";

    public static void main(String[] args) throws IOException {

        //start threads
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        //start server
        HttpServer server = HttpServer.create(
                new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT),
                0);


        //declare endpoints
        server.createContext("/transcribeAudio", new RequestHandle("/transcribeAudio"));
        server.createContext("/getAnswer", new RequestHandle("/getAnswer"));
        server.setExecutor(threadPoolExecutor);
        server.start();

        System.out.println("Server started on port " + SERVER_PORT);
    }
}
