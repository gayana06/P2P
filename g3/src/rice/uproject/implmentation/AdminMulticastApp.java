/*
 ============================================================================
 Name        : AdminMulticastApp.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Class used to multicast the Admin statistic request to the 
			   peers. Upon the delivary of the message to the peers, will use
			   the same implementation to call the AdminMessageApp.
 ============================================================================
 */
package rice.uproject.implmentation;

import rice.p2p.commonapi.*;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.commonapi.PastryIdFactory;


@SuppressWarnings("deprecation")
public class AdminMulticastApp implements ScribeClient, Application {


  int seqNum = 0;
  

  CancellableTask publishTask;
  

  Scribe myScribe;
  

  Topic myTopic;


  protected Endpoint endpoint;


  public AdminMulticastApp(Node node) {
    
    this.endpoint = node.buildEndpoint(this, "ADMIN_MULTICAST");    
    myScribe = new ScribeImpl(node,"ADMIN_ORDER");
    myTopic = new Topic(new PastryIdFactory(node.getEnvironment()), "NODE_LOAD");
    System.out.println("myTopic = "+myTopic);
    endpoint.register();
  }
  
  public void subscribe() {
    myScribe.subscribe(myTopic, this); 
  }

  public void startPublishTask() {
    publishTask = endpoint.scheduleMessage(new PublishContent(), 5000, 25000);    
  }
  
  public void deliver(Id id, Message message) {
    if (message instanceof PublishContent) {
      sendMulticast();
    }
  }
  
  public void sendMulticast() {
    System.out.println("Node "+endpoint.getLocalNodeHandle()+" broadcasting "+seqNum);
    AdminMulticastContent myMessage = new AdminMulticastContent(endpoint.getLocalNodeHandle());
    Util.SettempFileCount(0);
    Util.SettempStorage(0);
    Util.ReSettempUserCount();
    myScribe.publish(myTopic, myMessage);
    seqNum++;
  }

  public void deliver(Topic topic, ScribeContent content) {
    System.out.println("AdminMulticastContent.deliver("+topic+","+content+")");
    if (((AdminMulticastContent)content).from != null) {
    	Util.GetadminMsgApp().routeMyMsgDirect(((AdminMulticastContent)content).from);
    }
  }


  public boolean anycast(Topic topic, ScribeContent content) {
    boolean returnValue = myScribe.getEnvironment().getRandomSource().nextInt(3) == 0;
    System.out.println("MyScribeClient.anycast("+topic+","+content+"):"+returnValue);
    return returnValue;
  }

  public void childAdded(Topic topic, NodeHandle child) {

  }

  public void childRemoved(Topic topic, NodeHandle child) {

  }

  public void subscribeFailed(Topic topic) {

  }

  public boolean forward(RouteMessage message) {
    return true;
  }


  public void update(NodeHandle handle, boolean joined) {
    
  }

  class PublishContent implements Message {
    public int getPriority() {
      return MAX_PRIORITY;
    }
  }

  
  public boolean isRoot() {
    return myScribe.isRoot(myTopic);
  }
  
  public NodeHandle getParent() {
    return ((ScribeImpl)myScribe).getParent(myTopic); 
  }
  
  public NodeHandle[] getChildren() {
    return myScribe.getChildren(myTopic); 
  }
  
}

