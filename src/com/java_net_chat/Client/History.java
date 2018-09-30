package com.java_net_chat.Client;

import com.java_net_chat.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/*
    Строки загружаются из файла в обратном порядке.
    На выходе возвращается List<String>
*/
public class History {
    private static final String TAG = "HISTORY";
    private RandomAccessFile historyFile;
    private long currentLineStart = -1;
    private long currentLineEnd = -1;

    public History(String nickName) {
        try {
            historyFile = new RandomAccessFile("history_" + nickName + ".txt", "rw");
        } catch (FileNotFoundException e) {
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
            historyFile.seek(filePointer);
            byte readByte = historyFile.readByte();
            if((readByte != '\r') && (readByte != '\n')) {
                currentLineEnd = filePointer + 1;
                break;
            }
            filePointer--;
        }

        while(filePointer >= 0) {
            historyFile.seek(filePointer);
            byte readByte = historyFile.readByte();
            if(readByte == '\n') {
                break;
            }
            filePointer--;
        }
        currentLineStart = filePointer + 1;
    }

    public void add(String text) {
        try {
            historyFile.seek(historyFile.length());
            historyFile.write((text + "\r\n").getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Record add error - " + e.getMessage());
        }
    }

    public List<String> getLastRecords(int count) {
        List<String> history = new ArrayList<>();
        try {
            currentLineStart = historyFile.length();
            while(true) {
                findPrevLine();
                if (currentLineStart == -1 || currentLineEnd == -1) {
                    break;
                }

                int len = (int)(currentLineEnd - currentLineStart);
                byte b[] = new byte[len];
                historyFile.seek(currentLineStart);
                historyFile.read (b, 0, len);
                history.add(new String(b));
                if(history.size() >= count) {
                    break;
                }
            }
            return history;
        } catch (IOException e) {
            Log.e(TAG, "Get last records error - " + e.getMessage());
        }
        return null;
    }
}
