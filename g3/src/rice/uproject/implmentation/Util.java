/*
 ============================================================================
 Name        : MessageObj.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : Interfacing class for FUSE and Pastry.
 ============================================================================
 */
package rice.uproject.implmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.past.PastContentHandle;
import rice.p2p.past.PastImpl;
import rice.pastry.commonapi.PastryIdFactory;

public class Util {

	private static PastryIdFactory pidFactory;
	private static DataTransfer dataTransferApp;
	private static HashMap<String, byte[]> retrievedFiles = new HashMap<String, byte[]>();

	// Add this- start
	private static PastImpl pastImpl;
	private static long avgFileCount;
	private static double avgStorage;
	private static long tempFileCount;
	private static double tempStorage;
	private static long FileCount;
	private static double Storage;
	private static long tempUserCount;
	private static long userCount;
	private static PastContentHandle[] handlers;
	private static AdminMessageApp adminMsgApp;
	private static AdminMulticastApp adminMulticastApp;

	// Add this-end

	// Add this- start
	public static void SetPastContentHandler(PastContentHandle[] hls) {
		handlers = hls;
	}

	public static PastContentHandle[] GetPastContentHandler() {
		return handlers;
	}

	public static void LookUpForHandlers(Id deleteId) {
		handlers = null;
		dataTransferApp.LookupHandlers(deleteId);
	}

	public static void ReSettempUserCount() {
		tempUserCount = 0;
	}

	public static void SetPastImpl(PastImpl past) {
		pastImpl = past;
	}

	public static PastImpl GetPastImpl() {
		return pastImpl;
	}

	public static long GettempUserCount() {
		return tempUserCount;
	}

	public static long GetuserCount() {
		return userCount;
	}

	public static void UpdatetempUserCount() {
		tempUserCount++;
	}

	public static void SetUserCount(long users) {
		userCount = users;
	}

	public static void SetadminMsgApp(AdminMessageApp adminMA) {
		adminMsgApp = adminMA;
	}

	public static AdminMessageApp GetadminMsgApp() {
		return adminMsgApp;
	}

	public static void SetadminMulticastApp(AdminMulticastApp adminMulA) {
		adminMulticastApp = adminMulA;
	}

	public static AdminMulticastApp GetadminMulticastApp() {
		return adminMulticastApp;
	}

	public static void SetavgFileCount(long avgFC) {
		avgFileCount = avgFC;
	}

	public static long GetavgFileCount() {
		return avgFileCount;
	}

	public static void UpdateavgFileCount(long avgFC) {
		avgFileCount += avgFC;
	}

	public static void SettempFileCount(long tempFC) {
		tempFileCount = tempFC;
	}

	public static long GettempFileCount() {
		return tempFileCount;
	}

	public static void UpdatetempFileCount(long tempFC) {
		tempFileCount += tempFC;
	}

	public static void SetFileCount(long FC) {
		FileCount = FC;
	}

	public static long GetFileCount() {
		return FileCount;
	}

	public static void UpdateFileCount(long FC) {
		FileCount += FC;
	}

	public static void SetavgStorage(double avgS) {
		avgStorage = avgS;
	}

	public static double GetavgStorage() {
		return avgStorage;
	}

	public static void UpdateavgStorage(double avgS) {
		avgStorage += avgS;
	}

	public static void SettempStorage(double tempS) {
		tempStorage = tempS;
	}

	public static double GettempStorage() {
		return tempStorage;
	}

	public static void UpdatetempStorage(double tempS) {
		tempStorage += tempS;
	}

	public static void SetStorage(double S) {
		Storage = S;
	}

	public static double GetStorage() {
		return Storage;
	}

	public static void UpdateStorage(double S) {
		Storage += S;
	}

	// Add this- end
	public static void SetPastryIdFactory(PastryIdFactory idf) {
		pidFactory = idf;
	}

	public static void SetDataTransferApp(DataTransfer dtapp) {
		dataTransferApp = dtapp;
	}

	public static void SendData(String hashKey, byte[] data) {
		System.out.println("<PASTRY> START FROM UTIL.SENDDATA>");
		Id hashId = RecreateID(hashKey);
		dataTransferApp.SendData(hashId, data);
		System.out.println("<PASTRY> END FROM UTIL.SENDDATA>");
	}

	public static Id RecreateID(String hashKey) {
		return pidFactory.buildIdFromToString(hashKey);
	}

	public static String GenerateHashKey(String rawKey) {
		return pidFactory.buildId(rawKey).toString();
	}

	public static void RequestFile(String uniqueValue, String[] hashKey) {
		System.out.println("<PASTRY> START FROM UTIL.REQUESTFILE>");
		for (int j = 0; j < hashKey.length; ++j) {
			System.out
					.println("<PASTRY> <SENDING FILE HASHES> - " + hashKey[j]);
		}
		dataTransferApp.RetrieveFile(uniqueValue, hashKey);
		System.out.println("<PASTRY> END FROM UTIL.REQUEST FILE>");
	}

	public static boolean IsRequestedFileAvailable() {
		boolean isAvailable = false;
		if (!retrievedFiles.isEmpty() || (retrievedFiles.size() > 0))
			isAvailable = true;
		return isAvailable;
	}

	public static void AddRetrivalFiles(String uniquekey, byte[] data) {
		synchronized (retrievedFiles) {
			retrievedFiles.put(uniquekey, data);
			System.out
					.println("<PASTRY> <REQUESTED FILE HAS RECEIVED FROM UTIL.ADDRETRIVEFILES>");
		}
	}

	public static RetrivedFile[] GetRetrivedFiles() {
		synchronized (retrievedFiles) {
			// System.out.println("Start front end collect the file brought Util.GetRetrivedFiles");
			ArrayList<RetrivedFile> files = new ArrayList<RetrivedFile>();
			ArrayList<String> uniqueKeys = new ArrayList<String>();
			RetrivedFile tempFile;
			for (Entry<String, byte[]> item : retrievedFiles.entrySet()) {
				tempFile = new RetrivedFile();
				tempFile.SetUniqueKey(item.getKey());
				tempFile.SetContent(item.getValue());
				files.add(tempFile);
				uniqueKeys.add(item.getKey());
			}
			RemoveRetrivedFilesList(uniqueKeys.toArray(new String[uniqueKeys
					.size()]));
			// System.out.println("End front end collect the file brought Util.GetRetrivedFiles");
			return files.toArray(new RetrivedFile[files.size()]);
		}
	}

	public static void RemoveRetrivedFilesList(String[] uniqueKeys) {
		for (String uniqueKey : uniqueKeys) {
			if (retrievedFiles.containsKey(uniqueKey)) {
				retrievedFiles.remove(uniqueKey);
				System.out
						.println("<PASTRY><FRONT END COLLECTED DATA, DATA REMOVED FROM MEMORY UTIL.REMOVERETRIVEDFILELIST>");
			}
		}
	}


}

class RetrivedFile {
	private String uniqueKey;
	private byte[] content;

	public void SetUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public void SetContent(byte[] content) {
		this.content = content;
	}

	public String GetUniqueKey() {
		return uniqueKey;
	}

	public byte[] GetContent() {
		return content;
	}
}

enum MSG_COMMAND {
	GET, PUT, DELETE, R_PUT
}

class FileTransferThread extends Thread {

	FileTransferApp filetransfer;
	String filepath;
	NodeHandle nh;

	public FileTransferThread(FileTransferApp filetransfer, NodeHandle nh,
			String filepath) {
		this.filetransfer = filetransfer;
		this.filepath = filepath;
		this.nh = nh;
	}

	@Override
	public void run() {
		try {
			filetransfer.SendFileDirect(nh, filepath);
		} catch (Exception e) {
			System.err.println("Util Thread error " + e.getMessage());
		}
	}
}
