all:				P2P.class

P2P.class:		P2P.java
				@javac P2P.java

SuperNode.class:		SuperNodeThread.class SuperNodeGroupReceiver.class SuperNode.java
				@javac SuperNode.java

SuperNodeThread.class:		SuperNodeThread.java
				@javac SuperNodeThread.java

SuperNodeGroupReceiver.class:		SuperNodeGroupReceiver.java
				@javac SuperNodeGroupReceiver.java

Node.class:			NodeThread.class Heartbeat.class NodeClient.class Node.java
				@javac Node.java

NodeThread.class:		NodeThread.java
				@javac NodeThread.java
				
Heartbeat.class:		Heartbeat.java
				@javac Heartbeat.java

NodeClient.class:		NodeClient.java
				@javac NodeClient.java

clean:
				@rm -rf *.class *~
