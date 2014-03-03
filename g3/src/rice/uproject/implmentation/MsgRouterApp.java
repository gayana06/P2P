/*
 ============================================================================
 Name        : MsgRouerApp.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Class to route delete message request to the relevant peers
			   and remove the data object from the ring when reached the 
			   correct peer.
 ============================================================================
 */
package rice.uproject.implmentation;

import rice.Continuation;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;


public class MsgRouterApp implements Application {

  protected Endpoint endpoint;
  protected NodeHandle mynh;

  public MsgRouterApp(Node node) {
    
    this.endpoint = node.buildEndpoint(this, "MESSAGE_ROUTER");
    this.mynh=node.getLocalNodeHandle();

    this.endpoint.register();
  }


  public void routeMyMsgDirect(NodeHandle remotenh  ,MSG_COMMAND command,Id deleteId) {
    System.out.println(this+" sending direct to "+remotenh);    
    Message msg = new MessageObj(mynh,command,deleteId);
    endpoint.route(null, msg, remotenh);
  }
    

  public void deliver(Id id, Message message) {
    System.out.println(this+" received "+message);
    ProcessReceivedMessage(id, message);
  }
  
  private void ProcessReceivedMessage(Id id, Message message)
  {
	    MessageObj messageObj=(MessageObj)message;
    	if(messageObj.command==MSG_COMMAND.DELETE)
    	{
    		Util.GetPastImpl().remove(messageObj.deleteId, new Continuation<Object, Exception>() {
		        public void receiveResult(Object o) {
		           System.out.println("Removed object "+o);
		              }    		            
				public void receiveException(Exception ex){
					System.err.println("Remove failed"+ex.getMessage());
		          }
		        });
    	}
    	else
    	{
    		System.out.println("Unknown command");
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

