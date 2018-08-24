package q2p.quickclick.help;

import q2p.quickclick.base.*;

import java.io.*;

public final class FlushableOutputStream extends OutputStream {
	private OutputStream outputStream;
	private int bufferSize;
	private int left = 0;
	
	public FlushableOutputStream(final OutputStream outputStream, final int bufferSize) {
		this.outputStream = outputStream;
		this.bufferSize = bufferSize;
	}
	
	public void setBufferSize(final int bufferSize) throws IOException {
		if(bufferSize < this.bufferSize-left)
			outputStream.flush();
		
		left += bufferSize - this.bufferSize;
		this.bufferSize = bufferSize;
	}
	public int getBufferSize() {
		return bufferSize;
	}
	
	public void write(final int b) throws IOException {
		outputStream.write(b);
		
		if(--left == 0) {
			outputStream.flush();
			left = bufferSize;
		}
	}
	public void write(final byte[] data, int offset, final int length) throws IOException {
		while(offset != length) {
			final int toWrite = Math.min(left, length - offset);
			outputStream.write(data, offset, toWrite);
			offset += toWrite;
			left -= toWrite;
			if(left == 0) {
				outputStream.flush();
				left = bufferSize;
			}
		}
	}
	public void write(final byte[] data) throws IOException {
		write(data, 0, data.length);
	}
	
	public void flush() throws IOException {
		outputStream.flush();
		left = bufferSize;
	}
	
	public void close() {
		Closeables.safeClose(outputStream);
		outputStream = null;
	}
}