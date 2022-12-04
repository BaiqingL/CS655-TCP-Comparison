To run the code in the SimpleMultiplayerTTTGame directory:

1. Navigate to SimpleMultiplayerTTTGame/

2. Compile with `javac *.java`

3. After compilation, you can copy the resulting byte-code (.class) files to any node on the GENI slice and run them from there. You can also run them locally.

4. First, run the view server from whichever machine/node you want:

`java ViewServer <port number>` \t ex:`java ViewServer 58001`

5. Next, run the game server from whichever machine/node:

`java GameServer <port> <viewServer> <viewPort>` \t ex: `java GameServer 58000 "localhost" 58001`

6. Now, two game clients from whichever machine/nodes. These are the players:

`java GameClient <host> <port>` \t ex: `java GameClient localhost 58000`

7. Finally, you can add viewers with:

`java ViewClient <host> <port>` \t ex: `java ViewClient localhost 58001`



 See SimpleMultiplayerTTTGame/commands_to_run.txt for an example walkthrough on which commands to run on which nodes.