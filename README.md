Steps to run:
1. Ensure file is in correct location corresponding to Database.java file
2. Run Database.java
3. Run Server files 1-5 ensuring that each file starts before the first client request is sent (can run with any # of servers)

If all 5 servers used, this will generate 50 client requests and should show all different reader/writer possibilities.
If not, just run again.

Note: One error can rarely occur. If two servers send request simulatenously with same lamport clock, and the server with a higher 
number somehow receives all responses before it receives the other servers request, then it will incorrectly enter the CS.
It works okay as long as the other servers request is received before that server's response, or if it is lower server number.
