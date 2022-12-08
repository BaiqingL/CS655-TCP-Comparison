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
ssh -o StrictHostKeychecking=no $username@$router_host -p $router_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh -o StrictHostKeychecking=no $username@$game_server_host -p $game_server_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh -o StrictHostKeychecking=no $username@$game_client_1_host -p $game_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh -o StrictHostKeychecking=no $username@$game_client_2_host -p $game_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh -o StrictHostKeychecking=no $username@$view_client_1_host -p $view_client_1_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
ssh -o StrictHostKeychecking=no $username@$view_client_2_host -p $view_client_2_port sudo sysctl -w net.ipv4.tcp_congestion_control=$algo
echo "Starting experiment..."

for i in {1..10}
do
    echo "Starting view server..."
    ssh -i $private_key $username@$view_server_host -p $view_server_port "pkill -9 screen; screen -wipe; screen -d -m; screen -X stuff \"cd game; java ViewServer 58001\n\"" 
    echo "View server started."
    sleep 1
    echo "Starting game server..."
    ssh -i $private_key $username@$game_server_host -p $game_server_port "pkill -9 screen; screen -wipe; screen -d -m; screen -X stuff \"cd game; java GameServer 58000 \\\"10.10.1.1\\\" 58001\n\"" 
    echo "Game server started."
    echo "Sleep 1 second to allow for everything to load"
    sleep 1
    echo "Start game client 1..."
    ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -wipe; screen -d -m; screen -X stuff \"cd game; java GameClient \\\"10.10.1.2\\\" 58000 Alex 100 Auto 2000 \n\""
    echo "Start game client 2..."
    ssh -i $private_key $username@$game_client_1_host -p $game_client_1_port "pkill -9 screen; screen -wipe; screen -d -m; screen -X stuff \"cd game; java GameClient \\\"10.10.1.2\\\" 58000 Bob 100 Auto 2000 \n\""
    echo "Clean view client 1..."
    ssh -i $private_key $username@$view_client_1_host -p $view_client_1_port "killall -9 java"
    echo "Start view client 2..."
    ssh -i $private_key $username@$view_client_2_host -p $view_client_2_port "pkill -9 screen; screen -wipe; screen -d -m; screen -X stuff \"cd game; java ViewClient \\\"10.10.1.1\\\" 58001 Paul Alex Auto 30 500 \n\"" 
    echo "Starting experiment iteration $i"
    # Execute java ViewPerformance eth1 10 50 10 "viewPerf.txt" "10.10.1.1" 58001 Bob Auto 30 5000 on view client 1
    ssh -i $private_key $username@$view_client_1_host -p $view_client_1_port "cd game; java ViewPerformance eth1 10 50 10 \"viewPerf.txt\" \"10.10.1.1\" 58001 Alex; killall -9 java"
    # scp back /users/$username/game/viewPerf.txt to local machine
    scp -i $private_key $username@$view_client_1_host:/users/$username/game/viewPerf.txt viewPerf_{$i}_{$algo}.txt
done