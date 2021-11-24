I have created a Java project which uses a client and a server. The client can send messages to the server given its address and port.
The server recieves the messages and displays them. Clients can also see messages that other clients send.

# Client commands:
Use /command to run commands. Any text after the command is an argument.

## /address
When used on its own, this command displays the address and port that the client will try to connect to.
When used with an argument, the address and port will be set to the argument's value. The argument
should be in the format address:port (e.g. 123.456.789.123:12345).

## /name
This works similarly to the /address command. When used on its own, the current client name will be displayed. When used
with an argument, the client name will be set to the argument's value. The client name will be displayed whenever a message
is sent by the client.
