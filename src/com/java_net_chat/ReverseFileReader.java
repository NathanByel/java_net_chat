package com.java_net_chat;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReverseFileReader {
    private static final String TAG = "FILE";

    private RandomAccessFile file;
    private long currentLineStart = -1;
    private long currentLineEnd = -1;


    public ReverseFileReader(String filePath) {
        try {
            file = new RandomAccessFile(filePath, "rw");
            currentLineStart = file.length();
        } catch (Exception e) {
            Log.e(TAG, "File access error - " + e.getMessage());
        }
    }

    private void findPrevLine() throws IOException {
        currentLineEnd = currentLineStart;

        if (currentLineEnd == 0) {
            currentLineEnd = -1;
            currentLineStart = -1;
            return;
        }

        long filePointer = currentLineStart - 1;
        while(filePointer >= 0) {
            file.seek(filePointer);
            byte readByte = file.readByte();
            if((readByte != '\r') && (readByte != '\n')) {
                currentLineEnd = filePointer + 1;
                break;
            }
            filePointer--;
        }

        while(filePointer >= 0) {
            file.seek(filePointer);
            byte readByte = file.readByte();
            if(readByte == '\n') {
                break;
            }
            filePointer--;
        }
        currentLineStart = filePointer + 1;
    }

    public String readLine() {
        try {
            findPrevLine();
            if (currentLineStart == -1 || currentLineEnd == -1) {
                return null;
            }

            int len = (int)(currentLineEnd - currentLineStart);
            byte b[] = new byte[len];

            file.seek(currentLineStart);
            file.read (b, 0, len);
            return new String(b);
        } catch (IOException e) {
            Log.e(TAG, "Read line error - " + e.getMessage());
        }
        return null;
    }

    public List<String> readLines(int count) {
        List<String> lines = new ArrayList<>();
        while(true) {
            String s = readLine();
            if (currentLineStart == -1 || currentLineEnd == -1) {
                break;
            }

            lines.add(s);
            if(lines.size() >= count) {
                break;
            }
        }
        Collections.reverse(lines);
        return lines;
    }

    public void seekToEnd() {
        currentLineStart = -1;
        currentLineEnd = -1;
    }

    public void addToEnd(String text) {
        try {
            file.seek(file.length());
            file.write((text + "\r\n").getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Record add error - " + e.getMessage());
        }
    }

}
