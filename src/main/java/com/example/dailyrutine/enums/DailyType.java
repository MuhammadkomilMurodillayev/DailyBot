package com.example.dailyrutine.enums;

/**
 * @author Muhammadkomil Murodillayev, сб 13:34. 03/06/23
 */
public enum DailyType implements BaseType{
    ONLY_ONE("Faqat bir kun"),
    EVERY_DAY("Har kuni"),
    EVERY_WEEK("Har hafta"),
    EVERY_MONTH("Har oy"),
    EVERY_YEAR("Har yili"),
    EVERY_CUSTOM("Har ...(o'zingiz kiriting)");

    private final String name;

    DailyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
