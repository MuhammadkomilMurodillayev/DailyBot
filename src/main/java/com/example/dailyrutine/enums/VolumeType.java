package com.example.dailyrutine.enums;

/**
 * @author Muhammadkomil Murodillayev, сб 13:17. 03/06/23
 */
public enum VolumeType implements BaseType {
    MIN("minut"),
    COUNT("sanoq", "sahifa");

    private final String name;
    private String name1;

    VolumeType(String name, String name1) {
        this.name = name;
        this.name1 = name1;
    }

    VolumeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getName1() {
        return name1;
    }

    public String getNameAndName1() {
        return name + ", " + name1;
    }
}
