package ir.bigz.springbootreal.dto;

public enum SqlOperation {

    EQUAL("="),
    NOT_EQUAL("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    IN("in"),
    NOT_IN("not in"),
    CONTAINS("like"),
    STARTS_WITH("like"),
    ENDS_WITH("like"),
    NOT_CONTAINS("not like"),
    BETWEEN("between"),
    NOT_BETWEEN("not between");

    public final String operationSign;

    SqlOperation(String operationSign) {
        this.operationSign = operationSign;
    }

    public String getOperationSign() {
        return operationSign;
    }
}
