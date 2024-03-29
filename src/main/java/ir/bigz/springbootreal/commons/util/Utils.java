package ir.bigz.springbootreal.commons.util;

import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.dto.SqlOperation;
import ir.bigz.springbootreal.dto.ValueCondition;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.SampleExceptionType;
import org.javatuples.Quartet;
import org.springframework.data.domain.Sort;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ir.bigz.springbootreal.dto.ValueCondition.*;

public class Utils {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
    static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss");

    public static String getTimeNow() {
        return ZonedDateTime.now(ZoneId.of("Asia/Tehran")).format(FORMATTER);
    }

    public static String getLocalTimeNow() {
        return LocalDateTime.now().format(SIMPLE_FORMATTER);
    }

    public static Timestamp getTimestampNow() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String convertTimestamp(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(SIMPLE_FORMATTER);
    }

    public static Timestamp convertTimeString(String time) {
        LocalDateTime from = LocalDateTime.from(SIMPLE_FORMATTER.parse(time));
        return Timestamp.valueOf(from);
    }

    public static boolean isNotNull(String s) {
        return !isNull(s);
    }

    public static boolean isNull(String s) {
        return s == null || s.equals("") || s.equalsIgnoreCase("null");
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isNull(Object obj) {
        if (obj == null)
            return true;
        String str = obj.toString();
        return isNull(str);
    }

    public static boolean isEqual(String s1, String s2) {
        return (!isNull(s1) && !isNull(s2) && s1.equals(s2));
    }

    public static <K> Predicate getCriteriaPredicate(CriteriaBuilder criteriaBuilder, Root<K> root,Quartet<String, String, SqlOperation, ValueCondition> rule, String value){

        SqlOperation operation = rule.getValue2();
        switch (operation){
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(rule.getValue1()), createValueFromValueCondition(value, rule.getValue3()));
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(rule.getValue1()), createValueFromValueCondition(value, rule.getValue3()));
            case CONTAINS:
                return criteriaBuilder.like(root.get(rule.getValue1()), createValueFromValueCondition(value, rule.getValue3()));
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(rule.getValue1()), createValueFromValueCondition(value, rule.getValue3()));
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(rule.getValue1()), createValueFromValueCondition(value, rule.getValue3()));
            default:
                return criteriaBuilder.equal(root.get(rule.getValue1()), createValueFromValueCondition(value, EQUAL));
        }
    }

    private static String createValueFromValueCondition(String value, ValueCondition condition){
        switch (condition){
            case CONTAINS: case NOT_CONTAINS: return  "%" + value + "%";
            case STARTS_WITH: return value + "%";
            case ENDS_WITH: return "%" + value;
            default: return value;
        }
    }

    public static List<Sort.Order> getSortOrderFromPagedQuery(PagedQuery pagedQuery, Class clazz){

        List<Sort.Order> orders = new ArrayList<>();

        pagedQuery.getOrdering().forEach(
                orderParam -> {
                    try {
                        orderParam = orderParam.trim();
                        String[] orderField = orderParam.split("_");
                        Field field = getDeclaredField(clazz, orderField[0]);
                        if(field == null){
                            return;
                        }
                        String orderColumn = field.getName();
                        String orderDirection = PagedQuery.ORDER_ASC;
                        if (orderField.length > 1 && orderField[1].equalsIgnoreCase(PagedQuery.ORDER_DESC)) {
                            orderDirection = PagedQuery.ORDER_DESC;
                        }
                        Sort.Order order = new Sort.Order(Sort.Direction.valueOf(orderDirection), orderColumn);
                        orders.add(order);
                    }catch (Exception e){
                        throw AppException.newInstance(
                                SampleExceptionType.CREATE_QUERY_ERROR, String.format("field %s ordering is wrong", orderParam)
                        );
                    }
                }
        );

        return orders;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getQueryString(Map<String, String> queryString,
                                       String key,
                                       ValueCondition condition,
                                       T defaultValue,
                                       Class<T> type) {

        if (queryString == null) {
            return null;
        }

        String value = queryString.get(key);
        T returnValue = null;
        if (type.equals(String.class)) {
            returnValue = (T) "";
        }

        if (value != null) {
            if (condition == EQUAL) {
                if (type.equals(Integer.class))
                    returnValue = (T) Integer.valueOf(value);
                if (type.equals(Double.class))
                    returnValue = (T) Double.valueOf(value);
                if (type.equals(Long.class))
                    returnValue = (T) Long.valueOf(value);
                if (type.equals(Boolean.class))
                    returnValue = (T) Boolean.valueOf(value);
                if (type.equals(String.class)) {
                    returnValue = (T) value;
                }
            } else if (condition == CONTAINS) returnValue = (T) ("%" + value + "%");
            else if (condition == STARTS_WITH) returnValue = (T) (value + "%");
            else if (condition == ENDS_WITH) returnValue = (T) ("%" + value);
            else if (condition == IN) {
                String[] array = value.split(",");
                returnValue = (T) Arrays.asList(array);
            }
        } else if (isNotNull(defaultValue)) {
            returnValue = defaultValue;
        }

        return returnValue;
    }

    public static <T> void buildNativeQueryCondition(Map<String, String> queryString,
                                                     Map<String, String> conditionsMap,
                                                     Map<String, Object> parametersMap,
                                                     Map<String, String> parameterQueryWithFieldNameMap,
                                                     Map<String, SqlOperation> parameterQueryWithOperationMap,
                                                     Class<T> type) {
        parameterQueryWithFieldNameMap.keySet().forEach(
                paramQuery -> {
                    if (queryString != null) {
                        String value = queryString.get(paramQuery);
                        T parameterVal = null;
                        String queryCondition = "";
                        if (type.equals(String.class)) {
                            parameterVal = (T) "";
                        }
                        SqlOperation operation = parameterQueryWithOperationMap.get(paramQuery);
                        if (value != null && !Utils.isNull(value)) {
                            if (operation == SqlOperation.EQUAL ||
                                    operation == SqlOperation.NOT_EQUAL ||
                                    operation == SqlOperation.GREATER_THAN ||
                                    operation == SqlOperation.GREATER_THAN_OR_EQUAL ||
                                    operation == SqlOperation.LESS_THAN ||
                                    operation == SqlOperation.LESS_THAN_OR_EQUAL) {
                                if (type.equals(Integer.class)) {
                                    parameterVal = (T) Integer.valueOf(value);
                                }
                                if (type.equals(Double.class)) {
                                    parameterVal = (T) Double.valueOf(value);
                                }
                                if (type.equals(Long.class)) {
                                    parameterVal = (T) Long.valueOf(value);
                                }
                                if (type.equals(Boolean.class)) {
                                    parameterVal = (T) Boolean.valueOf(value);
                                }
                                if (type.equals(String.class)) {
                                    parameterVal = (T) value;
                                }
                                parametersMap.put(paramQuery, parameterVal);
                                queryCondition = " and " + parameterQueryWithFieldNameMap.get(paramQuery) + " " + operation.operationSign + " " + ":" + paramQuery;
                            } else if (operation == SqlOperation.CONTAINS || operation == SqlOperation.NOT_CONTAINS) {
                                if (type.equals(String.class)) {
                                    parametersMap.put(paramQuery, "%" + value + "%");
                                    queryCondition = " and " + parameterQueryWithFieldNameMap.get(paramQuery) + " " + operation.operationSign + " " + ":" + paramQuery;
                                }
                            } else if (operation == SqlOperation.STARTS_WITH) {
                                if (type.equals(String.class)) {
                                    parametersMap.put(paramQuery, value + "%");
                                    queryCondition = " and " + parameterQueryWithFieldNameMap.get(paramQuery) + " " + operation.operationSign + " " + ":" + paramQuery;
                                }
                            } else if (operation == SqlOperation.ENDS_WITH) {
                                if (type.equals(String.class)) {
                                    parametersMap.put(paramQuery, "%" + value);
                                    queryCondition = " and " + parameterQueryWithFieldNameMap.get(paramQuery) + " " + operation.operationSign + " " + ":" + paramQuery;
                                }
                            } else if (operation == SqlOperation.IN) {
                                String[] listArray = value.split(",");
                                parametersMap.put(paramQuery, Arrays.asList(listArray));
                                queryCondition = " and " + parameterQueryWithFieldNameMap.get(paramQuery) + " " + operation.operationSign + " (" + ":" + paramQuery + ")";
                            }

                            conditionsMap.put(paramQuery, queryCondition);
                        }
                    }
                });

    }

    public static String getWhereSimple(){
        return  " where 1=1";
    }

    public static Field getDeclaredField(Class className, String fieldName) {
        Field field = null;
        while (className != null && field == null) {
            try {
                field = className.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
            }
            className = className.getSuperclass();
        }
        return field;
    }
}
