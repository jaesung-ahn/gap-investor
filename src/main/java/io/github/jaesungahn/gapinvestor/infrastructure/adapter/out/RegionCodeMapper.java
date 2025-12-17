package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import java.util.HashMap;
import java.util.Map;

public class RegionCodeMapper {

    private static final Map<String, String[]> REGION_MAP = new HashMap<>();

    static {
        // Sample Data for MVP (Seoul)
        REGION_MAP.put("11110", new String[] { "Seoul", "Jongno-gu" });
        REGION_MAP.put("11140", new String[] { "Seoul", "Jung-gu" });
        REGION_MAP.put("11170", new String[] { "Seoul", "Yongsan-gu" });
        REGION_MAP.put("11200", new String[] { "Seoul", "Seongdong-gu" });
        REGION_MAP.put("11215", new String[] { "Seoul", "Gwangjin-gu" });
        REGION_MAP.put("11230", new String[] { "Seoul", "Dongdaemun-gu" });
        REGION_MAP.put("11260", new String[] { "Seoul", "Jungnang-gu" });
        REGION_MAP.put("11290", new String[] { "Seoul", "Seongbuk-gu" });
        REGION_MAP.put("11305", new String[] { "Seoul", "Gangbuk-gu" });
        REGION_MAP.put("11320", new String[] { "Seoul", "Dobong-gu" });
        REGION_MAP.put("11350", new String[] { "Seoul", "Nowon-gu" });
        REGION_MAP.put("11380", new String[] { "Seoul", "Eunpyeong-gu" });
        REGION_MAP.put("11410", new String[] { "Seoul", "Seodaemun-gu" });
        REGION_MAP.put("11440", new String[] { "Seoul", "Mapo-gu" });
        REGION_MAP.put("11470", new String[] { "Seoul", "Yangcheon-gu" });
        REGION_MAP.put("11500", new String[] { "Seoul", "Gangseo-gu" });
        REGION_MAP.put("11530", new String[] { "Seoul", "Guro-gu" });
        REGION_MAP.put("11545", new String[] { "Seoul", "Geumcheon-gu" });
        REGION_MAP.put("11560", new String[] { "Seoul", "Yeongdeungpo-gu" });
        REGION_MAP.put("11590", new String[] { "Seoul", "Dongjak-gu" });
        REGION_MAP.put("11620", new String[] { "Seoul", "Gwanak-gu" });
        REGION_MAP.put("11650", new String[] { "Seoul", "Seocho-gu" });
        REGION_MAP.put("11680", new String[] { "Seoul", "Gangnam-gu" });
        REGION_MAP.put("11710", new String[] { "Seoul", "Songpa-gu" });
        REGION_MAP.put("11740", new String[] { "Seoul", "Gangdong-gu" });
    }

    public static String getCity(String regionCode) {
        String[] parts = REGION_MAP.get(regionCode);
        return parts != null ? parts[0] : "Unknown";
    }

    public static String getDistrict(String regionCode) {
        String[] parts = REGION_MAP.get(regionCode);
        return parts != null ? parts[1] : "Unknown";
    }
}
