// A view stage: a game, players playing the game
// player broadcast/publish sockets (player state received from here)
// viewers watching the game (via viewer sockets)

import java.net.*;
import java.io.*;
import java.util.*;

public class ViewStage {
    // Maintain list of all client sockets for broadcast
    public TTTgame game;
    public String[] players; // names of players
    public int[] playerIDs; // IDs of players
    public Socket[] playerPublishSockets; // player publish/broadcast sockets accepted 
    public String[] viewers; // names of viewers
    public Socket[] viewerSockets; // viewer sockets accepted 
    public DataOutputStream[] outputToViewers; // output streams to viewers

    public static final int NUMBER_OF_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_VIEWERS = 100;

    public ViewStage() {
        this.game = new TTTgame();
        this.players = new String[NUMBER_OF_PLAYERS];
        this.playerIDs = new int[NUMBER_OF_PLAYERS];
        this.playerPublishSockets = new Socket[NUMBER_OF_PLAYERS];
        this.viewers = new String[MAX_NUMBER_OF_VIEWERS];
        this.viewerSockets = new Socket[MAX_NUMBER_OF_VIEWERS];
        this.outputToViewers = new DataOutputStream[MAX_NUMBER_OF_VIEWERS];

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            this.players[i] = null;
            this.playerIDs[i] = 0;
            this.playerPublishSockets[i] = null;
        }

        for (int i = 0; i < MAX_NUMBER_OF_VIEWERS; i++) {
            this.viewers[i] = null;
            this.viewerSockets[i] = null;
            this.outputToViewers[i] = null;
        }
    }

}
