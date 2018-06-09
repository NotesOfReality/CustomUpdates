package com.hardik.updates.Xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class DateIntervalXml {

    @Element(name = "Interval")
    private int interval;


    public DateIntervalXml(int interval) {
        this.interval = interval;
    }

    public DateIntervalXml() {
    }

    public int getInterval() {
        return interval;
    }

}
