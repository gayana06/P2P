package rice.uproject.implmentation;

/*
============================================================================
Name        : FileSpliting.java
Author      : Group-3 (Sri,Gayana)
Version     : 1.0
Copyright   : P2P
Description : When file available split the file and send to peers
============================================================================
*/


import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

public class FileSpliting {

	/** the maximum size of each file "chunk" generated, in bytes */
	public static long chunkSize = 20; // here i put only 1 MB

	/**
	 * split the file specified by filename into pieces, each of size chunkSize
	 * except for the last one, which may be smaller
	 */
	public ArrayList<String> split(String sFileName)
			throws FileNotFoundException, IOException {
		BufferedInputStream lbins = new BufferedInputStream(
				new FileInputStream(sFileName));

		File f = new File(sFileName);
		long fileSize = f.length();

		System.out.println("THE FILE IS GOING TO SPLIT - FILE SIZE - "
				+ fileSize + "FILE NAME:" + sFileName);
		int subfile;
		byte[] buffer = new byte[(int) chunkSize];
		ArrayList<String> fSendToPeers = new ArrayList<>();
		String sRawKey = "Sri" + sFileName;
		for (subfile = 0; subfile < fileSize / chunkSize; subfile++) {
			lbins.read(buffer);
			long epoch = System.currentTimeMillis();
			sRawKey += epoch;
			String sFileHash = Util.GenerateHashKey(sRawKey);
			Util.SendData(sFileHash, buffer);

			fSendToPeers.add(sFileHash);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		if (fileSize != chunkSize * (subfile - 1)) {

			int iRestSize = (int) (fileSize - (chunkSize * subfile));
			byte[] lrestbuffer = new byte[iRestSize];
			lbins.read(lrestbuffer);
			sRawKey += Calendar.getInstance().get(Calendar.MILLISECOND);
			String sFileHash = Util.GenerateHashKey(sRawKey);
			Util.SendData(sFileHash, buffer);

			fSendToPeers.add(sFileHash);
			System.out.println("File hash value :" + sFileHash + "\n");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lbins.close();

		return fSendToPeers;
	}

	public void FetchFileFromPeers() {

	}

	public void join(String baseFilename) throws IOException {
		int numberParts = getNumberParts(baseFilename);

		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(baseFilename));
		for (int part = 0; part < numberParts; part++) {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(baseFilename + "." + part));

			int b;
			while ((b = in.read()) != -1)
				out.write(b);

			in.close();
		}
		out.close();
	}

	public int getNumberParts(String baseFilename) throws IOException {
		// list all files in the same directory
		File directory = new File(baseFilename).getAbsoluteFile()
				.getParentFile();
		final String justFilename = new File(baseFilename).getName();
		String[] matchingFiles = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(justFilename)
						&& name.substring(justFilename.length()).matches(
								"^\\.\\d+$");
			}
		});
		return matchingFiles.length;
	}
}
