package com.amadeus.orbital;

public class satClass {

    int Imageres;
    String name;
    String desc;

    satClass(int Imageres,String name,String desc){
        this.Imageres=Imageres;
        this.desc=desc;
        this.name=name;
    }

    public int getImageres() {
        return Imageres;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
