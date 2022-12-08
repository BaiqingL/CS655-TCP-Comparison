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
echo "This may take a few minutes..."

echo "Installing Java on game server..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_server_host -p $game_server_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
wait
echo "Installing Java on view server..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_server_host -p $view_server_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
wait
echo "Installing Java on game client 1..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_1_host -p $game_client_1_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
echo "Installing Java on game client 2..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_2_host -p $game_client_2_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
wait
echo "Installing Java on view client 1..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_1_host -p $view_client_1_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
echo "Installing Java on view client 2..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_2_host -p $view_client_2_port "echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections; sudo apt-get update -y; sudo apt-get install openjdk-11-jdk-headless -y; mkdir game" &
wait
echo "Done installing Java on all machines."

echo "Copying java files and compiling to all machines..."
# Copy all java files in SimpleMultiplayerTTTGame to the machine's ~/game/ directory, then compile them
scp -i $private_key -P $game_server_port SimpleMultiplayerTTTGame/*.java $username@$game_server_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_server_host -p $game_server_port  "cd game; javac *.java" &

scp -i $private_key -P $view_server_port SimpleMultiplayerTTTGame/*.java $username@$view_server_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_server_host -p $view_server_port "cd game; javac *.java" &

scp -i $private_key -P $game_client_1_port SimpleMultiplayerTTTGame/*.java $username@$game_client_1_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_1_host -p $game_client_1_port "cd game; javac *.java" &

scp -i $private_key -P $game_client_2_port SimpleMultiplayerTTTGame/*.java $username@$game_client_2_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_2_host -p $game_client_2_port "cd game; javac *.java" &

scp -i $private_key -P $view_client_1_port SimpleMultiplayerTTTGame/*.java $username@$view_client_1_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_1_host -p $view_client_1_port "cd game; javac *.java" &

scp -i $private_key -P $view_client_2_port SimpleMultiplayerTTTGame/*.java $username@$view_client_2_host:~/game/ &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_2_host -p $view_client_2_port "cd game; javac *.java" &
wait
echo "Done copying java files and compiled them to all machines."

# Execute the set_congestion script based on which congestion they chose
algo=""

# Start a while loop
while true; do
  # Ask the user for input
  read -p "Enter one of the options: reno, cubic, bbr: " option

  # Check if the user entered one of the valid options
  if [[ "$option" == "reno" ]]; then
    # If the user entered "reno", set the value of the algo variable to "reno"
    algo="reno"
    break
  elif [[ "$option" == "cubic" ]]; then
    # If the user entered "cubic", set the value of the algo variable to "cubic"
    algo="cubic"
    break
  elif [[ "$option" == "bbr" ]]; then
    # If the user entered "bbr", set the value of the algo variable to "bbr"
    algo="bbr"
    break
  else
    # If the user didn't enter a valid option, print an error message
    echo "Invalid option. Please try again."
  fi
done

echo "Congestion control algorithm: $algo"
ssh $username@$router_host -p $router_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh $username@$game_server_host -p $game_server_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh $username@$game_client_1_host -p $game_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh $username@$game_client_2_host -p $game_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh $username@$view_client_1_host -p $view_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh $username@$view_client_2_host -p $view_client_2_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo

# Start the game and view servers in their perspective machines
ssh -i $private_key $username@$game_server_host -p $game_server_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java ViewServer 58001\n\"" 

# Start the game server
ssh -i $private_key $username@$view_server_host -p $view_server_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java 58000 \"10.10.1.1\" 58001\n\"" 
