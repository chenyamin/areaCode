package cn.stylefeng.guns.modular;

import java.util.ArrayList;
import java.util.List;

public class RegionEntry {
    private String code;
    private String name;
    private List<RegionEntry> sub = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RegionEntry> getSub() {
        return sub;
    }

    public void setSub(List<RegionEntry> sub) {
        this.sub = sub;
    }

    public RegionEntry(String code, String name, List<RegionEntry> sub) {
        this.code = code;
        this.name = name;
        this.sub = sub;
    }

    public RegionEntry() {
    }

    @Override
    public String toString() {
        return "RegionEntry{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sub=" + sub +
                '}';
    }
}
