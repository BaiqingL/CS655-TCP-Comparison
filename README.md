To run the code in the SimpleMultiplayerTTTGame directory:

1. Navigate to SimpleMultiplayerTTTGame/

2. Compile with `javac *.java`

3. After compilation, you can copy the resulting byte-code (.class) files to any node on the GENI slice and run them from there. You can also run them locally.

4. First, run the view server from whichever machine/node you want:

&emsp;`java ViewServer <port number>`

&emsp;ex: `java ViewServer 58001`

5. Next, run the game server from whichever machine/node:

&emsp;`java GameServer <port> <viewServer> <viewPort>`

&emsp;ex: `java GameServer 58000 "localhost" 58001`

6. Now, run the following command to run a game client from whichever machine/node. Run two of them to play:

&emsp;`java GameClient <host> <port>` 

&emsp;ex: `java GameClient localhost 58000`

7. Finally, you can add a viewer with:

&emsp;`java ViewClient <host> <port>` 

&emsp;ex: `java ViewClient localhost 58001`



 
