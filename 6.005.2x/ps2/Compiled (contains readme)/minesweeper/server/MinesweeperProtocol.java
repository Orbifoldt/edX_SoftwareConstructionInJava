package minesweeper.server;

import minesweeper.Board;

/**
 * Protocol for a minesweeper (multi-player) server
 */
public class MinesweeperProtocol {

    private final String helpMessage = "Type one of the following commands and press enter: " +
            "(help|look|bye|dig x y|flag x y|deflag x y)\r";

    /**
     * Handler for client input, performing requested operations and returning an output message.
     *
     * @param input message from client
     * @return message to client, or null if none
     */
    String handleRequest(String input, Board board) {
        String regex = "(look)|(help)|(bye)|"
                + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            return "Invalid input '" + input + "'. " + helpMessage;
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            // 'look' request
            return board.look();
        } else if (tokens[0].equals("help")) {
            // 'help' request
            return helpMessage;
        } else if (tokens[0].equals("bye")) {
            // 'bye' request
            return "bye";
        } else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                // 'dig x y' request
                return board.dig(x, y);
            } else if (tokens[0].equals("flag")) {
                // 'flag x y' request
                return board.flag(x, y);
            } else if (tokens[0].equals("deflag")) {
                // 'deflag x y' request
                return board.deflag(x, y);
            }
        }
        throw new UnsupportedOperationException();
    }
}
