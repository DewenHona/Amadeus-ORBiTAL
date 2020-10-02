package com.amadeus.orbital;

public class satClass {

   String satDocId;
    String name;
    String desc;

    satClass(String satDocId, String name, String desc){
        this.satDocId=satDocId;
        this.desc=desc;
        this.name=name;
    }

    public String getSatDocId() {
        return satDocId;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
