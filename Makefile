all:				P2P.class

P2P.class:		P2P.java
				@javac P2P.java

SuperNode.class:		SuperNodeThread.class SuperNodeGroupReceiver.class Peer.class Resource.class SuperNode.java
				@javac SuperNode.java

SuperNodeThread.class:		SuperNodeThread.java
				@javac SuperNodeThread.java

SuperNodeGroupReceiver.class:		SuperNodeGroupReceiver.java
				@javac SuperNodeGroupReceiver.java

Peer.class:		Peer.java
				@javac Peer.java

Resource.class:		Resource.java
				@javac Resource.java

Node.class:			NodeThread.class Heartbeat.class NodeClient.class Resource.class Node.java
				@javac Node.java

NodeThread.class:		NodeThread.java
				@javac NodeThread.java
				
Heartbeat.class:		Heartbeat.java
				@javac Heartbeat.java

NodeClient.class:		NodeClient.java
				@javac NodeClient.java

clean:
				@rm -rf *.class *~
