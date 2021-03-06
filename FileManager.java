import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.logging.*;

public class FileManager {
	private File f;
	private RandomAccessFile file;
	private int numberOfPieces;
	private int pieceSize;
	private int fileSize;
	private FileHandler fileHandler;

	public FileManager(int numberOfPieces, int pieceSize, int fileSize, String fileName, int peerID, boolean hasFile) throws FileNotFoundException, IOException {
		String directory = "peer_" + peerID + "/";
		File dir = new File(directory);
		if(!dir.exists()) {
			dir.mkdirs();
			System.out.println("Peer:" + peerID + " creating directory " + directory);
		}
		
		f = new File(directory + fileName);
		if(hasFile && !f.exists() && !f.isDirectory()) {
			System.out.println("Peer:" + peerID + " should have the file, but it doesn't");
			System.exit(0);
		}
		file = new RandomAccessFile(f, "rw");
		
		this.numberOfPieces = numberOfPieces;
		this.pieceSize = pieceSize;
		this.fileSize = fileSize;

		fileHandler = new FileHandler(directory + "log_peer_" + peerID + ".log");
	}

	public Pieces getPiece(int index) throws IOException {
		int length = 0;
		if(index == numberOfPieces - 1) {
			length = fileSize - pieceSize*index; //make the length the size of the remaing bytes if this is the last index
		}
		else {
			length = pieceSize;
		}

		int offSet = index*pieceSize; //get the location in the file to start reading from
		byte[] bytes = new byte[length]; 

		file.seek(offSet);
		for(int i = 0; i < length; i++) {
			bytes[i] = file.readByte(); //read all the bytes for the piece into the bytes array
		}

		Pieces piece = new Pieces(index, bytes); //create a piece from index and bytes array
		return piece;
	}

	public void putPiece(Pieces piece) throws IOException {
		int offSet = piece.getPieceIndex()*pieceSize; //get the location in the file to read too
		int length = piece.getPieceBytes().length; //get the length of the piece

		byte[] bytes = piece.getPieceBytes(); 
		file.seek(offSet);
		for(int i = 0; i < length; i++) {
			file.writeByte(bytes[i]); //write to the file's bytes
		}
	}

	public FileHandler getFileHandler() {
		return fileHandler;
	}
}