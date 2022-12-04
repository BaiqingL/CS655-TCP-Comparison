// A game stage: a game, players playing the game (via player sockets)
// player sockets
// player broadcast/publish sockets (send player state to ViewServer)

import java.net.*;
import java.io.*;
import java.util.*;

public class GameStage {
    // Maintain list of all client sockets for broadcast
    public TTTgame game;
    public String[] players; // names of players
    public int[] playerIDs; // IDs of players
    public Socket[] playerSockets; // player sockets accepted
    public Socket[] playerPublishSockets; // player state to ViewServer

    public String viewHostname;
    public int viewPort;

    public static final int NUMBER_OF_PLAYERS = 2;

    public GameStage(String viewHost, int port) {
        this.game = new TTTgame();
        this.players = new String[NUMBER_OF_PLAYERS];
        this.playerIDs = new int[NUMBER_OF_PLAYERS];
        this.playerSockets = new Socket[NUMBER_OF_PLAYERS];
        this.playerPublishSockets = new Socket[NUMBER_OF_PLAYERS];

        this.viewHostname = viewHost;
        this.viewPort = port;

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            this.players[i] = null;
            this.playerIDs[i] = 0;
            this.playerSockets[i] = null;
            this.playerPublishSockets[i] = null;
        }
    }

}