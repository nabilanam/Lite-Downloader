package com.nabilanam.litedownloader.model;

/**
 *
 * @author nabil
 */
public enum Column
{
    NO(0, "No"),
    CONTENT_TYPE(1,"File type"),
    NAME(2, "Name"),
    SIZE(3, "Size"),
    DONE(4, "Done"),
    DOWNLOADED(5, "Downloaded"),
    STATUS(6, "Status");
    
    private final int id;
    private final String text;
    
    private Column(int id, String text)
    {
        this.id = id;
        this.text = text;
    }
    
    public int getId()
    {
        return id;
    }
    
    public String getText()
    {
        return text;
    }
}
