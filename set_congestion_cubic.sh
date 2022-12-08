echo "Enter username:"
read username
ssh $username@pc5.genirack.nyu.edu -p 30610 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc5.genirack.nyu.edu -p 30611 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc5.genirack.nyu.edu -p 30612 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc5.genirack.nyu.edu -p 30613 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc5.genirack.nyu.edu -p 30614 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc2.genirack.nyu.edu -p 30610 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic
ssh $username@pc1.genirack.nyu.edu -p 30610 sudo sysctl -w net.ipv4.tcp_congestion_control=cubic