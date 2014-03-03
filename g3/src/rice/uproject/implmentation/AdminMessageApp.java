/*
 ============================================================================
 Name        : AdminMessageApp.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Admin console message routing application. This will send the 
			   load details to the admin instance directly.
			   Also this will process the retrieved messages and update the 
			   load details in the admin instance.
 ============================================================================
 */
package rice.uproject.implmentation;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;


public class AdminMessageApp implements Application {

  protected Endpoint endpoint;
  protected NodeHandle mynh;

  public AdminMessageApp(Node node) {
    // We are only going to use one instance of this application on each PastryNode
    this.endpoint = node.buildEndpoint(this, "ADMIN_ROUTER");
    this.mynh=node.getLocalNodeHandle();
    
    // the rest of the initialization code could go here
    
    // now we can receive messages
    this.endpoint.register();
  }


  

  public void routeMyMsgDirect(NodeHandle remotenh) {
    System.out.println(this+" sending direct to "+remotenh);    
    Message msg = new AdminMessage(mynh,Util.GetFileCount(),Util.GetStorage());
    endpoint.route(null, msg, remotenh);
  }
    

  public void deliver(Id id, Message message) {
    System.out.println(this+" received "+message);
    ProcessReceivedMessage(id, message);
  }
  
  private void ProcessReceivedMessage(Id id, Message message)
  {
	  synchronized (endpoint) {
	    AdminMessage messageObj=(AdminMessage)message;
	    Util.UpdatetempFileCount(messageObj.numberOfFiles);
	    Util.UpdatetempStorage(messageObj.storage);
	    Util.UpdatetempUserCount();
	  }
  }


  public void update(NodeHandle handle, boolean joined) {
  }
  

  public boolean forward(RouteMessage message) {
    return true;
  }
  
  public String toString() {
    return "MyApp "+endpoint.getId();
  }

}


