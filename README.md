### Preparation:

Create your slice using the Rspec

A list of commands below used to prepare and run test on a slice
for your reference (note: parameters in scp for your slice will be different)

Execute the `init_machines.sh` script, which will install java, copy the files to the machines, and set the correct congestion algorithm. The script will ask the user to input all hosts and ports of their machines corresponding to the functions the machines are providing. Ensure these are correct before proceeding. It will also ask for a desired congestion control algorithm to be used.

### Running the game:


First, run ViewServer and GameServer on their respective nodes:

```
// on ViewServer (10.10.1.1)
java ViewServer 58001 
// on GameServer (10.10.1.2)
java GameServer 58000 "10.10.1.1" 58001
```


Next, there are multiple ways to run GameClients 1 and 2:

```
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
```

Next, we need to run the ViewClients: 

```
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
```


Finally, the following command, to be run on a ViewClient node, will run an experiment for max users with acceptable delay over a range of bandwidth values: 

```
// scripts to get performance
java ViewPerformance eth1 10 50 10 "viewPerf.txt" "10.10.1.1" 58001 Alex
java ViewPerformance eth1 20 50 10 "viewPerf.txt" "10.10.1.1" 58001 Alex Auto 30 5000
```

```
// commands to change link bandwidth 
sudo tc qdisc replace dev eth1 root netem rate 10kbit
sudo tc qdisc replace dev eth2 root netem rate 10kbit
sudo tc qdisc replace dev eth3 root netem rate 10kbit

sudo tc qdisc replace dev eth1 root netem delay 100ms
sudo tc qdisc replace dev eth2 root netem delay 100ms
sudo tc qdisc replace dev eth3 root netem delay 100ms`
```
