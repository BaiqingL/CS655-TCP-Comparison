echo "Enter username:"
read username
private_key="~/.ssh/id_rsa"
echo "Enter path to private key [default ~/.ssh/id_rsa]:"
read private_key_input
if [ -n "$private_key_input" ]; then
    private_key = "$private_key_input"
fi

# Read in the host for router
echo "Enter router host [default pc5.genirack.nyu.edu]:"
read router_host
if [ -z "$router_host" ]; then
    router_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for router
echo "Enter router port [default 30612]:"
read router_port
if [ -z "$router_port" ]; then
    router_port = "30612"
fi

# Read in the host for game server
echo "Enter game server host [default pc5.genirack.nyu.edu]:"
read game_server_host
if [ -z "$game_server_host" ]; then
    game_server_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for game server
echo "Enter game server port [default 30610]:"
read game_server_port
if [ -z "$game_server_port" ]; then
    game_server_port = "30610"
fi

# Read in the host for view server
echo "Enter view server host [default pc5.genirack.nyu.edu]:"
read view_server_host
if [ -z "$view_server_host" ]; then
    view_server_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for view server
echo "Enter view server port [default 30610]:"
read view_server_port
if [ -z "$view_server_port" ]; then
    view_server_port = "30610"
fi

# Read in the host for game client 1
echo "Enter game client 1 host [default pc5.genirack.nyu.edu]:"
read game_client_1_host
if [ -z "$game_client_1_host" ]; then
    game_client_1_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for game client 1
echo "Enter game client 1 port [default 30611]:"
read game_client_1_port
if [ -z "$game_client_1_port" ]; then
    game_client_1_port = "30611"
fi

# Read in the host for game client 2
echo "Enter game client 2 host [default pc5.genirack.nyu.edu]:"
read game_client_2_host
if [ -z "$game_client_2_host" ]; then
    game_client_2_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for game client 2
echo "Enter game client 2 port [default 30613]:"
read game_client_2_port
if [ -z "$game_client_2_port" ]; then
    game_client_2_port = "30613"
fi

# Read in the host for view client 1
echo "Enter view client 1 host [default pc5.genirack.nyu.edu]:"
read view_client_1_host
if [ -z "$view_client_1_host" ]; then
    view_client_1_host = "pc5.genirack.nyu.edu"
fi

# Read in the port for view client 1
echo "Enter view client 1 port [default 30614]:"
read view_client_1_port
if [ -z "$view_client_1_port" ]; then
    view_client_1_port = "30614"
fi

# Read in the host for view client 2
echo "Enter view client 2 host [default pc2.genirack.nyu.edu]:"
read view_client_2_host
if [ -z "$view_client_2_host" ]; then
    view_client_2_host = "pc2.genirack.nyu.edu"
fi

# Read in the port for view client 2
echo "Enter view client 2 port [default 30610]:"
read view_client_2_port
if [ -z "$view_client_2_port" ]; then
    view_client_2_port = "30610"
fi


echo "Installing Java on all machines..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_server_host -p $game_server_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on game server."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_server_host -p $view_server_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on view server."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_1_host -p $game_client_1_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on game client 1."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_2_host -p $game_client_2_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on game client 2."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_1_host -p $view_client_1_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on view client 1."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_2_host -p $view_client_2_port "sudo apt-get update -y; sudo apt-get install default-jdk -y; mkdir game" > /dev/null
echo "Finished installing Java on view client 2."
echo "Done installing Java on all machines."

echo "Copying java files and compiling to all machines..."
# Copy all java files in SimpleMultiplayerTTTGame to the machine's ~/game/ directory, then compile them
scp -i $private_key -P $game_server_port SimpleMultiplayerTTTGame/*.java $username@$game_server_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_server_host -p $game_server_port  "cd game; javac *.java" > /dev/null

scp -i $private_key -P $view_server_port SimpleMultiplayerTTTGame/*.java $username@$view_server_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_server_host -p $view_server_port "cd game; javac *.java" > /dev/null

scp -i $private_key -P $game_client_1_port SimpleMultiplayerTTTGame/*.java $username@$game_client_1_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_1_host -p $game_client_1_port "cd game; javac *.java" > /dev/null

scp -i $private_key -P $game_client_2_port SimpleMultiplayerTTTGame/*.java $username@$game_client_2_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_2_host -p $game_client_2_port "cd game; javac *.java" > /dev/null

scp -i $private_key -P $view_client_1_port SimpleMultiplayerTTTGame/*.java $username@$view_client_1_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_1_host -p $view_client_1_port "cd game; javac *.java" > /dev/null

scp -i $private_key -P $view_client_2_port SimpleMultiplayerTTTGame/*.java $username@$view_client_2_host:~/game/ > /dev/null
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_2_host -p $view_client_2_port "cd game; javac *.java" > /dev/null
echo "Done copying java files and compiled them to all machines."

# Ask the user if they want to use reno, cubic, or bbr
echo "Enter congestion control algorithm [reno, cubic, bbr]:"
read congestion_control
while [ "$congestion_control" != "reno" ] && [ "$congestion_control" != "cubic" ] && [ "$congestion_control" != "bbr" ]; then
    echo "Invalid congestion control algorithm. Try again"
    exit 1
fi

# Execute the set_congestion script based on which congestion they chose
if [ "$congestion_control" = "reno" ]; then
    chmod +x set_congestion_reno.sh
    ./set_congestion_reno.sh
elif [ "$congestion_control" = "cubic" ]; then
    chmod +x set_congestion_cubic.sh
    ./set_congestion_cubic.sh
elif [ "$congestion_control" = "bbr" ]; then
    chmod +x set_congestion_bbr.sh
    ./set_congestion_bbr.sh
fi

# Start the game and view servers in their perspective machines
ssh -i $private_key $username@$game_server_host -p $game_server_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java ViewServer 58001\n\"" > /dev/null

# Start the game server
ssh -i $private_key $username@$view_server_host -p $view_server_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java 58000 \"10.10.1.1\" 58001\n\"" > /dev/null
