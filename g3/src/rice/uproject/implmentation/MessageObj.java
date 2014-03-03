/*
 ============================================================================
 Name        : MessageObj.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Class definition for object deletion message content
 ============================================================================
 */

package rice.uproject.implmentation;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;


public class MessageObj implements Message {

  MSG_COMMAND command;
  NodeHandle nh;
  Id deleteId;
  

  public MessageObj(NodeHandle nh, MSG_COMMAND command,Id deleteId) {
    this.nh = nh;
    this.command = command;
    this.deleteId=deleteId;
    
  }
  
  public String toString() {
    return "Node handler:"+nh+", command "+command;
  }


  public int getPriority() {
    return Message.LOW_PRIORITY;
  }
}

