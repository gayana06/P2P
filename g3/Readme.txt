How to run 
------------------------------

Copy the gs3FS.jar and lib folder in to destination folder. 
Go to that folder from terminal and execute following command.

/home/sri/fuse/mem1  is mounting path where you want to mount fuse file system, ( this has to be created before program start)
/home/sri/emdc/p2p/p2p1 is applicaton path where meta.ini file resides.

9001            192.168.1.13     9001
listening port  boot ip        boot port 

Run the below command to boot up a new node. 
java -Djna.nosys=true -cp gs3FS.jar:lib/fusejan.jar:lib/fuse-jna-uber.jar:lib/Pastry_v1.jar:lib/xmlpull_1_1_3_4a.jar:lib/xpp3-1.1.3.4d_b2.jar rice.uproject.implmentation.StartUp 9001 192.168.1.13 9001 /home/sri/fuse/mem1 /home/sri/emdc/p2p/p2p1

Run the below command to boot up an admin console
java -Djna.nosys=true -cp gs3FS.jar:lib/fusejan.jar:lib/fuse-jna-uber.jar:lib/Pastry_v1.jar:lib/xmlpull_1_1_3_4a.jar:lib/xpp3-1.1.3.4d_b2.jar rice.uproject.implmentation.StartUp 9001 192.168.1.13 9001 /home/sri/fuse/mem1 /home/sri/emdc/p2p/p2p1 1

