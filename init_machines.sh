echo "Enter username:"
read username
private_key="~/.ssh/id_rsa"
echo "Enter path to private key [default ~/.ssh/id_rsa]:"
read private_key_input
if [ -n "$private_key_input" ]; then
    private_key = "$private_key_input"
fi

# Read in the host for router
echo "Enter router host [default pc3.instageni.research.umich.edu]:"
read router_host
if [ -z "$router_host" ]; then
    router_host="pc3.instageni.research.umich.edu"
fi

# Read in the port for router
echo "Enter router port [default 28612]:"
read router_port
if [ -z "$router_port" ]; then
    router_port="28612"
fi

# Read in the host for game server
echo "Enter game server host [default pc1.instageni.research.umich.edu]:"
read game_server_host
if [ -z "$game_server_host" ]; then
    game_server_host="pc1.instageni.research.umich.edu"
fi

# Read in the port for game server
echo "Enter game server port [default 28610]:"
read game_server_port
if [ -z "$game_server_port" ]; then
    game_server_port="28610"
fi

# Read in the host for view server
echo "Enter view server host [default pc4.instageni.research.umich.edu]:"
read view_server_host
if [ -z "$view_server_host" ]; then
    view_server_host="pc4.instageni.research.umich.edu"
fi

# Read in the port for view server
echo "Enter view server port [default 28610]:"
read view_server_port
if [ -z "$view_server_port" ]; then
    view_server_port="28610"
fi

# Get game client 1 host
echo "Enter game client 1 host [default pc3.instageni.research.umich.edu]:"
read game_client_1_host
if [ -z "$game_client_1_host" ]; then
    game_client_1_host="pc3.instageni.research.umich.edu"
fi

# Get game client 1 port
echo "Enter game client 1 port [default 28610]:"
read game_client_1_port
if [ -z "$game_client_1_port" ]; then
    game_client_1_port="28610"
fi

# Get game client 2 host
echo "Enter game client 2 host [default pc3.instageni.research.umich.edu]:"
read game_client_2_host
if [ -z "$game_client_2_host" ]; then
    game_client_2_host="pc3.instageni.research.umich.edu"
fi

# Get game client 2 port
echo "Enter game client 2 port [default 28611]:"
read game_client_2_port
if [ -z "$game_client_2_port" ]; then
    game_client_2_port="28611"
fi

# Get view client 1 host
echo "Enter view client 1 host [default pc3.instageni.research.umich.edu]:"
read view_client_1_host
if [ -z "$view_client_1_host" ]; then
    view_client_1_host="pc3.instageni.research.umich.edu"
fi

# Get view client 1 port
echo "Enter view client 1 port [default 28613]:"
read view_client_1_port
if [ -z "$view_client_1_port" ]; then
    view_client_1_port="28613"
fi

# Get view client 2 host
echo "Enter view client 2 host [default pc3.instageni.research.umich.edu]:"
read view_client_2_host
if [ -z "$view_client_2_host" ]; then
    view_client_2_host="pc3.instageni.research.umich.edu"
fi

# Get view client 2 port
echo "Enter view client 2 port [default 28614]:"
read view_client_2_port
if [ -z "$view_client_2_port" ]; then
    view_client_2_port="28614"
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

echo "Copying java files to all machines..."
# Copy all java files in SimpleMultiplayerTTTGame to the machine's ~/game/ directory, then compile them
scp -i $private_key -P $game_server_port SimpleMultiplayerTTTGame/*.java $username@$game_server_host:~/game/ &
scp -i $private_key -P $view_server_port SimpleMultiplayerTTTGame/*.java $username@$view_server_host:~/game/ &
scp -i $private_key -P $game_client_1_port SimpleMultiplayerTTTGame/*.java $username@$game_client_1_host:~/game/ &
scp -i $private_key -P $game_client_2_port SimpleMultiplayerTTTGame/*.java $username@$game_client_2_host:~/game/ &
scp -i $private_key -P $view_client_1_port SimpleMultiplayerTTTGame/*.java $username@$view_client_1_host:~/game/ &
scp -i $private_key -P $view_client_2_port SimpleMultiplayerTTTGame/*.java $username@$view_client_2_host:~/game/ &
wait
echo "Done copying java files to all machines."
echo "Compiling java files on all machines..."
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_server_host -p $game_server_port  "cd game; javac *.java" &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_server_host -p $view_server_port "cd game; javac *.java" &
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_1_host -p $game_client_1_port "cd game; javac *.java" &
ssh -o StrictHostKeychecking=no -i $private_key $username@$game_client_2_host -p $game_client_2_port "cd game; javac *.java" &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_1_host -p $view_client_1_port "cd game; javac *.java" &
ssh -o StrictHostKeychecking=no -i $private_key $username@$view_client_2_host -p $view_client_2_port "cd game; javac *.java" &
wait
echo "Done copying java files and compiled them to all machines."

echo "Setup complete!"