package imei.track;

public class Phones {

    private String imei;
    private String name;
    private String history;
    private String createdAt;
    private String updatedAt;
    private String lastSeenCell;
    private String status;

    public String getImei()         { return imei; }
    public String getName()         { return name; }
    public String getHistory()      { return history; }
    public String getCreatedAt()    { return createdAt; }
    public String getUpdatedAt()    { return updatedAt; }
    public String getLastSeenCell() { return lastSeenCell; }
    public String getStatus()       { return status; }

    public void setImei(String i)          { imei = i; }
    public void setName(String n)          { name = n; }
    public void setHistory(String h)       { history = h; }
    public void setCreatedAt(String c)     { createdAt = c; }
    public void setUpdatedAt(String u)     { updatedAt = u; }
    public void setLastSeenCell(String c)  { lastSeenCell = c; }
    public void setStatus(String s)        { status = s; }
}
