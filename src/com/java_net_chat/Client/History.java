package com.java_net_chat.Client;

import com.java_net_chat.Log;
import com.java_net_chat.ReverseFileReader;
import java.util.List;

public class History {
    private static final String TAG = "HISTORY";
    private ReverseFileReader file;

    public History(String nickName) {
        file = new ReverseFileReader("history_" + nickName + ".txt");
    }


    public List<String> load(int count) {
        return file.readLines(count);
    }

    public void add(String text) {
        file.addToEnd(text);
    }
}
