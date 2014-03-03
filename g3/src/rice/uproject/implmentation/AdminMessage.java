/*
 ============================================================================
 Name        : AdminMessage.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Admin console message definition
 ============================================================================
 */
package rice.uproject.implmentation;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;


public class AdminMessage implements Message {


	private static final long serialVersionUID = 1L;
NodeHandle nh;	
  long numberOfFiles;
  double storage;
  

  public AdminMessage(NodeHandle nh,long numberOfFiles, double storage) {
	this.nh=nh;
    this.numberOfFiles = numberOfFiles;
    this.storage = storage;
  }
  
  public String toString() {
    return "Node handler:"+nh+" sent admin data";
  }


  public int getPriority() {
    return Message.LOW_PRIORITY;
  }
}


