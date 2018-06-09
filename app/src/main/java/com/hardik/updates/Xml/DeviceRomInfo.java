package com.hardik.updates.Xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "RomInfo")
public class DeviceRomInfo {

    @Element
    private String Command;

    @Element
    private String Output;

    @Element
    private String XmlLink;

    public DeviceRomInfo(String command, String output, String xmlLink) {
        Command = command;
        Output = output;
        XmlLink = xmlLink;
    }

    public DeviceRomInfo() {
    }

    public String getCommand() {
        return Command;
    }

    public String getOutput() {
        return Output;
    }

    public String getXmlLink() {
        return XmlLink;
    }
}
