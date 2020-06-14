package utilities;

import com.google.gson.Gson;
import com.propertydekho.fetcherservice.entity.PropFilterableSortableData;


import java.util.*;


public class Utilities {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private Map<Integer, String> areaMap;
    private Map<Integer, String> consMap;
    private Map<Integer, String> saleMap;
    private Map<String, Double> areaPriceMap;

    public Utilities() {

    }

    private double getPropPrice(PropFilterableSortableData prop) {
        String area = prop.getArea();
        String bedroom = prop.getBedroom();
        double basePrice = areaPriceMap.get(area) * Integer.parseInt(bedroom.split(" ")[0]);
        return basePrice + (double) getRandomIntegerBetweenRange(0, 1000000);
    }

    public static void getRandomNumber() {
        Random objGenerator = new Random();
        int randomNumber = objGenerator.nextInt(10000);
        System.out.println("Random No : " + randomNumber);
    }

    public static int getRandomIntegerBetweenRange(int min, int max) {
        int x = (int) (Math.random() * ((max - min) + 1)) + min;
        return x;
    }

    public static void main(String[] args) {
        new Utilities();
    }

    private String getPropId(int count) {
        StringBuilder builder = new StringBuilder();
        int length = ALPHA_NUMERIC_STRING.length();
        while (count-- != 0) {
            int character = (int) (Math.random() * length);
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public String getArea() {
        int index = getRandomIntegerBetweenRange(0, 4);
        return areaMap.get(index);
    }

    public void setAreaMap() {
        areaMap = new HashMap<>();
        areaMap.put(0, "Whitefield");
        areaMap.put(1, "Bellandur");
        areaMap.put(2, "Marathahalli");
        areaMap.put(3, "Electronic City");
        areaMap.put(4, "Doddanekundi");
    }

    public String getBedroom() {
        String bedroom = getRandomIntegerBetweenRange(1, 4) + " BHK";
        return bedroom;
    }

    public void setConstructionMap() {
        consMap = new HashMap<>();
        consMap.put(0, "Under Construction");
        consMap.put(1, "Ready To Move");
    }

    public void setSaleType() {
        saleMap = new HashMap<>();
        saleMap.put(0, "New");
        saleMap.put(1, "Resale");
    }

    public void setAreaPrice() {
        areaPriceMap = new HashMap<>();
        areaPriceMap.put("Whitefield", 3400000.0);
        areaPriceMap.put("Bellandur", 3800000.0);
        areaPriceMap.put("Marathahalli", 3000000.0);
        areaPriceMap.put("Electronic City", 2900000.0);
        areaPriceMap.put("Doddanekundi", 2800000.0);
    }

    public List<PropFilterableSortableData> getProperties() {
        List<PropFilterableSortableData> props = new ArrayList<>();
        setAreaMap();
        setConstructionMap();
        setSaleType();
        setAreaPrice();
        for (int i = 0; i < 100; i++) {
            PropFilterableSortableData prop = PropFilterableSortableData.builder()
                    .propID(getPropId(6))
                    .area(getArea())
                    .bedroom(getBedroom())
                    .constructionStatus(consMap.get(getRandomIntegerBetweenRange(0, 1)))
                    .saleType(saleMap.get(getRandomIntegerBetweenRange(0, 1)))
                    .build();
            double price = getPropPrice(prop);
            prop.setPropPrice(price);
            prop.setSqft(getSqFt(prop));
            prop.setPropName(getPropName(prop));
            props.add(prop);
        }
        Gson gson = new Gson();
        String json = gson.toJson(props);
        System.out.println(json);

        return props;
    }

    public int getSqFt(PropFilterableSortableData prop) {
        String bedroom = prop.getBedroom();
        return (Integer.parseInt(bedroom.split(" ")[0]) * 450 + Utilities.getRandomIntegerBetweenRange(0, 500));
    }

    public String getPropName(PropFilterableSortableData prop) {
        return prop.getBedroom() + " " + prop.getSqft() + " Sq-ft Flat for Sale in " + prop.getArea();
    }
}
