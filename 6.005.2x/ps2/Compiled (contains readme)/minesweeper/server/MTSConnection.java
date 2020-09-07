package minesweeper.server;


import minesweeper.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * MultiThreadedServerConnection (usage: "new Thread(new MTSConnection(socket,debug,board)).start();" )
 * This facilitates the connection between a multithreaded server and a client. When started this listens
 * for inputs and answers according to the MinesweeperProtocol, which also mutates the board.
 */
public class MTSConnection implements Runnable {

    // Socket where the client is connected
    private final Socket socket;

    // I/O fields:
    private PrintWriter out = null;
    private BufferedReader in = null;

    // MultiPlayerMinesweeper specific references:
    private final boolean debug;
    private final Board board;

    // Abstraction function
    //      A connection between a server and a client is specified by a pair of sockets. The client has its own socket,
    //      the socket to which the server is listening is given by the field socket. When this connection is started
    //      we initialize an inputstream in and an outputstream out. The input from the client is answered on out in
    //      accordance to the protocol. This protocol observes and mutates the board which represents the game of
    //      minesweeper. The debug field forces the server to not disconnect a client who lost the game.
    //
    // Rep invariant
    //      none (TODO maybe socket != null, board != null?)
    //
    // Safety from rep exposure
    //      All fields are private, none of them are returned by any of the methods, there are no setters or getters
    //
    // Thread safety
    //      This class is NOT thread-safe, (but the board is so the game itself is threadsafe)

    public MTSConnection(Socket socket, boolean debug, Board board) {
        this.socket = socket;
        this.debug = debug;
        this.board = board;
        this.board.addPlayer();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            this.out = out;
            this.in = in;
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate serve()
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //do nothing
            }
        }
    }


    /**
     * Handle a single client connection. Returns when client disconnects.
     *
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     */
    private void handleConnection() throws IOException {
        // Make a new protocol for this connection
        MinesweeperProtocol msProtocol = new MinesweeperProtocol();
        // display the welcom message
        out.println(board.getWelcomeMessage());

        // Start listening for input and answer according to the protocol
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String answer = msProtocol.handleRequest(line, board);
                if (answer.equals(board.getBOOM_message()) && !debug) {
                    out.println(answer);
                    break;
                } else if (answer.equals("bye")) break;
                else out.println(answer);
            }
        }
        finally {
            closeConnection();
        }
    }

    /**
     * Remove the player from the board and then close the input and output streams.
     */
    private void closeConnection() throws IOException {
        board.removePlayer();
        out.close();
        in.close();
    }



}
