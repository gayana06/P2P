/*
 ============================================================================
 Name        : AdminMulticastContent.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : The multicast content definition
 ============================================================================
 */
package rice.uproject.implmentation;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;


public class AdminMulticastContent implements ScribeContent {

  NodeHandle from;

  public AdminMulticastContent(NodeHandle from) {
    this.from = from;
  }

  public String toString() {
    return "MyScribeContent from "+from;
  }  
}

