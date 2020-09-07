/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;

/**
 * TODO add tests
 */
public class MinesweeperServerTest {

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // enable assertions with VM argument: -ea
    }

    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 4000 + new Random().nextInt(1 << 15);
    private static final int MAX_CONNECTION_ATTEMPTS = 10;


//    private static final String inStringStub = "command1 \n command2 \n bye";
//    private static ByteArrayInputStream inBytes = new ByteArrayInputStream(inStringStub.getBytes());
//    private static ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
//
//    // read a stream of characters from the fixed input string
//    private static BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
//    // write characters to temporary storage
//    private static PrintWriter out = new PrintWriter(outBytes, true);

    // TODO write testing strategy


//    ============================= test files =============================

    //    testBoard: 7/24 bombs
    //    6 4
    //    0 1 0 0 1 0
    //    1 0 1 1 0 0
    //    0 0 0 0 0 1
    //    0 0 0 0 1 0
    private final File file = new File("./test/minesweeper/testBoards/testBoard");

    //    emptyBoard: 0/30 bombs
    //    6 5
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    private final File empty = new File("./test/minesweeper/testBoards/emptyBoard");

    //    nearlyEmptyBoard: 3/30 bombs
    //    6 5
    //    0 0 0 1 0 1
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 1 0
    //    0 0 0 0 0 0
    private final File nearlyEmpty = new File("./test/minesweeper/testBoards/nearlyEmptyBoard");


    //    oneBombSurroundedByBombs: 9/30 bombs
    //    6 5
    //    0 0 0 0 0 0
    //    0 1 1 1 0 0
    //    0 1 1 1 0 0
    //    0 1 1 1 0 0
    //    0 0 0 0 0 0
    private final File oneBombSurroundedByBombs = new File("./test/minesweeper/testBoards/oneBombSurroundedByBombs");



//    ============================= Helper methods  =============================

    /**
     * Start a MinesweeperServer in debug mode without a board file
     * @return thread running the server
     */
    private static Thread startMinesweeperServer(){
        String[] args = new String[]{
                "--debug",
                "--port", Integer.toString(PORT)};
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Start a MinesweeperServer in debug mode with a specified board file
     * @return thread running the server
     */
    private static Thread startMinesweeperServer(String filePath){
        String[] args = new String[]{
                "--debug",
                "--port", Integer.toString(PORT),
                "--file", filePath};
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Start a MinesweeperServer in debug mode with a specified size
     * @return thread running the server
     */
    private static Thread startMinesweeperServer(int sizeX, int sizeY){
        String[] args = new String[]{
                "--debug",
                "--port", Integer.toString(PORT),
                "--size", Integer.toString(sizeX), Integer.toString(sizeY)};
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Connect to a MinesweeperServer and return the connected socket (i.e. client-side socket).
     *
     * @param server abort connection attempts if the server thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server) throws IOException {
        int attempts = 0;
        while (true) {
//            System.out.println("Trying to make a socket on address " + LOCALHOST + " on port " + PORT+". Attempt: " + attempts);
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                System.out.println("Created socket on local address: " + socket.getLocalSocketAddress());
                return socket;
            } catch (ConnectException ce) {
                if ( ! server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }



//     ============================= Tests  =============================

    @Test(timeout = 10000)
    public void testServerStartsSingleConnection() throws IOException {
        Thread thread = startMinesweeperServer();

        Socket socket = connectToMinesweeperServer(thread);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));
    }



    @Test(timeout = 10000)
    public void testServerStartsMultipleConnections() throws IOException {
        final int numberConnections = 4;
        
        // Set up the server
        Thread thread = startMinesweeperServer();

        // Make arrays of the sockets, inputLines and OutputLines
        Socket[] clientSockets = new Socket[numberConnections];
        BufferedReader[] clientIns = new BufferedReader[numberConnections];
        PrintWriter[] clientOuts = new PrintWriter[numberConnections];

        for (int i = 0; i < numberConnections; i++) {
            clientSockets[i] = connectToMinesweeperServer(thread);
            clientIns[i] = new BufferedReader(new InputStreamReader(clientSockets[i].getInputStream()));
            clientOuts[i] = new PrintWriter(clientSockets[i].getOutputStream(), true);
        }

        // Check that all clients receive a welcome message
        for (int i = 0; i < numberConnections; i++) {
            assertTrue("expected HELLO message for client " + i + ".", clientIns[i].readLine().startsWith("Welcome"));
        }
    }

//
//    @Test(timeout = 10000)
//    public void testServerStartsMultipleConnectionsMultipleInput() throws IOException {
//        final int numberConnections = 4;
//
//        Thread thread = startMinesweeperServer();
//
//        Socket socket = connectToMinesweeperServer(thread);
//
//
//        String inStringStub = "command1 \n command2 \n bye";
//        ByteArrayInputStream inBytes = new ByteArrayInputStream(inStringStub.getBytes());
//        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
//
//        // read a stream of characters from the fixed input string
//        BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
//        // write characters to temporary storage
//        PrintWriter out = new PrintWriter(outBytes, true);
//
//        socket.getInputStream();
////        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));
//
//
//
//
//    }





}
