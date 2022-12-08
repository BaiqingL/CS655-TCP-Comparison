echo "Enter username:"
read username
private_key="~/.ssh/id_rsa"
echo "Enter path to private key [default ~/.ssh/id_rsa]:"
read private_key_input
if [ -n "$private_key_input" ]; then
    private_key = "$private_key_input"
fi
# Get game client 1 host
echo "Enter game client 1 host [default pc5.genirack.nyu.edu]:"
read game_client_1_host
if [ -z "$game_client_1_host" ]; then
    game_client_1_host="pc5.genirack.nyu.edu"
fi

# Get game client 1 port
echo "Enter game client 1 port [default 30611]:"
read game_client_1_port
if [ -z "$game_client_1_port" ]; then
    game_client_1_port="30611"
fi

# Get game client 2 host
echo "Enter game client 2 host [default pc5.genirack.nyu.edu]:"
read game_client_2_host
if [ -z "$game_client_2_host" ]; then
    game_client_2_host="pc5.genirack.nyu.edu"
fi

# Get game client 2 port
echo "Enter game client 2 port [default 30613]:"
read game_client_2_port
if [ -z "$game_client_2_port" ]; then
    game_client_2_port="30613"
fi

# Get view client 1 host
echo "Enter view client 1 host [default pc5.genirack.nyu.edu]:"
read view_client_1_host
if [ -z "$view_client_1_host" ]; then
    view_client_1_host="pc5.genirack.nyu.edu"
fi

# Get view client 1 port
echo "Enter view client 1 port [default 30612]:"
read view_client_1_port
if [ -z "$view_client_1_port" ]; then
    view_client_1_port="30612"
fi

# Get view client 2 host
echo "Enter view client 2 host [default pc5.genirack.nyu.edu]:"
read view_client_2_host
if [ -z "$view_client_2_host" ]; then
    view_client_2_host="pc5.genirack.nyu.edu"
fi

# Get view client 2 port
echo "Enter view client 2 port [default 30614]:"
read view_client_2_port
if [ -z "$view_client_2_port" ]; then
    view_client_2_port="30614"
fi

echo "Start game client 1..."
ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java GameClient \"10.10.1.2\" 58000 Alex 100 Auto 2000\""
echo "Start game client 2..."
ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java GameClient \"10.10.1.2\" 58000 Bob 100 Auto 2000\""
echo "Start view client 1..."
ssh -i $private_key $username@$view_client_1_host -p $view_client_1_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java ViewClient \"10.10.1.1\" 58001 \""
echo "Start view client 2..."
ssh -i $private_key $username@$view_client_2_host -p $view_client_2_port "pkill -9 screen; screen -d -m; screen -X stuff \"cd game; java ViewClient \"10.10.1.1\" 58001 \"" 