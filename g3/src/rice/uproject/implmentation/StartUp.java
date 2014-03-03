package rice.uproject.implmentation;

/*
 ============================================================================
 Name        : StartUp.java
 Author      : Group-3 (Sri,Gayana)
 Version     : 1.0
 Copyright   : P2P
 Description : P2P Distributed File System
 ============================================================================
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.fusejna.FuseException;
import rice.environment.Environment;
import rice.p2p.past.PastImpl;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.PersistentStorage;
import rice.persistence.Storage;
import rice.persistence.StorageManagerImpl;

public class StartUp {

	private static MsgRouterApp msgRouter;
	private static FileTransferApp fileTransfer;
	private static PastryIdFactory idf;
	private static PastImpl pastApp;
	private static DataTransfer dataTransferApp;
	public static boolean isAdminConsole = false;

	public static MsgRouterApp GetMsgROuterApp() {
		return msgRouter;
	}

	public static FileTransferApp GetFileTransferApp() {
		return fileTransfer;
	}

	public static PastryIdFactory GetPastryIdFactory() {
		return idf;
	}

	public static PastImpl GetPastImplementation() {
		return pastApp;
	}

	public static DataTransfer GetDataTransferApp() {
		return dataTransferApp;
	}

	public StartUp() {

	}

	public StartUp(int bindport, InetSocketAddress bootaddress, Environment env)
			throws Exception {

		// Generate the NodeIds Randomly
		NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

		// construct the PastryNodeFactory, this is how we use
		// rice.pastry.socket
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory,
				bindport, env);

		// construct a node
		PastryNode node = factory.newNode();

		// construct a new MyApp
		msgRouter = new MsgRouterApp(node);
		fileTransfer = new FileTransferApp(node);

		// past
		idf = new rice.pastry.commonapi.PastryIdFactory(env);
		Util.SetPastryIdFactory(idf);
		// create a different storage root for each node
		String storageDirectory = "./DATA_STORE/storage"
				+ node.getId().hashCode();

		// create the persistent part
		Storage stor = new PersistentStorage(idf, storageDirectory,
				4 * 1024 * 1024, node.getEnvironment());
		// Storage stor = new MemoryStorage(idf);
		pastApp = new PastImpl(node, new StorageManagerImpl(idf, stor,
				new LRUCache(new MemoryStorage(idf), 512 * 1024,
						node.getEnvironment())), 2, "");
		dataTransferApp = new DataTransfer(pastApp);
		Util.SetDataTransferApp(dataTransferApp);
		// past

		Util.SetPastImpl(pastApp);
	    //past
	    AdminMessageApp ama=new AdminMessageApp(node);
	    AdminMulticastApp amulti=new AdminMulticastApp(node);
	    Util.SetadminMsgApp(ama);
	    Util.SetadminMulticastApp(amulti);

		node.boot(bootaddress);

		// the node may require sending several messages to fully boot into the
		// ring
		synchronized (node) {
			while (!node.isReady() && !node.joinFailed()) {
				// delay so we don't busy-wait
				node.wait(500);

				// abort if can't join
				if (node.joinFailed()) {
					throw new IOException(
							"Could not join the FreePastry ring.  Reason:"
									+ node.joinFailedReason());
				}
			}
		}

		if (!isAdminConsole) {
			System.out.println("<PASTRY> FINISHED CREATING NEW NODE> " + node);

			StartUp dt1 = new StartUp();
			dt1.new FuseThread().start();
			System.out
					.println("<FUSE> STARTING NEW FUSE INSTANCE THREAD NOW ......> \n");

			Thread.sleep(5000);
			System.out
					.println("<FUSE>FILE REQUEST THREAD HAS STARTED ..........> ");
			dt1.new FileRequestThread().start();
		}
		// wait 10 seconds
		env.getTimeSource().sleep(5000);

		/*  -----------------Admin console part start here -----------------------------------*/
		
		 amulti.subscribe();
		if (!isAdminConsole) {
			while (true) {
				Thread.sleep(10000);
				Util.SetFileCount(FileProcessing.fnNumberOfFiles());
				Util.SetStorage(FileProcessing.fnTotalFileSize());
				System.out
				.println("<PASTRY> STAT DETAIL - FILE COUNT> "+FileProcessing.fnNumberOfFiles() + "<TOTAL SIZE> "+FileProcessing.fnTotalFileSize() );
			}
		}
	   
	   
	    
	    
		if (isAdminConsole) {
			amulti.startPublishTask();
			while (true) {
				Thread.sleep(5000);
				
				Util.SetUserCount(Util.GettempUserCount());
				
				if (Util.GettempUserCount() > 0) {
					Util.SetavgFileCount(Util.GettempFileCount()
							/ Util.GettempUserCount());
					Util.SetavgStorage(Util.GettempStorage()
							/ Util.GettempUserCount());
					System.out.println("*******************************************");
					System.out.println("File count:" + Util.GettempFileCount());
					System.out.println("Storage:" + Util.GettempStorage());
					System.out.println("setusercount:" + Util.GetuserCount());
					System.out.println("Average Storage :" + Util.GetavgStorage());
					System.out.println("Average File Count :" + Util.GetavgFileCount());
					System.out.println("******************************************* \n ");
				}
			}

		}

	}

	class FuseThread extends Thread {

		public FuseThread() {

		}

		public void run() {
			try {

				FileProcessing.StartFuse();
			} catch (FuseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class FileRequestThread extends Thread {

		public FileRequestThread() {

		}

		public void run() {

			try {
				FileRequesThread.RequestFileFromPeers();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {

		FileProcessing.SetStartupFiles(args[3], args[4]);

		if ((args.length ==6)) {
			int iVal = Integer.parseInt(args[5]);
			if (iVal == 0)
				isAdminConsole = false;
			else if (iVal == 1){
				isAdminConsole = true;
				System.out.println("<PASTRY><ADMIND CONSOLE IS GOING TO BE STARTING NOW>");
			}
		}

		// Loads pastry settings
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted
		// LAN)
		env.getParameters().setString("nat_search_policy", "never");

		try {
			// the port to use locally
			int bindport = Integer.parseInt(args[0]);

			// build the bootaddress from the command line args
			InetAddress bootaddr = InetAddress.getByName(args[1]);
			int bootport = Integer.parseInt(args[2]);
			InetSocketAddress bootaddress = new InetSocketAddress(bootaddr,
					bootport);

			// launch our node!
			new StartUp(bindport, bootaddress, env);

		} catch (Exception e) {
			// remind user how to use
			System.out.println("Usage:");
			System.out
					.println("java [-cp GS_3.jar]  rice.uproject.implmentation mounthpath applicaton path localbindport bootIP bootPort <isAdminpannel><possible values are 0,1>");
			throw e;
		}
	}
}
