package com.amadeus.orbital;

public class satClass {

   String Imageres;
    String name;
    String desc;

    satClass(String Imageres, String name, String desc){
        this.Imageres=Imageres;
        this.desc=desc;
        this.name=name;
    }

    public String getImageres() {
        return Imageres;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
