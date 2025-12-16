package io.github.jaesungahn.gapinvestor.domain.location;

public class Location {
    private final String city; // 시/도 (e.g., 서울특별시)
    private final String district; // 시/군/구 (e.g., 종로구)
    private final String dong; // 읍/면/동 (e.g., 사직동)
    private final String code; // 법정동 코드

    public Location(String city, String district, String dong, String code) {
        this.city = city;
        this.district = district;
        this.dong = dong;
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getDong() {
        return dong;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return city + " " + district + " " + dong;
    }
}
