echo "Enter username:"
read username
private_key="~/.ssh/id_rsa"
echo "Enter path to private key [default ~/.ssh/id_rsa]:"
read private_key_input
if [ -n "$private_key_input" ]; then
    private_key="$private_key_input"
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


echo "Cleaning view server..."
ssh -i $private_key $username@$view_server_host -p $view_server_port "pkill -9 screen; screen -wipe" 
echo "Cleaning game server..."
ssh -i $private_key $username@$game_server_host -p $game_server_port "pkill -9 screen; screen -wipe" 
echo "Cleaning game client 1..."
ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -wipe;"
echo "Cleaning game client 2..."
ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -wipe;"
echo "Clean view client 1..."
ssh -i $private_key $username@$view_client_1_host -p $view_client_1_port "killall -9 java"
echo "Cleaning view client 2..."
ssh -i $private_key $username@$view_client_2_host -p $view_client_2_port "pkill -9 screen; screen -wipe;" 
echo "Done cleaning"