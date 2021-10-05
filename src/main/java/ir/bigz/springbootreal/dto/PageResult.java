package ir.bigz.springbootreal.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {

    private final List<T> result;
    private final int pageSize;
    private final int pageNumber;
    private final int offset;
    private final long total;

    public PageResult(List<T> result, int pageSize, int pageNumber, int offset, long total) {
        this.result = result;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.offset = offset;
        this.total = total;
    }
}
