package com.hardik.updates.Xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Rom")
public class RomXml {

    @Element(name = "IconID")
    private String iconId;

    @Element(name = "BuildCode")
    private int buildCode;

    @Element(name = "BuildCommand")
    private String buildCommand;

    @Element(name = "Delimiter")
    private String delimiter;

    @Element(name = "Position")
    private int position;

    @Element(name = "Maintainer")
    private String maintainer;

    @Element(name = "BuildLink")
    private String latestLink;

    @Element(name = "ChangelogLink")
    private String changelog;

    @Element(name = "GappsLink")
    private String gappsLink;

    @Element(name = "ForumLink")
    private String forumLink;

    @Element(name = "RecoveryLink")
    private String recoveryLink;

    @Element(name = "DonationLink")
    private String donationLink;

    @Element(name = "TelegramLink")
    private String telegramLink;

    public RomXml(String iconId, int buildCode, String buildCommand, String delimiter, int position, String maintainer, String latestLink, String changelog, String gappsLink, String forumLink, String recoveryLink, String donationLink, String telegramLink) {
        this.iconId = iconId;
        this.buildCode = buildCode;
        this.buildCommand = buildCommand;
        this.delimiter = delimiter;
        this.position = position;
        this.maintainer = maintainer;
        this.latestLink = latestLink;
        this.changelog = changelog;
        this.gappsLink = gappsLink;
        this.forumLink = forumLink;
        this.recoveryLink = recoveryLink;
        this.donationLink = donationLink;
        this.telegramLink = telegramLink;
    }

    public RomXml() {
    }

    public int getPosition() {
        return position;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getIconId() {
        return iconId;
    }

    public int getBuildCode() {
        return buildCode;
    }

    public String getBuildCommand() {
        return buildCommand;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public String getLatestLink() {
        return latestLink;
    }

    public String getChangelog() {
        return changelog;
    }

    public String getGappsLink() {
        return gappsLink;
    }

    public String getForumLink() {
        return forumLink;
    }

    public String getRecoveryLink() {
        return recoveryLink;
    }

    public String getDonationLink() {
        return donationLink;
    }

    public String getTelegramLink() {
        return telegramLink;
    }
}
