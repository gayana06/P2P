/*
 ============================================================================
 Name        : DataTransfer.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : This class will send PAST data contents with byte[] and Id 
               to peers.
			   Retrieve PAST data chunks from different peers when requested
			   with hash Ids.
			   Lookup for replica nodes holding the same hash Id.
 ============================================================================
 */
package rice.uproject.implmentation;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sun.org.apache.xml.internal.serialize.OutputFormat.DTD;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastContentHandle;
import rice.p2p.past.PastImpl;

public class DataTransfer {
	private PastImpl pastApp;
	HashMap<String, HashMap<String, byte[]>> retrivalObjectMap;
	private String[] hashMapSent;
	public DataTransfer(PastImpl pastApp) {
		this.pastApp = pastApp;
		retrivalObjectMap = new HashMap<String, HashMap<String, byte[]>>();
		new RetrivalFileChecker(this).start();
	}

	public void SendData(Id hashKey, byte[] data) {
		System.out.println("Start send data DataTransfer.SendData");
		final PastContent datacontent = new DataContent(hashKey, data);
		System.out.println("Inserting " + datacontent + " at node "
				+ pastApp.getLocalNodeHandle());
		pastApp.insert(datacontent, new Continuation<Boolean[], Exception>() {
			public void receiveResult(Boolean[] results) {
				int numSuccessfulStores = 0;
				for (int ctr = 0; ctr < results.length; ctr++) {
					if (results[ctr].booleanValue())
						numSuccessfulStores++;
				}
				System.out.println(datacontent + " successfully stored at "
						+ numSuccessfulStores + " locations.");
				System.out.println("End send data DataTransfer.SendData");
			}

			public void receiveException(Exception result) {
				result.printStackTrace();
				System.err.println("Send Data Error DataTransfer.SendData");
			}
		});
	}

	public void RetrieveFile(String uniqueValue, final String[] hashKey) {
		System.out.println("Start retrieve file DataTransfer.RetrieveFile");
		hashMapSent=hashKey;
		UpdateRetrivalMap(uniqueValue, hashKey);
		for (int i = 0; i < hashKey.length; i++) {
			final String key = hashKey[i];
			System.out.println("Looking up " + key + " at node "
					+ pastApp.getLocalNodeHandle());
			pastApp.lookup(Util.RecreateID(hashKey[i]),
					new Continuation<PastContent, Exception>() {
						public void receiveResult(PastContent result) {

							try {
								synchronized (retrivalObjectMap) {

									DataContent obj = (DataContent) result;
									String resultKey = obj.getId().toString();
									byte[] data = obj.content;
									UpdateRetrivalMapData(resultKey, data);
									System.out
											.println("End retrieve file DataTransfer.RetrieveFile");
								}
							} catch (Exception ex) {
								System.err.println(ex.getMessage());
							}
							System.out.println("Successfully looked up "
									+ result + " for key " + key + ".");
						}

						public void receiveException(Exception result) {
							System.out.println("Error looking up " + key);
							result.printStackTrace();
						}
					});
		}
	}

	public void LookupHandlers(Id deletionId) {
		pastApp.lookupHandles(deletionId, 3,
				new Continuation<PastContentHandle[], Exception>() {
					public void receiveResult(PastContentHandle[] result) {
						try {
							Util.SetPastContentHandler(result);
							System.out
									.println("End retrieve content handlers DataTransfer.LookupHandlers");
						} catch (Exception ex) {
							System.err.println(ex.getMessage());
						}
						System.out.println("Successfully looked up handlers");
					}

					public void receiveException(Exception result) {
						result.printStackTrace();
					}
				});
	}

	public void DeleteObjects(final Id deletionId) {
		pastApp.remove(deletionId, new Continuation<Object, Exception>() {
			public void receiveResult(Object result) {
				try {
					System.out.println("Successfully deleted object "
							+ deletionId + " DataTransfer.DeleteObjects");
				} catch (Exception ex) {
					System.err.println("Error deleted object " + deletionId
							+ "--" + ex.getMessage());
				}
				System.out.println("Successfully deleted");
			}

			public void receiveException(Exception result) {
				result.printStackTrace();
			}
		});
	}

	public void CompleteFileRetrival() {
		synchronized (retrivalObjectMap) {
			if (retrivalObjectMap.size() > 0) {
				ArrayList<String> completedFiles = new ArrayList<String>();
				for (Entry<String, HashMap<String, byte[]>> temp : retrivalObjectMap
						.entrySet()) {
					if (!temp.getValue().containsValue(null)) {
						completedFiles.add(temp.getKey());
						Util.AddRetrivalFiles(temp.getKey(),
								GetFullContent(temp.getValue()));
						System.out
								.println("Fully retrieved files sent to front DataTransfer.CompleteFileRetrival");
					}
				}
				if (completedFiles.size() > 0)
					RemoveMapItems(completedFiles
							.toArray(new String[completedFiles.size()]));

			}
		}
	}

	private void RemoveMapItems(String[] keys) {
		for (String uniqueKey : keys) {
			if (retrivalObjectMap.containsKey(uniqueKey)) {
				retrivalObjectMap.remove(uniqueKey);
				System.out
						.println("Removed retrieve map item DataTransfer.RemoveMapItems");
			}
		}
	}

	private byte[] GetFullContent(HashMap<String, byte[]> file) {
		///ArrayList<Byte> byteList = new ArrayList<Byte>();
		int byteSize = 0;
		for (byte[] b : file.values()) {
			byteSize += b.length;
		}
		ByteBuffer merged = ByteBuffer.allocate(byteSize);
		for(String h:hashMapSent)
		{
			merged.put(file.get(h));
		}
		/*for (byte[] b : file.values()) {
			merged.put(b);
		}*/
		System.out.println("Full content merged DataTransfer.GetFullContent");
		return (merged.compact()).array();
	}

	private void UpdateRetrivalMapData(String key, byte[] data) {
		synchronized (retrivalObjectMap) {
			if (retrivalObjectMap.size() > 0) {

				for (HashMap<String, byte[]> temp : retrivalObjectMap.values()) {
					if (temp.containsKey(key)) {
						temp.put(key, data);
						System.out
								.println("Updated retrival map when chunk arrive DataTransfer.UpdateRetrivalMapData");
						break;
					}
				}
			}
		}
	}

	private void UpdateRetrivalMap(String uniqueValue, String[] hashKey) {
		synchronized (retrivalObjectMap) {
			HashMap<String, byte[]> chunks = new HashMap<String, byte[]>();
			for (int i = 0; i < hashKey.length; i++) {
				chunks.put(hashKey[i], null);
			}
			retrivalObjectMap.put(uniqueValue, chunks);
			System.out
					.println("Updated retrival map with hashlist DataTransfer.UpdateRetrivalMap");
		}
	}

}

class RetrivalFileChecker extends Thread {
	DataTransfer dt;

	public RetrivalFileChecker(DataTransfer dt) {
		this.dt = dt;
	}

	public void run() {
		while (true) {
			try {
				dt.CompleteFileRetrival();
				Thread.sleep(5000);
			} catch (Exception ex) {
				System.err
						.println(ex.getMessage() + "---" + ex.getStackTrace());
			}
		}
	}
}
