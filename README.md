###Preparation:

Create your slice using the Rspec

A list of commands below used to prepare and run test on a slice
for your reference (note: parameters in scp for your slice will be different)

`
// install java on 6 hosts of the slice
sudo apt update
sudo apt install default-jdk

mkdir game
cd game
`

For copying the game java files to slice test-slice:

`
// copy to ViewServer
scp -i id_geni_ssh_rsa -P 30610 *.java richchen@pc2.genirack.nyu.edu:~/game/
// copy to GameClient2
scp -i id_geni_ssh_rsa -P 30611 *.java richchen@pc5.genirack.nyu.edu:~/game/
// copy to GameServer
scp -i id_geni_ssh_rsa -P 30610 *.java richchen@pc1.genirack.nyu.edu:~/game/
// copy to  GameClient1
scp -i id_geni_ssh_rsa -P 30610 *.java richchen@pc5.genirack.nyu.edu:~/game/
// copy to ViewClient1
scp -i id_geni_ssh_rsa -P 30613 *.java richchen@pc5.genirack.nyu.edu:~/game/
// copy to ViewClient2
scp -i id_geni_ssh_rsa -P 30614 *.java richchen@pc5.genirack.nyu.edu:~/game/`



Compile 

`javac *.java`

Run the game:
`
// on ViewServer (10.10.1.1)
java ViewServer 58001 
// on GameServer (10.10.1.2)
java GameServer 58000 "10.10.1.1" 58001

// on GameClient1 and GameClient2
java GameClient "10.10.1.2" 58000
// GameClient will prompt/ask to input player name and time limit (in seconds)

// playerName and timeLimit as args
java GameClient "10.10.1.2" 58000 Alex 100
// player name Alex and time limit 100 (s) as args

java GameClient "10.10.1.2" 58000 Bob 100
// player name Bob and time limit 100 (s) as args

// playerName, timeLimit, Auto <interval> as args
java GameClient "10.10.1.2" 58000 Alex 100 Auto 2000
// player name Alex, time limit 100(s), Auto and 2000(ms) per move as args
// GameClient will play a move/2000ms for Alex automatically 

java GameClient "10.10.1.2" 58000 Bob 100 Auto 2000
// player name Bob, time limit 100(s), Auto and 2000(ms) per move as args
// GameClient will play a move/2000ms for Bob automatically 

// on ViewClient1 and ViewClient2
java ViewClient "10.10.1.1" 58001
// ViewClient will prompt/ask to input viewer name and player name

// ViewerName and playerName as args
java ViewClient "10.10.1.1" 58001 Paul Alex 
// viewer name Paul and player name Alex as args

java ViewClient "10.10.1.1" 58001 Peter Alex 
// viewer name Peter and player name Alex as args

// ViewerName, playerName, Auto <length> <interval> as args
java ViewClient "10.10.1.1" 58001 Paul Alex Auto 30 500
// viewer name Paul, player name Alex, Auto 30 500 as args
// automatically generate a comment of 30 bytes every 500ms

java ViewClient "10.10.1.1" 58001 Peter Alex Auto 30 500
// viewer name Peter, player name Alex, Auto 30 500 as args
// automatically generate a comment of 30 bytes every 500ms


// scripts to get performance
java ViewPerformance eth1 10 50 10 "viewPerf.txt" "10.10.1.1" 58001 Alex
java ViewPerformance eth1 20 50 10 "viewPerf.txt" "10.10.1.1" 58001 Alex Auto 30 5000

// commands to change link bandwidth 
sudo tc qdisc replace dev eth1 root netem rate 10kbit
sudo tc qdisc replace dev eth2 root netem rate 10kbit
sudo tc qdisc replace dev eth3 root netem rate 10kbit

sudo tc qdisc replace dev eth1 root netem delay 100ms
sudo tc qdisc replace dev eth2 root netem delay 100ms
sudo tc qdisc replace dev eth3 root netem delay 100ms`
