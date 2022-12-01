To run the code in the SimpleMultiplayerTTTGame directory:

1. Navigate to SimpleMultiplayerTTTGame/

2. Compile with `javac *.java`

3. After compilation, you can copy the resulting byte-code (.class) files to any node on the GENI slice and run them from there. You can also run them locally.

4. First, run the view server from whichever machine/node you want:

&emsp;`java ViewServer <port number>`

&emsp;ex: `java ViewServer 58001`

5. Next, run the game server from whichever machine/node:

&emsp;`java GameServer <port> <viewServer> <viewPort>` where `<port>` (e.g. 58000) is the port number GameServer will listen on, `<ViewServer>` is the ID of the host/node running ViewServer, `<ViewPort>` is the port number the ViewServer will listen on.

&emsp;ex: `java GameServer 58000 "localhost" 58001` to run the GameServer on localhost, `java GameServer 58000 hostNameRunningGameServer 58001` in general.

6. Now, run the following command to run a game client from whichever machine/node. Run two of them to play:

&emsp;`java GameClient <GameServerHostName> <port>` where `<GameServerHostName>` is the host name of the computer running GameServer and `<port>` is the port number (e.g., 58000) that the GameServer will listen. ex: `java GameClient "localhost" 58000` to run the GameClient on localhost.

7. Finally, you can add a viewer with:

&emsp;`java ViewClient <ViewServerHostName> <viewPort>` where `<ViewServerHostName>` is the host name of the host/node running ViewServer and `<viewPort>` is the port number (e.g. 58001) that ViewServer will listen on.



 
