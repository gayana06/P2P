package rice.uproject.implmentation;

/*
 ============================================================================
 Name        : FileRequestThread.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : This thread will be started from StartUp.java, in order to 
 request retrieve the data from peers , once data received 
 write them in to fuse file system.
 // Every iteration we have put 5 sec thread sleep to avoid 
 * stressing
 ============================================================================
 */

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import rice.uproject.implmentation.FileProcessing.FileSizeMeta;

public class FileRequesThread {

	public FileRequesThread() {

	}

	public static void RequestFileFromPeers() throws IOException {
		for (Entry<String, ArrayList<String>> item : FileProcessing.mapMetaMemyStore
				.entrySet()) {
			String[] l_sListofHashes = new String[item.getValue().size()];
			l_sListofHashes = item.getValue().toArray(l_sListofHashes);
            boolean isAvi= false;
			Util.RequestFile(item.getKey(), l_sListofHashes);

			while (true) {
				if (Util.IsRequestedFileAvailable()) {

					isAvi =true;
					RetrivedFile[] GetFiles = Util.GetRetrivedFiles();
					if (GetFiles.length != 0) {
						System.out
								.println("<FUSE> <SOME REQUESTED FILES ARE AVAILABLE. GOING TO PEFORM FILE CREATION.>");
						for (int i = 0; i < GetFiles.length; ++i) {
							System.out
									.println("<FUSE> <WRITING IS PEFORMING IN THE FILE...PLEASE WAIT..FILE NAME> "
											+ GetFiles[i].GetUniqueKey());

							FileOutputStream fsFileRequester;

							fsFileRequester = new FileOutputStream(
									GetFiles[i].GetUniqueKey());
							BufferedOutputStream out = new BufferedOutputStream(
									fsFileRequester);
							out.write(GetFiles[i].GetContent());
							
							
							

							out.close();

						}
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(isAvi)
					break;

			}
		}
	}
}
