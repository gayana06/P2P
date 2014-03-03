/*
 ============================================================================
 Name        : DataContent.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : The definition of the PAST data content which will be routed 
			   to peers.
 ============================================================================
 */

package rice.uproject.implmentation;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class DataContent extends ContentHashPastContent{

	private static final long serialVersionUID = 1L;
	byte[] content;
    
    public DataContent(Id id,byte[] content)
    {
        super(id);
        this.content = content;        
    }
    
    public String toString()
    {
        return "MyPastContent ["+content+"]";
    }
}
