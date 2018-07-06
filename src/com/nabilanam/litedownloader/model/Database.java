package com.nabilanam.litedownloader.model;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nabil
 */
public class Database implements Serializable
{
    private static final long serialVersionUID = 905795729829071092L;
    private static Database instance;

    public static Database getInstance()
    {
        if (instance != null || (instance = FileUtil.deserialize()) != null)
        {
            return instance;
        }
        return (instance = new Database()); 
    }

    private final LinkedList<Download> downloads;
    private int did;
    private String directoryPath;

    public Database()
    {
        did = -1;
        downloads = new LinkedList<>();
        directoryPath = System.getProperty("user.home") + File.separator + "Downloads";
    }

    public int getNewDId()
    {
        return did + 1;
    }

    public void add(Download download)
    {
        did++;
        downloads.add(download);
    }

    public void remove(int did)
    {
        downloads.remove(did);
        this.did = -1;
        for (Download dFile : downloads)
        {
            dFile.setDId(++this.did);
        }
    }

    public Download get(int id)
    {
        return downloads.get(id);
    }

    public List<Download> getDownloads()
    {
        return Collections.unmodifiableList(downloads);
    }

    public String getFileDirectory()
    {
        return directoryPath;
    }

    public void setFileDirectory(String directoryPath)
    {
        this.directoryPath = directoryPath;
    }

}
