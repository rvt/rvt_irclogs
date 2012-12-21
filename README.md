rvt_irclogs
===========

Display's IRC logfiles in Jahia
Currently only Eggdrop files are supported, but it should be possibe
to create others, or even mix/match them.

Installation
============

Ensure that eggdrop stores the files in a file name format like this : jahia.log.01Nov2012
Each line entry within the log should look like this:
[19:58] <ries> Example line as written by eggdrop


To add channel so they will be loaded into Jahia, create a bean that looks like this:

`<bean id="libreCADChannel" class="org.jahia.modules.irclogs.eggdrop.EggChatlogChannel">
    <property name="channel" value="librecad" />
    <property name="directory" value="/opt/egglogs"/>
</bean>`

Where channel is the first part of the filename and directory is the location of all logfiles
To complete this, add this bean to this bean

`<bean id="chatLogCache" class="org.jahia.modules.irclogs.ChatLogCache">
    <property name="channels">
        <list>
            <ref bean="libreCADChannel" />
            <ref bean="otherCHannel" />
            <ref bean="nextChannel" />
        </list>
    </property>
</bean>`

There is a full example configured in META-INF/spring/rvt_irclogs.xml

When this is done, add the plugin to the page using the displayIrcLogs module.
It should automatically show the available year, motnhs and days.


Please consider this module as beta and feedback is appreciated!
R. van Twisk
rvt_irclogs@rvt.dds.nl
