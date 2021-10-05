package ir.bigz.springbootreal.dto;

import ir.bigz.springbootreal.commons.util.Utils;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class PagedQuery {

    int DEFAULT_PAGE_SIZE = 10;
    public static String PAGE_SIZE = "size"; // -1 means all record
    public static String PAGE_NUMBER = "page";
    public static String OFFSET = "offset";
    public static String ORDERING = "orderBy";
    public static String ORDER_DESC = "DESC";
    public static String ORDER_ASC = "ASC";

    private Integer pageSize = DEFAULT_PAGE_SIZE;
    private int pageNumber = 1;
    private int offset = 0; // start record
    private final List<String> ordering = new LinkedList<>();


    public PagedQuery(Map<String, String> parameters){

        if(parameters.containsKey(PAGE_SIZE)){
            pageSize = Utils.getQueryString(parameters, PAGE_SIZE, ValueCondition.EQUAL, DEFAULT_PAGE_SIZE, Integer.class);
        }

        if(pageSize <= 0){
            pageSize = -1;
        }

        String pageNumberStr = parameters.get(PAGE_NUMBER);
        if (pageNumberStr != null) {
            try {
                int anInt = Integer.parseInt(pageNumberStr);
                pageNumber = anInt > 0 ? anInt : 1;
            } catch (Exception e) {
                System.out.println("wrong page number format = " + pageNumberStr);
            }
        }

        String offsetStr = parameters.get(OFFSET);
        if (offsetStr != null) {
            try {
                int anInt = Integer.parseInt(offsetStr);
                offset = anInt > 0 ? anInt : 0;
            } catch (Exception e) {
                System.out.println("wrong offset format = " + offsetStr);
            }
        }

        String orderingQuery = parameters.get(ORDERING);
        if (orderingQuery != null) {
            try {
                String[] orderingList = orderingQuery.split(",");
                ordering.addAll(Arrays.asList(orderingList));
            } catch (Exception e) {
                System.out.println("wrong ordering format = " + orderingQuery);
            }
        }
    }

    public int getFirstResult() {
        return pageSize == -1 ? 0 : (offset + ((pageNumber - 1) * pageSize));
    }
}
