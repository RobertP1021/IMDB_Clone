package org.example;

import java.util.ArrayList;
import java.util.List;

public interface Observer {
        void sendNotification(ArrayList<String> notifications, String notification);

}
