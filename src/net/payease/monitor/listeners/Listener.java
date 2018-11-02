package net.payease.monitor.listeners;

import net.payease.monitor.events.Event;

public interface Listener {

    public void notify(Event event);
    public void notifyError(String recipients,String serverIP);


}
