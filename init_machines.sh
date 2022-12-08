echo "Enter username:"
read username
private_key="~/.ssh/id_rsa"
echo "Enter path to private key [default ~/.ssh/id_rsa]:"
read private_key_input
if [ -n "$private_key_input" ]; then
    private_key = "$private_key_input"
fi

echo "Installing Java on all machines..."
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30610 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30611 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
# This is the router machine
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30612 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30613 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30614 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
ssh -i $private_key $username@pc2.genirack.nyu.edu -p 30610 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
ssh -i $private_key $username@pc1.genirack.nyu.edu -p 30610 "sudo apt update -y; sudo apt install default-jdk -y; mkdir game" > /dev/null
echo "Done installing Java on all machines."

echo "Copying java files and compiling to all machines..."
# Copy all java files in SimpleMultiplayerTTTGame to the machine's ~/game/ directory, then compile them
scp -i $private_key -P 30610 SimpleMultiplayerTTTGame/*.java $username@pc5.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30610 "cd game; javac *.java" > /dev/null

scp -i $private_key -P 30611 SimpleMultiplayerTTTGame/*.java $username@pc5.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30611 "cd game; javac *.java" > /dev/null

scp -i $private_key -P 30613 SimpleMultiplayerTTTGame/*.java $username@pc5.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30613 "cd game; javac *.java" > /dev/null

scp -i $private_key -P 30614 SimpleMultiplayerTTTGame/*.java $username@pc5.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc5.genirack.nyu.edu -p 30614 "cd game; javac *.java" > /dev/null

scp -i $private_key -P 30610 SimpleMultiplayerTTTGame/*.java $username@pc2.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc2.genirack.nyu.edu -p 30610 "cd game; javac *.java" > /dev/null

scp -i $private_key -P 30610 SimpleMultiplayerTTTGame/*.java $username@pc1.genirack.nyu.edu:~/game/ > /dev/null
ssh -i $private_key $username@pc1.genirack.nyu.edu -p 30610 "cd game; javac *.java" > /dev/null
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