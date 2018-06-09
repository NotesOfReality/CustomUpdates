package com.hardik.updates.Xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "DeviceRomInfoList")
public class DeviceRomCheckInfo {

    @ElementList
    private ArrayList<DeviceRomInfo> RomList;

    public DeviceRomCheckInfo(ArrayList<DeviceRomInfo> romList) {
        RomList = romList;
    }

    public DeviceRomCheckInfo() {
    }

    public ArrayList<DeviceRomInfo> getRomList() {
        return RomList;
    }
}
